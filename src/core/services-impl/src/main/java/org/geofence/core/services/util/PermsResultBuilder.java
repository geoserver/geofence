/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.services.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.model.Rule;
import org.geofence.core.model.enums.GrantType;
import org.geotools.api.filter.Filter;
import org.geotools.api.filter.FilterFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.visitor.SimplifyingFilterVisitor;

/**
 * Computes a {@link PermsResultInternal} (CQL filter + accessible-resources set) from a single role's sorted rule list,
 * honoring rule priority: a higher-priority full-block DENY punches a hole in any lower-priority ALLOW it overlaps with
 * workspace/layer-wise. Partial-block DENYs (service/request-scoped) don't affect this discovery query, since
 * {@link org.geofence.core.services.RuleReaderService#getPermissionFilter} is only ever asked with service/request left
 * as {@code ANY}.
 *
 * @author etj
 */
public class PermsResultBuilder {

    private static final Logger LOGGER = LogManager.getLogger(PermsResultBuilder.class);
    private static final FilterFactory ff = CommonFactoryFinder.getFilterFactory();

    public static PermsResultInternal computePerms(List<Rule> sortedRules) {

        List<Rule> survivingAllows = new ArrayList<>();
        List<Rule> activeDenies = new ArrayList<>();

        for (Rule rule : sortedRules) {
            boolean isFullBlock = (rule.getService() == null && rule.getRequest() == null);

            if (GrantType.DENY == rule.getAccess()) {
                if (isFullBlock) {
                    LOGGER.debug("Adding full block " + rule);
                    activeDenies.add(rule);
                } else {
                    LOGGER.debug("Skipping partial block " + rule);
                }
            } else if (GrantType.ALLOW == rule.getAccess()) {
                if (!isCompletelyBlocked(rule, activeDenies)) {
                    survivingAllows.add(rule);
                    LOGGER.debug("Adding unblocked allow " + rule);
                } else {
                    LOGGER.debug("Skipping blocked allow " + rule);
                }
            }
        }
        LOGGER.debug("Compacting grants ALLOW --> " + survivingAllows);
        LOGGER.debug("Compacting grants DENY  --> " + activeDenies);

        return buildPerms(survivingAllows, activeDenies);
    }

    private static boolean isCompletelyBlocked(Rule allowRule, List<Rule> denies) {
        for (Rule deny : denies) {
            boolean wsMatches =
                    (deny.getWorkspace() == null || deny.getWorkspace().equals(allowRule.getWorkspace()));
            boolean layerMatches = (deny.getLayer() == null || deny.getLayer().equals(allowRule.getLayer()));

            if (wsMatches && layerMatches) {
                return true; // the DENY completely covers this ALLOW
            }
        }
        return false;
    }

    private static PermsResultInternal buildPerms(List<Rule> finalAllowedRules, List<Rule> activeDenies) {
        Map<String, Set<String>> resources = new HashMap<>();
        // workspace -> priority of the highest-priority (lowest-numbered) full-workspace ALLOW, so a duplicate
        // lower-priority grant for the same workspace can't add holes that the winning grant doesn't have
        Map<String, Long> fullWorkspaceGrantPriority = new HashMap<>();
        for (Rule rule : finalAllowedRules) {
            if (rule.getWorkspace() != null && rule.getLayer() == null) {
                fullWorkspaceGrantPriority.merge(rule.getWorkspace(), rule.getPriority(), Math::min);
            }
        }

        for (Rule rule : finalAllowedRules) {
            List<Rule> outrankingDenies = higherPriorityDenies(activeDenies, rule);

            if (rule.getWorkspace() == null && rule.getLayer() == null) {
                // global grant: record "*:*" plus any higher-priority DENY as an exclusion, and stop - it covers
                // every other ALLOW
                Set<String> globalLayers = resources.computeIfAbsent("*", k -> new HashSet<>());
                globalLayers.add("*");

                for (Rule deny : outrankingDenies) {
                    String dWs = deny.getWorkspace() == null ? "*" : deny.getWorkspace();
                    String dLy = deny.getLayer() == null ? "*" : deny.getLayer();

                    if (!"*".equals(dWs)) {
                        resources.computeIfAbsent(dWs, k -> new HashSet<>()).add("!" + dLy);
                    } else {
                        globalLayers.add("!" + dLy);
                    }
                }
                return new PermsResultInternal(compactGrants(finalAllowedRules, activeDenies), resources);
            }

            String ws = rule.getWorkspace() == null ? "*" : rule.getWorkspace();
            String ly = rule.getLayer() == null ? "*" : rule.getLayer();

            Set<String> layers = resources.computeIfAbsent(ws, k -> new HashSet<>());
            layers.add(ly);

            if ("*".equals(ly)) {
                // workspace-wide grant: list any DENY holes outranking the winning (highest-priority) grant for
                // this workspace - not just this specific rule, in case of a duplicate lower-priority grant
                long grantPriority = fullWorkspaceGrantPriority.get(rule.getWorkspace());
                for (Rule deny : activeDenies) {
                    if (deny.getPriority() < grantPriority
                            && ws.equals(deny.getWorkspace())
                            && deny.getLayer() != null) {
                        layers.add("!" + deny.getLayer());
                    }
                }
            }
        }

        return new PermsResultInternal(compactGrants(finalAllowedRules, activeDenies), resources);
    }

    /**
     * Denies only take effect on rules evaluated after them (i.e. at a lower priority - a higher priority number). A
     * DENY that comes after {@code allow} in evaluation order can never affect it: a matching request would already
     * have been decided by {@code allow} first.
     */
    private static List<Rule> higherPriorityDenies(List<Rule> denies, Rule allow) {
        List<Rule> result = new ArrayList<>();
        for (Rule deny : denies) {
            if (deny.getPriority() < allow.getPriority()) {
                result.add(deny);
            }
        }
        return result;
    }

    private static Filter compactGrants(List<Rule> finalAllowedRules, List<Rule> activeDenies) {
        Map<String, Set<String>> workspaceToLayers = new HashMap<>();
        // workspace -> priority of the highest-priority (lowest-numbered) ALLOW granting it fully, so only DENYs
        // outranking THAT rule can punch a hole in it
        Map<String, Long> fullWorkspaceGrants = new HashMap<>();
        Set<String> crossWorkspaceLayers = new HashSet<>(); // ws=null, layer=X

        for (Rule rule : finalAllowedRules) {
            String ws = rule.getWorkspace();
            String layer = rule.getLayer();

            if (ws == null && layer == null) {
                Filter globalBase = Filter.INCLUDE;
                List<Rule> outrankingDenies = higherPriorityDenies(activeDenies, rule);
                if (!outrankingDenies.isEmpty()) {
                    List<Filter> holes = new ArrayList<>();
                    for (Rule deny : outrankingDenies) {
                        holes.add(createRuleFilter(deny));
                    }
                    globalBase = ff.and(globalBase, ff.not(ff.or(holes)));
                }
                // a global grant is the most permissive possible outcome - it covers all other grants
                return (Filter) globalBase.accept(new SimplifyingFilterVisitor(), null);

            } else if (ws == null && layer != null) {
                crossWorkspaceLayers.add(layer);
            } else if (ws != null && layer == null) {
                fullWorkspaceGrants.merge(ws, rule.getPriority(), Math::min);
            } else {
                workspaceToLayers.computeIfAbsent(ws, k -> new HashSet<>()).add(layer);
            }
        }

        List<Filter> groupedFilters = new ArrayList<>();

        for (String layer : crossWorkspaceLayers) {
            groupedFilters.add(ff.equals(ff.property("layer"), ff.literal(layer)));
        }

        for (Map.Entry<String, Long> grant : fullWorkspaceGrants.entrySet()) {
            String ws = grant.getKey();
            long grantPriority = grant.getValue();
            Filter wsFilter = ff.equals(ff.property("workspace"), ff.literal(ws));

            List<Filter> holes = new ArrayList<>();
            for (Rule deny : activeDenies) {
                if (deny.getPriority() < grantPriority && ws.equals(deny.getWorkspace()) && deny.getLayer() != null) {
                    holes.add(ff.equals(ff.property("layer"), ff.literal(deny.getLayer())));
                }
            }

            if (holes.isEmpty()) {
                groupedFilters.add(wsFilter);
            } else {
                Filter combinedHoles = holes.size() == 1 ? holes.get(0) : ff.or(holes);
                groupedFilters.add(ff.and(wsFilter, ff.not(combinedHoles)));
            }
        }

        for (Map.Entry<String, Set<String>> entry : workspaceToLayers.entrySet()) {
            String ws = entry.getKey();
            if (fullWorkspaceGrants.containsKey(ws)) continue;

            Set<String> layers = entry.getValue();
            Filter wsFilter = ff.equals(ff.property("workspace"), ff.literal(ws));

            Filter layersFilter;
            if (layers.size() == 1) {
                layersFilter = ff.equals(
                        ff.property("layer"), ff.literal(layers.iterator().next()));
            } else {
                List<Filter> layerEquals = new ArrayList<>();
                for (String layer : layers) {
                    // already covered by a cross-workspace grant on this same layer name
                    if (!crossWorkspaceLayers.contains(layer)) {
                        layerEquals.add(ff.equals(ff.property("layer"), ff.literal(layer)));
                    }
                }
                if (layerEquals.isEmpty()) continue;

                layersFilter = layerEquals.size() == 1 ? layerEquals.get(0) : ff.or(layerEquals);
            }

            groupedFilters.add(ff.and(wsFilter, layersFilter));
        }

        if (groupedFilters.isEmpty()) return Filter.EXCLUDE;
        if (groupedFilters.size() == 1) return groupedFilters.get(0);
        return ff.or(groupedFilters);
    }

    /** Converts a single Rule into a GeoTools Filter - used for the global-grant holes and DENY exclusions above. */
    private static Filter createRuleFilter(Rule rule) {
        List<Filter> conditions = new ArrayList<>();

        if (rule.getWorkspace() != null) {
            conditions.add(ff.equals(ff.property("workspace"), ff.literal(rule.getWorkspace())));
        }
        if (rule.getLayer() != null) {
            conditions.add(ff.equals(ff.property("layer"), ff.literal(rule.getLayer())));
        }

        if (conditions.isEmpty()) {
            return Filter.INCLUDE;
        }
        return conditions.size() == 1 ? conditions.get(0) : ff.and(conditions);
    }
}

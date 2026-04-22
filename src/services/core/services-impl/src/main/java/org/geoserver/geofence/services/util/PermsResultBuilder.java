
package org.geoserver.geofence.services.util;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.api.filter.Filter;
import org.geotools.api.filter.FilterFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.core.model.enums.GrantType;


public class PermsResultBuilder {

    private final static Logger LOGGER = LogManager.getLogger(PermsResultBuilder.class);        
    private static final FilterFactory ff = CommonFactoryFinder.getFilterFactory();

    public PermsResultInternal computePerms(List<Rule> sortedRules) {
        
        List<Rule> survivingAllows = new ArrayList<>();
        List<Rule> activeDenies = new ArrayList<>();

        for (Rule rule : sortedRules) {
            boolean isFullBlock = (rule.getService() == null && rule.getRequest() == null);

            if (GrantType.DENY == rule.getAccess()) {
                // 1. Remember this DENY. It will punch holes in any lower-priority ALLOWs.
                if (isFullBlock) {
                    LOGGER.debug("Adding full block " + rule);
                    activeDenies.add(rule);
                } else {
                    LOGGER.debug("Skipping partial block " + rule);
                }

            } else if (GrantType.ALLOW == rule.getAccess()) {
                // 2. We found an ALLOW. Let's build its base filter.
                if (!isCompletelyBlocked(rule, activeDenies)) {
                    // It survived! Add it to our valid list.
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

    private boolean isCompletelyBlocked(Rule allowRule, List<Rule> denies) {
        for (Rule deny : denies) {
            boolean wsMatches = (deny.getWorkspace() == null || deny.getWorkspace().equals(allowRule.getWorkspace()));
            boolean layerMatches = (deny.getLayer() == null || deny.getLayer().equals(allowRule.getLayer()));
            
            if (wsMatches && layerMatches) {
                return true; // The DENY completely covers this ALLOW
            }
        }
        return false;
    }   
      
    private PermsResultInternal buildPerms(List<Rule> finalAllowedRules, List<Rule> activeDenies) {
        Map<String, Set<String>> resources = new HashMap<>();
        for (Rule rule : finalAllowedRules) {
            if(rule.getWorkspace() == null && rule.getLayer() == null) {
                resources.clear();
                resources.put("*", Set.of("*"));
                return new PermsResultInternal(Filter.INCLUDE, resources);
            }
            
            String ws = rule.getWorkspace() == null ? "*" : rule.getWorkspace();
            String ly = rule.getLayer() == null ? "*" : rule.getLayer();
            
            Set<String> layers = resources.computeIfAbsent(ws, k -> new HashSet<>());
            layers.add(ly);
                       
            // If we just added a workspace-wide grant ("*"), 
            // we must check if there are any high-priority DENY holes to list
            if ("*".equals(ly)) {
                for (Rule deny : activeDenies) {
                    // If the deny is for this workspace and specifies a layer
                    if (ws.equals(deny.getWorkspace()) && deny.getLayer() != null) {
                        layers.add("!" + deny.getLayer()); // Prefix with ! to indicate exclusion
                    }
                }
            }
        }  
        
        return new PermsResultInternal(compactGrants(finalAllowedRules, activeDenies),resources);
    }
    
    private Filter compactGrants(List<Rule> finalAllowedRules, List<Rule> activeDenies) {
        Map<String, Set<String>> workspaceToLayers = new HashMap<>();
        Set<String> fullWorkspaceGrants = new HashSet<>();
        Set<String> crossWorkspaceLayers = new HashSet<>(); // Handles ws=null, layer=X

        for (Rule rule : finalAllowedRules) {
            String ws = rule.getWorkspace();
            String layer = rule.getLayer();

            if (ws == null && layer == null) {
                // global wildcard (e.g., a rule granting global service access)
                return Filter.INCLUDE; 
            } else if (ws == null && layer != null) {
                // Layer applies to ANY workspace
                crossWorkspaceLayers.add(layer);
            } else if (ws != null && layer == null) {
                // Entire workspace allowed
                fullWorkspaceGrants.add(ws);
            } else {
                // Specific workspace and layer
                workspaceToLayers.computeIfAbsent(ws, k -> new HashSet<>()).add(layer);
            }           
        }

        List<Filter> groupedFilters = new ArrayList<>();

        // 1. Build filters for cross-workspace layers (e.g., layer = 'A')
        for (String layer : crossWorkspaceLayers) {
            groupedFilters.add(ff.equals(ff.property("layer"), ff.literal(layer)));
        }

        // 2. Build filters for fully allowed workspaces (e.g., workspace = 'SITGEO')
//        for (String ws : fullWorkspaceGrants) {
//            groupedFilters.add(ff.equals(ff.property("workspace"), ff.literal(ws)));
//        }
        for (String ws : fullWorkspaceGrants) {
            Filter wsFilter = ff.equals(ff.property("workspace"), ff.literal(ws));

            // FIND HOLES: Are there any DENYs that apply specifically to this workspace?
            List<Filter> holes = new ArrayList<>();
            for (Rule deny : activeDenies) {
                // If the deny is for this workspace and specifies a layer
                if (ws.equals(deny.getWorkspace()) && deny.getLayer() != null) {
                    holes.add(ff.equals(ff.property("layer"), ff.literal(deny.getLayer())));
                }
            }

            if (holes.isEmpty()) {
                groupedFilters.add(wsFilter);
            } else {
                // Punch the holes: workspace = 'test' AND NOT (layer = 'questo_no' OR layer = 'other_no')
                Filter combinedHoles = holes.size() == 1 ? holes.get(0) : ff.or(holes);
                groupedFilters.add(ff.and(wsFilter, ff.not(combinedHoles)));
            }
        }

        // 3. Build factored filters for workspaces with specific layers
        for (Map.Entry<String, Set<String>> entry : workspaceToLayers.entrySet()) {
            String ws = entry.getKey();
            if (fullWorkspaceGrants.contains(ws)) continue;

            Set<String> layers = entry.getValue();
            Filter wsFilter = ff.equals(ff.property("workspace"), ff.literal(ws));
            
            Filter layersFilter;
            if (layers.size() == 1) {
                layersFilter = ff.equals(ff.property("layer"), ff.literal(layers.iterator().next()));
            } else {
                List<Filter> layerEquals = new ArrayList<>();
                for (String layer : layers) {
                    // Check if this layer was already granted globally across all workspaces
                    if(!crossWorkspaceLayers.contains(layer)) {
                        layerEquals.add(ff.equals(ff.property("layer"), ff.literal(layer)));
                    }
                }
                if (layerEquals.isEmpty()) continue; // All layers were covered by crossWorkspaceLayers
                
                layersFilter = layerEquals.size() == 1 ? layerEquals.get(0) : ff.or(layerEquals);
            }

            groupedFilters.add(ff.and(wsFilter, layersFilter));
        }

        if (groupedFilters.isEmpty()) return Filter.EXCLUDE;
        if (groupedFilters.size() == 1) return groupedFilters.get(0);
        return ff.or(groupedFilters);
    }   
}
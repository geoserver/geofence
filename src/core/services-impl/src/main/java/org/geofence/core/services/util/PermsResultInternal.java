/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.services.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.services.dto.PermsResult;
import org.geotools.api.filter.Filter;
import org.geotools.api.filter.FilterFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.filter.visitor.SimplifyingFilterVisitor;

/**
 * Accumulator used while merging per-role {@link PermsResult}s in {@code RuleReaderServiceImpl.getPermissionFilter}:
 * unlike the public DTO, it keeps the filter as a live GeoTools {@link Filter} (rather than a CQL string) and the
 * resources as a {@code Map<workspace, Set<layer>>} so {@link #or} can merge/simplify without round-tripping through
 * CQL parsing.
 *
 * @author etj
 */
public class PermsResultInternal {

    private static final Logger LOGGER = LogManager.getLogger(PermsResultInternal.class);
    private static final FilterFactory ff = CommonFactoryFinder.getFilterFactory();

    private Filter filter;
    private final Map<String, Set<String>> accessibleResources; // Map<Workspace, Set<Layers>>

    public PermsResultInternal() {
        filter = Filter.EXCLUDE;
        accessibleResources = new HashMap<>();
    }

    public PermsResultInternal(Filter filter, Map<String, Set<String>> accessibleResources) {
        this.filter = filter;
        this.accessibleResources = accessibleResources;
    }

    public Filter getFilter() {
        return filter;
    }

    public String getCqlFilter() {
        return ECQL.toCQL(filter);
    }

    public Map<String, Set<String>> getAccessibleResources() {
        return accessibleResources;
    }

    /** Merges another result into this one (union/superset). */
    public void or(PermsResultInternal other) {
        if (other == null) return;

        if (this.filter.equals(Filter.INCLUDE) || other.filter.equals(Filter.INCLUDE)) {
            this.filter = Filter.INCLUDE;
        } else if (this.filter.equals(Filter.EXCLUDE)) {
            this.filter = other.filter;
        } else if (!other.filter.equals(Filter.EXCLUDE)) {
            Filter combined = ff.or(this.filter, other.filter);
            this.filter = (Filter) combined.accept(new SimplifyingFilterVisitor(), null);
        }

        other.accessibleResources.forEach((ws, layers) -> this.accessibleResources
                .computeIfAbsent(ws, k -> new HashSet<>())
                .addAll(layers));

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Merged PermsResultInternal -> " + this.getCqlFilter());
        }

        cleanUpRedundancies();
    }

    /**
     * A workspace granted "*" (every layer) doesn't need any specific layer names or exclusion markers left over from a
     * role that only got a partial grant, once another role's "*" covers it.
     */
    private void cleanUpRedundancies() {
        for (Set<String> layers : accessibleResources.values()) {
            if (layers.contains("*")) {
                Set<String> exclusions =
                        layers.stream().filter(l -> l.startsWith("!")).collect(Collectors.toSet());

                for (String ex : exclusions) {
                    String deniedLayer = ex.substring(1);
                    // another role granted the layer this exclusion was punching a hole in - heal it
                    if (layers.contains(deniedLayer)) {
                        layers.remove(ex);
                    }
                }

                layers.removeIf(l -> !l.equals("*") && !l.startsWith("!"));
            }
        }
    }

    public PermsResult toPermsResult() {
        return new PermsResult(getCqlFilter(), accessibleResources);
    }
}

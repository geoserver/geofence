package org.geoserver.geofence.services.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.geoserver.geofence.services.dto.PermsResult;
import org.geotools.api.filter.Filter;
import org.geotools.api.filter.FilterFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.filter.visitor.SimplifyingFilterVisitor;

/**
 *
 * @author etj
 */
public class PermsResultInternal {

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

    public Filter getFilter() { return filter; }
    public String getCqlFilter() { return ECQL.toCQL(filter); }
    public Map<String, Set<String>> getAccessibleResources() { return accessibleResources; }
    
   /**
    * Merges another result into this one (Union/Superset).
    */
    public void or(PermsResultInternal other) {
        if (other == null) return;

        // 1. Merge Filters
        if (this.filter.equals(Filter.INCLUDE) || other.filter.equals(Filter.INCLUDE)) {
            this.filter = Filter.INCLUDE;
        } else if (this.filter.equals(Filter.EXCLUDE)) {
            this.filter = other.filter;
        } else if (!other.filter.equals(Filter.EXCLUDE)) {
            // OR the filters and simplify
            Filter combined = ff.or(this.filter, other.filter);
            this.filter = (Filter) combined.accept(new SimplifyingFilterVisitor(), null);
        }

        // 2. Merge Map of Resources
        other.accessibleResources.forEach((ws, layers) -> {
            this.accessibleResources.computeIfAbsent(ws, k -> new HashSet<>())
                                     .addAll(layers);
        });
        
        // 3. Optimization: Clean up redundant specific layers if a workspace has "*"
        cleanUpRedundancies();
    }

    private void cleanUpRedundancies() {
        
//        for (Set<String> layers : accessibleResources.values()) {
//            if (layers.size() > 1 && layers.contains("*")) {
//                layers.clear();
//                layers.add("*");
//            }
//        }
        for (Set<String> layers : accessibleResources.values()) {
           if (layers.contains("*")) {
               // 1. Identify all exclusions (starting with !)
               Set<String> exclusions = layers.stream()
                       .filter(l -> l.startsWith("!"))
                       .collect(Collectors.toSet());

               for (String ex : exclusions) {
                   String deniedLayer = ex.substring(1);
                   // 2. If the set ALSO contains the raw layer name, 
                   // it means another group granted it. Remove the exclusion.
                   if (layers.contains(deniedLayer)) {
                       layers.remove(ex);
                   }
               }

               // 3. Remove specific layer names that are redundant because of "*"
               // But KEEP exclusions that weren't "healed"
               layers.removeIf(l -> !l.equals("*") && !l.startsWith("!"));
           }
        }
    } 
 
    public PermsResult toPermsResult() {
        return new PermsResult(getCqlFilter(), accessibleResources);
    }
    
}
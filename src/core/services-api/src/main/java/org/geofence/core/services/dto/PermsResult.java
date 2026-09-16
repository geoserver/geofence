/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.services.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A closed representation of what a caller may access, as computed by
 * {@link org.geofence.core.services.RuleReaderService#getPermissionFilter}: a CQL filter (over the
 * {@code workspace}/{@code layer} properties) plus the flattened set of accessible resources, as
 * {@code "workspace:layer"} entries (either side may be {@code "*"} for a wildcard grant).
 *
 * @author etj
 */
public class PermsResult implements Serializable {

    private String cqlFilter;

    private SortedSet<String> accessibleResources = new TreeSet<>();

    public PermsResult() {}

    public PermsResult(String cqlFilter, Map<String, Set<String>> resourcesMap) {
        this.cqlFilter = cqlFilter;
        addAccessibleResources(resourcesMap);
    }

    public String getCqlFilter() {
        return cqlFilter;
    }

    public void setCqlFilter(String cqlFilter) {
        this.cqlFilter = cqlFilter;
    }

    public SortedSet<String> getAccessibleResources() {
        return accessibleResources;
    }

    public void setAccessibleResources(SortedSet<String> accessibleResources) {
        this.accessibleResources = accessibleResources;
    }

    public void addAccessibleResources(Map<String, Set<String>> resourcesMap) {
        if (resourcesMap == null || resourcesMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Set<String>> entry : resourcesMap.entrySet()) {
            String ws = entry.getKey();
            for (String layer : entry.getValue()) {
                this.accessibleResources.add(ws + ":" + layer);
            }
        }
    }

    @Override
    public String toString() {
        return "PermsResult[cql:" + cqlFilter + " resources:" + accessibleResources + "]";
    }
}

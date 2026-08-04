/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.SortedSet;

/**
 * REST representation of a {@code PermsResult} - the closed representation (CQL filter + accessible-resources set)
 * returned by {@code RuleReaderService.getPermissionFilter}.
 *
 * @author etj
 */
@XmlRootElement(name = "PermsResult")
public class RESTPermsResult {

    private String cqlFilter;
    private SortedSet<String> accessibleResources;

    public String getCqlFilter() {
        return cqlFilter;
    }

    public void setCqlFilter(String cqlFilter) {
        this.cqlFilter = cqlFilter;
    }

    @XmlElementWrapper(name = "accessibleResources")
    @XmlElement(name = "resource")
    public SortedSet<String> getAccessibleResources() {
        return accessibleResources;
    }

    public void setAccessibleResources(SortedSet<String> accessibleResources) {
        this.accessibleResources = accessibleResources;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[cql:" + cqlFilter + " resources:" + accessibleResources + "]";
    }
}

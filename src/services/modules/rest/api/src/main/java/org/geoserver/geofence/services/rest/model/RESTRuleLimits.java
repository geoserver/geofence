/* (c) 2022 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.services.rest.model;

import org.geoserver.geofence.core.model.enums.CatalogMode;
import org.geoserver.geofence.core.model.enums.SpatialFilterType;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Limits")
@XmlType(propOrder={"restrictedAreaWkt","spatialFilterType","catalogMode"})
public class RESTRuleLimits {

    private String restrictedAreaWkt;
    private SpatialFilterType spatialFilterType;
    private CatalogMode catalogMode;

    public String getRestrictedAreaWkt() {
        return restrictedAreaWkt;
    }

    public void setRestrictedAreaWkt(String restrictedAreaWkt) {
        this.restrictedAreaWkt = restrictedAreaWkt;
    }

    public SpatialFilterType getSpatialFilterType() {
        return spatialFilterType;
    }

    public void setSpatialFilterType(SpatialFilterType spatialFilterType) {
        this.spatialFilterType = spatialFilterType;
    }

    public CatalogMode getCatalogMode() {
        return catalogMode;
    }

    public void setCatalogMode(CatalogMode catalogMode) {
        this.catalogMode = catalogMode;
    }

    @Override
    public String toString() {
        return "RESTRuleLimits{" +
                "restrictedAreaWkt='" + restrictedAreaWkt + '\'' +
                ", spatialFilterType=" + spatialFilterType +
                ", catalogMode=" + catalogMode +
                '}';
    }
}

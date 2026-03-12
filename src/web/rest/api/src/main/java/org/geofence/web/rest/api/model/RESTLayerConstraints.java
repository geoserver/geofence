/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.geofence.web.rest.api.model.enums.RESTLayerType;
import org.geofence.web.rest.api.model.enums.RESTSpatialFilterType;

/** @author Etj (etj at geo-solutions.it) */
@XmlRootElement(name = "LayerConstraints")
@XmlType(
        propOrder = {
            "type",
            "defaultStyle",
            "cqlFilterRead",
            "cqlFilterWrite",
            "restrictedAreaWkt",
            "spatialFilterType",
            "allowedStyles",
            "attributes"
        })
public class RESTLayerConstraints {

    private RESTLayerType type;
    private String defaultStyle;
    private String cqlFilterRead;
    private String cqlFilterWrite;
    private String restrictedAreaWkt;
    private RESTSpatialFilterType spatialFilterType;
    private Set<String> allowedStyles;
    private Set<RESTLayerAttribute> attributes;

    @XmlElementWrapper(name = "allowedStyles")
    @XmlElement(name = "style")
    public Set<String> getAllowedStyles() {
        return allowedStyles;
    }

    public void setAllowedStyles(Set<String> allowedStyles) {
        this.allowedStyles = allowedStyles;
    }

    @XmlElementWrapper(name = "attributes")
    @XmlElement(name = "attribute")
    public Set<RESTLayerAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Collection<RESTLayerAttribute> attributes) {
        this.attributes = new HashSet<>(attributes);
    }

    public String getCqlFilterRead() {
        return cqlFilterRead;
    }

    public void setCqlFilterRead(String cqlFilterRead) {
        this.cqlFilterRead = cqlFilterRead;
    }

    public String getCqlFilterWrite() {
        return cqlFilterWrite;
    }

    public void setCqlFilterWrite(String cqlFilterWrite) {
        this.cqlFilterWrite = cqlFilterWrite;
    }

    public String getDefaultStyle() {
        return defaultStyle;
    }

    public void setDefaultStyle(String defaultStyle) {
        this.defaultStyle = defaultStyle;
    }

    public String getRestrictedAreaWkt() {
        return restrictedAreaWkt;
    }

    public void setRestrictedAreaWkt(String restrictedAreaWkt) {
        this.restrictedAreaWkt = restrictedAreaWkt;
    }

    public RESTLayerType getType() {
        return type;
    }

    public void setType(RESTLayerType type) {
        this.type = type;
    }

    public RESTSpatialFilterType getSpatialFilterType() {
        return spatialFilterType;
    }

    public void setSpatialFilterType(RESTSpatialFilterType spatialFilterType) {
        this.spatialFilterType = spatialFilterType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append('[');
        if (type != null) sb.append("type:").append(type);
        if (defaultStyle != null) sb.append(" defStyle:").append(defaultStyle);
        if (cqlFilterRead != null) sb.append(" cqlR:").append(cqlFilterRead);
        if (cqlFilterWrite != null) sb.append(" cqlW:").append(cqlFilterWrite);
        if (restrictedAreaWkt != null) sb.append(" wkt:").append(restrictedAreaWkt);
        if (spatialFilterType != null) sb.append(" spatialFilterType").append(spatialFilterType);
        if (allowedStyles != null)
            sb.append(" styles(").append(allowedStyles.size()).append("):").append(allowedStyles);
        if (attributes != null) sb.append(" attrs:").append(attributes);
        sb.append(']');
        return sb.toString();
    }
}

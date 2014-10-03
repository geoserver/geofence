/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;

import org.geoserver.geofence.core.model.LayerAttribute;
import org.geoserver.geofence.core.model.enums.LayerType;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Etj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "LayerConstraints")
@XmlType(propOrder = {"type", "defaultStyle", "cqlFilterRead", "cqlFilterWrite", "restrictedAreaWkt",
    "allowedStyles", "attributes"})
public class RESTLayerConstraints {

    private LayerType type;
    private String defaultStyle;
    private String cqlFilterRead;
    private String cqlFilterWrite;
    private String restrictedAreaWkt;
    private Set<String> allowedStyles;
    private Set<LayerAttribute> attributes;

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
    public Set<LayerAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<LayerAttribute> attributes) {
        this.attributes = attributes;
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

    public LayerType getType() {
        return type;
    }

    public void setType(LayerType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append('[');
        if(type != null)
            sb.append("type:").append(type);
        if(defaultStyle != null)
            sb.append(" defStyle:").append(defaultStyle);
        if(cqlFilterRead != null)
            sb.append(" cqlR:").append(cqlFilterRead);
        if(cqlFilterWrite != null)
            sb.append(" cqlW:").append(cqlFilterWrite);
        if(restrictedAreaWkt != null)
            sb.append(" wkt:").append(restrictedAreaWkt);
        if(allowedStyles != null)
            sb.append(" styles(").append(allowedStyles.size()).append("):").append(allowedStyles);
        if(attributes != null)
            sb.append(" attrs:").append(attributes);
        sb.append(']');
        return sb.toString();
    }
}

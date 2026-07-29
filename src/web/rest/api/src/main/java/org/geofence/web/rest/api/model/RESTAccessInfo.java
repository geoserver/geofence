/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.Set;
import org.geofence.web.rest.api.model.enums.RESTCatalogMode;
import org.geofence.web.rest.api.model.enums.RESTGrantType;

/**
 * REST representation of a runtime {@code AccessInfo} evaluation result.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "AccessInfo")
@XmlType(
        propOrder = {
            "grant",
            "adminRights",
            "areaWkt",
            "clipAreaWkt",
            "catalogMode",
            "defaultStyle",
            "cqlFilterRead",
            "cqlFilterWrite",
            "allowedStyles",
            "attributes"
        })
public class RESTAccessInfo {

    private RESTGrantType grant;
    private boolean adminRights;
    private String areaWkt;
    private String clipAreaWkt;
    private RESTCatalogMode catalogMode;
    private String defaultStyle;
    private String cqlFilterRead;
    private String cqlFilterWrite;
    private Set<String> allowedStyles;
    private Set<RESTLayerAttribute> attributes;

    public RESTGrantType getGrant() {
        return grant;
    }

    public void setGrant(RESTGrantType grant) {
        this.grant = grant;
    }

    public boolean isAdminRights() {
        return adminRights;
    }

    public void setAdminRights(boolean adminRights) {
        this.adminRights = adminRights;
    }

    public String getAreaWkt() {
        return areaWkt;
    }

    public void setAreaWkt(String areaWkt) {
        this.areaWkt = areaWkt;
    }

    public String getClipAreaWkt() {
        return clipAreaWkt;
    }

    public void setClipAreaWkt(String clipAreaWkt) {
        this.clipAreaWkt = clipAreaWkt;
    }

    public RESTCatalogMode getCatalogMode() {
        return catalogMode;
    }

    public void setCatalogMode(RESTCatalogMode catalogMode) {
        this.catalogMode = catalogMode;
    }

    public String getDefaultStyle() {
        return defaultStyle;
    }

    public void setDefaultStyle(String defaultStyle) {
        this.defaultStyle = defaultStyle;
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

    public void setAttributes(Set<RESTLayerAttribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        StringBuilder sb =
                new StringBuilder(getClass().getSimpleName()).append("[grant:").append(grant);
        sb.append(" admin:").append(adminRights);
        if (catalogMode != null) sb.append(" cmode:").append(catalogMode);
        if (defaultStyle != null) sb.append(" defSty:").append(defaultStyle);
        if (cqlFilterRead != null) sb.append(" cqlR:").append(cqlFilterRead);
        if (cqlFilterWrite != null) sb.append(" cqlW:").append(cqlFilterWrite);
        if (allowedStyles != null && !allowedStyles.isEmpty())
            sb.append(" allSty:").append(allowedStyles);
        if (attributes != null && !attributes.isEmpty()) sb.append(" attr:").append(attributes);
        sb.append(']');
        return sb.toString();
    }
}

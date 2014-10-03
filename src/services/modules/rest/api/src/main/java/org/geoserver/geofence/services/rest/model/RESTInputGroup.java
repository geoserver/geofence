/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A compact representation of UserGroup holding only the insertable/updatadable fields
 *
 * @author Etj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "userGroup")
@XmlType(name="Group", propOrder = {"extId", "name"})
public class RESTInputGroup extends AbstractRESTPayload {

    private static final long serialVersionUID = -8410646966443187827L;
    private String name;
    private String extId;
    private Boolean enabled;

    public RESTInputGroup() {
    }

    @XmlAttribute(name = "enabled")
    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "["
                + (extId!=null? " extid=" + extId : "")
                + (name != null? " name=" + name : "")
                + (enabled != null? (enabled? " enabled" : " disabled") : "")
                + ']';
    }
}

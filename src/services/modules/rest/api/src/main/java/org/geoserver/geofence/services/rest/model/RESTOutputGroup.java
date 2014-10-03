/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A compact representation of UserGroup holding only the insertable/updatadable fields
 *
 * @author Etj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "UserGroup")
public class RESTOutputGroup implements Serializable {

    private Long id;
    private String name;
    private String extId;
    private Boolean enabled;

    public RESTOutputGroup() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEnabled() {
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
                + "[id:" + id
                + (extId!=null? " extid=" + extId : "")
                + " name=" + name
                + (enabled? "" : "disabled")
                + ']';
    }
}

/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.dto;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geoserver.geofence.core.model.UserGroup;

/**
 * A compact representation of UserGroup useful in lists.
 *
 * @author Etj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "Group")
@XmlType(propOrder = {"id", "extId", "name"})
public class ShortGroup implements Serializable {

    private static final long serialVersionUID = -43187828L;
    private Long id;
    private String name;
    private String extId;
    private Boolean enabled;

    public ShortGroup() {}

    public ShortGroup(UserGroup group) {
        this.id = group.getId();
        this.name = group.getName();
        this.enabled = group.getEnabled();
        this.extId = group.getExtId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "[id="
                + id
                + " name="
                + name
                + " enabled="
                + enabled
                + ']';
    }
}

/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model.config;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A grouping for {@link GSUser}s.
 *
 * <p>
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "UserGroup")
@XmlType(propOrder = {"id", "extId", "name", "dateCreation"})
public class RESTUserGroup implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8457036587275531556L;

    private Long id;
    private String extId;
    private String name;
    private LocalDateTime dateCreation;
    private boolean enabled;

    public RESTUserGroup() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation == null ? null : dateCreation.withNano(0);
    }

    @XmlAttribute
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.extId);
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.dateCreation);
        hash = 97 * hash + (this.enabled ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RESTUserGroup other = (RESTUserGroup) obj;
        if (this.enabled != other.enabled) {
            return false;
        }
        if (!Objects.equals(this.extId, other.extId)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.dateCreation, other.dateCreation)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());
        builder.append("[");
        if (id != null) builder.append("id:").append(id);
        if (extId != null) builder.append(" ext:").append(extId);
        if (name != null) builder.append(" name:").append(name);
        if (dateCreation != null) builder.append(" date:").append(dateCreation);
        if (!enabled) builder.append(" disabled");
        builder.append("]");
        return builder.toString();
    }
}

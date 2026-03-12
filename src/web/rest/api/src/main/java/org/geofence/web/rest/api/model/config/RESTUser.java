/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model.config;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.geofence.web.rest.api.model.xmladapters.FK2UserGroupSetAdapter;

/**
 * A User that can access GeoServer resources.
 *
 * <p>A GSUser is <B>not</B> in the domain of the users which can log into Geofence.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "GSUser")
@XmlType(propOrder = {"id", "extId", "name", "fullName", "password", "emailAddress", "dateCreation", "groups"})
public class RESTUser implements Serializable {

    private static final long serialVersionUID = 3808458156939088033L;

    private Long id;
    private String extId;
    private String name;
    private String fullName;
    private String password;
    private String emailAddress;
    private LocalDateTime dateCreation;
    private boolean enabled = true;
    private boolean admin = false;
    private Set<RESTUserGroup> userGroups = new HashSet<>();

    public RESTUser() {}

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

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setPassword(String password) {
        this.password = null; // todo decrypt and save pw
    }

    public String getPassword() {
        return null; // todo encrypt pw
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    @XmlAttribute
    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(Boolean isAdmin) {
        if (isAdmin != null) this.admin = isAdmin;
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
        if (enabled != null) this.enabled = enabled;
    }

    @XmlElement(name = "groups")
    @XmlJavaTypeAdapter(FK2UserGroupSetAdapter.class)
    public Set<RESTUserGroup> getGroups() {
        return userGroups;
    }

    public void setGroups(Set<RESTUserGroup> groups) {
        this.userGroups = groups;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.id);
        hash = 29 * hash + Objects.hashCode(this.extId);
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.fullName);
        hash = 29 * hash + Objects.hashCode(this.password);
        hash = 29 * hash + Objects.hashCode(this.emailAddress);
        hash = 29 * hash + Objects.hashCode(this.dateCreation);
        hash = 29 * hash + (this.enabled ? 1 : 0);
        hash = 29 * hash + (this.admin ? 1 : 0);
        hash = 29 * hash + Objects.hashCode(this.userGroups);
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
        final RESTUser other = (RESTUser) obj;
        if (this.enabled != other.enabled) {
            return false;
        }
        if (this.admin != other.admin) {
            return false;
        }
        if (!Objects.equals(this.extId, other.extId)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.fullName, other.fullName)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        if (!Objects.equals(this.emailAddress, other.emailAddress)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.dateCreation, other.dateCreation)) {
            return false;
        }
        if (!Objects.equals(this.userGroups, other.userGroups)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("User [");
        if (id != null) builder.append("id=").append(id).append(", ");
        if (extId != null) builder.append("extid=").append(extId).append(", ");
        if (name != null) builder.append("name=").append(name).append(", ");
        //        if (userGroups != null)
        //            builder.append("groups=").append(userGroups.size());
        builder.append("enabled=").append(enabled).append(", ");
        builder.append("admin=").append(admin).append(", ");
        if (dateCreation != null)
            builder.append("dateCreation=").append(dateCreation).append(", ");
        builder.append("]");
        return builder.toString();
    }
}

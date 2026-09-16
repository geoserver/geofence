/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model.config;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.time.LocalDateTime;

/** A GeoServer instance. */
@XmlRootElement(name = "GSInstance")
@XmlType(propOrder = {"id", "name", "description", "dateCreation", "baseURL", "username", "password"})
public class RESTInstance implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3804592064221812813L;

    private Long id;
    private String name;
    private String description;
    private LocalDateTime dateCreation;
    private String baseURL;
    private String username;
    private String password;

    public RESTInstance() {}

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation == null ? null : dateCreation.withNano(0);
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dateCreation == null) ? 0 : dateCreation.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((baseURL == null) ? 0 : baseURL.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RESTInstance)) {
            return false;
        }
        RESTInstance other = (RESTInstance) obj;
        if (dateCreation == null) {
            if (other.dateCreation != null) {
                return false;
            }
        } else if (!dateCreation.equals(other.dateCreation)) {
            return false;
        }
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (baseURL == null) {
            if (other.baseURL != null) {
                return false;
            }
        } else if (!baseURL.equals(other.baseURL)) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Instance [");
        if (dateCreation != null)
            builder.append("dateCreation=").append(dateCreation).append(", ");
        if (description != null)
            builder.append("description=").append(description).append(", ");
        if (baseURL != null) builder.append("baseURL=").append(baseURL).append(", ");
        builder.append("id=").append(id).append(", ");
        if (name != null) builder.append("name=").append(name);
        builder.append("]");
        return builder.toString();
    }
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A GeoServer instance.
 *
 * <p><B>TODO</B>: how does a GeoServer instance identify itself?
 */
@Entity(name = "GSInstance")
@Table(name = "gf_gsinstance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "gsinstance")
public class GSInstance implements Identifiable, Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3804592064221812813L;

    /** The id. */
    @Id
    @GeneratedValue
    @Column
    private Long id;

    /** The name. */
    @Column(nullable = false, updatable = true, unique = true)
    private String name;

    /** The description. */
    @Column(nullable = true, updatable = true)
    private String description;

    /** The date creation. */
    @Column(updatable = false)
    private LocalDateTime dateCreation;

    /** The host. */
    @Column(nullable = false, updatable = true)
    private String baseURL;

    @Column(nullable = false, updatable = true)
    private String username;

    @Column(nullable = false, updatable = true)
    private String password;

    /** Instantiates a new instance. */
    public GSInstance() {}

    /** @return the id */
    public Long getId() {
        return id;
    }

    /** @param id the id to set */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return the name */
    public String getName() {
        return name;
    }

    /** @param name the name to set */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the description */
    public String getDescription() {
        return description;
    }

    /** @param description the description to set */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return the dateCreation */
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    /** @param dateCreation the dateCreation to set */
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation == null ? null : dateCreation.withNano(0);
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
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

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof GSInstance)) {
            return false;
        }
        GSInstance other = (GSInstance) obj;
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
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

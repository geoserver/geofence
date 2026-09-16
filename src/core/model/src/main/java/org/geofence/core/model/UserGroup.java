/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.Objects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A grouping for {@link GSUser}s.
 *
 * <p>
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Entity(name = "UserGroup")
@Table(
        name = "gf_usergroup",
        uniqueConstraints = {
            @UniqueConstraint(
                    columnNames = {"extid"},
                    name = "gf_usergroup_extid_key"), // @InternalModel
            @UniqueConstraint(
                    columnNames = {"name"},
                    name = "gf_usergroup_name_key") // @InternalModel
        })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "usergroup")
public class UserGroup implements Identifiable {

    /** The id. */
    @Id
    @GeneratedValue
    @Column
    private Long id;

    /**
     * External Id. An ID used in an external systems. This field should simplify Geofence integration in complex
     * systems.
     */
    @Column(nullable = true, updatable = false)
    private String extId;

    /** The name. */
    @Column(nullable = false, updatable = true)
    private String name;

    /** The date creation. */
    @Column(updatable = false)
    private LocalDateTime dateCreation;

    /** The enabled. */
    @Column(nullable = false)
    private boolean enabled;

    /** Instantiates a new profile. */
    public UserGroup() {}

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(Long id) {
        this.id = id;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the date creation.
     *
     * @return the date creation
     */
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    /**
     * Sets the date creation.
     *
     * @param dateCreation the new date creation
     */
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation == null ? null : dateCreation.withNano(0);
    }

    /**
     * Gets the enabled.
     *
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled.
     *
     * @param enabled the new enabled
     */
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
        final UserGroup other = (UserGroup) obj;
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

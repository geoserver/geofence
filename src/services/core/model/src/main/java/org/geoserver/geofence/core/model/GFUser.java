/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A User that can access Geofence.
 *
 */
@Entity(name = "GFUser")
@Table(name = "gf_gfuser")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "GFUser")
@XmlRootElement(name = "GFUser")
@XmlType(propOrder={"id","extId","name","enabled","fullName","password","emailAddress","dateCreation"})
public class GFUser implements Identifiable, Serializable {


    /**
	 * 
	 */
	private static final long serialVersionUID = -5161617651332259455L;

	/** The id. */
    @Id
    @GeneratedValue
    @Column
    private Long id;

    /** External Id. An ID used in an external systems.
     * This field should simplify Geofence integration in complex systems.
     */
    @Column(nullable=true, updatable=false, unique=true)
    private String extId;

    /** The name. */
    @Column(nullable=false, unique=true)
    private String name;

    /** The user name. */
    @Column(nullable=true)
    private String fullName;

    /** The password. */
    @Column(nullable=true)
    private String password;

    /** The email address. */
    @Column(nullable=true)
    private String emailAddress;

    /** The date of creation of this user */
    @Column(updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    /** Is the GFUser Enabled or not in the system? */
    @Column(nullable=false)
    private boolean enabled = true;

    /**
     * Instantiates a new user.
     */
    public GFUser() {
    }

    protected GFUser(String name) {
        setName(name);
    }

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
     * @param id
     *            the new id
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
     * @param name
     *            the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * @return the emailAddress
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * @return the dateCreation
     */
    public Date getDateCreation() {
        return dateCreation;
    }

    /**
     * @param dateCreation
     *            the dateCreation to set
     */
    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    /**
     * @return the enabled
     */
    @XmlAttribute
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled
     *            the enabled to set
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dateCreation == null) ? 0 : dateCreation.hashCode());
        result = prime * result + (Boolean.valueOf(enabled).hashCode());
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
        if (!(obj instanceof GFUser)) {
            return false;
        }
        GFUser other = (GFUser) obj;
        if (dateCreation == null) {
            if (other.dateCreation != null) {
                return false;
            }
        } else if (!dateCreation.equals(other.dateCreation)) {
            return false;
        }
        if(enabled != other.enabled) {
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName()).append('[');
        builder.append("id=").append(id);
        if (name != null)
            builder.append(", ").append("name=").append(name);
        builder.append(", ").append("enabled=").append(enabled);
        if (dateCreation != null)
            builder.append(", ").append("created=").append(dateCreation);
        builder.append(']');
        return builder.toString();
    }

}

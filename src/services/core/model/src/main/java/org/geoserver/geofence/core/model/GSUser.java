/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import org.geoserver.geofence.core.model.adapter.FK2UserGroupSetAdapter;
import org.geoserver.geofence.core.model.util.PwEncoder;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

/**
 * A User that can access GeoServer resources.
 *
 * <P>A GSUser is <B>not</B> in the domain of the users which can log into Geofence.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Entity(name = "GSUser")
@Table(name = "gf_gsuser")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "GSUser")
@XmlRootElement(name = "GSUser")
@XmlType(propOrder={"id","extId","name","fullName","password","emailAddress","dateCreation","groups"})
public class GSUser implements Identifiable, Serializable {

    private static final long serialVersionUID = 7718458156939088033L;

    /** The id. */
    @Id
    @GeneratedValue
    @Column
    private Long id;

    /**
     * External Id. An ID used in an external systems.
     * This field should simplify Geofence integration in complex systems.
     */
    @Column(nullable=true, updatable=false, unique=true)
    private String extId;

    /** The name. */
    @Index(name = "idx_gsuser_name")
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

    /** Is the GSUser Enabled or not in the system? */
    @Column(nullable=false)
    private boolean enabled = true;

    /** Is the GSUser a GS admin? */
    @Column(nullable=false)
    private boolean admin = false;

    /** Groups to which the user is associated */
    @ManyToMany(fetch= FetchType.LAZY)
    @JoinTable( name = "gf_user_usergroups", joinColumns = @JoinColumn(name = "user_id"),  inverseJoinColumns=@JoinColumn(name = "group_id") )
    @Column(name = "u_id")
    @ForeignKey(name="fk_uug_user", inverseName="fk_uug_group")
    @Fetch(FetchMode.SUBSELECT) // without this, hibernate will duplicate results(!)
    private Set<UserGroup> userGroups = new HashSet<UserGroup>();

    /**
     * Instantiates a new user.
     */
    public GSUser() {
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
        this.password = password==null?null:PwEncoder.encode(password);
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password==null?null:PwEncoder.decode(password);
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

    @XmlAttribute
    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(Boolean isAdmin) {
        if(isAdmin != null)
            this.admin = isAdmin;
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
        if(enabled != null)
            this.enabled = enabled;
    }

    /**
     * @return the groups associated to the user
     */
    @XmlElement(name="groups")
//    @XmlJavaTypeAdapter(FK2UserGroupSetAdapter.class)
    public Set<UserGroup> getGroups() {
        return userGroups;
    }

    /**
     * @param profile the profile to set
     */
    public void setGroups(Set<UserGroup> groups) {
        this.userGroups = groups;
    }

    @Override
    public int hashCode()
    {
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
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GSUser other = (GSUser) obj;
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
        if(id != null)
            builder.append("id=").append(id).append(", ");
        if(extId != null)
            builder.append("extid=").append(extId).append(", ");
        if (name != null)
            builder.append("name=").append(name).append(", ");
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

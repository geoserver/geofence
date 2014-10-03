/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import org.geoserver.geofence.core.model.adapter.FK2UserGroupSetAdapter;
import org.geoserver.geofence.core.model.util.PwEncoder;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
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

//    /** The user. */
//    @ManyToOne(optional = false)
//    @ForeignKey(name="fk_user_profile")
//    private UserGroup profile;

//	@Type(type = "org.hibernatespatial.GeometryUserType")
//	@Column(name = "allowedArea")
//	private MultiPolygon allowedArea;

    /** Groups to which the user is associated */
    @ManyToMany(fetch= FetchType.LAZY)
    @JoinTable( name = "gf_user_usergroups", joinColumns = @JoinColumn(name = "user_id"),  inverseJoinColumns=@JoinColumn(name = "group_id") )
    @Column(name = "u_id")
    @ForeignKey(name="fk_uug_user", inverseName="fk_uug_group")
    @Fetch(FetchMode.SUBSELECT) // without this, hibernate will duplicate results(!)
    private Set<UserGroup> userGroups = new HashSet<UserGroup>();

//    @org.hibernate.annotations.CollectionOfElements
//    @JoinTable( name = "gf_user_usergroups",
//                joinColumns = @JoinColumn(name = "gsuser_id"))
////    @Column(name = "propvalue")
//    @ForeignKey(name="fk_uug_group", inverseName="fk_uug_user")
//    private Set<UserGroup> userGroups = new HashSet<UserGroup>();
//


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
    @XmlJavaTypeAdapter(FK2UserGroupSetAdapter.class)
    public Set<UserGroup> getGroups() {
        return userGroups;
    }

    /**
     * @param profile the profile to set
     */
    public void setGroups(Set<UserGroup> groups) {
        this.userGroups = groups;
    }

//    /**
//     * @deprecated LIMIT rules should be used
//     */
//    @Deprecated
//    @XmlJavaTypeAdapter(MultiPolygonAdapter.class)
//    public MultiPolygon getAllowedArea() {
//        return allowedArea;
//    }
//
//    /**
//     * @deprecated LIMIT rules should be used
//     */
//    @Deprecated
//    public void setAllowedArea(MultiPolygon allowedArea) {
//        this.allowedArea = allowedArea;
//    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dateCreation == null) ? 0 : dateCreation.hashCode());
        result = prime * result + (Boolean.valueOf(enabled).hashCode());
        result = prime * result + (Boolean.valueOf(admin).hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((userGroups == null) ? 0 : userGroups.hashCode());
//        result = prime * result + ((allowedArea == null) ? 0 : allowedArea.hashCode());
//        result = prime * result + ((allowedArea == null) ? 0 : allowedArea.getSRID());
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
        if (!(obj instanceof GSUser)) {
            return false;
        }
        GSUser other = (GSUser) obj;
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
        if(admin != other.admin) {
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

//        if (allowedArea == null) {
//            if (other.allowedArea != null) {
//                return false;
//            }
//        } else if (!allowedArea.equals(other.allowedArea)) {
//            return false;
//        } else if(other.allowedArea != null && other.allowedArea.getSRID() != allowedArea.getSRID()) {
//            return false;
//        }

        if ( this.userGroups != other.userGroups && (this.userGroups == null || !this.userGroups.equals(other.userGroups)) ) {
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
        builder.append("User [");
        builder.append("id=").append(id).append(", ");
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

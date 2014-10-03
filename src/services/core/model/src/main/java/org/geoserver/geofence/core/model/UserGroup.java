/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import org.geoserver.geofence.core.model.adapter.MapAdapter;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;

/**
 * A grouping for {@link GSUser}s.
 * <P>
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Entity(name = "UserGroup")
@Table(name = "gf_usergroup")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "usergroup")
@XmlRootElement(name = "UserGroup")
@XmlType(propOrder={"id","extId","name","dateCreation"/*,"customProps"*/})
public class UserGroup implements Identifiable, Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8457036587275531556L;

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
    @Column(nullable=false, updatable=true, unique=true)
    private String name;

    /** The date creation. */
    @Column(updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    /** The enabled. */
    @Column(nullable=false)
    private boolean enabled;

//    /** Custom properties associated to the profile */
//    @org.hibernate.annotations.CollectionOfElements
//    @JoinTable( name = "gf_group_custom_props",
//                joinColumns = @JoinColumn(name = "profile_id"))
//    @org.hibernate.annotations.MapKey(columns =@Column(name = "propkey"))
//    @Column(name = "propvalue")
//    @ForeignKey(name="fk_custom_profile")
//    private Map<String, String> customProps = new HashMap<String, String>();

    /**
     * Instantiates a new profile.
     */
    public UserGroup() {
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
     * Gets the date creation.
     *
     * @return the date creation
     */
    public Date getDateCreation() {
        return dateCreation;
    }

    /**
     * Sets the date creation.
     *
     * @param dateCreation
     *            the new date creation
     */
    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    /**
     * Gets the enabled.
     *
     * @return the enabled
     */
    @XmlAttribute
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled.
     *
     * @param enabled
     *            the new enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

//    @XmlJavaTypeAdapter(MapAdapter.class)
//    public Map<String, String> getCustomProps() {
//        return customProps;
//    }
//
//    public void setCustomProps(Map<String, String> customProps) {
//        this.customProps = customProps;
//    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dateCreation == null) ? 0 : dateCreation.hashCode());
        result = prime * result + Boolean.valueOf(enabled).hashCode();
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final UserGroup other = (UserGroup) obj;
        if ( this.id != other.id && (this.id == null || !this.id.equals(other.id)) ) {
            return false;
        }
        if ( (this.extId == null) ? (other.extId != null) : !this.extId.equals(other.extId) ) {
            return false;
        }
        if ( (this.name == null) ? (other.name != null) : !this.name.equals(other.name) ) {
            return false;
        }
        if ( this.dateCreation != other.dateCreation && (this.dateCreation == null || !this.dateCreation.equals(other.dateCreation)) ) {
            return false;
        }
        if ( this.enabled != other.enabled ) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null) {
//            return false;
//        }
//        if (!(obj instanceof UserGroup)) {
//            return false;
//        }
//        UserGroup other = (UserGroup) obj;
//        if (dateCreation == null) {
//            if (other.dateCreation != null) {
//                return false;
//            }
//        } else if (!dateCreation.equals(other.dateCreation)) {
//            return false;
//        }
//        if ( enabled != other.enabled) {
//            return false;
//        }
//        if (id != other.id) {
//            return false;
//        }
//        if (name == null) {
//            if (other.name != null) {
//                return false;
//            }
//        } else if (!name.equals(other.name)) {
//            return false;
//        }
//        return true;
//    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());
        builder.append("[");
        builder.append("id:").append(id);
        if (name != null)
            builder.append(" name:").append(name);
        if( ! enabled )
            builder.append(" disabled");
        builder.append("]");
        return builder.toString();
    }

}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import org.geoserver.geofence.core.model.enums.AccessType;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Embeddable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "LayerAttribute")
@XmlRootElement(name = "LayerAttribute")
public class LayerAttribute implements Serializable, Cloneable {

    private static final long serialVersionUID = -4739817113509675752L;

    @Column(nullable=false)
    private String name;

    @Column(name="data_type")
    private String datatype; // should be an enum?

    /** 
     * Tells if the attribute can be read, written, or not accessed at all.
     * <P>
     * This field should be notnull, but making it so, hibernate will insist to
     * put it into the PK.
     * We'll making it notnull in the {@link LayerDetails#attributes parent class},
     * but this seems not to work. We're enforncing the notnull at the DAO level.
     *
     */
    @Enumerated(EnumType.STRING)
    @Column(name="access_type", nullable = true /*false*/)
    private AccessType access;

    public LayerAttribute() {
    }

    public LayerAttribute(String name, AccessType access) {
        this.name = name;
        this.access = access;
    }

    public LayerAttribute(String name, String datatype, AccessType access) {
        this.name = name;
        this.datatype = datatype;
        this.access = access;
    }

    @XmlAttribute
    public AccessType getAccess() {
        return access;
    }

    public void setAccess(AccessType access) {
        this.access = access;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LayerAttribute other = (LayerAttribute) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.datatype == null) ? (other.datatype != null) : !this.datatype.equals(other.datatype)) {
            return false;
        }
        if (this.access != other.access) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 29 * hash + (this.datatype != null ? this.datatype.hashCode() : 0);
        hash = 29 * hash + (this.access != null ? this.access.hashCode() : 0);
        return hash;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName())
                .append("[name:").append(name)
                .append(" access:").append(access);

        if (datatype != null) {
            sb.append(" type:").append(datatype);
        }
        sb.append("]");

        return sb.toString();

    }

    @Override
    public LayerAttribute clone() {
        try {
            return (LayerAttribute) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Unexpected exception", ex);
        }
    }


}

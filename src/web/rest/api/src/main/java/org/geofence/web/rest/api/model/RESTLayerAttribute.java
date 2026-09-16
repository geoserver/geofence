/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import org.geofence.web.rest.api.model.enums.RESTAccessType;

/** @author ETj (etj at geo-solutions.it) */
@XmlRootElement(name = "LayerAttribute")
public class RESTLayerAttribute implements Serializable, Cloneable {

    private static final long serialVersionUID = 3809817113509675752L;

    private String name;
    private String datatype; // should be an enum?
    private RESTAccessType access;

    public RESTLayerAttribute() {}

    public RESTLayerAttribute(String name, RESTAccessType access) {
        this.name = name;
        this.access = access;
    }

    public RESTLayerAttribute(String name, String datatype, RESTAccessType access) {
        this.name = name;
        this.datatype = datatype;
        this.access = access;
    }

    @XmlAttribute
    public RESTAccessType getAccess() {
        return access;
    }

    public void setAccess(RESTAccessType access) {
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
        final RESTLayerAttribute other = (RESTLayerAttribute) obj;
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
                .append("[name:")
                .append(name)
                .append(" access:")
                .append(access);

        if (datatype != null) {
            sb.append(" type:").append(datatype);
        }
        sb.append("]");

        return sb.toString();
    }

    @Override
    public RESTLayerAttribute clone() {
        try {
            return (RESTLayerAttribute) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Unexpected exception", ex);
        }
    }
}

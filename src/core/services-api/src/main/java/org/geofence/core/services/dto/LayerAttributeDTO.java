/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.services.dto;

import java.io.Serializable;

public class LayerAttributeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String datatype;
    private AccessTypeDTO access;

    public LayerAttributeDTO() {}

    public LayerAttributeDTO(String name, AccessTypeDTO access) {
        this.name = name;
        this.access = access;
    }

    public LayerAttributeDTO(String name, String datatype, AccessTypeDTO access) {
        this.name = name;
        this.datatype = datatype;
        this.access = access;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public AccessTypeDTO getAccess() {
        return access;
    }

    public void setAccess(AccessTypeDTO access) {
        this.access = access;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LayerAttributeDTO)) return false;
        LayerAttributeDTO other = (LayerAttributeDTO) obj;
        return java.util.Objects.equals(name, other.name)
                && java.util.Objects.equals(datatype, other.datatype)
                && access == other.access;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, datatype, access);
    }

    @Override
    public String toString() {
        return "LayerAttributeDTO[name:" + name + " datatype:" + datatype + " access:" + access + "]";
    }
}

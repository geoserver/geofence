/* (c) 2020 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.gui.client.model.data;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.IsSerializable;
import org.geoserver.geofence.gui.client.model.BeanKeyValue;

public class ClientSpatialFilterType extends BeanModel implements IsSerializable {


    private static final long serialVersionUID = 545243241879482276L;

    public static String INTERSECT_NAME ="INTERSECT";
    public static String CLIP_NAME="CLIP";

    public static ClientSpatialFilterType INTERSECT = new ClientSpatialFilterType(INTERSECT_NAME);

    public static ClientSpatialFilterType CLIP = new ClientSpatialFilterType(CLIP_NAME);

    /** The type. */
    private String type;


    protected ClientSpatialFilterType(String type)
    {
        setType(type);
    }

    /**
     * Instantiates a new type.
     */
    public ClientSpatialFilterType() {

    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(String type)
    {
        this.type = type;
        set(BeanKeyValue.SPATIAL_FILTER_TYPE.getValue(), this.type);
    }


    public String getType()
    {
        return type;
    }


    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((type == null) ? 0 : type.hashCode());

        return result;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof AccessType))
        {
            return false;
        }

        AccessType other = (AccessType) obj;
        if (type == null)
        {
            if (other.getType() != null)
            {
                return false;
            }
        }
        else if (!type.equals(other.getType()))
        {
            return false;
        }

        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SpatialFilterType [");
        if (type != null)
        {
            builder.append("type=").append(type).append(", ");
        }
        builder.append("]");

        return builder.toString();
    }
}

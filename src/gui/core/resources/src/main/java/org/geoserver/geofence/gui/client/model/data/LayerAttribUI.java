/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.model.data;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.IsSerializable;

import org.geoserver.geofence.gui.client.model.BeanKeyValue;


/**
 * The Class LayerAttributes.
 *
 * @author Tobia di Pisa
 */
public class LayerAttribUI extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 283407523026763286L;

    /** The name. */
    private String name;

    /** The dataType. */
    private String dataType;

    /** The accessType. */
    private String accessType;

    /**
     * Instantiates a new layer attributes.
     */
    public LayerAttribUI()
    {
        super();
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
        set(BeanKeyValue.ATTR_NAME.getValue(), this.name);
    }

    /**
     * @return the dataType
     */
    public String getDataType()
    {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(String dataType)
    {
        this.dataType = dataType;
        set(BeanKeyValue.ATTR_TYPE.getValue(), this.dataType);
    }

    /**
     * @return the accessType
     */
    public String getAccessType()
    {
        return accessType;
    }

    /**
     * @param accessType the accessType to set
     */
    public void setAccessType(String accessType)
    {
        this.accessType = accessType;
        set(BeanKeyValue.ATTR_ACCESS.getValue(), this.accessType);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((name == null) ? 0 : name.hashCode());
        result = (prime * result) + ((dataType == null) ? 0 : dataType.hashCode());
        result = (prime * result) + ((accessType == null) ? 0 : accessType.hashCode());

        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
        if (!(obj instanceof LayerAttribUI))
        {
            return false;
        }

        LayerAttribUI other = (LayerAttribUI) obj;
        if (name == null)
        {
            if (other.name != null)
            {
                return false;
            }
        }
        else if (!name.equals(other.name))
        {
            return false;
        }
        if (dataType == null)
        {
            if (other.dataType != null)
            {
                return false;
            }
        }
        else if (!dataType.equals(other.dataType))
        {
            return false;
        }
        if (accessType == null)
        {
            if (other.accessType != null)
            {
                return false;
            }
        }
        else if (!accessType.equals(other.accessType))
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
        builder.append("LayerAttributes [");
        if (name != null)
        {
            builder.append("name=").append(name).append(", ");
        }
        if (dataType != null)
        {
            builder.append("dataType=").append(dataType).append(", ");
        }
        if (accessType != null)
        {
            builder.append("accessType=").append(accessType);
        }
        builder.append("]");

        return builder.toString();
    }

}

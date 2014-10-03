/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.model.data;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.IsSerializable;

import org.geoserver.geofence.gui.client.model.BeanKeyValue;


/**
 * The Class AccessType.
 *
 * @author Tobia di Pisa
 */
public class AccessType extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7479296357425226701L;

    /** The type. */
    private String type;

    /**
     * Instantiates a new type.
     *
     * @param grant
     *            the type
     */
    public AccessType(String type)
    {
        this();
        setType(type);
    }

    /**
     * Instantiates a new type.
     */
    public AccessType()
    {
        super();
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(String type)
    {
        this.type = type;
        set(BeanKeyValue.ATTR_ACCESS.getValue(), this.type);
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((type == null) ? 0 : type.hashCode());

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
        if (!(obj instanceof AccessType))
        {
            return false;
        }

        AccessType other = (AccessType) obj;
        if (type == null)
        {
            if (other.type != null)
            {
                return false;
            }
        }
        else if (!type.equals(other.type))
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
        builder.append("AccessType [");
        if (type != null)
        {
            builder.append("type=").append(type).append(", ");
        }
        builder.append("]");

        return builder.toString();
    }

}

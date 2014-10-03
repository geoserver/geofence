/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.model.data;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.IsSerializable;

import org.geoserver.geofence.gui.client.model.BeanKeyValue;


/**
 * The Class ProfileCustomProps.
 */
public class ProfileCustomProps extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8661646874896039507L;

    /** The prop key. */
    private String propKey;

    /** The prop value. */
    private String propValue;

    /** The path. */
    private String path;

    /**
     * Instantiates a new layer custom props.
     */
    public ProfileCustomProps()
    {
        super();
        setPath("geofence/resources/images/layer.jpg");
    }

    /**
     * Sets the layer.
     *
     * @param propKey
     *            the new layer
     */
    public void setPropKey(String propKey)
    {
        this.propKey = propKey;
        set(BeanKeyValue.PROFILE_PROP_KEY.getValue(), this.propKey);
    }

    /**
     * Sets the path.
     *
     * @param path
     *            the new path
     */
    public void setPath(String path)
    {
        this.path = path;
        set(BeanKeyValue.PATH.getValue(), path);
    }

    /**
     * Gets the path.
     *
     * @return the path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Gets the layer.
     *
     * @return the layer
     */
    public String getPropKey()
    {
        return propKey;
    }

    /**
     * Sets the path.
     *
     * @param propValue
     *            the new path
     */
    public void setPropValue(String propValue)
    {
        this.propValue = propValue;
        set(BeanKeyValue.PROFILE_PROP_VALUE.getValue(), this.propValue);
    }

    /**
     * Gets the path.
     *
     * @return the path
     */
    public String getPropValue()
    {
        return propValue;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((path == null) ? 0 : path.hashCode());
        result = (prime * result) + ((propKey == null) ? 0 : propKey.hashCode());
        result = (prime * result) + ((propValue == null) ? 0 : propValue.hashCode());

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
        if (!(obj instanceof ProfileCustomProps))
        {
            return false;
        }

        ProfileCustomProps other = (ProfileCustomProps) obj;
        if (path == null)
        {
            if (other.path != null)
            {
                return false;
            }
        }
        else if (!path.equals(other.path))
        {
            return false;
        }
        if (propKey == null)
        {
            if (other.propKey != null)
            {
                return false;
            }
        }
        else if (!propKey.equals(other.propKey))
        {
            return false;
        }
        if (propValue == null)
        {
            if (other.propValue != null)
            {
                return false;
            }
        }
        else if (!propValue.equals(other.propValue))
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
        builder.append("ProfileCustomProps [");
        if (path != null)
        {
            builder.append("path=").append(path).append(", ");
        }
        if (propKey != null)
        {
            builder.append("propKey=").append(propKey).append(", ");
        }
        if (propValue != null)
        {
            builder.append("propValue=").append(propValue);
        }
        builder.append("]");

        return builder.toString();
    }

}

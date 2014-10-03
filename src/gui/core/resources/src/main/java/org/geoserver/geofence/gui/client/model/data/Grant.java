/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.model.data;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.IsSerializable;

import org.geoserver.geofence.gui.client.model.BeanKeyValue;


// TODO: Auto-generated Javadoc
/**
 * The Class Grant.
 */
public class Grant extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3711302358289438531L;

    /** The grant. */
    private String grant;

    /** The path. */
    private String path;

    /**
     * Instantiates a new grant.
     *
     * @param grant
     *            the grant
     */
    public Grant(String grant)
    {
        this();
        setGrant(grant);
    }

    /**
     * Instantiates a new grant.
     */
    public Grant()
    {
        super();
        setPath("geofence/resources/images/grant.jpg");
    }

    /**
     * Sets the grant.
     *
     * @param grant
     *            the new grant
     */
    public void setGrant(String grant)
    {
        this.grant = grant;
        set(BeanKeyValue.GRANT.getValue(), this.grant);
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
     * Gets the grant.
     *
     * @return the grant
     */
    public String getGrant()
    {
        return grant;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((grant == null) ? 0 : grant.hashCode());
        result = (prime * result) + ((path == null) ? 0 : path.hashCode());

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
        if (!(obj instanceof Grant))
        {
            return false;
        }

        Grant other = (Grant) obj;
        if (grant == null)
        {
            if (other.grant != null)
            {
                return false;
            }
        }
        else if (!grant.equals(other.grant))
        {
            return false;
        }
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

        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Grant [");
        if (grant != null)
        {
            builder.append("grant=").append(grant).append(", ");
        }
        if (path != null)
        {
            builder.append("path=").append(path);
        }
        builder.append("]");

        return builder.toString();
    }

}

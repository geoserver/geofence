/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.model;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.IsSerializable;

import org.geoserver.geofence.gui.client.model.BeanKeyValue;


// TODO: Auto-generated Javadoc
/**
 */
public class UsernameModel extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2828906795801803648L;

    private String username;

    /** The path. */
    private String path;


    public UsernameModel()
    {
        super();
        setPath("geofence/resources/images/username.jpg");
    }

    public UsernameModel(String username)
    {
        this();
        setUsername(username);
    }

    /**
     * Sets the service.
     *
     * @param username
     *            the new service
     */
    public void setUsername(String username)
    {
        this.username = username;
        set(BeanKeyValue.USERNAME.getValue(), this.username);
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

    public String getUsername() {
        return username;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((username == null) ? 0 : username.hashCode());

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
        if (!(obj instanceof UsernameModel))
        {
            return false;
        }

        UsernameModel other = (UsernameModel) obj;
        if (username == null)
        {
            if (other.username != null)
            {
                return false;
            }
        }
        else if (!username.equals(other.username))
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
        builder.append("Username [");
        if (username != null)
        {
            builder.append("username=").append(username);
        }
        builder.append("]");

        return builder.toString();
    }

}

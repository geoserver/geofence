/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.model;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.IsSerializable;



/**
 */
public class RolenameModel extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2828906795801803648L;

    private String rolename;

    /** The path. */
    private String path;


    public RolenameModel()
    {
        super();
        setPath("geofence/resources/images/rolename.jpg");
    }

    public RolenameModel(String rolename)
    {
        this();
        setRolename(rolename);
    }

    /**
     * Sets the service.
     *
     * @param username
     *            the new service
     */
    public void setRolename(String rolename)
    {
        this.rolename = rolename;
        set(BeanKeyValue.ROLENAME.getValue(), this.rolename);
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

    public String getRolename() {
        return rolename;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((rolename == null) ? 0 : rolename.hashCode());

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
        if (!(obj instanceof RolenameModel))
        {
            return false;
        }

        RolenameModel other = (RolenameModel) obj;
        if (rolename == null)
        {
            if (other.rolename != null)
            {
                return false;
            }
        }
        else if (!rolename.equals(other.rolename))
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
        builder.append("Rolename [");
        if (rolename != null)
        {
            builder.append("name=").append(rolename);
        }
        builder.append("]");

        return builder.toString();
    }

}

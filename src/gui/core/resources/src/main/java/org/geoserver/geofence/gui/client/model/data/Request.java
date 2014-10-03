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
 * The Class Request.
 */
public class Request extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -750499207539541183L;

    /** The request. */
    private String request;

    /** The path. */
    private String path;

    /**
     * Instantiates a new request.
     *
     * @param request
     *            the request
     */
    public Request(String request)
    {
        this();
        setRequest(request);
    }

    /**
     * Instantiates a new request.
     */
    public Request()
    {
        super();
        setPath("geofence/resources/images/request.jpg");
    }

    /**
     * Sets the request.
     *
     * @param request
     *            the new request
     */
    public void setRequest(String request)
    {
        this.request = request;
        set(BeanKeyValue.REQUEST.getValue(), this.request);
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
     * Gets the request.
     *
     * @return the request
     */
    public String getRequest()
    {
        return request;
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
        result = (prime * result) + ((request == null) ? 0 : request.hashCode());

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
        if (!(obj instanceof Request))
        {
            return false;
        }

        Request other = (Request) obj;
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
        if (request == null)
        {
            if (other.request != null)
            {
                return false;
            }
        }
        else if (!request.equals(other.request))
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
        builder.append("Request [");
        if (path != null)
        {
            builder.append("path=").append(path).append(", ");
        }
        if (request != null)
        {
            builder.append("request=").append(request);
        }
        builder.append("]");

        return builder.toString();
    }

}

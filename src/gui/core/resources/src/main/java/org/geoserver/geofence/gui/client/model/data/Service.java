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
 * The Class Service.
 */
public class Service extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2828906795801803648L;

    /** The service. */
    private String service;

    /** The path. */
    private String path;

    /**
     * Instantiates a new service.
     *
     * @param service
     *            the service
     */
    public Service(String service)
    {
        this();
        setService(service);
    }

    /**
     * Instantiates a new service.
     */
    public Service()
    {
        super();
        setPath("geofence/resources/images/service.jpg");
    }

    /**
     * Sets the service.
     *
     * @param service
     *            the new service
     */
    public void setService(String service)
    {
        this.service = service;
        set(BeanKeyValue.SERVICE.getValue(), this.service);
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
     * Gets the service.
     *
     * @return the service
     */
    public String getService()
    {
        return service;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((service == null) ? 0 : service.hashCode());

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
        if (!(obj instanceof Service))
        {
            return false;
        }

        Service other = (Service) obj;
        if (service == null)
        {
            if (other.service != null)
            {
                return false;
            }
        }
        else if (!service.equals(other.service))
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
        builder.append("Service [");
        if (service != null)
        {
            builder.append("service=").append(service);
        }
        builder.append("]");

        return builder.toString();
    }

}

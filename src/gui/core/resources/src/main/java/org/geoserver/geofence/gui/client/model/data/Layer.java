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
 * The Class Layer.
 */
public class Layer extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7312704465426834699L;

    /** The layer. */
    private String layer;

    /** The path. */
    private String path;

    /**
     * Instantiates a new layer.
     *
     * @param layer
     *            the layer
     */
    public Layer(String layer)
    {
        this();
        setLayer(layer);
    }

    /**
     * Instantiates a new layer.
     */
    public Layer()
    {
        super();
        setPath("geofence/resources/images/layer.jpg");
    }

    /**
     * Sets the layer.
     *
     * @param layer
     *            the new layer
     */
    public void setLayer(String layer)
    {
        this.layer = layer;
        set(BeanKeyValue.LAYER.getValue(), this.layer);
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
    public String getLayer()
    {
        return layer;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((layer == null) ? 0 : layer.hashCode());
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
        if (!(obj instanceof Layer))
        {
            return false;
        }

        Layer other = (Layer) obj;
        if (layer == null)
        {
            if (other.layer != null)
            {
                return false;
            }
        }
        else if (!layer.equals(other.layer))
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
        builder.append("Layer [");
        if (layer != null)
        {
            builder.append("layer=").append(layer).append(", ");
        }
        if (path != null)
        {
            builder.append("path=").append(path);
        }
        builder.append("]");

        return builder.toString();
    }

}

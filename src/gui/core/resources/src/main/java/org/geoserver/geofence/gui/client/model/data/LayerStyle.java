/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.model.data;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.IsSerializable;

import org.geoserver.geofence.gui.client.model.BeanKeyValue;


/**
 * The Class LayerStyle.
 *
 * @author Tobia di Pisa
 */
public class LayerStyle extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2934306559184716813L;

    /** The style. */
    private String style;

    private boolean enabled;

    /**
    * Instantiates a new style.
    *
    * @param style
    *            the style
    */
    public LayerStyle(String style)
    {
        this();
        setStyle(style);
    }

    /**
     * Instantiates a new style.
     */
    public LayerStyle()
    {
        super();
    }

    /**
     * Sets the style.
     *
     * @param type the new type
     */
    public void setStyle(String style)
    {
        this.style = style;
        set(BeanKeyValue.STYLES_COMBO.getValue(), this.style);
    }

    /**
     * Gets the style.
     *
     * @return the style
     */
    public String getStyle()
    {
        return style;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        set(BeanKeyValue.STYLE_ENABLED.getValue(), this.enabled);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((style == null) ? 0 : style.hashCode());
        result = (prime * result) + ((enabled) ? 1 : 0);

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

        LayerStyle other = (LayerStyle) obj;
        if (style == null)
        {
            if (other.style != null)
            {
                return false;
            }
        }
        else if (!style.equals(other.style))
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
        builder.append("Style [");

        if (style != null)
        {
            builder.append("style=").append(style).append(", ");
        }

        builder.append("enabled=").append(enabled);

        builder.append("]");

        return builder.toString();
    }

}

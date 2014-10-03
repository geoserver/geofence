/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.model.data;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.IsSerializable;

import org.geoserver.geofence.gui.client.model.BeanKeyValue;


/**
 * The Class LayerDetailsForm.
 *
 * @author Tobia di Pisa
 */
public class LayerDetailsInfo extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1015403375883622286L;

    private Long ruleId;

    private String type;

    /** The default style. */
    private String defaultStyle;

    private String cqlFilterRead;

    private String cqlFilterWrite;

    private String allowedArea;

    private String srid;

    private ClientCatalogMode catalogMode;

    /**
     * Instantiates a new style.
     */
    public LayerDetailsInfo()
    {
        super();
    }

    /**
     * @return the ruleId
     */
    public Long getRuleId()
    {
        return ruleId;
    }

    /**
     * @param ruleId the ruleId to set
     */
    public void setRuleId(Long ruleId)
    {
        this.ruleId = ruleId;
    }

    /**
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Sets the style.
     *
     * @param type the new type
     */
    public void setDefaultStyle(String defaultStyle)
    {
        this.defaultStyle = defaultStyle;
        set(BeanKeyValue.STYLES_COMBO.getValue(), this.defaultStyle);
    }

    /**
     * Gets the style.
     *
     * @return the style
     */
    public String getDefaultStyle()
    {
        return defaultStyle;
    }

    /**
     * @return the cqlFilterRead
     */
    public String getCqlFilterRead()
    {
        return cqlFilterRead;
    }

    /**
     * @param cqlFilterRead the cqlFilterRead to set
     */
    public void setCqlFilterRead(String cqlFilterRead)
    {
        this.cqlFilterRead = cqlFilterRead;
        set(BeanKeyValue.CQL_READ.getValue(), this.cqlFilterRead);
    }

    /**
     * @return the cqlFilterWrite
     */
    public String getCqlFilterWrite()
    {
        return cqlFilterWrite;
    }

    /**
     * @param cqlFilterWrite the cqlFilterWrite to set
     */
    public void setCqlFilterWrite(String cqlFilterWrite)
    {
        this.cqlFilterWrite = cqlFilterWrite;
        set(BeanKeyValue.CQL_WRITE.getValue(), this.cqlFilterWrite);
    }

    /**
     * @return the allowedArea
     */
    public String getAllowedArea()
    {
        return allowedArea;
    }

    /**
     * @param allowedArea the allowedArea to set
     */
    public void setAllowedArea(String allowedArea)
    {
        this.allowedArea = allowedArea;
        set(BeanKeyValue.ALLOWED_AREA.getValue(), this.allowedArea);
    }

    /**
     * @return the srid
     */
    public String getSrid()
    {
        return srid;
    }

    /**
     * @param srid the srid to set
     */
    public void setSrid(String srid)
    {
        this.srid = srid;
        set(BeanKeyValue.RULE_ALLOWED_AREA_SRID.getValue(), this.srid);
    }

    public ClientCatalogMode getCatalogMode() {
        return catalogMode;
    }

    public void setCatalogMode(ClientCatalogMode catalogMode) {
        this.catalogMode = catalogMode;
        set(BeanKeyValue.CATALOG_MODE.getValue(), this.catalogMode);
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((ruleId == null) ? 0 : ruleId.hashCode());
        result = (prime * result) + ((defaultStyle == null) ? 0 : defaultStyle.hashCode());
        result = (prime * result) + ((cqlFilterRead == null) ? 0 : cqlFilterRead.hashCode());
        result = (prime * result) + ((cqlFilterWrite == null) ? 0 : cqlFilterWrite.hashCode());
        result = (prime * result) + ((allowedArea == null) ? 0 : allowedArea.hashCode());
        result = (prime * result) + ((srid == null) ? 0 : srid.hashCode());
        result = (prime * result) + ((catalogMode == null) ? 0 : catalogMode.hashCode());

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

        LayerDetailsInfo other = (LayerDetailsInfo) obj;

        if (ruleId == null)
        {
            if (other.ruleId != null)
            {
                return false;
            }
        }
        else if (!ruleId.equals(other.ruleId))
        {
            return false;
        }

        if (defaultStyle == null)
        {
            if (other.defaultStyle != null)
            {
                return false;
            }
        }
        else if (!defaultStyle.equals(other.defaultStyle))
        {
            return false;
        }

        if (cqlFilterRead == null)
        {
            if (other.cqlFilterRead != null)
            {
                return false;
            }
        }
        else if (!cqlFilterRead.equals(other.cqlFilterRead))
        {
            return false;
        }

        if (cqlFilterWrite == null)
        {
            if (other.cqlFilterWrite != null)
            {
                return false;
            }
        }
        else if (!cqlFilterWrite.equals(other.cqlFilterWrite))
        {
            return false;
        }

        if (allowedArea == null)
        {
            if (other.allowedArea != null)
            {
                return false;
            }
        }
        else if (!allowedArea.equals(other.allowedArea))
        {
            return false;
        }

        if (srid == null)
        {
            if (other.srid != null)
            {
                return false;
            }
        }
        else if (!srid.equals(other.srid))
        {
            return false;
        }

        if (catalogMode == null)
        {
            if (other.catalogMode != null)
            {
                return false;
            }
        }
        else if (!catalogMode.equals(other.catalogMode))
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
        builder.append("LayerDetailsForm [");

        if (ruleId != null)
        {
            builder.append("ruleId=").append(ruleId).append(", ");
        }
        if (defaultStyle != null)
        {
            builder.append("defaultStyle=").append(defaultStyle).append(", ");
        }
        if (cqlFilterRead != null)
        {
            builder.append("cqlFilterRead=").append(cqlFilterRead).append(", ");
        }
        if (cqlFilterWrite != null)
        {
            builder.append("cqlFilterWrite=").append(cqlFilterWrite).append(", ");
        }
        if (allowedArea != null)
        {
            builder.append("allowedArea=").append(allowedArea).append(", ");
        }
        if (srid != null)
        {
            builder.append("srid=").append(srid).append(", ");
        }
        if (catalogMode != null)
        {
            builder.append("cmode=").append(catalogMode).append(", ");
        }

        builder.append("]");

        return builder.toString();
    }

}

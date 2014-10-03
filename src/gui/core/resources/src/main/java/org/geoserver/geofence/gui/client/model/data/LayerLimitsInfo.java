/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.model.data;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.IsSerializable;

import org.geoserver.geofence.gui.client.model.BeanKeyValue;


/**
 * Class LayerLimitsInfo.
 *
 * @author Tobia di Pisa
 *
 */
public class LayerLimitsInfo extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3714459295153015643L;

    private Long ruleId;

    private String allowedArea;

    private String srid;

    private ClientCatalogMode catalogMode;


    /**
     * Instantiates a new limits.
     */
    public LayerLimitsInfo()
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
        set(BeanKeyValue.LAYER_ALLOWED_AREA_SRID.getValue(), this.srid);
    }

    public ClientCatalogMode getCatalogMode() {
        return catalogMode;
    }

    public void setCatalogMode(ClientCatalogMode catalogMode) {
        this.catalogMode = catalogMode;
        set(BeanKeyValue.CATALOG_MODE.getValue(), catalogMode);
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
        result = (prime * result) + ((allowedArea == null) ? 0 : allowedArea.hashCode());
        result = (prime * result) + ((srid == null) ? 0 : srid.hashCode());
        result = (prime * result) + ((catalogMode == null) ? 0 : catalogMode.hashCode());

        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LayerLimitsInfo other = (LayerLimitsInfo) obj;
        if (this.ruleId != other.ruleId && (this.ruleId == null || !this.ruleId.equals(other.ruleId))) {
            return false;
        }
        if ((this.allowedArea == null) ? (other.allowedArea != null) : !this.allowedArea.equals(other.allowedArea)) {
            return false;
        }
        if ((this.srid == null) ? (other.srid != null) : !this.srid.equals(other.srid)) {
            return false;
        }
        if (this.catalogMode != other.catalogMode && (this.catalogMode == null || !this.catalogMode.equals(other.catalogMode))) {
            return false;
        }
        return true;
    }



    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LayerLimitsForm [");

        if (ruleId != null)
        {
            builder.append("ruleId=").append(ruleId).append(", ");
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
            builder.append("mode=").append(catalogMode).append(", ");
        }

        builder.append("]");

        return builder.toString();
    }

}

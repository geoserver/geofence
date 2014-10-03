/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.model.data;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.IsSerializable;

import org.geoserver.geofence.gui.client.model.BeanKeyValue;


/**
 * Class UserLimitsInfo.
 *
 * @author Tobia di Pisa
 *
 */
public class UserLimitsInfo extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1760111388826438170L;

    private Long userId;

    private String allowedArea;

    private String srid;


    /**
     * Instantiates a new limits.
     */
    public UserLimitsInfo()
    {
        super();
    }

    /**
     * @return the userId
     */
    public Long getUserId()
    {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    /**
     * @return the allowedArea
     * @deprecated removed from base model
     */
    public String getAllowedArea()
    {
        return allowedArea;
    }

    /**
     * @param allowedArea the allowedArea to set
     * @deprecated removed from base model
     */
    public void setAllowedArea(String allowedArea)
    {
        this.allowedArea = allowedArea;
        set(BeanKeyValue.USER_ALLOWED_AREA.getValue(), this.allowedArea);
    }

    /**
     * @return the srid
     * @deprecated removed from base model
     */
    public String getSrid()
    {
        return srid;
    }

    /**
     * @param srid the srid to set
     * @deprecated removed from base model
     */
    public void setSrid(String srid)
    {
        this.srid = srid;
        set(BeanKeyValue.USER_ALLOWED_AREA_SRID.getValue(), this.srid);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((userId == null) ? 0 : userId.hashCode());
        result = (prime * result) + ((allowedArea == null) ? 0 : allowedArea.hashCode());
        result = (prime * result) + ((srid == null) ? 0 : srid.hashCode());

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

        UserLimitsInfo other = (UserLimitsInfo) obj;

        if (userId == null)
        {
            if (other.userId != null)
            {
                return false;
            }
        }
        else if (!userId.equals(other.userId))
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

        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("LayerLimitsForm [");

        if (userId != null)
        {
            builder.append("userId=").append(userId).append(", ");
        }
        if (allowedArea != null)
        {
            builder.append("allowedArea=").append(allowedArea).append(", ");
        }
        if (srid != null)
        {
            builder.append("srid=").append(srid).append(", ");
        }

        builder.append("]");

        return builder.toString();
    }

}

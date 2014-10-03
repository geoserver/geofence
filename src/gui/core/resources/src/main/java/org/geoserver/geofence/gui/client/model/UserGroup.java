/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.IsSerializable;


// TODO: Auto-generated Javadoc
/**
 * The Class Profile.
 */
public class UserGroup extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3475163929906592234L;

    /** The id. */
    private long id;

    /** The name. */
    private String name;

    /** The date creation. */
    private Date dateCreation;

    /** The enabled. */
    private boolean enabled;

    /** The extID. */
    private String extId;

    /** The path. */
    private String path;

    /**
     * Instantiates a new profile.
     */
    public UserGroup()
    {
        setPath("geofence/resources/images/profile.jpg");
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public long getId()
    {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id
     *            the new id
     */
    public void setId(long id)
    {
        this.id = id;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name
     *            the new name
     */
    public void setName(String name)
    {
        this.name = name;
        set(BeanKeyValue.NAME.getValue(), name);
    }

    /**
     * Gets the date creation.
     *
     * @return the date creation
     */
    public Date getDateCreation()
    {
        return dateCreation;
    }

    /**
     * Sets the date creation.
     *
     * @param dateCreation
     *            the new date creation
     */
    public void setDateCreation(Date dateCreation)
    {
        this.dateCreation = dateCreation;
        set(BeanKeyValue.DATE_CREATION.getValue(), dateCreation);
    }

    /**
     * Checks if is the enabled.
     *
     * @return the enabled
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Sets the enabled.
     *
     * @param enabled
     *            the new enabled
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        set(BeanKeyValue.PROFILE_ENABLED.getValue(), enabled);
    }

    /**
	 * @param extId the extId to set
	 */
	public void setExtId(String extId) {
		this.extId = extId;
	}

	/**
	 * @return the extId
	 */
	public String getExtId() {
		return extId;
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

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dateCreation == null) ? 0 : dateCreation.hashCode());
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result + ((extId == null) ? 0 : extId.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

    /* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof UserGroup)) {
			return false;
		}
		UserGroup other = (UserGroup) obj;
		if (dateCreation == null) {
			if (other.dateCreation != null) {
				return false;
			}
		} else if (!dateCreation.equals(other.dateCreation)) {
			return false;
		}
		if (enabled != other.enabled) {
			return false;
		}
		if (extId == null) {
			if (other.extId != null) {
				return false;
			}
		} else if (!extId.equals(other.extId)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!path.equals(other.path)) {
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
		builder.append("Profile [id=").append(id).append(", ");
		if (name != null)
			builder.append("name=").append(name).append(", ");
		if (dateCreation != null)
			builder.append("dateCreation=").append(dateCreation).append(", ");
		builder.append("enabled=").append(enabled).append(", ");
		if (extId != null)
			builder.append("extId=").append(extId).append(", ");
		if (path != null)
			builder.append("path=").append(path);
		builder.append("]");
		return builder.toString();
	}
}

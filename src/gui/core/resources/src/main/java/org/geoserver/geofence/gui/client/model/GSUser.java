/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.IsSerializable;


// TODO: Auto-generated Javadoc
/**
 * The Class GSUser.
 */
public class GSUser extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 270623297309308741L;

    /** The id. */
    private long id;

    /** The name. */
    private String name;

    /** The full name. */
    private String fullName;

    /** The password. */
    private String password;

    /** The email address. */
    private String emailAddress;

    /** The date creation. */
    private Date dateCreation;

    /** The enabled. */
    private boolean enabled;

    /** The admin. */
    private boolean admin;

    /** The user groups. */
    private Set<UserGroup> userGroups = new HashSet<UserGroup>();

    /** The path. */
    private String path;

    /**
     * Instantiates a new gS user.
     */
    public GSUser()
    {
        setPath("geofence/resources/images/userChoose.jpg");
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
        set(BeanKeyValue.NAME.getValue(), this.name);
    }

    /**
     * Sets the full name.
     *
     * @param fullName
     *            the new full name
     */
    public void setFullName(String fullName)
    {
        this.fullName = fullName;
        set(BeanKeyValue.FULL_NAME.getValue(), fullName);
    }

    /**
     * Gets the full name.
     *
     * @return the full name
     */
    public String getFullName()
    {
        return fullName;
    }


    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password
     *            the new password
     */
    public void setPassword(String password)
    {
        this.password = password;
        set(BeanKeyValue.PASSWORD.getValue(), password);
    }

    /**
     * Gets the email address.
     *
     * @return the email address
     */
    public String getEmailAddress()
    {
        return emailAddress;
    }

    /**
     * Sets the email address.
     *
     * @param emailAddress
     *            the new email address
     */
    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
        set(BeanKeyValue.EMAIL.getValue(), emailAddress);
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
     * Gets the date creation.
     *
     * @return the date creation
     */
    public Date getDateCreation()
    {
        return dateCreation;
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
        set(BeanKeyValue.USER_ENABLED.getValue(), enabled);
    }

    /**
     * @return the admin
     */
    public boolean isAdmin()
    {
        return admin;
    }

    /**
     * @param admin the admin to set
     */
    public void setAdmin(boolean admin)
    {
        this.admin = admin;
        set(BeanKeyValue.USER_ADMIN.getValue(), admin);
    }

    /**
     * Sets the profile.
     *
     * @param profile
     *            the new profile
     */
//    public void setProfile(Profile profile)
//    {
//        this.profile = profile;
//        set(BeanKeyValue.PROFILE.getValue(), profile);
//    }

    /**
     * Gets the profile.
     *
     * @return the profile
     */
//    public Profile getProfile()
//    {
//        return profile;
//    }

    /**
	 * @param userGroups the userGroups to set
	 */
	public void setUserGroups(Set<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}

	/**
	 * @return the userGroups
	 */
	public Set<UserGroup> getUserGroups() {
		return userGroups;
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
     * Sets the path.
     *
     * @param path
     *            the new path
     */
    public void setPath(String path)
    {
        this.path = path;
        set(BeanKeyValue.PATH.getValue(), this.path);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((dateCreation == null) ? 0 : dateCreation.hashCode());
        result = (prime * result) + ((emailAddress == null) ? 0 : emailAddress.hashCode());
        result = (prime * result) + (enabled ? 1231 : 1237);
        result = (prime * result) + ((fullName == null) ? 0 : fullName.hashCode());
        result = (prime * result) + (int) (id ^ (id >>> 32));
        result = (prime * result) + ((name == null) ? 0 : name.hashCode());
        result = (prime * result) + ((password == null) ? 0 : password.hashCode());
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
        if (!(obj instanceof GSUser))
        {
            return false;
        }

        GSUser other = (GSUser) obj;
        if (dateCreation == null)
        {
            if (other.dateCreation != null)
            {
                return false;
            }
        }
        else if (!dateCreation.equals(other.dateCreation))
        {
            return false;
        }
        if (emailAddress == null)
        {
            if (other.emailAddress != null)
            {
                return false;
            }
        }
        else if (!emailAddress.equals(other.emailAddress))
        {
            return false;
        }
        if (enabled != other.enabled)
        {
            return false;
        }
        if (fullName == null)
        {
            if (other.fullName != null)
            {
                return false;
            }
        }
        else if (!fullName.equals(other.fullName))
        {
            return false;
        }
        if (id != other.id)
        {
            return false;
        }
        if (name == null)
        {
            if (other.name != null)
            {
                return false;
            }
        }
        else if (!name.equals(other.name))
        {
            return false;
        }
        if (password == null)
        {
            if (other.password != null)
            {
                return false;
            }
        }
        else if (!password.equals(other.password))
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
        builder.append("User [");
        if (dateCreation != null)
        {
            builder.append("dateCreation=").append(dateCreation).append(", ");
        }
        if (emailAddress != null)
        {
            builder.append("emailAddress=").append(emailAddress).append(", ");
        }
        builder.append("enabled=").append(enabled).append(", ");
        if (fullName != null)
        {
            builder.append("fullName=").append(fullName).append(", ");
        }
        builder.append("id=").append(id).append(", ");
        if (name != null)
        {
            builder.append("name=").append(name).append(", ");
        }
        if (password != null)
        {
            builder.append("password=").append(password).append(", ");
        }
        if (path != null)
        {
            builder.append("path=").append(path);
        }
        builder.append("]");

        return builder.toString();
    }

}

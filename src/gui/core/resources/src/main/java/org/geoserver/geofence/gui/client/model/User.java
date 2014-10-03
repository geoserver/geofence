/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.model;

import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.IsSerializable;


// TODO: Auto-generated Javadoc
/**
 * The Class User.
 */
public class User extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5117714882113396553L;

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

    /** The path. */
    private String path;

    /** The granted authorizations. */
    private List<Authorization> grantedAuthorizations;

    /**
     * Instantiates a new user.
     */
    public User()
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
        set(BeanKeyValue.USER_NAME.getValue(), this.name);
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

    /**
     * Sets the granted authorizations.
     *
     * @param ga
     *            the new granted authorizations
     */
    public void setGrantedAuthorizations(List<Authorization> ga)
    {
        this.grantedAuthorizations = ga;
    }

    /**
     * Gets the granted authorizations.
     *
     * @return the granted authorizations
     */
    public List<Authorization> getGrantedAuthorizations()
    {
        return this.grantedAuthorizations;
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
        result = (prime * result) +
            ((grantedAuthorizations == null) ? 0 : grantedAuthorizations.hashCode());
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
        if (!(obj instanceof User))
        {
            return false;
        }

        User other = (User) obj;
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
        if (grantedAuthorizations == null)
        {
            if (other.grantedAuthorizations != null)
            {
                return false;
            }
        }
        else if (!grantedAuthorizations.equals(other.grantedAuthorizations))
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
        if (grantedAuthorizations != null)
        {
            builder.append("grantedAuthorizations=").append(grantedAuthorizations).append(", ");
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

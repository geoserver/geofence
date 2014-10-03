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
 * The Class GSInstance.
 */
public class GSInstance extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1367631391102175779L;

    /** The id. */
    private long id;

    /** The name. */
    private String name;

    /** The description. */
    private String description;

    /** The date creation. */
    private Date dateCreation;

    /** The base url. */
    private String baseURL;

    /** The path. */
    private String path;

    /** The username */
    private String username;

    /** The password */
    private String password;


    /**
    * Instantiates a new gS instance.
    */
    public GSInstance()
    {
        setPath("geofence/resources/images/instance.jpg");
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
     * Sets the description.
     *
     * @param description
     *            the new description
     */
    public void setDescription(String description)
    {
        this.description = description;
        set(BeanKeyValue.DESCRIPTION.getValue(), this.description);
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription()
    {
        return description;
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
        set(BeanKeyValue.DATE_CREATION.getValue(), this.dateCreation);
    }

    /**
     * Sets the base url.
     *
     * @param baseURL
     *            the new base url
     */
    public void setBaseURL(String baseURL)
    {
        this.baseURL = baseURL;
        set(BeanKeyValue.BASE_URL.getValue(), this.baseURL);
    }

    /**
     * Gets the base url.
     *
     * @return the base url
     */
    public String getBaseURL()
    {
        return baseURL;
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
     * Gets the path.
     *
     * @return the path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Get the username
     * @return
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Set the username
     * @param username
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * Get the password
     * @return
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Set the password
     * @param password
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((baseURL == null) ? 0 : baseURL.hashCode());
        result = (prime * result) + ((dateCreation == null) ? 0 : dateCreation.hashCode());
        result = (prime * result) + ((description == null) ? 0 : description.hashCode());
        result = (prime * result) + (int) (id ^ (id >>> 32));
        result = (prime * result) + ((name == null) ? 0 : name.hashCode());
        result = (prime * result) + ((baseURL == null) ? 0 : baseURL.hashCode());
        result = (prime * result) + ((path == null) ? 0 : path.hashCode());
        result = (prime * result) + ((username == null) ? 0 : username.hashCode());
        result = (prime * result) + ((password == null) ? 0 : password.hashCode());

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
        if (!(obj instanceof GSInstance))
        {
            return false;
        }

        GSInstance other = (GSInstance) obj;
        if (baseURL == null)
        {
            if (other.baseURL != null)
            {
                return false;
            }
        }
        else if (!baseURL.equals(other.baseURL))
        {
            return false;
        }
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
        if (description == null)
        {
            if (other.description != null)
            {
                return false;
            }
        }
        else if (!description.equals(other.description))
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
        if (username == null)
        {
            if (other.username != null)
            {
                return false;
            }
        }
        else if (!username.equals(other.username))
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
        builder.append("GSInstance [");
        if (baseURL != null)
        {
            builder.append("baseURL=").append(baseURL).append(", ");
        }
        if (dateCreation != null)
        {
            builder.append("dateCreation=").append(dateCreation).append(", ");
        }
        if (description != null)
        {
            builder.append("description=").append(description).append(", ");
        }
        builder.append("id=").append(id).append(", ");
        if (name != null)
        {
            builder.append("name=").append(name).append(", ");
        }
        if (username != null)
        {
            builder.append("username=").append(username).append(", ");
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

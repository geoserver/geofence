/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.model;


import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.IsSerializable;


// TODO: Auto-generated Javadoc
/**
 * The Class Rule.
 */
public class Rule extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5445560155635714470L;

    /** The id. */
    private long id;

    /** The priority. */
    private long priority;

    /** The user. */
    private GSUser user;

    /** The profile. */
    private UserGroup profile;

    /** The instance. */
    private GSInstance instance;

    private String sourceIPRange;

    /** The service. */
    private String service;

    /** The request. */
    private String request;

    /** The workspace. */
    private String workspace;

    /** The layer. */
    private String layer;

    /** The grant. */
    private String grant;

    /** The path. */
    private String path;

    /**
     * Instantiates a new rule.
     */
    public Rule()
    {
        setPath("geofence/resources/images/rule.jpg");
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
     * Gets the priority.
     *
     * @return the priority
     */
    public long getPriority()
    {
        return priority;
    }

    /**
     * Sets the priority.
     *
     * @param priority
     *            the new priority
     */
    public void setPriority(long priority)
    {
        this.priority = priority;
        set(BeanKeyValue.PRIORITY.getValue(), this.priority);
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public GSUser getUser()
    {
        return user;
    }

    /**
     * Sets the user.
     *
     * @param user
     *            the new user
     */
    public void setUser(GSUser user)
    {
        this.user = user;
        set(BeanKeyValue.USER.getValue(), this.user);
    }

    /**
     * Gets the profile.
     *
     * @return the profile
     */
    public UserGroup getProfile()
    {
        return profile;
    }

    /**
     * Sets the profile.
     *
     * @param profile
     *            the new profile
     */
    public void setProfile(UserGroup profile)
    {
        this.profile = profile;
        set(BeanKeyValue.PROFILE.getValue(), this.profile);
    }

    /**
     * Sets the instance.
     *
     * @param instance
     *            the new instance
     */
    public void setInstance(GSInstance instance)
    {
        this.instance = instance;
        set(BeanKeyValue.INSTANCE.getValue(), this.instance);
    }

    /**
     * Gets the single instance of Rule.
     *
     * @return single instance of Rule
     */
    public GSInstance getInstance()
    {
        return instance;
    }

    public String getSourceIPRange() {
        return sourceIPRange;
    }

    public void setSourceIPRange(String sourceIPRange) {
        this.sourceIPRange = sourceIPRange;
        set(BeanKeyValue.SOURCE_IP_RANGE.getValue(), this.sourceIPRange);
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
     * Gets the request.
     *
     * @return the request
     */
    public String getRequest()
    {
        return request;
    }

    /**
     * Sets the request.
     *
     * @param request
     *            the new request
     */
    public void setRequest(String request)
    {
        this.request = request;
        set(BeanKeyValue.REQUEST.getValue(), this.request);
    }

    /**
     * Gets the workspace.
     *
     * @return the workspace
     */
    public String getWorkspace()
    {
        return workspace;
    }

    /**
     * Sets the workspace.
     *
     * @param workspace
     *            the new workspace
     */
    public void setWorkspace(String workspace)
    {
        this.workspace = workspace;
        set(BeanKeyValue.WORKSPACE.getValue(), this.workspace);
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
     * Gets the grant.
     *
     * @return the grant
     */
    public String getGrant()
    {
        return grant;
    }

    /**
     * Sets the grant.
     *
     * @param grant
     *            the new grant
     */
    public void setGrant(String grant)
    {
        this.grant = grant;
        set(BeanKeyValue.GRANT.getValue(), this.grant);
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
        result = (prime * result) + ((grant == null) ? 0 : grant.hashCode());
        result = (prime * result) + (int) (id ^ (id >>> 32));
        result = (prime * result) + ((instance == null) ? 0 : instance.hashCode());
        result = (prime * result) + ((sourceIPRange == null) ? 0 : sourceIPRange.hashCode());
        result = (prime * result) + ((layer == null) ? 0 : layer.hashCode());
        result = (prime * result) + ((path == null) ? 0 : path.hashCode());
        result = (prime * result) + (int) (priority ^ (priority >>> 32));
        result = (prime * result) + ((profile == null) ? 0 : profile.hashCode());
        result = (prime * result) + ((request == null) ? 0 : request.hashCode());
        result = (prime * result) + ((service == null) ? 0 : service.hashCode());
        result = (prime * result) + ((user == null) ? 0 : user.hashCode());
        result = (prime * result) + ((workspace == null) ? 0 : workspace.hashCode());

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
        if (!(obj instanceof Rule)) {
            return false;
        }

        Rule other = (Rule) obj;
        if (grant == null) {
            if (other.grant != null) {
                return false;
            }
        } else if (!grant.equals(other.grant)) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (instance == null) {
            if (other.instance != null) {
                return false;
            }
        } else if (!instance.equals(other.instance)) {
            return false;
        }
        if (layer == null) {
            if (other.layer != null) {
                return false;
            }
        } else if (!layer.equals(other.layer)) {
            return false;
        }
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }
        if (priority != other.priority) {
            return false;
        }
        if (profile == null) {
            if (other.profile != null) {
                return false;
            }
        } else if (!profile.equals(other.profile)) {
            return false;
        }
        if (request == null) {
            if (other.request != null) {
                return false;
            }
        } else if (!request.equals(other.request)) {
            return false;
        }
        if (service == null) {
            if (other.service != null) {
                return false;
            }
        } else if (!service.equals(other.service)) {
            return false;
        }
        if (user == null) {
            if (other.user != null) {
                return false;
            }
        } else if (!user.equals(other.user)) {
            return false;
        }
        if (workspace == null) {
            if (other.workspace != null) {
                return false;
            }
        } else if (!workspace.equals(other.workspace)) {
            return false;
        }
        if (sourceIPRange == null) {
            if (other.sourceIPRange != null) {
                return false;
            }
        } else if (!sourceIPRange.equals(other.sourceIPRange)) {
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
        builder.append("Rule [");
        if (grant != null) {
            builder.append("grant=").append(grant).append(", ");
        }
        builder.append("id=").append(id).append(", ");
        if (instance != null) {
            builder.append("instance=").append(instance).append(", ");
        }
        if (sourceIPRange != null) {
            builder.append("src=").append(sourceIPRange).append(", ");
        }
        if (layer != null) {
            builder.append("layer=").append(layer).append(", ");
        }
        if (path != null) {
            builder.append("path=").append(path).append(", ");
        }
        builder.append("priority=").append(priority).append(", ");
        if (profile != null) {
            builder.append("profile=").append(profile).append(", ");
        }
        if (request != null) {
            builder.append("request=").append(request).append(", ");
        }
        if (service != null) {
            builder.append("service=").append(service).append(", ");
        }
        if (user != null) {
            builder.append("user=").append(user).append(", ");
        }
        if (workspace != null) {
            builder.append("workspace=").append(workspace);
        }
        builder.append("]");

        return builder.toString();
    }

}

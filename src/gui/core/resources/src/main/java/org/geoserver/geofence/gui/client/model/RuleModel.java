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
public class RuleModel extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5445560155635714470L;

    /** The id. */
    private long id;

    /** The priority. */
    private long priority;

    /** The user. */
    private String username;

    /** The profile. */
    private String rolename;

    /** The instance. */
    private GSInstanceModel instance;

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
    public RuleModel()
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
        set(BeanKeyValue.USERNAME.getValue(), this.username);
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
        set(BeanKeyValue.ROLENAME.getValue(), this.rolename);
    }

    /**
     * Sets the instance.
     *
     * @param instance
     *            the new instance
     */
    public void setInstance(GSInstanceModel instance)
    {
        this.instance = instance;
        set(BeanKeyValue.INSTANCE.getValue(), this.instance);
    }

    /**
     * Gets the single instance of Rule.
     *
     * @return single instance of Rule
     */
    public GSInstanceModel getInstance()
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
        result = (prime * result) + ((rolename == null) ? 0 : rolename.hashCode());
        result = (prime * result) + ((request == null) ? 0 : request.hashCode());
        result = (prime * result) + ((service == null) ? 0 : service.hashCode());
        result = (prime * result) + ((username == null) ? 0 : username.hashCode());
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
        if (!(obj instanceof RuleModel)) {
            return false;
        }

        RuleModel other = (RuleModel) obj;
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
        if (rolename == null) {
            if (other.rolename != null) {
                return false;
            }
        } else if (!rolename.equals(other.rolename)) {
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
        if (username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!username.equals(other.username)) {
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

        builder.append("id=").append(id);
        builder.append(", priority=").append(priority);

        if (username != null) {
            builder.append(", user=").append(username);
        }
        if (rolename != null) {
            builder.append(", role=").append(rolename);
        }
        if (instance != null) {
            builder.append(", instance=").append(instance.getName());
        }
        if (sourceIPRange != null) {
            builder.append(", ip=").append(sourceIPRange);
        }
        if (service != null) {
            builder.append(", service=").append(service);
        }
        if (request != null) {
            builder.append(", request=").append(request);
        }
        if (workspace != null) {
            builder.append(", workspace=").append(workspace);
        }
        if (layer != null) {
            builder.append(", layer=").append(layer);
        }
        if (grant != null) {
            builder.append(", grant=").append(grant);
        }
        builder.append("]");

        return builder.toString();
    }

}

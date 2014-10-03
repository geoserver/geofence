/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.model.data;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.IsSerializable;

import org.geoserver.geofence.gui.client.model.BeanKeyValue;


// TODO: Auto-generated Javadoc
/**
 * The Class Workspace.
 */
public class Workspace extends BeanModel implements IsSerializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4900649353701687980L;

    /** The workspace. */
    private String workspace;

    /** The path. */
    private String path;

    /**
     * Instantiates a new workspace.
     *
     * @param workspace
     *            the request
     */
    public Workspace(String workspace)
    {
        this();
        setWorkspace(workspace);
    }

    /**
     * Instantiates a new workspace.
     */
    public Workspace()
    {
        super();
        setPath("geofence/resources/images/workspace.jpg");
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

    /**
     * Gets the workspace.
     *
     * @return the workspace
     */
    public String getWorkspace()
    {
        return workspace;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((path == null) ? 0 : path.hashCode());
        result = (prime * result) + ((workspace == null) ? 0 : workspace.hashCode());

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
        if (!(obj instanceof Workspace))
        {
            return false;
        }

        Workspace other = (Workspace) obj;
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
        if (workspace == null)
        {
            if (other.workspace != null)
            {
                return false;
            }
        }
        else if (!workspace.equals(other.workspace))
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
        builder.append("Workspace [");
        if (path != null)
        {
            builder.append("path=").append(path).append(", ");
        }
        if (workspace != null)
        {
            builder.append("workspace=").append(workspace);
        }
        builder.append("]");

        return builder.toString();
    }

}

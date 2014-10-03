/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.configuration;

/**
 * Class WorkspaceConfigOpts.
 *
 * @author Tobia Di Pisa
 *
 */
public class WorkspaceConfigOpts
{

    private boolean showDefaultGroups = false;

    /**
     *
     */
    public WorkspaceConfigOpts()
    {
        super();
    }

    /**
     * @param showDefaultGroups
     */
    public WorkspaceConfigOpts(boolean showDefaultGroups)
    {
        super();
        this.showDefaultGroups = showDefaultGroups;
    }

    /**
     * @return the showDefaultGroups
     */
    public boolean isShowDefaultGroups()
    {
        return showDefaultGroups;
    }

    /**
     * @param showDefaultGroups
     *            the showDefaultGroups to set
     */
    public void setShowDefaultGroups(boolean showDefaultGroups)
    {
        this.showDefaultGroups = showDefaultGroups;
    }
}

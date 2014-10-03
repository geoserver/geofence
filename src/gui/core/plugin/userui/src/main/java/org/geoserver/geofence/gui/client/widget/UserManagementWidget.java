/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

import org.geoserver.geofence.gui.client.Constants;
import org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;


// TODO: Auto-generated Javadoc
/**
 * The Class UserManagementWidget.
 */
public class UserManagementWidget extends ContentPanel
{

    /** The users info. */
    private UserGridWidget usersInfo;

    /**
     * Instantiates a new user management widget.
     *
     * @param gsManagerServiceRemote
     *            the gs manager service remote
     * @param profilesManagerServiceRemote
     *            the profiles manager service remote
     */
    public UserManagementWidget(GsUsersManagerRemoteServiceAsync gsManagerServiceRemote,
        ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote)
    {
        setMonitorWindowResize(true);
        setHeaderVisible(false);
        setFrame(true);
        setLayout(new FitLayout());
        setScrollMode(Scroll.NONE);
        setAutoWidth(true);
        setHeight(Constants.SOUTH_PANEL_DIMENSION - 25);

        setUsersInfo(new UserGridWidget(gsManagerServiceRemote, profilesManagerServiceRemote));
        setBottomComponent(this.getUsersInfo().getToolBar());
        add(getUsersInfo().getGrid());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.widget.Component#onWindowResize(int, int)
     */
    @Override
    protected void onWindowResize(int width, int height)
    {
        // TODO Auto-generated method stub
        super.setWidth(width - 5);
        super.layout();
    }

    /**
     * Sets the users info.
     *
     * @param usersInfo
     *            the new users info
     */
    public void setUsersInfo(UserGridWidget usersInfo)
    {
        this.usersInfo = usersInfo;
    }

    /**
     * Gets the users info.
     *
     * @return the users info
     */
    public UserGridWidget getUsersInfo()
    {
        return usersInfo;
    }

}

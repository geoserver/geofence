/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

import org.geoserver.geofence.gui.client.Constants;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;


// TODO: Auto-generated Javadoc
/**
 * The Class ProfileManagementWidget.
 */
public class ProfileManagementWidget extends ContentPanel
{

    /** The profiles info. */
    private ProfileGridWidget profilesInfo;

    /**
     * Instantiates a new profile management widget.
     *
     * @param profilesManagerServiceRemote
     *            the profiles manager service remote
     */
    public ProfileManagementWidget(ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote)
    {
        setMonitorWindowResize(true);
        setHeaderVisible(false);
        setFrame(true);
        setLayout(new FitLayout());
        setScrollMode(Scroll.NONE);
        setAutoWidth(true);
        setHeight(Constants.SOUTH_PANEL_DIMENSION - 25);

        setProfilesInfo(new ProfileGridWidget(profilesManagerServiceRemote));
        add(getProfilesInfo().getGrid());
        setBottomComponent(this.getProfilesInfo().getToolBar());
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
     * Sets the profiles info.
     *
     * @param profilesInfo
     *            the new profiles info
     */
    public void setProfilesInfo(ProfileGridWidget profilesInfo)
    {
        this.profilesInfo = profilesInfo;
    }

    /**
     * Gets the profiles info.
     *
     * @return the profiles info
     */
    public ProfileGridWidget getProfilesInfo()
    {
        return profilesInfo;
    }

}

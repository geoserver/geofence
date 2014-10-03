/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.rule.detail;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

import org.geoserver.geofence.gui.client.model.UserGroup;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;


/**
 * The Class ProfileDetailsWidget.
 *
 * @author Tobia di Pisa
 *
 */
public class ProfileDetailsWidget extends ContentPanel
{

    /** The profile details info. */
    private ProfileDetailsGridWidget profileDetailsInfo;

    private UserGroup profile;

    /**
     * Instantiates a new layer custom props widget.
     * @param model
     *
     * @param rulesService
     *            the rules service
     */
    public ProfileDetailsWidget(UserGroup profile, ProfilesManagerRemoteServiceAsync profilesService)
    {
        this.profile = profile;

        setHeaderVisible(false);
        setFrame(true);
        setHeight(330);
        setLayout(new FitLayout());

        setProfileDetailsInfo(new ProfileDetailsGridWidget(this.profile, profilesService));

        add(getProfileDetailsInfo().getGrid());

        super.setMonitorWindowResize(true);

        setScrollMode(Scroll.NONE);

        setBottomComponent(this.getProfileDetailsInfo().getToolBar());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.widget.Component#onWindowResize(int, int)
     */
    @Override
    protected void onWindowResize(int width, int height)
    {
        super.setWidth(width - 5);
        super.layout();
    }

    /**
     * @return the profileDetailsInfo
     */
    public ProfileDetailsGridWidget getProfileDetailsInfo()
    {
        return profileDetailsInfo;
    }

    /**
     * @param profileDetailsInfo the profileDetailsInfo to set
     */
    public void setProfileDetailsInfo(ProfileDetailsGridWidget profileDetailsInfo)
    {
        this.profileDetailsInfo = profileDetailsInfo;
    }

}

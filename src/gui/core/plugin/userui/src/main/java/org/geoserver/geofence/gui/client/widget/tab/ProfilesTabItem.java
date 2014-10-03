/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.tab;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.TabItem;

import org.geoserver.geofence.gui.client.Constants;
import org.geoserver.geofence.gui.client.Resources;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.widget.ProfileManagementWidget;


// TODO: Auto-generated Javadoc
/**
 * The Class ProfilesTabItem.
 */
public class ProfilesTabItem extends TabItem
{

    /** The profile management widget. */
    private ProfileManagementWidget profileManagementWidget;

    /**
     * Instantiates a new profiles tab item.
     */
    public ProfilesTabItem(String tabItemId)
    {
        // TODO: add I18n message
        // super(I18nProvider.getMessages().profiles());
        super("Groups");
        setId(tabItemId);
        setIcon(Resources.ICONS.pageEdit());
    }

    /**
     * Instantiates a new profiles tab item.
     *
     * @param profilesTabItemId
     *
     * @param profilesManagerServiceRemote
     *            the profiles manager service remote
     */
    public ProfilesTabItem(String tabItemId,
        ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote)
    {
        this(tabItemId);
        setScrollMode(Scroll.NONE);
        setAutoWidth(true);
        setHeight(Constants.SOUTH_PANEL_DIMENSION - 25);

        setProfileManagementWidget(new ProfileManagementWidget(profilesManagerServiceRemote));
        add(getProfileManagementWidget());

        getProfileManagementWidget().getProfilesInfo().getLoader().load(0,
            org.geoserver.geofence.gui.client.Constants.DEFAULT_PAGESIZE);

    }

    /**
     * Sets the feature management widget.
     *
     * @param profileManagementWidget
     *            the new feature management widget
     */
    public void setProfileManagementWidget(ProfileManagementWidget profileManagementWidget)
    {
        this.profileManagementWidget = profileManagementWidget;
    }

    /**
     * Gets the feature management widget.
     *
     * @return the feature management widget
     */
    public ProfileManagementWidget getProfileManagementWidget()
    {
        return profileManagementWidget;
    }

}

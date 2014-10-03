/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.tab;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.TabItem;

import org.geoserver.geofence.gui.client.Constants;
import org.geoserver.geofence.gui.client.Resources;
import org.geoserver.geofence.gui.client.i18n.I18nProvider;
import org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.widget.UserManagementWidget;


// TODO: Auto-generated Javadoc
/**
 * The Class GsUsersTabItem.
 */
public class GsUsersTabItem extends TabItem
{

    /** The profile management widget. */
    private UserManagementWidget userManagementWidget;

    /**
     * Instantiates a new gs users tab item.
     */
    public GsUsersTabItem(String tabItemId)
    {
        super(I18nProvider.getMessages().userManagementLabel());
        setId(tabItemId);
        setIcon(Resources.ICONS.user());
    }

    /**
     * Instantiates a new gs users tab item.
     * @param usersTabItemId
     *
     * @param gsManagerServiceRemote
     *            the gs manager service remote
     * @param profilesManagerServiceRemote
     */
    public GsUsersTabItem(String tabItemId, GsUsersManagerRemoteServiceAsync gsManagerServiceRemote,
        ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote)
    {
        this(tabItemId);
        setScrollMode(Scroll.NONE);
        setAutoWidth(true);
        setHeight(Constants.SOUTH_PANEL_DIMENSION - 25);

        setUserManagementWidget(new UserManagementWidget(gsManagerServiceRemote,
                profilesManagerServiceRemote));
        add(getUserManagementWidget());

        getUserManagementWidget().getUsersInfo().getLoader().load(0, org.geoserver.geofence.gui.client.Constants.DEFAULT_PAGESIZE);
    }

    /**
     * Sets the feature management widget.
     *
     * @param userManagementWidget
     *            the new feature management widget
     */
    public void setUserManagementWidget(UserManagementWidget userManagementWidget)
    {
        this.userManagementWidget = userManagementWidget;
    }

    /**
     * Gets the feature management widget.
     *
     * @return the feature management widget
     */
    public UserManagementWidget getUserManagementWidget()
    {
        return userManagementWidget;
    }

}

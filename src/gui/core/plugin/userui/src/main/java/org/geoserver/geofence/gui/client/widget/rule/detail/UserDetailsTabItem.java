/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.rule.detail;

import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.Resources;
import org.geoserver.geofence.gui.client.model.GSUser;
import org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.TabItem;


/**
 * The Class UserDetailsTabItem.
 */
public class UserDetailsTabItem extends TabItem
{

    /** The user details widget. */
    private UserDetailsWidget userDetailsWidget;

    /** The user. */
    private GSUser user;

    /**
     * Instantiates a new user tab item.
     *
     * @param tabItemId
     *            the tab item id
     */
    private UserDetailsTabItem(String tabItemId)
    {
        // TODO: add I18n message
        // super(I18nProvider.getMessages().profiles());
        super("User Groups");
        setId(tabItemId);
        setIcon(Resources.ICONS.addAOI());
    }

    /**
     * Instantiates a new rule details tab item.
     *
     * @param tabItemId
     *            the tab item id
     * @param model
     *            the model
     * @param profilesManagerServiceRemote 
     * @param workspacesService
     *            the workspaces service
     */
    public UserDetailsTabItem(String tabItemId, GSUser model, GsUsersManagerRemoteServiceAsync usersService, ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote)
    {
        this(tabItemId);
        this.user = model;

        setUserDetailsWidget(new UserDetailsWidget(this.user, usersService, profilesManagerServiceRemote));
        add(getUserDetailsWidget());

        setScrollMode(Scroll.NONE);

        this.addListener(Events.Select, new Listener<BaseEvent>()
            {

                public void handleEvent(BaseEvent be)
                {
                    if (userDetailsWidget.getUserDetailsInfo().getModel() == null)
                    {
                        Dispatcher.forwardEvent(GeofenceEvents.LOAD_USER_LIMITS, user);
                    }
                    
                    if (userDetailsWidget.getProfilesInfo().getStore().getCount() < 1)
                    {
                    	userDetailsWidget.getProfilesInfo().getLoader().load();
                    }
                }

            });

    }

    /**
     * Sets the user details widget.
     *
     * @param userDetailsWidget
     *            the new user details widget
     */
    public void setUserDetailsWidget(UserDetailsWidget userDetailsWidget)
    {
        this.userDetailsWidget = userDetailsWidget;
    }

    /**
     * Gets the user limits widget.
     *
     * @return the user limits widget
     */
    public UserDetailsWidget getUserDetailsWidget()
    {
        return userDetailsWidget;
    }

}

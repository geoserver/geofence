/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.view;

import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.widget.AddGsUserWidget;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;


// TODO: Auto-generated Javadoc
/**
 * The Class UsersView.
 */
public class UsersView extends View
{

    /** The gs manager service remote. */
    private GsUsersManagerRemoteServiceAsync gsManagerServiceRemote =
        GsUsersManagerRemoteServiceAsync.Util.getInstance();

    /** The profiles manager service remote. */
    private ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote =
        ProfilesManagerRemoteServiceAsync.Util.getInstance();

    private AddGsUserWidget addGsUser;

    /**
     * Instantiates a new users view.
     *
     * @param controller
     *            the controller
     */
    public UsersView(Controller controller)
    {
        super(controller);

        this.addGsUser = new AddGsUserWidget(GeofenceEvents.SAVE_USER, true,gsManagerServiceRemote,profilesManagerServiceRemote);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.mvc.View#handleEvent(com.extjs.gxt.ui.client.mvc.AppEvent)
     */
    @Override
    protected void handleEvent(AppEvent event)
    {
        if (event.getType() == GeofenceEvents.CREATE_NEW_USER)
        {
            onCreateNewUser(event);
        }

    }

    /**
     * On create new profile.
     *
     * @param event
     *            the event
     */
    private void onCreateNewUser(AppEvent event)
    {
        this.addGsUser.show();
    }
}

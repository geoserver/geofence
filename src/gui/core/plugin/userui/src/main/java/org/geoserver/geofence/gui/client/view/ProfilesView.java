/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.view;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;

import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.widget.AddProfileWidget;


// TODO: Auto-generated Javadoc
/**
 * The Class UsersView.
 */
public class ProfilesView extends View
{

    /** The profiles manager service remote. */
    private ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote =
        ProfilesManagerRemoteServiceAsync.Util.getInstance();

    private AddProfileWidget addProfile;

    /**
     * Instantiates a new users view.
     *
     * @param controller
     *            the controller
     */
    public ProfilesView(Controller controller)
    {
        super(controller);

        this.addProfile = new AddProfileWidget(GeofenceEvents.SAVE_PROFILE, true);
        this.addProfile.setProfileService(profilesManagerServiceRemote);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.mvc.View#handleEvent(com.extjs.gxt.ui.client.mvc.AppEvent)
     */
    @Override
    protected void handleEvent(AppEvent event)
    {
        if (event.getType() == GeofenceEvents.CREATE_NEW_PROFILE)
        {
            onCreateNewProfile(event);
        }

    }

    /**
     * On create new profile.
     *
     * @param event
     *            the event
     */
    private void onCreateNewProfile(AppEvent event)
    {
        this.addProfile.show();
    }

}

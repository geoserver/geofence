/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.mvc;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;

import org.geoserver.geofence.gui.client.GeofenceEvents;


// TODO: Auto-generated Javadoc
/**
 * The Class AppController.
 */
public class AppController extends Controller
{

    /** The app view. */
    private AppView appView;

    /**
     * Instantiates a new app controller.
     */
    public AppController()
    {
        registerEventTypes(
            GeofenceEvents.INIT_GEOFENCE_MAIN_UI,
            GeofenceEvents.SESSION_EXPIRED,
            GeofenceEvents.SAVE);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.mvc.Controller#initialize()
     */
    @Override
    public void initialize()
    {
        appView = new AppView(this);
    }

    /**
     * On error.
     *
     * @param ae
     *            the ae
     */
    protected void onError(AppEvent ae)
    {
        System.out.println("error: " + ae.<Object>getData());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.mvc.Controller#handleEvent(com.extjs.gxt.ui.client
     * .mvc.AppEvent)
     */
    @Override
    public void handleEvent(AppEvent event)
    {
        if (event.getType() == GeofenceEvents.SESSION_EXPIRED)
        {
            appView.reload();
        }

        if (event.getType() == GeofenceEvents.SAVE)
        {
            onSaveContext();
        }

        forwardToView(appView, event);
    }

    /**
     * On save context.
     */
    private void onSaveContext()
    {
        // TODO: this logic should probably not occur in the Controller, and should not
        // have to assume names of controls, etc. Some Controller/View reorganization is probably
        // necessary,
        // possibly a View/Controller for each admin mode
//        switch (this.currentAdminMode) {
//        case NOTIFICATION_DISTRIBUTION:
//            saveWatch();
//            break;
//        case GEOCONSTRAINTS:
//            saveGeoConstraint();
//            break;
//        case MEMBER:
//            saveMemberNodeAssignment();
//            break;
//        }
    }

}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client;

import org.geoserver.geofence.gui.client.GeofenceEvents;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;

import org.geoserver.geofence.gui.client.controller.InstanceController;
import org.geoserver.geofence.gui.client.controller.LoginController;
import org.geoserver.geofence.gui.client.controller.ProfilesController;
import org.geoserver.geofence.gui.client.controller.RulesController;
import org.geoserver.geofence.gui.client.controller.UsersController;


// TODO: Auto-generated Javadoc
/**
 * The Class UserUI.
 */
public class UserUI implements EntryPoint
{

    /** The dispatcher. */
    private Dispatcher dispatcher;

    /*
     * (non-Javadoc)
     *
     * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
     */
    public void onModuleLoad()
    {
        dispatcher = Dispatcher.get();
        dispatcher.addController(new LoginController());
        dispatcher.addController(new UsersController());
        dispatcher.addController(new ProfilesController());
        dispatcher.addController(new InstanceController());
        dispatcher.addController(new RulesController());
        dispatcher.dispatch(GeofenceEvents.INIT_USER_UI_MODULE);
    }
}

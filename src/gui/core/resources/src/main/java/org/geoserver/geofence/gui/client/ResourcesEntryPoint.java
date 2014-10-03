/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client;

import org.geoserver.geofence.gui.client.mvc.MessagesController;

import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;

// TODO: Auto-generated Javadoc
/**
 * The Class ResourcesEntryPoint.
 */
public class ResourcesEntryPoint implements EntryPoint {

    /** The dispatcher. */
    private Dispatcher dispatcher;

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
     */
    public void onModuleLoad() {
        // TODO Auto-generated method stub
        dispatcher = Dispatcher.get();

        dispatcher.addController(new MessagesController());

        dispatcher.dispatch(GeofenceEvents.INIT_RESOURCES_MODULE);
    }
}

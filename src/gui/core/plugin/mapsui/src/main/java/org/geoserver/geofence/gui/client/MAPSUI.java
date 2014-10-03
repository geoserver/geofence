/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client;

import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;

import org.geoserver.geofence.gui.client.mvc.MAPSController;
import org.geoserver.geofence.gui.client.mvc.MapController;


// TODO: Auto-generated Javadoc
/**
 * The Class MAPSUI.
 */
public class MAPSUI implements EntryPoint
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

        dispatcher.addController(new MapController());
        dispatcher.addController(new MAPSController());
        // dispatcher.addController(new FeatureController());
//        dispatcher.addController(new FilterController());

        dispatcher.dispatch(GeofenceEvents.INIT_MAPS_UI_MODULE);

    }

}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.mvc;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;

import org.geoserver.geofence.gui.client.GeofenceEvents;
import it.geosolutions.geogwt.gui.client.GeoGWTEvents;


// TODO: Auto-generated Javadoc
/**
 * The Class MapController.
 */
public class MapController extends Controller
{

    /** The map view. */
    private MapView mapView;

    /**
     * Instantiates a new map controller.
     */
    public MapController()
    {
        registerEventTypes(
            GeofenceEvents.INIT_MAPS_UI_MODULE,
            GeofenceEvents.ATTACH_MAP_WIDGET,
            GeofenceEvents.UPDATE_MAP_SIZE,
            GeofenceEvents.ATTACH_TOOLBAR,
            GeofenceEvents.ACTIVATE_DRAW_FEATURES,
            GeofenceEvents.DEACTIVATE_DRAW_FEATURES,
            GeofenceEvents.ERASE_AOI_FEATURES,
            GeofenceEvents.ENABLE_DRAW_BUTTON,
            GeofenceEvents.DISABLE_DRAW_BUTTON,
            GeofenceEvents.DRAW_AOI_ON_MAP,
            GeofenceEvents.ZOOM_TO_CENTER,
            GeofenceEvents.ADMIN_MODE_CHANGE,
            GeofenceEvents.LOGIN_SUCCESS,
            GeoGWTEvents.INJECT_WKT);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.mvc.Controller#initialize()
     */
    @Override
    public void initialize()
    {
        this.mapView = new MapView(this);
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
        forwardToView(mapView, event);

    }

}

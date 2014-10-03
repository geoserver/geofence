/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.mvc;

import org.geoserver.geofence.gui.client.GeofenceEvents;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;

// TODO: Auto-generated Javadoc
/**
 * The Class MessagesController.
 */
public class MessagesController extends Controller {

    /** The messages view. */
    private MessagesView messagesView;

    /**
     * Instantiates a new messages controller.
     */
    public MessagesController() {
        registerEventTypes(GeofenceEvents.INIT_RESOURCES_MODULE, GeofenceEvents.SEND_ALERT_MESSAGE,
                GeofenceEvents.SEND_ERROR_MESSAGE, GeofenceEvents.SEND_INFO_MESSAGE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.extjs.gxt.ui.client.mvc.Controller#initialize()
     */
    @Override
    protected void initialize() {
        // TODO Auto-generated method stub
        this.messagesView = new MessagesView(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.extjs.gxt.ui.client.mvc.Controller#handleEvent(com.extjs.gxt.ui.client
     * .mvc.AppEvent)
     */
    @Override
    public void handleEvent(AppEvent event) {
        // TODO Auto-generated method stub
        forwardToView(messagesView, event);
    }
}

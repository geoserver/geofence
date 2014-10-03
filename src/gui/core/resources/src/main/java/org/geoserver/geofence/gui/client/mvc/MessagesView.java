/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.mvc;

import org.geoserver.geofence.gui.client.GeofenceEvents;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;

// TODO: Auto-generated Javadoc
/**
 * The Class MessagesView.
 */
public class MessagesView extends View {

    /**
     * Instantiates a new messages view.
     * 
     * @param controller
     *            the controller
     */
    public MessagesView(Controller controller) {
        super(controller);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.extjs.gxt.ui.client.mvc.View#handleEvent(com.extjs.gxt.ui.client. mvc.AppEvent)
     */
    @Override
    protected void handleEvent(AppEvent event) {
        // TODO Auto-generated method stub
        if (event.getType() == GeofenceEvents.SEND_ALERT_MESSAGE) {
            onSendAlertMessage(event);
            return;
        }

        if (event.getType() == GeofenceEvents.SEND_INFO_MESSAGE) {
            onSendInfoMessage(event);
            return;
        }

        if (event.getType() == GeofenceEvents.SEND_ERROR_MESSAGE)
            onSendErrorMessage(event);

    }

    /**
     * On send error message.
     * 
     * @param event
     *            the event
     */
    private void onSendErrorMessage(AppEvent event) {
        String[] message = (String[]) event.getData();
        MessageBox box = new MessageBox();
        box.setIcon(MessageBox.ERROR);
        box.setTitle(message[0]);
        box.setMessage(message[1]);
        box.show();
    }

    /**
     * On send info message.
     * 
     * @param event
     *            the event
     */
    private void onSendInfoMessage(AppEvent event) {
        String[] message = (String[]) event.getData();
        Info.display(message[0], message[1]);
    }

    /**
     * On send alert message.
     * 
     * @param event
     *            the event
     */
    private void onSendAlertMessage(AppEvent event) {
        String[] message = (String[]) event.getData();
        MessageBox.alert(message[0], message[1], new Listener<MessageBoxEvent>() {

            public void handleEvent(MessageBoxEvent be) {
                // TODO Auto-generated method stub

            }
        });
    }

}

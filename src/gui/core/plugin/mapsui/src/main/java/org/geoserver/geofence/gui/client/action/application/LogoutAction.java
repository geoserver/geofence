/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.action.application;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;

import it.geosolutions.geogwt.gui.client.widget.map.action.ToolbarMapAction;
import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.Resources;
import org.geoserver.geofence.gui.client.i18n.I18nProvider;


// TODO: Auto-generated Javadoc
/**
 * The Class LogoutAction.
 */
public class LogoutAction extends ToolbarMapAction
{

    /**
     *
     */
    private static final long serialVersionUID = -8343538202482412376L;

    /**
     * Instantiates a new logout action.
     */
    public LogoutAction()
    {
        super();
    }

    @Override
    public boolean initialize()
    {
        if (!isInitialized())
        {
            setTooltip(I18nProvider.getMessages().logoutTooltip());
            setIcon(Resources.ICONS.logout());
            this.initialiazed = true;
        }

        return isInitialized();
    }

    @Override
    public void performAction(Button button)
    {
        MessageBox.confirm(I18nProvider.getMessages().logoutDialogTitle(), I18nProvider.getMessages().logoutDialogMessage(), new Listener<MessageBoxEvent>()
            {

                public void handleEvent(MessageBoxEvent be)
                {
                    Button btn = be.getButtonClicked();
                    if (btn.getText().equalsIgnoreCase("YES"))
                    {
                        Dispatcher.forwardEvent(GeofenceEvents.LOGOUT);
                    }
                }
            });
    }
}

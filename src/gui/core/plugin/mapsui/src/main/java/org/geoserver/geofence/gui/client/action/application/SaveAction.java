/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.action.application;

import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.button.Button;

import it.geosolutions.geogwt.gui.client.widget.map.action.ToolbarMapAction;
import org.geoserver.geofence.gui.client.GeofenceEvents;


// TODO: Auto-generated Javadoc
/**
 * The Class SaveAction.
 */
public class SaveAction extends ToolbarMapAction
{

    /**
     *
     */
    private static final long serialVersionUID = 3947820148787724972L;

    /**
     * Instantiates a new save action.
     */
    public SaveAction()
    {
        super();
    }

    @Override
    public boolean initialize()
    {
        // TODO Auto-generated method stub

        // I18nProvider.getMessages().saveToolTip()

        return false;
    }

    @Override
    public void performAction(Button button)
    {
        Dispatcher.forwardEvent(GeofenceEvents.SAVE);
    }
}

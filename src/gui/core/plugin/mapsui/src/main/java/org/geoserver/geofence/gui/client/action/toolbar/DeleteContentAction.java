/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.action.toolbar;

import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.button.Button;

import it.geosolutions.geogwt.gui.client.widget.map.action.ToolbarMapAction;
import org.geoserver.geofence.gui.client.GeofenceEvents;


// TODO: Auto-generated Javadoc
/**
 * The Class DeleteContentAction.
 */
public class DeleteContentAction extends ToolbarMapAction
{

    /**
     *
     */
    private static final long serialVersionUID = 3253090668327732004L;

    /**
     * Instantiates a new delete content action.
     */
    public DeleteContentAction()
    {
        super();
    }

    @Override
    public boolean initialize()
    {
        // TODO Auto-generated method stub

        // I18nProvider.getMessages().deleteContentToolTip();

        return false;
    }

    @Override
    public void performAction(Button button)
    {
        Dispatcher.forwardEvent(GeofenceEvents.DELETE_CONTENT);
    }
}

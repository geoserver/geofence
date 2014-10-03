/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.configuration;

import java.util.Collections;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class MenuClientTool.
 */
public class MenuClientTool extends GenericClientTool {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8189124026216386133L;

    /** The enabled. */
    private boolean enabled;

    /** The action tools. */
    private List<ActionClientTool> actionTools;

    /**
     * Checks if is the enabled.
     * 
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled.
     * 
     * @param enabled
     *            the new enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the action tools.
     * 
     * @return the action tools
     */
    public List<ActionClientTool> getActionTools() {
        return actionTools;
    }

    /**
     * Sets the action tools.
     * 
     * @param actionTools
     *            the new action tools
     */
    public void setActionTools(List<ActionClientTool> actionTools) {
        Collections.sort(actionTools);
        this.actionTools = actionTools;
    }

}

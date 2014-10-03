/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.configuration;

// TODO: Auto-generated Javadoc
/**
 * The Class ActionClientTool.
 */
public class ActionClientTool extends GenericClientTool {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7373117917871895237L;

    /** The type. */
    private String type;

    /** The enabled. */
    private boolean enabled;

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     * 
     * @param type
     *            the new type
     */
    public void setType(String type) {
        this.type = type;
    }

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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ActionClientTool [enabled=" + enabled + ", getId()=" + getId() + ", getOrder()="
                + getOrder() + ", getType()=" + getType() + "]";
    }

}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.configuration;

// TODO: Auto-generated Javadoc
/**
 * The Enum ConfigurationMainUI.
 */
public enum ConfigurationMainUI {

    /** The CENTER. */
    CENTER("CENTER_PANEL"), 
 
 /** The EAST. */
    EAST("EAST_PANEL"), 
 
 /** The SOUTH. */
    SOUTH("SOUTH_PANEL"), 
 
 /** The VIEWPORT. */
    VIEWPORT("VIEWPORT");

    /** The value. */
    private String value;

    /**
     * Instantiates a new configuration main ui.
     * 
     * @param value
     *            the value
     */
    ConfigurationMainUI(String value) {
        this.value = value;
    }

    /**
     * Gets the value.
     * 
     * @return the value
     */
    public String getValue() {
        return value;
    }

}

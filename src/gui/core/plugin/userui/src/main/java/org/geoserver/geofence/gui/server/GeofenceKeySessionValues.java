/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server;

// TODO: Auto-generated Javadoc
/**
 * The Enum GeofenceKeySessionValues.
 */
public enum GeofenceKeySessionValues
{

    /** The USE r_ logge d_ token. */
    USER_LOGGED_TOKEN("userLoggedToken");

    /** The value. */
    private String value;

    /**
     * Instantiates a new geo repo key session values.
     *
     * @param value
     *            the value
     */
    GeofenceKeySessionValues(String value)
    {
        this.value = value;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue()
    {
        return value;
    }

}

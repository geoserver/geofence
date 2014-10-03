/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client;

import org.geoserver.geofence.gui.client.configuration.IGeofenceConfiguration;

// TODO: Auto-generated Javadoc
/**
 * The Class GeofenceUtils.
 */
public class GeofenceUtils {

    /** The INSTANCE. */
    private static GeofenceUtils INSTANCE;

    /** The global configuration. */
    private IGeofenceConfiguration globalConfiguration;

    /**
     * Gets the single instance of GeofenceUtils.
     * 
     * @return single instance of GeofenceUtils
     */
    public static GeofenceUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GeofenceUtils();
        }
        return INSTANCE;
    }

    /**
     * Gets the global configuration.
     * 
     * @return the global configuration
     */
    public IGeofenceConfiguration getGlobalConfiguration() {
        return globalConfiguration;
    }

    /**
     * Sets the global configuration.
     * 
     * @param globalConfiguration
     *            the new global configuration
     */
    public void setGlobalConfiguration(IGeofenceConfiguration globalConfiguration) {
        this.globalConfiguration = globalConfiguration;
    }

}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.service;

import org.geoserver.geofence.gui.client.configuration.GeofenceGlobalConfiguration;


// TODO: Auto-generated Javadoc
/**
 * The Interface IStartupService.
 */
public interface IStartupService
{

    /**
     * Inits the server configuration.
     *
     * @return the geo repo global configuration
     */
    public GeofenceGlobalConfiguration initServerConfiguration();

}

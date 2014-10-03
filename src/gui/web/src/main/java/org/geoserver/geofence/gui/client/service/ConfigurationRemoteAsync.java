/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.geoserver.geofence.gui.client.configuration.GeofenceGlobalConfiguration;


// TODO: Auto-generated Javadoc
/**
 * The Interface ConfigurationRemoteAsync.
 */
public interface ConfigurationRemoteAsync
{

    /**
     * Inits the server configuration.
     *
     * @param callback
     *            the callback
     */
    public void initServerConfiguration(AsyncCallback<GeofenceGlobalConfiguration> callback);

}

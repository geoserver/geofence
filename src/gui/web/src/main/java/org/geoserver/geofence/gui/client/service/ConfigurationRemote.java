/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import org.geoserver.geofence.gui.client.configuration.GeofenceGlobalConfiguration;


// TODO: Auto-generated Javadoc
/**
 * The Interface ConfigurationRemote.
 */
public interface ConfigurationRemote extends RemoteService
{

    /**
     * Inits the server configuration.
     *
     * @return the geo repo global configuration
     */
    public GeofenceGlobalConfiguration initServerConfiguration();

    /**
     * The Class Util.
     */
    public static class Util
    {

        /** The instance. */
        private static ConfigurationRemoteAsync instance;

        /**
         * Gets the instance.
         *
         * @return the instance
         */
        public static ConfigurationRemoteAsync getInstance()
        {
            if (instance == null)
            {
                instance = (ConfigurationRemoteAsync) GWT.create(ConfigurationRemote.class);

                ServiceDefTarget target = (ServiceDefTarget) instance;
                target.setServiceEntryPoint(GWT.getModuleBaseURL() + "ConfigurationRemote");
            }

            return instance;
        }
    }

}

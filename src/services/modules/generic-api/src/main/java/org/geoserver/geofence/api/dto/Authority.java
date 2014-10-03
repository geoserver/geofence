/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.api.dto;

/**
 * @author etj
 */
public enum Authority {
    /**
     * Authorization to log into the application
     */
    LOGIN

    /**
     * Authorization to perform remote calls
     */
    , REMOTE

    ;
}

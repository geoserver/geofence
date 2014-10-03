/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Used as a catchall when forwarding exceptions
 *
 * @author ETj (etj at geo-solutions.it)
 */
public abstract class GeoFenceRestEx extends WebApplicationException {

    private String message;

    public GeoFenceRestEx(String message, Response response) {
        super(response);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

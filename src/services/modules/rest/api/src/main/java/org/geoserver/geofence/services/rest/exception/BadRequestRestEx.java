/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class BadRequestRestEx extends GeoFenceRestEx {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -2585698525010604674L;

    public BadRequestRestEx(String message) {
        super(message, Response.status(Status.BAD_REQUEST).type("text/plain").entity(message).build());
    }
}

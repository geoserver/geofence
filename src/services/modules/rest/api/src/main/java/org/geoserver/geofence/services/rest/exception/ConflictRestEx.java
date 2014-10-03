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
public class ConflictRestEx extends GeoFenceRestEx {

    public ConflictRestEx(String message) {
        super(message, Response.status(Status.CONFLICT).type("text/plain").entity(message).build());
    }
}

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
public class InternalErrorRestEx extends GeoFenceRestEx {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 9014519381293787498L;

    public InternalErrorRestEx(String message) {
        super(message, Response.status(Status.INTERNAL_SERVER_ERROR).type("text/plain").entity(message).build());
    }
}

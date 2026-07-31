/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.exception;

/** @author ETj (etj at geo-solutions.it) */
public class NotFoundRestEx extends GeoFenceRestEx {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1263563388095079971L;

    public NotFoundRestEx(String message) {
        super(message, 404);
    }
}

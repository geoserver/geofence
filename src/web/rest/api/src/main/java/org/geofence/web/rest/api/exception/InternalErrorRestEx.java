/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.exception;

/** @author ETj (etj at geo-solutions.it) */
public class InternalErrorRestEx extends GeoFenceRestEx {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 9014519381293787498L;

    public InternalErrorRestEx(String message) {
        super(message, 500);
    }

    public InternalErrorRestEx(String message, Throwable cause) {
        super(message, 500, cause);
    }
}

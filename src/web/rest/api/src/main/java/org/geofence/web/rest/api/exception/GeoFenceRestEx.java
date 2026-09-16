/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.exception;

/**
 * Used as a catchall when forwarding exceptions. Carries a plain numeric HTTP status instead of depending on any
 * particular REST framework's response type, so this module (and its exception hierarchy) has no server-framework
 * coupling - the actual HTTP response is built from {@link #getStatus()}/{@link #getMessage()} wherever the request is
 * actually dispatched.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public abstract class GeoFenceRestEx extends RuntimeException {

    private final int status;

    protected GeoFenceRestEx(String message, int status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    protected GeoFenceRestEx(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.services.exception;

/** @author ETj (etj at geo-solutions.it) */
public class InternalErrorServiceEx extends WebApplicationException {
    private static final long serialVersionUID = 190485042398L;

    private String message;

    public InternalErrorServiceEx(String message) {
        super(Response.Status.BAD_REQUEST);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

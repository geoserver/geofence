/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.api.exception;

import javax.xml.ws.WebFault;

/**
 * 
 * @author ETj (etj at geo-solutions.it)
 */
@WebFault(name = "AuthFault", faultBean = "org.geoserver.geofence.login.exception.AuthException")
public class AuthException extends RuntimeException {

    public AuthException(Throwable cause) {
        super(cause);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthException(String message) {
        super(message);
    }

    public AuthException() {
    }

}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.login;

import org.geoserver.geofence.api.dto.GrantedAuths;
import org.geoserver.geofence.api.exception.AuthException;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * 
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@WebService(name = "LoginService", targetNamespace = "http://www.geo-solutions.it/org.geoserver.geofence.login")
public interface LoginService {

    @WebResult(name = "token")
    String login(@WebParam(name = "username") String username,
            @WebParam(name = "password") String password, String pwFromDb) throws AuthException;

    GrantedAuths getGrantedAuthorities(@WebParam(name = "token") String token);

    void logout(@WebParam(name = "token") String token);
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.api;

import org.geoserver.geofence.api.dto.GrantedAuths;
import org.geoserver.geofence.api.exception.AuthException;

/**
 * This interface should be provided by classes that bridges toward and external auth source. <BR>
 * Let's say we have and external LDAP service, we may want to forward login requests to it.
 * 
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface AuthProvider {
    GrantedAuths login(String username, String password, String pwFromDB) throws AuthException;

    void logout(String token);
}

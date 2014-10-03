/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.login.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.geoserver.geofence.api.AuthProvider;
import org.geoserver.geofence.api.dto.Authority;
import org.geoserver.geofence.api.dto.GrantedAuths;
import org.geoserver.geofence.api.exception.AuthException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * A dummy AuthProvider which grants all auths to every request.
 *
 * TODO: this class used to provide a GrantAll grant, but since long an MD5 check
 * was implemented here. Naming should be fixed
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GrantAll implements AuthProvider {

    private static final Logger LOGGER = LogManager.getLogger(GrantAll.class);

    @Override
    public GrantedAuths login(String username, String password, String pwFromDb) throws AuthException {
        // allow auth to anybody
        LOGGER.warn("Login request from '" + username + "'");

        GrantedAuths ga = new GrantedAuths();
        String hashedPw = MD5Util.getHash(password);

//        LOGGER.info("storedPassword: " + pwFromDb);
        LOGGER.info("hashedPw: " + hashedPw);

        if (hashedPw.equals(pwFromDb)) {
            ga.setAuthorities(Arrays.asList(Authority.values()));
        } else {
            ga.setAuthorities(Arrays.asList(Authority.REMOTE));
        }

        return ga;
    }

    @Override
    public void logout(String token) {
        // nothing to do
    }


}

/* (c) 2014, 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.login.util;

import org.geoserver.geofence.api.exception.AuthException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class MD5Util {
    private static final Logger LOGGER = LogManager.getLogger(MD5Util.class);

    public static String getHash(String password) throws AuthException {
        byte[] passwordByteArr;
        try {
            passwordByteArr = password.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            throw new AuthException(e.getLocalizedMessage(), e);
        }
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.reset();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            throw new AuthException(e.getLocalizedMessage(), e);
        }
        byte[] thedigest = md.digest(passwordByteArr);
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < thedigest.length; i++) {
            String hexByte = Integer.toHexString(0xFF & thedigest[i]);
            if(hexByte.length()==1) {
                hexString.append('0');
            }
            hexString.append(hexByte);
        }
        return hexString.toString();
    }
}

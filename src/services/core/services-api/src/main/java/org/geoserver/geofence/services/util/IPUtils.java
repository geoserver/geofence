/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.util;

import java.util.regex.Pattern;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class IPUtils {

    private static final String IPV4_ADDRESS = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})";
    private static final String SLASH_FORMAT = IPV4_ADDRESS + "/(\\d{1,3})";
    private static final Pattern cidrPattern = Pattern.compile(SLASH_FORMAT);
    private static final Pattern ipv4Pattern = Pattern.compile(IPV4_ADDRESS);

    public static boolean isAddressValid(String ipAddress) {
        if(ipAddress == null) {
            return false;
        }

        return ipv4Pattern.matcher(ipAddress).matches();
    }

    public static boolean isRangeValid(String ipAddressRange) {
        if(ipAddressRange == null) {
            return false;
        }

        return cidrPattern.matcher(ipAddressRange).matches();
    }


}

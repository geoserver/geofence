/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.ldap.utils;

import org.geoserver.geofence.ldap.LdapAttributesMapper;

import org.springframework.ldap.core.AttributesMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 */
public class LdapUtils
{
    private static Logger LOGGER = LogManager.getLogger(LdapUtils.class);

    public static String createLDAPFilterEqual(String propertyName, String value, AttributesMapper mapper)
    {
        if (mapper instanceof LdapAttributesMapper) {
            propertyName = ((LdapAttributesMapper) mapper)
                    .getLdapAttribute(propertyName);
        }
        return propertyName + "=" + value;
    }
}

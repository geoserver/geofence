/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.ldap.utils;

import com.googlecode.genericdao.search.Filter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.geoserver.geofence.ldap.LdapAttributesMapper;
import org.springframework.ldap.core.AttributesMapper;

/** @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it" */
public class LdapUtils {
    private static Logger LOGGER = LogManager.getLogger(LdapUtils.class);

    /**
     * Creates and LDAP filter from the DAO search filter. Currently only "property = value" filters
     * are supported.
     *
     * @param filter
     * @return
     */
    public static String createLDAPFilter(Filter filter, AttributesMapper mapper) {
        // TODO add other filter types
        if (filter.getOperator() == Filter.OP_EQUAL) {
            String propertyName = filter.getProperty();
            if (mapper instanceof LdapAttributesMapper) {
                propertyName = ((LdapAttributesMapper) mapper).getLdapAttribute(propertyName);
            }
            return propertyName + "=" + filter.getValue().toString();
        } else {
            LOGGER.error("MISSING IMPLEMENTATION FOR " + filter);
        }
        return null;
    }
}

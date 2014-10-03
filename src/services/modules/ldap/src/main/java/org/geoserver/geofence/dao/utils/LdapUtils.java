/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.dao.utils;

import org.geoserver.geofence.dao.ldap.LdapAttributesMapper;

import java.util.List;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;

import com.googlecode.genericdao.search.Filter;

/**
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 *
 */
public class LdapUtils {
	/**
	 * Creates and LDAP filter from the DAO search filter.
	 * Currently only "property = value" filters are supported.
	 * 
	 * @param filter
	 * @return
	 */
	public static String createLdapFilter(Filter filter, AttributesMapper mapper) {
		// TODO add other filter types
		if(filter.getOperator() == Filter.OP_EQUAL) {
			String propertyName = filter.getProperty();
			if(mapper instanceof LdapAttributesMapper) {
				propertyName = ((LdapAttributesMapper) mapper)
						.getLdapAttribute(propertyName);
			}
			return propertyName + "=" + filter.getValue().toString();
		}		
		return null;
	}

	/**
	 * @param searchBase
	 * @param filter
	 * @param attributesMapper
	 * @return
	 */
	public static <T> List<T> search(LdapTemplate ldapTemplate, String searchBase, String filter,
			AttributesMapper attributesMapper) {
		return ldapTemplate.search(searchBase, filter, attributesMapper);
	}
	
	/**
	 * @param searchBase
	 * @param filter
	 * @param attributesMapper
	 * @return
	 */
	public static <T> List<T> search(LdapTemplate ldapTemplate, String searchBase, Filter filter,
			AttributesMapper attributesMapper) {
		return search(ldapTemplate, searchBase, LdapUtils.createLdapFilter(filter, attributesMapper), attributesMapper);
	}
}

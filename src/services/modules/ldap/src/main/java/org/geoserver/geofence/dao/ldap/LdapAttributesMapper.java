/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.dao.ldap;

import org.springframework.ldap.core.AttributesMapper;

/**
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 * 
 */
public interface LdapAttributesMapper extends AttributesMapper {
	/**
	 * Maps a DAO attribute to the LDAP one.
	 * 
	 * @param attributeName
	 * @return
	 */
	public String getLdapAttribute(String attributeName);
}

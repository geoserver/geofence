/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.ldap.dao.impl;

import org.geoserver.geofence.core.model.GSUser;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
/**
 * AttributeMapper for GSUser objects.
 * 
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 *
 */
public class GSUserAttributesMapper extends BaseAttributesMapper {

	

	@Override
	public Object mapFromAttributes(Attributes attrs) throws NamingException {
		GSUser user = new GSUser();
		user.setId(Long.parseLong(getAttribute(attrs, "id")));
		user.setExtId(-user.getId()+"");
		user.setName(getAttribute(attrs, "username"));
		user.setEmailAddress(getAttribute(attrs, "email"));
		user.setEnabled(true);
		user.setFullName(getAttribute(attrs, "name") + " "
				+ getAttribute(attrs, "surname"));
		user.setPassword(getAttribute(attrs, "password"));
				
		return user;
	}
	
	

}

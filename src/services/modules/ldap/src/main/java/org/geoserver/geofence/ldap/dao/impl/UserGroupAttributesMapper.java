/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.ldap.dao.impl;

import org.geoserver.geofence.core.model.UserGroup;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

/**
 * AttributeMapper for UserGroup objects.
 * 
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 *
 */
public class UserGroupAttributesMapper extends BaseAttributesMapper {

	

	@Override
	public Object mapFromAttributes(Attributes attrs) throws NamingException {
		UserGroup group = new UserGroup();
		group.setId(Long.parseLong(getAttribute(attrs, "id")));
		group.setExtId(-group.getId()+"");
		group.setName(getAttribute(attrs, "groupname"));		
		group.setEnabled(true);		
				
		return group;
	}
	
	

}

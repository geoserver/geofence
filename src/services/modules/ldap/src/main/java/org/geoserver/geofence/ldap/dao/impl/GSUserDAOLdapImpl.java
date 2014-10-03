/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.ldap.dao.impl;

import org.geoserver.geofence.core.dao.GSUserDAO;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.UserGroup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.ldap.core.AttributesMapper;

import com.googlecode.genericdao.search.Filter;


/**
 * GSUserDAO implementation, using an LDAP server as a primary source, and the original
 * JPA based DAO as a backup.
 *  
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 *
 */
public class GSUserDAOLdapImpl extends BaseDAO<GSUserDAO,GSUser> implements GSUserDAO {
		
	private String groupsBase = "ou=Groups";
	
	private AttributesMapper groupsAttributesMapper;	
	String userDn = "uid=%s,ou=People";	
	
	
	/**
	 * 
	 */
	public GSUserDAOLdapImpl() {
		super();
		// set default search base and filter for users
		setSearchBase("ou=People");
		setSearchFilter("objectClass=inetOrgPerson");
	}

	/**
	 * Sets the base name for groups in LDAP server.
	 * Used to extract groups bounded to the user.
	 * 
	 * @param groupsBase the groupsBase to set
	 */
	public void setGroupsBase(String groupsBase) {
		this.groupsBase = groupsBase;
	}

	/**
	 * Sets the userDn template, to quickly locate a user into an LDAP server,
	 * by its distinguished name.
	 * It's a template, filled with the user name (use %s as a placeholder for that=.
	 *  
	 * @param userDn the userDn to set
	 */
	public void setUserDn(String userDn) {
		this.userDn = userDn;
	}
	

	/**
	 * Sets the AttributeMapper used to build UserGroup objects from LDAP
	 * objects.
	 * 
	 * @param groupsAttributesMapper the groupsAttributesMapper to set
	 */
	public void setGroupsAttributesMapper(AttributesMapper groupsAttributesMapper) {
		this.groupsAttributesMapper = groupsAttributesMapper;
	}

	

	@Override
	public Set<UserGroup> getGroups(Long id) {		
		GSUser user = find(id);
		fillWithGroups(user);
		return user.getGroups();		
	}
	
	/**
	 * Gets the list of user groups from the LDAP server for the given user.
	 * 
	 * @param user
	 * @return
	 */
	private Set<UserGroup> getGroups(GSUser user) {	
		Set<UserGroup> groups = new HashSet<UserGroup>();
		Filter filter = new Filter("member", user.getName());
		List<UserGroup> groupsList = search(groupsBase, filter, groupsAttributesMapper);				
		groups.addAll(groupsList);
		return groups;
	}


	@Override
	public GSUser getFull(Long id) {			
		GSUser user = find(id);
		if(user != null) {
			return fillWithGroups(user);
		}
		return null;
	}

	@Override
	public GSUser getFull(String name) {		
		return fillWithGroups(lookup(String.format(userDn, name)));
	}

	

	/**
	 * Updates the groups list for the given user.
	 * 
	 * @param gsUser
	 * @return
	 */
	private GSUser fillWithGroups(GSUser user) {		
		user.setGroups(getGroups(user));
		return user;
	}	
}

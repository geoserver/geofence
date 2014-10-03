/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.ldap.dao.impl;

import org.geoserver.geofence.core.dao.GSUserDAO;
import org.geoserver.geofence.core.dao.UserGroupDAO;
import org.geoserver.geofence.core.dao.impl.RuleDAOImpl;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.core.model.UserGroup;

/**
 * Implementation of RuleDAO compatible with ldap user and group daos.
 * 
 * It persists user and groups to db when a rule is bound to a new user or group.
 * 
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 *
 */
public class RuleDAOLdapImpl extends RuleDAOImpl {

	private GSUserDAO userDao;
	
	private UserGroupDAO userGroupDao;
	
	/**
	 * Original JPA GSUserDAO.
	 * Used to persist new users from ldap to db.
	 * 
	 * @param userDao the userDao to set
	 */
	public void setUserDao(GSUserDAO userDao) {
		this.userDao = userDao;
	}



	/**
	 * Original JPA UserGroupDAO.
	 * Used to persist new groups from ldap to db.
	 * 
	 * @param userGroupDao the userGroupDao to set
	 */
	public void setUserGroupDao(UserGroupDAO userGroupDao) {
		this.userGroupDao = userGroupDao;
	}

	

	@Override
	public void persist(Rule... entities) {
		for(Rule rule : entities) {
			checkUserAndGroup(rule);
		}
		super.persist(entities);
	}



	/**
	 * Checks a rule, to identify users or groups not persisted on db.
	 * If any is found, they are persisted before the rule, to avoid
	 * referential integrity issues.
	 * 
	 * @param rule
	 */
	private void checkUserAndGroup(Rule entity) {		
		if(notPersistedUser(entity)) {		
			// create a new persistable user, persist it and update the rule
			GSUser user = copyUser(entity.getGsuser());
			userDao.persist(user);			
			entity.setGsuser(user);			
		}
		if(notPersistedGroup(entity)) {
			// create a new persistable group, persist it and update the rule			 
			UserGroup group = copyGroup(entity.getUserGroup());
			userGroupDao.persist(group);
			entity.setUserGroup(group);
		}
	}



	/**
	 * Checks if the rule has a group defined, and if it is persisted.
	 * 
	 * @param rule
	 * @return
	 */
	private boolean notPersistedGroup(Rule rule) {
		return rule.getUserGroup() != null && userGroupDao.find(rule.getUserGroup().getId()) == null;
	}



	/**
	 * Checks if the rule has a user defined, and if it is persisted.
	 * 
	 * @param rule
	 * @return
	 */
	private boolean notPersistedUser(Rule rule) {
		return rule.getGsuser() != null && userDao.find(rule.getGsuser().getId()) == null;
	}



	@Override
	public Rule merge(Rule entity) {		
		checkUserAndGroup(entity);
		return super.merge(entity);
		
	}
	
	/**
	 * Creates a persistable copy of the given user.
	 * 
     * @param user   
     */
    private GSUser copyUser(GSUser user)
    {    	
        GSUser newUser = new GSUser();
    	newUser.setName(user.getName());
    	newUser.setFullName(user.getFullName());
    	newUser.setEmailAddress(user.getEmailAddress());
    	newUser.setEnabled(true);
    	newUser.setAdmin(user.isAdmin());
    	newUser.setPassword(user.getPassword());
    	// set external id to negative ldap id, so that it's easily identifiable in
    	// searches
    	newUser.setExtId(-user.getId()+"");
    	newUser.setDateCreation(user.getDateCreation());
    	return newUser;
    }
    
    /**
     * Creates a persistable copy of the given group.
     * 
     * @param user 
     */
    private UserGroup copyGroup(UserGroup group)
    {    	
    	UserGroup newGroup = new UserGroup();
    	newGroup.setName(group.getName());
    	newGroup.setExtId(-group.getId()+"");    	
    	newGroup.setEnabled(true);   
    	return newGroup;
    }
	
}

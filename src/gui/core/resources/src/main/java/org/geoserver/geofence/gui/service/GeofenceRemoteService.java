/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.service;

import org.geoserver.geofence.api.UserRegistry;
import org.geoserver.geofence.login.LoginService;
import org.geoserver.geofence.services.GFUserAdminService;
import org.geoserver.geofence.services.InstanceAdminService;
import org.geoserver.geofence.services.UserGroupAdminService;
import org.geoserver.geofence.services.RuleAdminService;
import org.geoserver.geofence.services.UserAdminService;

// TODO: Auto-generated Javadoc
/**
 * The Class GeofenceRemoteService.
 */
public class GeofenceRemoteService {

    /** The login service. */
    private LoginService loginService;

    /** The user admin service. */
    private UserAdminService userAdminService;
    
    /** The Geofence login service */
    private GFUserAdminService gfUserAdminService;

    /** The user provider. */
    private UserRegistry userProvider;

    /** The profile admin service. */
    private UserGroupAdminService userGroupAdminService;
    
    /** The instance admin service. */
    private InstanceAdminService instanceAdminService;
    
    /** The rule admin service. */
    private RuleAdminService ruleAdminService;
    
    /**
     * Gets the login service.
     * 
     * @return the login service
     */
    public LoginService getLoginService() {
        return loginService;
    }

    /**
     * Sets the login service.
     * 
     * @param loginService
     *            the new login service
     */
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * Gets the user admin service.
     * 
     * @return the user admin service
     */
    public UserAdminService getUserAdminService() {
        return userAdminService;
    }

    /**
     * Sets the user admin service.
     * 
     * @param userAdminService
     *            the new user admin service
     */
    public void setUserAdminService(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    /**
	 * @param grUserAdminService the grUserAdminService to set
	 */
	public void setGfUserAdminService(GFUserAdminService grUserAdminService) {
		this.gfUserAdminService = grUserAdminService;
	}

	/**
	 * @return the grUserAdminService
	 */
	public GFUserAdminService getGfUserAdminService() {
		return gfUserAdminService;
	}

	/**
     * Gets the user provider.
     * 
     * @return the user provider
     */
    public UserRegistry getUserProvider() {
        return userProvider;
    }

    /**
     * Sets the user provider.
     * 
     * @param userProvider
     *            the new user provider
     */
    public void setUserProvider(UserRegistry userProvider) {
        this.userProvider = userProvider;
    }

    /**
     * Sets the profile admin service.
     * 
     * @param profileAdminService
     *            the new profile admin service
     */
    public void setProfileAdminService(UserGroupAdminService profileAdminService) {
        this.userGroupAdminService = profileAdminService;
    }

    /**
     * Sets the profile admin service.
     * 
     * @return the profile admin service
     */

    public void setUserGroupAdminService(UserGroupAdminService userGroupAdminService) {
        this.userGroupAdminService = userGroupAdminService;
    }

    /**
     * Gets the profile admin service.
     * @return 
     * 
     * @return the profile admin service
     */

    public UserGroupAdminService getUserGroupAdminService() {
        return this.userGroupAdminService;
    }

    /**
     * Sets the instance admin service.
     * 
     * @param instanceAdminService
     *            the new instance admin service
     */
    public void setInstanceAdminService(InstanceAdminService instanceAdminService) {
        this.instanceAdminService = instanceAdminService;
    }

    /**
     * Gets the instance admin service.
     * 
     * @return the instance admin service
     */
    public InstanceAdminService getInstanceAdminService() {
        return instanceAdminService;
    }

    /**
     * Sets the rule admin service.
     * 
     * @param ruleAdminService
     *            the new rule admin service
     */
    public void setRuleAdminService(RuleAdminService ruleAdminService) {
        this.ruleAdminService = ruleAdminService;
    }

    /**
     * Gets the rule admin service.
     * 
     * @return the rule admin service
     */
    public RuleAdminService getRuleAdminService() {
        return ruleAdminService;
    }

}

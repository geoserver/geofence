/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.service;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.GSUserModel;
import org.geoserver.geofence.gui.client.model.UsernameModel;
import org.geoserver.geofence.gui.client.model.data.UserLimitsInfoModel;


/**
 * The Interface GsUsersManagerRemoteService.
 */
@RemoteServiceRelativePath("GsUsersManagerRemoteService")
public interface GsUsersManagerRemoteService extends RemoteService
{

    /**
     * Gets the gs users.
     *
     * @param config
     *            the config
     * @param full
     *            the full
     * @return the gs users
     * @throws ApplicationException
     *             the application exception
     */
    public PagingLoadResult<GSUserModel> getGsUsers(int offset, int limit, boolean full) throws ApplicationException;
    public PagingLoadResult<UsernameModel> getGsUsernames(int offset, int limit, boolean full) throws ApplicationException;

    /**
     * Save gs profile.
     *
     * @param profile
     *            the profile
     * @throws ApplicationException
     *             the application exception
     */
    public void saveGsUser(GSUserModel user) throws ApplicationException;

    /**
     * Delete gs profile.
     *
     * @param profile
     *            the profile
     * @throws ApplicationException
     *             the application exception
     */
    public void deleteGsUser(GSUserModel user) throws ApplicationException;

    /**
     * @param user
     * @return UserLimitInfo
     * @throws ApplicationException
     */
    public UserLimitsInfoModel getUserLimitsInfo(GSUserModel user) throws ApplicationException;

    /**
     * @param user
     * @return UserLimitInfo
     * @throws ApplicationException
     */
    public UserLimitsInfoModel saveUserLimitsInfo(UserLimitsInfoModel userLimitInfo) throws ApplicationException;
	
	/**
     * This service returns to the client the information about the need for load the users and group management tabs
     * 
     * @return true if the tab must be loaded, false otherwise
     * @throws ApplicationException
     */
    public boolean activateUserGroupTabs() throws ApplicationException;

}

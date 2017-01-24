/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.service;

import com.extjs.gxt.ui.client.data.PagingLoadResult;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.GSUserModel;
import org.geoserver.geofence.gui.client.model.UsernameModel;


/**
 * The Interface IGsUsersManagerService.
 */
public interface IGsUsersManagerService
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
     * Save profile.
     *
     * @param profile
     *            the profile
     * @throws ApplicationException
     *             the application exception
     */
    public void saveUser(GSUserModel user) throws ApplicationException;

    /**
     * Delete profile.
     *
     * @param profile
     *            the profile
     */
    public void deleteUser(GSUserModel user);
	
    /**
     * @return
     * @throws ApplicationException
     */
    public boolean activateUserGroupTabs() throws ApplicationException;
}

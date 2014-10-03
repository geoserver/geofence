/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.service;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.GSUser;
import org.geoserver.geofence.gui.client.model.data.UserLimitsInfo;


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
    public PagingLoadResult<GSUser> getGsUsers(int offset, int limit, boolean full) throws ApplicationException;

    /**
     * Save gs profile.
     *
     * @param profile
     *            the profile
     * @throws ApplicationException
     *             the application exception
     */
    public void saveGsUser(GSUser user) throws ApplicationException;

    /**
     * Delete gs profile.
     *
     * @param profile
     *            the profile
     * @throws ApplicationException
     *             the application exception
     */
    public void deleteGsUser(GSUser user) throws ApplicationException;

    /**
     * @param user
     * @return UserLimitInfo
     * @throws ApplicationException
     */
    public UserLimitsInfo getUserLimitsInfo(GSUser user) throws ApplicationException;

    /**
     * @param user
     * @return UserLimitInfo
     * @throws ApplicationException
     */
    public UserLimitsInfo saveUserLimitsInfo(UserLimitsInfo userLimitInfo) throws ApplicationException;

}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.service;

import javax.servlet.http.HttpSession;

import org.geoserver.geofence.gui.client.model.User;


// TODO: Auto-generated Javadoc
/**
 * The Interface ILoginService.
 */
public interface ILoginService
{

    /**
     * Authenticate.
     *
     * @param userName
     *            the profile name
     * @param password
     *            the password
     * @param session
     *            the session
     * @return the profile
     */
    public User authenticate(String userName, String password, HttpSession session);

    // public PagingLoadResult<User> loadUsers(PagingLoadConfig config,
    // String searchText) throws ApplicationException;
    //
    // // public PagingLoadResult<User> loadAllUsers(PagingLoadConfig config)
    // // throws ApplicationException;
    //
    // public User saveUser(User profile) throws ApplicationException;
    //
    // public void deleteUser(Long userId) throws ApplicationException;
    //
    // public User updateUser(User profile);
    //
    // public User getUserDetail(User profile) throws ApplicationException;
    //
    // public List<RegUser> findUserNames(HttpSession session)
    // throws ApplicationException;
    //
    // public PagingLoadResult<User> getRelatedUsers(PagingLoadConfig config,
    // long aoiID) throws ApplicationException;
    //
    // public int getRelatedUsersCount(long aoiId);
}

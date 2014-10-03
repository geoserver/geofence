/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.service;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.User;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * The Interface LoginRemote.
 */
@RemoteServiceRelativePath("LoginRemoteService")
public interface LoginRemoteService extends RemoteService
{

    /**
     * Authenticate.
     *
     * @param userName
     *            the profile name
     * @param password
     *            the password
     * @return the profile
     * @throws ApplicationException
     *             the application exception
     */
    public User authenticate(String userName, String password) throws ApplicationException;

    /**
     * Logout.
     */
    public void logout();

    public Boolean isAuthenticated();

}

/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.service;

import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadResult;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.RolenameModel;
import org.geoserver.geofence.gui.client.model.UserGroupModel;


// TODO: Auto-generated Javadoc
/**
 * The Interface IProfilesManagerService.
 */
public interface IProfilesManagerService
{

    /**
     * Gets the profiles.
     *
     * @param config
     *            the config
     * @param full
     *            the full
     * @return the profiles
     * @throws ApplicationException
     *             the application exception
     */
    public PagingLoadResult<UserGroupModel> getProfiles(int offset, int limit, boolean full) throws ApplicationException;
    public PagingLoadResult<RolenameModel> getRolenames(int offset, int limit, boolean full) throws ApplicationException;

    /**
     * Delete profile.
     *
     * @param profile
     *            the profile
     */
    public void deleteProfile(UserGroupModel profile);

    /**
     * Save profile.
     *
     * @param profile
     *            the profile
     */
    public void saveProfile(UserGroupModel profile);

}

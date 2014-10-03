/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.service;

import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadResult;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.UserGroup;
import org.geoserver.geofence.gui.client.model.data.ProfileCustomProps;


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
    public PagingLoadResult<UserGroup> getProfiles(int offset, int limit, boolean full) throws ApplicationException;

    /**
     * Delete profile.
     *
     * @param profile
     *            the profile
     */
    public void deleteProfile(UserGroup profile);

    /**
     * Save profile.
     *
     * @param profile
     *            the profile
     */
    public void saveProfile(UserGroup profile);

    /**
     * @param config
     * @param rule
     * @return
     */
    public PagingLoadResult<ProfileCustomProps> getProfileCustomProps(int offset, int limit, UserGroup profile);

    /**
     * @param ruleId
     * @param customProps
     */
    public void setProfileProps(Long profileId, List<ProfileCustomProps> customProps);
}

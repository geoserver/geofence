/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.gwt;

import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.UserGroup;
import org.geoserver.geofence.gui.client.model.data.ProfileCustomProps;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteService;
import org.geoserver.geofence.gui.server.service.IProfilesManagerService;
import org.geoserver.geofence.gui.spring.ApplicationContextUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: Auto-generated Javadoc
/**
 * The Class ProfilesManagerServiceImpl.
 */
public class ProfilesManagerServiceImpl extends RemoteServiceServlet implements ProfilesManagerRemoteService
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1466494799053878981L;

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** The profiles manager service. */
    private IProfilesManagerService profilesManagerService;

    /**
     * Instantiates a new profiles manager service impl.
     */
    public ProfilesManagerServiceImpl()
    {
        this.profilesManagerService = (IProfilesManagerService) ApplicationContextUtil.getInstance().getBean(
                "profilesManagerServiceGWT");
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteService#getProfiles(com.extjs.gxt.ui.client.data.PagingLoadConfig)
     */
    public PagingLoadResult<UserGroup> getProfiles(int offset, int limit, boolean full) throws ApplicationException
    {
        return profilesManagerService.getProfiles(offset, limit, full);
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteService#deleteProfile(org.geoserver.geofence.gui.client.model.Profile)
     */
    public void deleteProfile(UserGroup profile) throws ApplicationException
    {
        profilesManagerService.deleteProfile(profile);
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteService#saveProfile(org.geoserver.geofence.gui.client.model.Profile)
     */
    public void saveProfile(UserGroup profile) throws ApplicationException
    {
        profilesManagerService.saveProfile(profile);
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteService#getProfileCustomProps(com.extjs.gxt.ui.client.data.PagingLoadConfig, org.geoserver.geofence.gui.client.model.Rule)
     */
    public PagingLoadResult<ProfileCustomProps> getProfileCustomProps(int offset, int limit,
        UserGroup profile) throws ApplicationException
    {
        return profilesManagerService.getProfileCustomProps(offset, limit, profile);
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteService#setProfileProps(java.lang.Long, java.util.List)
     */
    public void setProfileProps(Long profileId, List<ProfileCustomProps> customProps) throws ApplicationException
    {
        profilesManagerService.setProfileProps(profileId, customProps);
    }

}

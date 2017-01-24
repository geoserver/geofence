/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.gwt;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.RolenameModel;
import org.geoserver.geofence.gui.client.model.UserGroupModel;
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
    public PagingLoadResult<UserGroupModel> getProfiles(int offset, int limit, boolean full) throws ApplicationException
    {
        return profilesManagerService.getProfiles(offset, limit, full);
    }

    public PagingLoadResult<RolenameModel> getRolenames(int offset, int limit, boolean full) throws ApplicationException
    {
        return profilesManagerService.getRolenames(offset, limit, full);
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteService#deleteProfile(org.geoserver.geofence.gui.client.model.Profile)
     */
    public void deleteProfile(UserGroupModel profile) throws ApplicationException
    {
        profilesManagerService.deleteProfile(profile);
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteService#saveProfile(org.geoserver.geofence.gui.client.model.Profile)
     */
    public void saveProfile(UserGroupModel profile) throws ApplicationException
    {
        profilesManagerService.saveProfile(profile);
    }

}

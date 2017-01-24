/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.service.impl;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.UserGroupModel;
import org.geoserver.geofence.gui.client.model.data.rpc.RpcPageLoadResult;
import org.geoserver.geofence.gui.server.service.IProfilesManagerService;
import org.geoserver.geofence.gui.service.GeofenceRemoteService;
import org.geoserver.geofence.services.dto.ShortGroup;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.gui.client.model.RolenameModel;


// TODO: Auto-generated Javadoc
/**
 * The Class ProfilesManagerServiceImpl.
 */
@Component("profilesManagerServiceGWT")
public class ProfilesManagerServiceImpl implements IProfilesManagerService
{

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** The geofence remote service. */
    @Autowired
    private GeofenceRemoteService geofenceRemoteService;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.server.service.IFeatureService#loadFeature(com.extjs.gxt.ui.
     * client.data.PagingLoadConfig, java.lang.String)
     */
    public PagingLoadResult<UserGroupModel> getProfiles(int offset, int limit, boolean full) throws ApplicationException
    {

        int start = offset;

        List<UserGroupModel> profileListDTO = new ArrayList<UserGroupModel>();

        if (full)
        {
            UserGroupModel all_profile = new UserGroupModel();
            all_profile.setId(-1l);
            all_profile.setName("*");
            all_profile.setEnabled(true);
            all_profile.setDateCreation(null);
            profileListDTO.add(all_profile);
        }

        long profilesCount = geofenceRemoteService.getUserGroupAdminService().getCount(null) + 1;

        Long t = new Long(profilesCount);

        int page = (start == 0) ? start : (start / limit);

        List<ShortGroup> profilesList = geofenceRemoteService.getUserGroupAdminService().getList(
                null, page, limit);

        if (profilesList == null)
        {
            if (logger.isErrorEnabled())
            {
                logger.error("No profile found on server");
            }
            throw new ApplicationException("No profile found on server");
        }

        Iterator<ShortGroup> it = profilesList.iterator();

        while (it.hasNext())
        {
            ShortGroup short_profile = it.next();

            UserGroupModel local_profile = new UserGroupModel();

            local_profile.setId(short_profile.getId());
            local_profile.setName(short_profile.getName());
            local_profile.setEnabled(short_profile.isEnabled());

            profileListDTO.add(local_profile);
        }

        return new RpcPageLoadResult<UserGroupModel>(profileListDTO, offset, t.intValue());
    }

    public PagingLoadResult<RolenameModel> getRolenames(int offset, int limit, boolean full) throws ApplicationException
    {

        int start = offset;

        List<RolenameModel> returnList = new ArrayList<RolenameModel>();

        if (full)
        {
            RolenameModel all_profile = new RolenameModel();
            all_profile.setRolename("*");
            returnList.add(all_profile);
        }

        long profilesCount = geofenceRemoteService.getUserGroupAdminService().getCount(null) + 1;

        Long t = new Long(profilesCount);

        int page = (start == 0) ? start : (start / limit);

        List<ShortGroup> profilesList = geofenceRemoteService.getUserGroupAdminService().getList(
                null, page, limit);

        if (profilesList == null)
        {
            if (logger.isErrorEnabled())
            {
                logger.error("No profile found on server");
            }
            throw new ApplicationException("No profile found on server");
        }

        Iterator<ShortGroup> it = profilesList.iterator();

        while (it.hasNext())
        {
            ShortGroup role = it.next();
            RolenameModel local_profile = new RolenameModel(role.getName());
            returnList.add(local_profile);
        }

        return new RpcPageLoadResult<RolenameModel>(returnList, offset, t.intValue());
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.server.service.IProfilesManagerService#deleteProfile(org.geoserver.geofence.gui.client.model.UserGroup)
     */
    public void deleteProfile(UserGroupModel profile)
    {
        org.geoserver.geofence.core.model.UserGroup remote_profile = null;
        try
        {
            remote_profile = geofenceRemoteService.getUserGroupAdminService().get(profile.getId());
            geofenceRemoteService.getUserGroupAdminService().delete(remote_profile.getId());
        }
        catch (NotFoundServiceEx e)
        {
            logger.error(e.getLocalizedMessage(), e.getCause());
            throw new ApplicationException(e.getLocalizedMessage(), e.getCause());
        }
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.server.service.IProfilesManagerService#saveProfile(org.geoserver.geofence.gui.client.model.UserGroup)
     */
    public void saveProfile(UserGroupModel profile)
    {
        org.geoserver.geofence.core.model.UserGroup remote_profile = null;
        if (profile.getId() >= 0)
        {
            try
            {
                remote_profile = geofenceRemoteService.getUserGroupAdminService().get(profile.getId());

                ShortGroup short_profile = new ShortGroup();
                short_profile.setId(remote_profile.getId());
                short_profile.setName(profile.getName());
                short_profile.setEnabled(profile.isEnabled());
                geofenceRemoteService.getUserGroupAdminService().update(short_profile);
            }
            catch (NotFoundServiceEx e)
            {
                logger.error(e.getLocalizedMessage(), e.getCause());
                throw new ApplicationException(e.getLocalizedMessage(), e.getCause());
            }
        }
        else
        {
            try
            {
                ShortGroup short_profile = new ShortGroup();
                short_profile.setName(profile.getName());
                short_profile.setEnabled(profile.isEnabled());
                geofenceRemoteService.getUserGroupAdminService().insert(short_profile);
            }
            catch (Exception e)
            {
                logger.error(e.getLocalizedMessage(), e.getCause());
                throw new ApplicationException(e.getLocalizedMessage(), e.getCause());
            }
        }
    }

}

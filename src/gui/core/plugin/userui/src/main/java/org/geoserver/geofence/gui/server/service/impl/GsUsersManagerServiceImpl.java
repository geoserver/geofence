/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.service.impl;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.GSUser;
import org.geoserver.geofence.gui.client.model.UserGroup;
import org.geoserver.geofence.gui.client.model.data.UserLimitsInfo;
import org.geoserver.geofence.gui.client.model.data.rpc.RpcPageLoadResult;
import org.geoserver.geofence.gui.server.service.IGsUsersManagerService;
import org.geoserver.geofence.gui.service.GeofenceRemoteService;
import org.geoserver.geofence.services.dto.ShortUser;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extjs.gxt.ui.client.data.PagingLoadResult;

/**
 * The Class GsUsersManagerServiceImpl.
 */
@Component("gsUsersManagerServiceGWT")
public class GsUsersManagerServiceImpl implements IGsUsersManagerService
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
    public PagingLoadResult<GSUser> getGsUsers(int offset, int limit, boolean full) throws ApplicationException
    {
        int start = offset;

        List<GSUser> usersListDTO = new ArrayList<GSUser>();

        if (full)
        {
            GSUser all_user = new GSUser();
            all_user.setId(-1);
            all_user.setName("*");
            all_user.setFullName("*");
            all_user.setEnabled(true);
            all_user.setAdmin(false);
            all_user.setEmailAddress(null);
            all_user.setDateCreation(null);
            usersListDTO.add(all_user);
        }

        long usersCount = geofenceRemoteService.getUserAdminService().getCount(null) + 1;

        Long t = new Long(usersCount);

        int page = (start == 0) ? start : (start / limit);

        List<ShortUser> usersList = geofenceRemoteService.getUserAdminService().getList(null, page, limit);

        if (usersList == null)
        {
            if (logger.isErrorEnabled())
            {
                logger.error("No user found on server");
            }
            throw new ApplicationException("No user found on server");
        }

        for (ShortUser short_usr : usersList)
        {
            org.geoserver.geofence.core.model.GSUser remote_user;
            try
            {
                remote_user = geofenceRemoteService.getUserAdminService().getFull(short_usr.getId());
            }
            catch (NotFoundServiceEx e)
            {
                if (logger.isErrorEnabled())
                {
                    logger.error("Details for profile " + short_usr.getName() +
                        " not found on Server!");
                }
                throw new ApplicationException(e);
            }

            GSUser local_user = new GSUser();
            local_user.setId(short_usr.getId());
            local_user.setName(remote_user.getName());
            local_user.setFullName(remote_user.getFullName());
            local_user.setEnabled(remote_user.getEnabled());
            local_user.setAdmin(remote_user.isAdmin());
            local_user.setEmailAddress(remote_user.getEmailAddress());
            local_user.setDateCreation(remote_user.getDateCreation());            
            local_user.setPassword(remote_user.getPassword());

            /*logger.error("TODO: profile refactoring!!!");*/
            //org.geoserver.geofence.core.model.UserGroup remote_profile = remote_user.getGroups().iterator().next();
            for(org.geoserver.geofence.core.model.UserGroup remote_profile : remote_user.getGroups())
            {
            	UserGroup local_group = new UserGroup();
            	local_group.setId(remote_profile.getId());
            	local_group.setName(remote_profile.getName());
            	local_group.setDateCreation(remote_profile.getDateCreation());
            	local_group.setEnabled(remote_profile.getEnabled());
            	local_user.getUserGroups().add(local_group);
            }

            usersListDTO.add(local_user);
        }

        return new RpcPageLoadResult<GSUser>(usersListDTO, offset, t.intValue());
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.server.service.IGsUsersManagerService#saveUser(org.geoserver.geofence.gui.client.model.GSUser)
     */
    public void saveUser(GSUser user) throws ApplicationException
    {
        org.geoserver.geofence.core.model.GSUser remote_user = null;
        if (user.getId() >= 0)
        {
            try
            {
                remote_user = geofenceRemoteService.getUserAdminService().get(user.getId());
                copyUser(user, remote_user);
                geofenceRemoteService.getUserAdminService().update(remote_user);
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
                remote_user = new org.geoserver.geofence.core.model.GSUser();
                copyUser(user, remote_user);
                geofenceRemoteService.getUserAdminService().insert(remote_user);
            }
            catch (NotFoundServiceEx e)
            {
                logger.error(e.getLocalizedMessage(), e.getCause());
                throw new ApplicationException(e.getLocalizedMessage(), e.getCause());
            }
        }

    }

    /**
     * @param profile
     * @param remote_user
     * @throws ResourceNotFoundFault
     */
    private void copyUser(GSUser user, org.geoserver.geofence.core.model.GSUser remote_user) throws NotFoundServiceEx
    {
        remote_user.setName(user.getName());
        remote_user.setFullName(user.getFullName());
        remote_user.setEmailAddress(user.getEmailAddress());
        remote_user.setEnabled(user.isEnabled());
        remote_user.setAdmin(user.isAdmin());
        remote_user.setPassword(user.getPassword());
        remote_user.setDateCreation(user.getDateCreation());
        
        Set<org.geoserver.geofence.core.model.UserGroup> remote_groups = new HashSet<org.geoserver.geofence.core.model.UserGroup>();
        for(UserGroup group : user.getUserGroups())
        {
            org.geoserver.geofence.core.model.UserGroup remote_group = geofenceRemoteService.getUserGroupAdminService().get(group.getId());
            logger.error("TODO: profile refactoring!!!");
            remote_groups.add(remote_group);
        }
        
		remote_user.setGroups(remote_groups);
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.server.service.IGsUsersManagerService#deleteUser(org.geoserver.geofence.gui.client.model.GSUser)
     */
    public void deleteUser(GSUser user) throws ApplicationException
    {
        org.geoserver.geofence.core.model.GSUser remote_user = null;
        try
        {
            remote_user = geofenceRemoteService.getUserAdminService().get(user.getId());
            geofenceRemoteService.getUserAdminService().delete(remote_user.getId());
        }
        catch (NotFoundServiceEx e)
        {
            logger.error(e.getLocalizedMessage(), e.getCause());
            throw new ApplicationException(e.getLocalizedMessage(), e.getCause());
        }
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.server.service.IGsUsersManagerService#getUserLimitsInfo(org.geoserver.geofence.gui.client.model.GSUser)
     */
    public UserLimitsInfo getUserLimitsInfo(GSUser user) throws ApplicationException
    {
        Long userId = user.getId();
        org.geoserver.geofence.core.model.GSUser gsUser = null;
        UserLimitsInfo userLimitInfo = null;

        logger.error("TODO: allowed area removed from base model!!!");

        try
        {
            gsUser = geofenceRemoteService.getUserAdminService().get(userId);

            if (gsUser != null)
            {
                userLimitInfo = new UserLimitsInfo();
                userLimitInfo.setUserId(userId);

//                MultiPolygon the_geom = gsUser.getAllowedArea();
//
//                if (the_geom != null)
//                {
//                    userLimitInfo.setAllowedArea(the_geom.toText());
//                    userLimitInfo.setSrid(String.valueOf(the_geom.getSRID()));
//                }
//                else
//                {
                    userLimitInfo.setAllowedArea(null);
                    userLimitInfo.setSrid(null);
//                }
            }
        }
        catch (NotFoundServiceEx e)
        {
            logger.error(e.getMessage(), e);
            throw new ApplicationException(e.getMessage(), e);
        }

        return userLimitInfo;
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.server.service.IGsUsersManagerService#saveUserLimitsInfo(org.geoserver.geofence.gui.client.model.GSUser)
     */
    public UserLimitsInfo saveUserLimitsInfo(UserLimitsInfo userLimitInfo) throws ApplicationException
    {
        logger.error("TODO: allowed area removed from base model!!!");

        Long userId = userLimitInfo.getUserId();
        org.geoserver.geofence.core.model.GSUser gsUser = null;

        try
        {
            gsUser = geofenceRemoteService.getUserAdminService().get(userId);

//            String allowedArea = userLimitInfo.getAllowedArea();
//
//            if (allowedArea != null)
//            {
//                WKTReader wktReader = new WKTReader();
//                MultiPolygon the_geom = (MultiPolygon) wktReader.read(allowedArea);
//                the_geom.setSRID(Integer.valueOf(userLimitInfo.getSrid()).intValue());
//                gsUser.setAllowedArea(the_geom);
//            }
//            else
//            {
//                gsUser.setAllowedArea(null);
//            }
//
//            geofenceRemoteService.getUserAdminService().update(gsUser);
        }
        catch (NotFoundServiceEx e)
        {
            logger.error(e.getMessage(), e);
            throw new ApplicationException(e.getMessage(), e);
        }
//        catch (ParseException e)
//        {
//            logger.error(e.getMessage(), e);
//            throw new ApplicationException(e.getMessage(), e);
//        }

        return userLimitInfo;
    }
}

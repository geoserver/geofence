/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.service.impl;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.UserGroup;
import org.geoserver.geofence.gui.client.model.data.ProfileCustomProps;
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
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extjs.gxt.ui.client.data.PagingLoadResult;


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
    public PagingLoadResult<UserGroup> getProfiles(int offset, int limit, boolean full) throws ApplicationException
    {

        int start = offset;

        List<UserGroup> profileListDTO = new ArrayList<UserGroup>();

        if (full)
        {
            UserGroup all_profile = new UserGroup();
            all_profile.setId(-1);
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

            org.geoserver.geofence.core.model.UserGroup remote_profile;
            try
            {
                remote_profile = geofenceRemoteService.getUserGroupAdminService().get(
                        short_profile.getId());
            }
            catch (NotFoundServiceEx e)
            {
                if (logger.isErrorEnabled())
                {
                    logger.error("Details for profile " + short_profile.getName() +
                        " not found on Server!");
                }
                throw new ApplicationException("Details for profile " + short_profile.getName() +
                    " not found on Server!");
            }

            UserGroup local_profile = new UserGroup();

            local_profile.setId(short_profile.getId());
            local_profile.setName(remote_profile.getName());
            local_profile.setDateCreation(remote_profile.getDateCreation());
            local_profile.setEnabled(remote_profile.getEnabled());
            // TODO: use specific API methods in order to load UserGroup custom props
            // local_profile.setCustomProps(remote_profile.getCustomProps());

            profileListDTO.add(local_profile);
        }

        return new RpcPageLoadResult<UserGroup>(profileListDTO, offset, t.intValue());
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.server.service.IProfilesManagerService#deleteProfile(org.geoserver.geofence.gui.client.model.UserGroup)
     */
    public void deleteProfile(UserGroup profile)
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
    public void saveProfile(UserGroup profile)
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
                remote_profile = new org.geoserver.geofence.core.model.UserGroup();

                ShortGroup short_profile = new ShortGroup();
                short_profile.setName(profile.getName());
                short_profile.setEnabled(profile.isEnabled());
                // removed by ETj
//                short_profile.setDateCreation(profile.getDateCreation());
                geofenceRemoteService.getUserGroupAdminService().insert(short_profile);
            }
            catch (Exception e)
            {
                logger.error(e.getLocalizedMessage(), e.getCause());
                throw new ApplicationException(e.getLocalizedMessage(), e.getCause());
            }
        }
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.server.service.IProfilesManagerService#getProfileCustomProps(com.extjs.gxt.ui.client.data.PagingLoadConfig, org.geoserver.geofence.gui.client.model.Rule)
     */
    public PagingLoadResult<ProfileCustomProps> getProfileCustomProps(int offset, int limit, UserGroup profile)
    {
        int start = offset;
        Long t = new Long(0);

        List<ProfileCustomProps> customPropsDTO = new ArrayList<ProfileCustomProps>();

        if ((profile != null) && (profile.getId() >= 0))
        {
            try
            {
                logger.error("TODO: profile refactoring!!! custom props have been removed");
                Map<String, String> customProperties = new HashMap<String, String>();
                customProperties.put("NOTE", "Custom properties are no longer supported. Code is unstable");
//                Map<String, String> customProperties = geofenceRemoteService.getUserGroupAdminService().getCustomProps(profile.getId());

                if (customProperties == null)
                {
                    if (logger.isErrorEnabled())
                    {
                        logger.error("No property found on server");
                    }
                    throw new ApplicationException("No rule found on server");
                }

                long rulesCount = customProperties.size();

                t = new Long(rulesCount);

                int page = (start == 0) ? start : (start / limit);

                SortedSet<String> sortedset = new TreeSet<String>(customProperties.keySet());
                Iterator<String> it = sortedset.iterator();

                while (it.hasNext())
                {
                    String key = it.next();
                    ProfileCustomProps property = new ProfileCustomProps();
                    property.setPropKey(key);
                    property.setPropValue(customProperties.get(key));
                    customPropsDTO.add(property);
                }

//                for (String key : customProperties.keySet()) {
//                    ProfileCustomProps property = new ProfileCustomProps();
//                    property.setPropKey(key);
//                    property.setPropValue(customProperties.get(key));
//                    customPropsDTO.add(property);
//                }
            }
            catch (Exception e)
            {
                // do nothing!
            }
        }

        return new RpcPageLoadResult<ProfileCustomProps>(customPropsDTO, offset, t.intValue());
    }

    /** (non-Javadoc)
     * @deprecated custom props have been removed
     */
    public void setProfileProps(Long profileId, List<ProfileCustomProps> customProps)
    {
        Map<String, String> props = new HashMap<String, String>();

        for (ProfileCustomProps prop : customProps)
        {
            props.put(prop.getPropKey(), prop.getPropValue());
        }

//        LayerDetails details = null;
//        try {
//            details = geofenceRemoteService.getRuleAdminService().getDetails(ruleId);
//        } catch (Exception e) {
//            details = new LayerDetails();
//            geofenceRemoteService.getRuleAdminService().setDetails(ruleId, details);
//        }

        logger.error("TODO: profile refactoring!!! custom props have been removed");
//        geofenceRemoteService.getUserGroupAdminService().setCustomProps(profileId, props);
    }
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.services.dto.ShortUser;
import org.geoserver.geofence.services.exception.BadRequestServiceEx;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;

import java.util.List;
import java.util.Set;


/**
 * Operations on {@link GSUser GSUser}s.
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface UserAdminService extends GetProviderService<GSUser>
{

    // ==========================================================================
    // Basic operations

    long insert(GSUser user);

    long update(GSUser user) throws NotFoundServiceEx;

    boolean delete(long id) throws NotFoundServiceEx;

    /**
     * Retrieves basic info on Users. <BR>
     * If you need structured info (such as Groups), use the {@link #getFull(long)} method.
     *
     * @return Basic GSUser, with some info left unreferenced.
     *
     * @throws NotFoundServiceEx
     */
    @Override
    GSUser get(long id) throws NotFoundServiceEx;
    GSUser get(String name) throws NotFoundServiceEx;
    GSUser getFull(long id) throws NotFoundServiceEx;

    Set<UserGroup> getUserGroups(long id);

    long getCount(String nameLike);

    List<ShortUser> getList(String nameLike, Integer page, Integer entries);

    List<GSUser> getFullList(String nameLike, Integer page, Integer entries) throws BadRequestServiceEx;
    List<GSUser> getFullList(String nameLike, Integer page, Integer entries, boolean fetchGroups) throws BadRequestServiceEx;
}

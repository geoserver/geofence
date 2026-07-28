/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.services;

import java.util.List;
import org.geofence.core.model.GSUser;
import org.geofence.core.services.dto.ShortUser;
import org.geofence.core.services.exception.BadRequestServiceEx;
import org.geofence.core.services.exception.NotFoundServiceEx;

/**
 * Operations on {@link GSUser GSUser}s.
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface UserAdminService extends GetProviderService<GSUser> {

    // ==========================================================================
    // Basic operations

    long insert(GSUser user);

    long update(GSUser user) throws NotFoundServiceEx;

    boolean delete(long id) throws NotFoundServiceEx;

    /**
     * Retrieves basic info on Users. <br>
     * If you need structured info (such as Groups), use the {@link #getFull(long)} method.
     *
     * @return Basic GSUser, with some info left unreferenced.
     * @throws NotFoundServiceEx
     */
    @Override
    GSUser get(long id) throws NotFoundServiceEx;

    GSUser getFull(String name) throws NotFoundServiceEx;

    long getCount(String nameLike);

    List<ShortUser> getList(String nameLike, Integer page, Integer entries);

    List<GSUser> getFullList(String nameLike, Integer page, Integer entries) throws BadRequestServiceEx;

    List<GSUser> getFullList(String nameLike, Integer page, Integer entries, boolean fetchGroups)
            throws BadRequestServiceEx;
}

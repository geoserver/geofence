/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.services;

import java.util.List;
import org.geofence.core.model.GSUser;
import org.geofence.core.services.dto.ShortUser;
import org.geofence.core.services.exception.NotFoundServiceEx;

/**
 * Operations on {@link GSUser GSUser}s.
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface ConfigAdminService {

    // ==========================================================================
    // Basic operations

    long insert(GSUser user);

    long update(GSUser user) throws NotFoundServiceEx;

    boolean delete(long id) throws NotFoundServiceEx;

    GSUser get(long id) throws NotFoundServiceEx;

    List<ShortUser> getAll();

    List<ShortUser> getList(String nameLike, Integer page, Integer entries);

    long getCount(String nameLike);
}

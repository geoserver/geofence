/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.core.model.GFUser;
import org.geoserver.geofence.services.dto.ShortUser;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;

import java.util.List;


/**
 * Operations on {@link GFUser GFUser}s.
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface GFUserAdminService extends GetProviderService<GFUser>
{

    // ==========================================================================
    // Basic operations

    long insert(GFUser user);

    long update(GFUser user) throws NotFoundServiceEx;

    boolean delete(long id) throws NotFoundServiceEx;

    @Override
    GFUser get(long id) throws NotFoundServiceEx;
    GFUser get(String name) throws NotFoundServiceEx;

    long getCount(String nameLike);

    List<ShortUser> getList(String nameLike, Integer page, Integer entries);

    List<GFUser> getFullList(String nameLike, Integer page, Integer entries);
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.services.dto.ShortGroup;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;

import java.util.List;


/**
 * Operations on {@link UserGroup UserGroup}s.
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface UserGroupAdminService extends GetProviderService<UserGroup>
{

    // ==========================================================================
    // Basic operations

    long insert(ShortGroup group);

    long update(ShortGroup group) throws NotFoundServiceEx;

    boolean delete(long id) throws NotFoundServiceEx;

    @Override
    UserGroup get(long id) throws NotFoundServiceEx;
    UserGroup get(String name) throws NotFoundServiceEx;

    long getCount(String nameLike);

    List<ShortGroup> getList(String nameLike, Integer page, Integer entries);

//    List<UserGroup> getFullList(String nameLike, Integer page, Integer entries);

    // ==========================================================================

//    public Map<String, String> getCustomProps(Long id);

//    public void setCustomProps(Long id, Map<String, String> props);

}

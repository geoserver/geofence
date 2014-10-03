/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.core.model.GSInstance;
import org.geoserver.geofence.services.dto.ShortInstance;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;

import java.util.List;


/**
 * Operations on {@link GSInstance GSInstance}s.
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface InstanceAdminService extends GetProviderService<GSInstance>
{

    // ==========================================================================
    // Basic operations

    long insert(GSInstance instance);

    long update(GSInstance instance) throws NotFoundServiceEx;

    boolean delete(long id) throws NotFoundServiceEx;

    @Override
    GSInstance get(long id) throws NotFoundServiceEx;
    GSInstance get(String name) throws NotFoundServiceEx;

    List<GSInstance> getAll();

    List<GSInstance> getFullList(String nameLike, Integer page, Integer entries);
    List<ShortInstance> getList(String nameLike, Integer page, Integer entries);

    long getCount(String nameLike);

}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;


/**
 * Operations on {@link UserGroup UserGroup}s.
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface GetProviderService<E>
{

    E get(long id) throws NotFoundServiceEx;
}

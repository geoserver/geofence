/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao;

import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.UserGroup;

import java.util.Set;

/**
 * Public interface to define operations on GSUsers
 * 
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */

public interface GSUserDAO extends RestrictedGenericDAO<GSUser> {

    Set<UserGroup> getGroups(Long id);

    /** Fetch a GSUser with all of its related groups */
    GSUser getFull(Long id);
    /** Fetch a GSUser with all of its related groups */
    GSUser getFull(String name);

}

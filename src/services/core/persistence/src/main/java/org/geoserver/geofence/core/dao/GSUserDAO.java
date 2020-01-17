/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao;

import java.util.List;
import org.geoserver.geofence.core.model.GSUser;

/**
 * Public interface to define operations on GSUsers
 * 
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */

public interface GSUserDAO extends RestrictedGenericDAO<GSUser>, RegistrableDAO {

    /** Fetch a GSUser with all of its related groups */
    GSUser getFull(String name);
    
    List<GSUser> search(String nameLike, Integer page, Integer entries, boolean fetchGroups) throws IllegalArgumentException;
    long countByNameLike(String nameLike);    
}

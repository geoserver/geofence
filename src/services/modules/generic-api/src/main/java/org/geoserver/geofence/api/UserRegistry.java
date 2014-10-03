/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.api;

import org.geoserver.geofence.api.dto.RegisteredUser;

import java.util.List;

/**
 * 
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface UserRegistry {

    List<RegisteredUser> getUsers(String nameLike, int page, int entries);

    long getUsersCount(String nameLike);

}

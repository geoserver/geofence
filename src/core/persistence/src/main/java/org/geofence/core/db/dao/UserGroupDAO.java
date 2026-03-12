/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db.dao;

import java.util.List;
import org.geofence.core.model.UserGroup;

/**
 * Public interface to define operations on UserGroups
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface UserGroupDAO //
extends BaseDAO<UserGroup> {

    UserGroup get(String name);

    List<UserGroup> search(String nameLike, Integer page, Integer entries) throws IllegalArgumentException;

    long countByNameLike(String nameLike);
}

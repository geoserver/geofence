/* (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.services;

import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.db.dao.GSUserDAO;
import org.geofence.core.db.dao.UserGroupDAO;
import org.geofence.core.model.GSUser;
import org.geofence.core.model.UserGroup;
import org.geofence.core.services.spi.UserResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A UserResolver that uses GeoFence internal DAOs to retrieve users and roles.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Service
public class DefaultUserResolver implements UserResolver {

    private static final Logger LOGGER = LogManager.getLogger(DefaultUserResolver.class);

    @Autowired
    private GSUserDAO userDAO;

    @Autowired
    private UserGroupDAO userGroupDAO;

    @Override
    public Set<String> getRoles(String username) {
        GSUser user = userDAO.getFull(username);

        Set<String> ret = new HashSet<>();
        if (user != null) {
            for (UserGroup role : user.getGroups()) {
                ret.add(role.getName());
            }
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Groups for user " + username + ": " + ret);
        }

        return ret;
    }

    // =========================================================================

    public void setGsUserDAO(GSUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setUserGroupDAO(UserGroupDAO userGroupDAO) {
        this.userGroupDAO = userGroupDAO;
    }
}

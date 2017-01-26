/* (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.geoserver.geofence.core.dao.GSUserDAO;
import org.geoserver.geofence.core.dao.UserGroupDAO;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.spi.UserResolver;

/**
 * A UserResolver that uses GeoFence internal DAOs 
 * to retrieve users and roles.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class DefaultUserResolver implements UserResolver {

    private final static Logger LOGGER = LogManager.getLogger(DefaultUserResolver.class);

    private GSUserDAO userDAO;
    private UserGroupDAO userGroupDAO;


    @Override
    @Deprecated
    public boolean existsUser(String username) {
        throw new IllegalStateException("This method is deprecated and should not be invoked");
    }

    @Override
    public Set<String> getRoles(String username) {
        GSUser user = userDAO.getFull(username);

        Set<String> ret = new HashSet<>();
        if(user != null) {
            for (UserGroup role : user.getGroups()) {
                ret.add(role.getName());
            }
        }

        if(LOGGER.isTraceEnabled()) {
            LOGGER.trace("Groups for user " + username + ": " + ret);
        }

        return ret;
    }

    @Override
    @Deprecated
    public boolean existsRole(String rolename) {
        throw new IllegalStateException("This method is deprecated and should not be invoked");
    }

    //=========================================================================
    
    public void setGsUserDAO(GSUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setUserGroupDAO(UserGroupDAO userGroupDAO) {
        this.userGroupDAO = userGroupDAO;
    }

}

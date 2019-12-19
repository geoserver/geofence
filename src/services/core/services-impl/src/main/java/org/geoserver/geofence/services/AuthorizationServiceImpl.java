/* (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.dao.GSUserDAO;
import org.geoserver.geofence.core.dao.search.Search;
import org.geoserver.geofence.services.dto.AuthUser;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class AuthorizationServiceImpl implements AuthorizationService {

    private final static Logger LOGGER = LogManager.getLogger(AuthorizationServiceImpl.class);

    private GSUserDAO userDAO;

    @Override
    public AuthUser authorize(String username, String password) {
        GSUser user = getUserByName(username);
        if(user == null) {
            LOGGER.debug("User not found " + username);
            return null;
        }
        if(! user.getPassword().equals(password)) {
            LOGGER.debug("Bad pw for user " + username);
            return null;
        }

        return new AuthUser(username, user.isAdmin() ? AuthUser.Role.ADMIN : AuthUser.Role.USER);
    }

    private GSUser getUserByName(String userName) {
        Search search = new Search(GSUser.class);
        search.addFilterEqual("name", userName);
        List<GSUser> users = userDAO.search(search);
        if(users.size() > 1)
            throw new IllegalStateException("Found more than one user with name '"+userName+"'");

        return users.isEmpty() ? null : users.get(0);
    }

    public void setGsUserDAO(GSUserDAO userDAO) {
        this.userDAO = userDAO;
    }

}

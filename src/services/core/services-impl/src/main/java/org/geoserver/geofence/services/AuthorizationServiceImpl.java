/* (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.dao.GSUserDAO;
import org.geoserver.geofence.services.dto.AuthUser;


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
        GSUser user = userDAO.getFull(username);
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

    public void setGsUserDAO(GSUserDAO userDAO) {
        this.userDAO = userDAO;
    }

}

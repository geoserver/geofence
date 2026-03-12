/* (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.db.dao.GSUserDAO;
import org.geofence.core.model.GSUser;
import org.geofence.core.services.dto.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** @author ETj (etj at geo-solutions.it) */
@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private static final Logger LOGGER = LogManager.getLogger(AuthorizationServiceImpl.class);

    @Autowired
    private GSUserDAO userDAO;

    @Override
    public AuthUser authorize(String username, String password) {
        GSUser user = userDAO.getFull(username);
        if (user == null) {
            LOGGER.debug("User not found " + username);
            return null;
        }
        if (!user.getPassword().equals(password)) {
            LOGGER.debug("Bad pw for user " + username);
            return null;
        }

        return new AuthUser(username, user.isAdmin() ? AuthUser.Role.ADMIN : AuthUser.Role.USER);
    }

    public void setGsUserDAO(GSUserDAO userDAO) {
        this.userDAO = userDAO;
    }
}

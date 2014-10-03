/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.login;

import org.geoserver.geofence.login.LoginService;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.geoserver.geofence.api.AuthProvider;
import org.geoserver.geofence.api.dto.Authority;
import org.geoserver.geofence.api.dto.GrantedAuths;
import org.geoserver.geofence.api.exception.AuthException;
import org.geoserver.geofence.login.util.GrantAll;
import org.geoserver.geofence.login.util.SessionManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;


/**
 * @author ETj (etj at geo-solutions.it)
 */
public class LoginServiceImpl implements LoginService, InitializingBean, DisposableBean
{

    private static final Logger LOGGER = LogManager.getLogger(LoginServiceImpl.class);

    // private List<String> authorizedRoles;

    private AuthProvider authProvider = new GrantAll(); // this provider should be overridden by
                                                        // injecting a true implementation.

    private SessionManager sessionManager;

    public LoginServiceImpl()
    {
        LOGGER.info("Creating " + getClass().getSimpleName() + " instance");
    }

    @Override
    public void afterPropertiesSet()
    {
        LOGGER.debug("afterPropertiesSet()");
    }

    @Override
    public void destroy() throws Exception
    {
        LOGGER.debug("destroy()");
    }

    @PostConstruct
    public void postConstruct()
    {
        LOGGER.debug("postConstruct()");
    }

    @PreDestroy
    public void preDestroy()
    {
        LOGGER.debug("preDestroy()");

    }

    // ==========================================================================
    // Service methods
    // ==========================================================================

    @Override
    public String login(String username, String password, String pwFromDb) throws AuthException
    {
        LOGGER.info("LOGIN REQUEST FOR " + username);

        // MessageContext msgCtxt = webServiceContext.getMessageContext();
        // HttpServletRequest req = (HttpServletRequest)msgCtxt.get(MessageContext.SERVLET_REQUEST);
        // String clientIP = req.getRemoteAddr();
        //
        // LOGGER.info("LOGIN REQUEST FOR " + username + " @ " + clientIP);

        if (username == null)
        {
            throw new AuthException("Null username");
        }
        else
        {
            try
            {
                GrantedAuths ga = authProvider.login(username, password, pwFromDb);
                if (!ga.getAuthorities().contains(Authority.LOGIN))
                {
                    LOGGER.warn("Login not granted to user [" + username + "]");
                    throw new AuthException("User " + username + " can't log in");
                }

                String token = sessionManager.createSession(username, ga);

                return token;
            }
            catch (AuthException ex)
            {
                LOGGER.warn("Authentication Failed for user [" + username + "]: " +
                    ex.getLocalizedMessage());
                throw new AuthException("Authentication error", ex);
            }
        }
    }

    @Override
    public void logout(String token)
    {
        LOGGER.info("LOGOUT:" + token);
        sessionManager.closeSession(token);
    }

    @Override
    public GrantedAuths getGrantedAuthorities(String token)
    {
        LOGGER.info("getGrantedAuthorities:" + token);

        return sessionManager.getGrantedAuthorities(token);
    }

    // ==========================================================================
    // Setters
    // ==========================================================================

    public void setAuthProvider(AuthProvider authProvider)
    {
        LOGGER.info("Setting AuthProvider: " + authProvider.getClass());
        this.authProvider = authProvider;
    }

    public void setSessionManager(SessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
    }
}

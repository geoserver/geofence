/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.auth;


import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.security.SecurityContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 *
 * Starting point was JAASLoginInterceptor
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GeofenceAuthenticationInterceptor extends AbstractPhaseInterceptor<Message>
{

    private static final Logger LOGGER = LogManager.getLogger(GeofenceAuthenticationInterceptor.class);

    // TODO: inject user service

    public GeofenceAuthenticationInterceptor()
    {
        super(Phase.UNMARSHAL);
    }

    @Override
    public void handleMessage(Message message) throws Fault
    {

        LOGGER.info("In handleMessage");
        LOGGER.info("Message --> " + message);

        String name = null;
        String password = null;

        AuthUser user = null;

        AuthorizationPolicy policy = (AuthorizationPolicy) message.get(AuthorizationPolicy.class);
        if (policy != null)
        {
            name = policy.getUserName();
            password = policy.getPassword();

            LOGGER.info("Requesting user: " + name);
            // TODO: read user from DB
            // if user and pw do not match, throw new AuthenticationException("Unauthorized");

            user = new AuthUser();
            user.setName(name);

        }
        else
        {
            LOGGER.info("No requesting user -- GUEST access");
        }

        GeofenceSecurityContext securityContext = new GeofenceSecurityContext();
        GeofencePrincipal principal = (user != null) ? new GeofencePrincipal(user) : GeofencePrincipal.createGuest();
        securityContext.setPrincipal(principal);

        message.put(SecurityContext.class, securityContext);
    }
}

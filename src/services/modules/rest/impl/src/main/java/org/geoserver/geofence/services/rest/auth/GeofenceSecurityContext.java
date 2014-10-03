/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.auth;

import java.security.Principal;

import org.apache.cxf.security.SecurityContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GeofenceSecurityContext implements SecurityContext
{

    private static final Logger LOGGER = LogManager.getLogger(GeofenceSecurityContext.class);

    private GeofencePrincipal principal;

    public void setPrincipal(GeofencePrincipal principal)
    {
        this.principal = principal;
    }

    @Override
    public Principal getUserPrincipal()
    {
        return principal;
    }


    @Override
    public boolean isUserInRole(String role)
    {
        boolean ret = isUserInRoleAux(role);
        LOGGER.info("User" + principal.getName() + " in " + role + " : " + ret);

        return ret;
    }

    public boolean isUserInRoleAux(String role)
    {
        // TODO pls use an enum here
        if ("*".equals(role))
        {
            return true;
        }

        if ("guest".equalsIgnoreCase(role))
        {
            return true;
        }

        if ("user".equalsIgnoreCase(role)) // so user is registered
        {
            return true;
        }

        if ("admin".equalsIgnoreCase(principal.getName()) && "admin".equalsIgnoreCase(role))
        {
            return true;
        }

        return false;
    }


}

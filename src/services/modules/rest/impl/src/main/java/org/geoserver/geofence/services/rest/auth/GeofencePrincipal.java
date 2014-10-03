/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.auth;

import java.security.Principal;


/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GeofencePrincipal implements Principal
{

    static GeofencePrincipal createGuest()
    {
        return new GeofencePrincipal();
    }

    private AuthUser user;

    public GeofencePrincipal()
    {
    }

    public GeofencePrincipal(AuthUser user)
    {
        if (user == null)
        {
            throw new NullPointerException("Null user");
        }
        this.user = user;
    }

    @Override
    public String getName()
    {
        return (user != null) ? user.getName() : "GUEST";
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }

        final GeofencePrincipal other = (GeofencePrincipal) obj;
        if ((this.user != other.user) && ((this.user == null) || !this.user.equals(other.user)))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = (97 * hash) + ((this.user != null) ? this.user.hashCode() : 0);

        return hash;
    }


}

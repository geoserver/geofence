/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.auth;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class AuthUser
{

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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

        final AuthUser other = (AuthUser) obj;
        if ((this.name == null) ? (other.name != null) : (!this.name.equals(other.name)))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = (19 * hash) + ((this.name != null) ? this.name.hashCode() : 0);

        return hash;
    }
}

/* (c) 2014 - 2017  Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.dto;

import org.geoserver.geofence.core.model.GFUser;
import org.geoserver.geofence.core.model.GSUser;

import java.io.Serializable;


/**
 * A compact representation of GSUser useful in lists.
 *
 * @author Etj (etj@geo-solutions.it)
 */
public class ShortUser implements Serializable
{

    private static final long serialVersionUID = -24846270926852L;

    private Long id;

    private String name;

    public ShortUser()
    {
    }

    public ShortUser(GSUser user)
    {
        this.id = user.getId();
        this.name = user.getName();
    }

    public ShortUser(GFUser user)
    {
        this.id = user.getId();
        this.name = user.getName();
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName()+"[id:" + id + " name:" + name + ']';
    }

}

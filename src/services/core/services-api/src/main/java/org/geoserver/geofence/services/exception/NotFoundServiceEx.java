/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.exception;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class NotFoundServiceEx extends WebApplicationException
{

    private String message;

    public NotFoundServiceEx(String message)
    {
        super(Response.Status.NOT_FOUND);
        this.message = message;
    }

    public NotFoundServiceEx(String message, Long id)
    {
        super(Response.Status.NOT_FOUND);
        this.message = message + " (id:" + id + ")";
    }

    @Override
    public String getMessage()
    {
        return message;
    }


}

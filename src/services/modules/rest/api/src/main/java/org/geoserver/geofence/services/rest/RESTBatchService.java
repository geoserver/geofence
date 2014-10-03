/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest;

import org.geoserver.geofence.services.rest.exception.BadRequestRestEx;
import org.geoserver.geofence.services.rest.exception.InternalErrorRestEx;
import org.geoserver.geofence.services.rest.exception.NotFoundRestEx;
import org.geoserver.geofence.services.rest.model.RESTBatch;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;


/**
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */

@Path("/")
public interface RESTBatchService
{
    @POST
    @Path("/exec")
    @Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    Response exec(@Multipart("batch")RESTBatch batch) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    /**
     * Similar to exec, but not transaction.
     * Used internally.
     */
    void runBatch(RESTBatch batch) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.interfaces;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.geofence.web.rest.api.exception.BadRequestRestEx;
import org.geofence.web.rest.api.exception.InternalErrorRestEx;
import org.geofence.web.rest.api.exception.NotFoundRestEx;
import org.geofence.web.rest.api.model.RESTBatch;
import org.glassfish.jersey.media.multipart.FormDataParam;

/** @author Emanuele Tajariol (etj at geo-solutions.it) */
@Path("/config")
public interface RESTConfigService {
    @GET
    @Path("/backup")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    RESTBatch backup(@QueryParam("includeGFUsers") @DefaultValue("False") Boolean includeGRUsers);

    @PUT
    @Path("/restore")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    void restore(@FormDataParam("batch") RESTBatch batch) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    @PUT
    @Path("/cleanup")
    void cleanup() throws InternalErrorRestEx;

    @GET
    @Path("/backup/groups")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    RESTBatch backupGroups();

    @GET
    @Path("/backup/users")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    RESTBatch backupUsers();

    @GET
    @Path("/backup/instances")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    RESTBatch backupInstances();

    @GET
    @Path("/backup/rules")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    RESTBatch backupRules();
}

/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.interfaces;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.geofence.web.rest.api.exception.BadRequestRestEx;
import org.geofence.web.rest.api.exception.InternalErrorRestEx;
import org.geofence.web.rest.api.exception.NotFoundRestEx;
import org.geofence.web.rest.api.interfaces.params.RESTAdminRuleFilter;
import org.geofence.web.rest.api.model.RESTInputAdminRule;
import org.geofence.web.rest.api.model.RESTOutputAdminRule;
import org.geofence.web.rest.api.model.RESTOutputAdminRuleList;
import org.glassfish.jersey.media.multipart.FormDataParam;

/** @author Emanuele Tajariol (etj at geo-solutions.it) */
@Path("/adminrules")
public interface RESTAdminRuleService {
    @POST
    @Path("/")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    Response insert(@FormDataParam("rule") RESTInputAdminRule rule) throws BadRequestRestEx, NotFoundRestEx;

    @GET
    @Path("/id/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    RESTOutputAdminRule get(@PathParam("id") Long id) throws BadRequestRestEx, NotFoundRestEx;

    @PUT
    @Path("/id/{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    void update(@PathParam("id") Long id, @FormDataParam("rule") RESTInputAdminRule rule)
            throws BadRequestRestEx, NotFoundRestEx;

    @DELETE
    @Path("/id/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    Response delete(@PathParam("id") Long id) throws BadRequestRestEx, NotFoundRestEx;

    @GET
    @Path("/")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    RESTOutputAdminRuleList get(
            @QueryParam("page") Integer page,
            @QueryParam("entries") Integer entries,
            @QueryParam("full") @DefaultValue("false") boolean full,
            @BeanParam RESTAdminRuleFilter query)
            throws BadRequestRestEx, InternalErrorRestEx;

    @GET
    @Path("/count")
    long count(@BeanParam RESTAdminRuleFilter query);
}

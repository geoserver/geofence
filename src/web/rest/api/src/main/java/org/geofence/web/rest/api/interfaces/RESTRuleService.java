/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.interfaces;

import jakarta.ws.rs.BeanParam;
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
import org.geofence.web.rest.api.interfaces.params.RESTRuleFilter;
import org.geofence.web.rest.api.model.RESTInputRule;
import org.geofence.web.rest.api.model.RESTOutputRule;
import org.geofence.web.rest.api.model.RESTOutputRuleList;
import org.glassfish.jersey.media.multipart.FormDataParam;

/** @author Emanuele Tajariol (etj at geo-solutions.it) */
@Path("/rules")
public interface RESTRuleService {
    @POST
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    Response insert(@FormDataParam("rule") RESTInputRule rule) throws BadRequestRestEx, NotFoundRestEx;

    @GET
    @Path("/id/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    RESTOutputRule get(@FormDataParam("id") Long id) throws BadRequestRestEx, NotFoundRestEx;

    @PUT
    @Path("/id/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    void update(@PathParam("id") Long id, @FormDataParam("rule") RESTInputRule rule)
            throws BadRequestRestEx, NotFoundRestEx;

    @DELETE
    @Path("/id/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    Response delete(@PathParam("id") Long id) throws BadRequestRestEx, NotFoundRestEx;

    @GET
    @Path("/")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    RESTOutputRuleList get(
            @QueryParam("page") Integer page,
            @QueryParam("entries") Integer entries,
            @QueryParam("full") @DefaultValue("false") boolean full,
            @BeanParam RESTRuleFilter query)
            throws BadRequestRestEx, InternalErrorRestEx;

    @GET
    @Path("/count")
    long count(@BeanParam RESTRuleFilter query);
    /**
     * Move the provided rules to the target priority. Rules will be sorted by their priority, first rule will be
     * updated with a priority equal to the target priority and the next ones will get an incremented priority value.
     */
    @GET
    @Path("/move")
    @Produces(MediaType.TEXT_XML)
    Response move(@QueryParam("rulesIds") String rulesIds, @QueryParam("targetPriority") Integer targetPriority)
            throws BadRequestRestEx, InternalErrorRestEx;
}

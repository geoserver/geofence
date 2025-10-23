/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest;

import org.geoserver.geofence.services.rest.exception.BadRequestRestEx;
import org.geoserver.geofence.services.rest.exception.InternalErrorRestEx;
import org.geoserver.geofence.services.rest.exception.NotFoundRestEx;

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

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.geoserver.geofence.services.rest.model.RESTInputAdminRule;
import org.geoserver.geofence.services.rest.model.RESTOutputAdminRule;
import org.geoserver.geofence.services.rest.model.RESTOutputAdminRuleList;


/**
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */

@Path("/")
public interface RESTAdminRuleService
{
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_XML)
    Response insert(@Multipart("rule") RESTInputAdminRule rule) throws BadRequestRestEx, NotFoundRestEx;

    @GET
    @Path("/id/{id}")
    @Produces(MediaType.APPLICATION_XML)
    RESTOutputAdminRule get(@PathParam("id") Long id) throws BadRequestRestEx, NotFoundRestEx;

    @PUT
    @Path("/id/{id}")
    @Produces(MediaType.APPLICATION_XML)
    void update(@PathParam("id") Long id,
        @Multipart("rule") RESTInputAdminRule rule) throws BadRequestRestEx, NotFoundRestEx;

    @DELETE
    @Path("/id/{id}")
    @Produces(MediaType.APPLICATION_XML)
    Response delete(@PathParam("id") Long id) throws BadRequestRestEx, NotFoundRestEx;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_XML)
    RESTOutputAdminRuleList get(
        @QueryParam("page") Integer page,
        @QueryParam("entries") Integer entries,
        @QueryParam("full")@DefaultValue("false")  boolean full,

        @QueryParam("userName") String userName,
        @QueryParam("userAny")  Boolean userAny,

        @QueryParam("groupName") String groupName,
        @QueryParam("groupAny")  Boolean groupAny,

        @QueryParam("instanceId")   Long instanceId,
        @QueryParam("instanceName") String  instanceName,
        @QueryParam("instanceAny")  Boolean instanceAny,

        @QueryParam("workspace") String  workspace,
        @QueryParam("workspaceAny")  Boolean workspaceAny

    ) throws BadRequestRestEx, InternalErrorRestEx;

    @GET
    @Path("/count")
    long count(
        @QueryParam("userName") String userName,
        @QueryParam("userAny")  Boolean userAny,

        @QueryParam("groupName") String groupName,
        @QueryParam("groupAny")  Boolean groupAny,

        @QueryParam("instanceId")   Long instanceId,
        @QueryParam("instanceName") String  instanceName,
        @QueryParam("instanceAny")  Boolean instanceAny,

        @QueryParam("workspace") String  workspace,
        @QueryParam("workspaceAny")  Boolean workspaceAny
    );

}

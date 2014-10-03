/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest;

import org.geoserver.geofence.services.rest.exception.BadRequestRestEx;
import org.geoserver.geofence.services.rest.exception.InternalErrorRestEx;
import org.geoserver.geofence.services.rest.exception.NotFoundRestEx;
import org.geoserver.geofence.services.rest.model.RESTInputRule;
import org.geoserver.geofence.services.rest.model.RESTOutputRule;
import org.geoserver.geofence.services.rest.model.RESTOutputRuleList;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;


/**
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */

@Path("/")
public interface RESTRuleService
{
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_XML)
    Response insert(@Multipart("rule") RESTInputRule rule) throws BadRequestRestEx, NotFoundRestEx;

    @GET
    @Path("/id/{id}")
    @Produces(MediaType.APPLICATION_XML)
    RESTOutputRule get(@PathParam("id") Long id) throws BadRequestRestEx, NotFoundRestEx;

    @PUT
    @Path("/id/{id}")
    @Produces(MediaType.APPLICATION_XML)
    void update(@PathParam("id") Long id,
        @Multipart("rule") RESTInputRule rule) throws BadRequestRestEx, NotFoundRestEx;

    @DELETE
    @Path("/id/{id}")
    @Produces(MediaType.APPLICATION_XML)
    Response delete(@PathParam("id") Long id) throws BadRequestRestEx, NotFoundRestEx;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_XML)
    RESTOutputRuleList get(
        @QueryParam("page") Integer page,
        @QueryParam("entries") Integer entries,
        @QueryParam("full")@DefaultValue("false")  boolean full,

        @QueryParam("userId")   Long userId,
        @QueryParam("userName") String userName,
        @QueryParam("userAny")  Boolean userAny,

        @QueryParam("groupId")   Long groupId,
        @QueryParam("groupName") String groupName,
        @QueryParam("groupAny")  Boolean groupAny,

        @QueryParam("instanceId")   Long instanceId,
        @QueryParam("instanceName") String  instanceName,
        @QueryParam("instanceAny")  Boolean instanceAny,

        @QueryParam("service")     String  serviceName,
        @QueryParam("serviceAny")  Boolean serviceAny,

        @QueryParam("request")     String  requestName,
        @QueryParam("requestAny")  Boolean requestAny,

        @QueryParam("workspace") String  workspace,
        @QueryParam("workspaceAny")  Boolean workspaceAny,

        @QueryParam("layer") String  layer,
        @QueryParam("layerAny")  Boolean layerAny
    ) throws BadRequestRestEx, InternalErrorRestEx;

    @GET
    @Path("/count")
    long count(
        @QueryParam("userId")   Long userId,
        @QueryParam("userName") String userName,
        @QueryParam("userAny")  Boolean userAny,

        @QueryParam("groupId")   Long groupId,
        @QueryParam("groupName") String groupName,
        @QueryParam("groupAny")  Boolean groupAny,

        @QueryParam("instanceId")   Long instanceId,
        @QueryParam("instanceName") String  instanceName,
        @QueryParam("instanceAny")  Boolean instanceAny,

        @QueryParam("service")     String  serviceName,
        @QueryParam("serviceAny")  Boolean serviceAny,

        @QueryParam("request")     String  requestName,
        @QueryParam("requestAny")  Boolean requestAny,

        @QueryParam("workspace") String  workspace,
        @QueryParam("workspaceAny")  Boolean workspaceAny,

        @QueryParam("layer") String  layer,
        @QueryParam("layerAny")  Boolean layerAny
    );

}

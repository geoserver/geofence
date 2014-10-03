/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest;

import org.geoserver.geofence.services.rest.exception.BadRequestRestEx;
import org.geoserver.geofence.services.rest.exception.ConflictRestEx;
import org.geoserver.geofence.services.rest.exception.InternalErrorRestEx;
import org.geoserver.geofence.services.rest.exception.NotFoundRestEx;
import org.geoserver.geofence.services.rest.model.RESTInputInstance;
import org.geoserver.geofence.services.rest.model.RESTOutputInstance;
import org.geoserver.geofence.services.rest.model.RESTShortInstanceList;

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
public interface RESTGSInstanceService
{

    /**
     * @return a sample user list
     * */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_XML)
    RESTShortInstanceList getList(@QueryParam("nameLike") String nameLike,
        @QueryParam("page") Integer page,
        @QueryParam("entries") Integer entries) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    @GET
    @Path("/count/{nameLike}")
    long count(@PathParam("nameLike") String nameLike);

    @GET
    @Path("/id/{id}")
    @Produces(MediaType.APPLICATION_XML)
    RESTOutputInstance get(@PathParam("id") Long id) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    @GET
    @Path("/name/{name")
    @Produces(MediaType.APPLICATION_XML)
    RESTOutputInstance get(@PathParam("name") String name) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_XML)
    Response insert(@Multipart("userGroup") RESTInputInstance instance) throws BadRequestRestEx, ConflictRestEx, InternalErrorRestEx;

    @PUT
    @Path("/id/{id}")
    @Produces(MediaType.APPLICATION_XML)
    void update(@PathParam("id") Long id,
        @Multipart("instance") RESTInputInstance instance) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    @PUT
    @Path("/name/{name}")
    @Produces(MediaType.APPLICATION_XML)
    void update(@PathParam("name") String name,
        @Multipart("instance") RESTInputInstance instance) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    /**
     * Deletes a GSInstance.
     *
     * @param id The id of the instance to delete
     * @param cascade When true, also delete all the Rules referring to that instance
     *
     * @throws BadRequestRestEx (HTTP code 400) if parameters are illegal
     * @throws NotFoundRestEx (HTTP code 404) if the instance is not found
     * @throws ConflictRestEx (HTTP code 409) if any rule refers to the instance and cascade is false
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @DELETE
    @Path("/id/{id}")
    Response delete(
            @PathParam("id") Long id,
            @QueryParam("cascade") @DefaultValue("false") boolean cascade) throws ConflictRestEx, NotFoundRestEx, InternalErrorRestEx;

    /**
     * Deletes a GSInstance.
     *
     * @param name The name of the instance to delete
     * @param cascade When true, also delete all the Rules referring to that instance
     *
     * @throws BadRequestRestEx (HTTP code 400) if parameters are illegal
     * @throws NotFoundRestEx (HTTP code 404) if the instance is not found
     * @throws ConflictRestEx (HTTP code 409) if any rule refers to the instance and cascade is false
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @DELETE
    @Path("/name/{name}")
    Response delete(
            @PathParam("name") String name,
            @QueryParam("cascade") @DefaultValue("false") boolean cascade) throws ConflictRestEx, NotFoundRestEx, InternalErrorRestEx;

}

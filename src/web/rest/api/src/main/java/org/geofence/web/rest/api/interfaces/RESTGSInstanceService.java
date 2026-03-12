/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.interfaces;

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
import org.geofence.web.rest.api.exception.ConflictRestEx;
import org.geofence.web.rest.api.exception.InternalErrorRestEx;
import org.geofence.web.rest.api.exception.NotFoundRestEx;
import org.geofence.web.rest.api.model.RESTInputInstance;
import org.geofence.web.rest.api.model.RESTOutputInstance;
import org.geofence.web.rest.api.model.RESTShortInstanceList;
import org.glassfish.jersey.media.multipart.FormDataParam;

/** @author Emanuele Tajariol (etj at geo-solutions.it) */
@Path("/")
public interface RESTGSInstanceService {

    /** @return a sample user list */
    @GET
    @Path("/")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    RESTShortInstanceList getList(
            @QueryParam("nameLike") String nameLike,
            @QueryParam("page") Integer page,
            @QueryParam("entries") Integer entries)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    @GET
    @Path("/count/{nameLike}")
    long count(@PathParam("nameLike") String nameLike);

    @GET
    @Path("/id/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    RESTOutputInstance get(@PathParam("id") Long id) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    @GET
    @Path("/name/{name")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    RESTOutputInstance get(@PathParam("name") String name) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    @POST
    @Path("/")
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    Response insert(@FormDataParam("instance") RESTInputInstance instance)
            throws BadRequestRestEx, ConflictRestEx, InternalErrorRestEx;

    @PUT
    @Path("/id/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    void update(@PathParam("id") Long id, @FormDataParam("instance") RESTInputInstance instance)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    @PUT
    @Path("/name/{name}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    void update(@PathParam("name") String name, @FormDataParam("instance") RESTInputInstance instance)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    /**
     * Deletes a GSInstance.
     *
     * @param id The id of the instance to delete
     * @param cascade When true, also delete all the Rules referring to that instance
     * @throws BadRequestRestEx (HTTP code 400) if parameters are illegal
     * @throws NotFoundRestEx (HTTP code 404) if the instance is not found
     * @throws ConflictRestEx (HTTP code 409) if any rule refers to the instance and cascade is false
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @DELETE
    @Path("/id/{id}")
    Response delete(@PathParam("id") Long id, @QueryParam("cascade") @DefaultValue("false") boolean cascade)
            throws ConflictRestEx, NotFoundRestEx, InternalErrorRestEx;

    /**
     * Deletes a GSInstance.
     *
     * @param name The name of the instance to delete
     * @param cascade When true, also delete all the Rules referring to that instance
     * @throws BadRequestRestEx (HTTP code 400) if parameters are illegal
     * @throws NotFoundRestEx (HTTP code 404) if the instance is not found
     * @throws ConflictRestEx (HTTP code 409) if any rule refers to the instance and cascade is false
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @DELETE
    @Path("/name/{name}")
    Response delete(@PathParam("name") String name, @QueryParam("cascade") @DefaultValue("false") boolean cascade)
            throws ConflictRestEx, NotFoundRestEx, InternalErrorRestEx;
}

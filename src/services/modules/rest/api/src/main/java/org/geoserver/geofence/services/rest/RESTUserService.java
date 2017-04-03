/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.geoserver.geofence.services.rest.exception.BadRequestRestEx;
import org.geoserver.geofence.services.rest.exception.ConflictRestEx;
import org.geoserver.geofence.services.rest.exception.InternalErrorRestEx;
import org.geoserver.geofence.services.rest.exception.NotFoundRestEx;
import org.geoserver.geofence.services.rest.model.RESTInputUser;
import org.geoserver.geofence.services.rest.model.RESTOutputUser;
import org.geoserver.geofence.services.rest.model.RESTShortUserList;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Path("/")
public interface RESTUserService {

    /**
     * Returns a paginated list of users.
     *
     * @param nameLike An optional LIKE filter on the username.
     * @param page     In a paginated list, the page number, 0-based. If not null,
     *                 <code>entries</code> must be defined as well.
     * @param entries  In a paginated list, the number of entries per page. If not null,
     *                 <code>page</code> must be defined as well.
     * @throws BadRequestRestEx    (HTTP code 400) if page/entries do no match
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_XML)
    RESTShortUserList getList(
            @QueryParam("nameLike") String nameLike,
            @QueryParam("page") Integer page,
            @QueryParam("entries") Integer entries)
            throws BadRequestRestEx, InternalErrorRestEx;

    /**
     * @return {@link Long}
     */
    @GET
    @Path(value = "/count")
    Long count();

    /**
     * Count users.
     *
     * @param nameLike An optional LIKE filter on the username.
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @GET
    @Path("/count/{nameLike}")
    long count(@PathParam("nameLike") String nameLike);

    /**
     * Returns a single user.
     *
     * @param name The userName
     * @throws NotFoundRestEx      (HTTP code 404) if no user with given name exists
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @GET
    @Path("/name/{name}")
    @Produces(MediaType.APPLICATION_XML)
    RESTOutputUser get(@PathParam("name") String name) throws NotFoundRestEx, InternalErrorRestEx;

    /**
     * Inserts a new GSUser.
     *
     * @param user the GSUser as payload
     * @return the id of the newly created user, in plain text
     * @throws BadRequestRestEx    (HTTP code 400) if parameters are illegal
     * @throws NotFoundRestEx      (HTTP code 404) if the profile is not found
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @POST
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    Response insert(@Multipart("user") RESTInputUser user) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx, ConflictRestEx;

    /**
     * Updates a GSUser.
     *
     * @param id   The id of the user to update
     * @param user The new GSUser data as payload
     * @throws BadRequestRestEx    (HTTP code 400) if parameters are illegal
     * @throws NotFoundRestEx      (HTTP code 404) if the old user or the profile is not found
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @PUT
    @Path("/id/{id}")
    void update(@PathParam("id") Long id,
            @Multipart("user") RESTInputUser user) throws BadRequestRestEx, NotFoundRestEx;

    /**
     * Updates a GSUser.
     *
     * @param name The name of the user to update
     * @param user The new GSUser data as payload
     * @throws BadRequestRestEx    (HTTP code 400) if parameters are illegal
     * @throws NotFoundRestEx      (HTTP code 404) if the old user or the profile is not found
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @PUT
    @Path("/name/{name}")
    void update(@PathParam("name") String name,
            @Multipart("user") RESTInputUser user) throws BadRequestRestEx, NotFoundRestEx;

    /**
     * Deletes a GSUser.
     *
     * @param name    The name of the user to delete
     * @param cascade When true, also delete all the Rules referring to that user
     * @throws BadRequestRestEx    (HTTP code 400) if parameters are illegal
     * @throws NotFoundRestEx      (HTTP code 404) if the user is not found
     * @throws if                  the user is used in a rule and cascade is false
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @DELETE
    @Path("/name/{name}")
    Response delete(
            @PathParam("name") String name,
            @QueryParam("cascade") @DefaultValue("false") boolean cascade) throws ConflictRestEx, NotFoundRestEx, InternalErrorRestEx;

    //=========================================================================
    //=== Group association stuff
    //=========================================================================

    /**
     * Adds a user into a userGroup
     *
     * @param userName  The name of the user to assign
     * @param groupName The name of the group the user should be added into
     * @throws BadRequestRestEx    (HTTP code 400) if parameters are illegal
     * @throws NotFoundRestEx      (HTTP code 404) if the user or the group are not found
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @PUT
    @Path("/name/{userName}/group/name/{groupName}")
    void addIntoGroup(
            @PathParam("userName") String userName,
            @PathParam("groupName") String groupName)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    //=========================================================================
    //=== Group removal stuff
    //=========================================================================

    /**
     * Remove a user from a userGroup
     *
     * @param userName  The name of the user
     * @param groupName The name of the group the user should be removed from
     * @throws BadRequestRestEx    (HTTP code 400) if parameters are illegal
     * @throws NotFoundRestEx      (HTTP code 404) if the user or the group are not found
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @DELETE
    @Path("/name/{userName}/group/name/{groupName}")
    void removeFromGroup(
            @PathParam("userName") String userName,
            @PathParam("groupName") String groupName)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;
}

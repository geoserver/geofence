/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.interfaces;

import org.geofence.web.rest.api.exception.BadRequestRestEx;
import org.geofence.web.rest.api.exception.ConflictRestEx;
import org.geofence.web.rest.api.exception.InternalErrorRestEx;
import org.geofence.web.rest.api.exception.NotFoundRestEx;
import org.geofence.web.rest.api.model.RESTInputUser;
import org.geofence.web.rest.api.model.RESTOutputUser;
import org.geofence.web.rest.api.model.RESTShortUserList;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** @author Emanuele Tajariol (etj at geo-solutions.it) */
@RequestMapping("/user")
public interface RESTUserService {

    /**
     * Returns a paginated list of users.
     *
     * @param nameLike An optional LIKE filter on the username.
     * @param page In a paginated list, the page number, 0-based. If not null, <code>entries</code> must be defined as
     *     well.
     * @param entries In a paginated list, the number of entries per page. If not null, <code>page</code> must be
     *     defined as well.
     * @throws BadRequestRestEx (HTTP code 400) if page/entries do no match
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @GetMapping(
            path = "/",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    RESTShortUserList getList(
            @RequestParam(name = "nameLike", required = false) String nameLike,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "entries", required = false) Integer entries)
            throws BadRequestRestEx, InternalErrorRestEx;

    /**
     * Count users.
     *
     * @param nameLike An optional LIKE filter on the username.
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @GetMapping("/count/{nameLike}")
    long count(@PathVariable("nameLike") String nameLike);

    @GetMapping("/count")
    long count2(@RequestParam(name = "nameLike", required = false) String nameLike);

    /**
     * Returns a single user.
     *
     * @param name The userName
     * @throws NotFoundRestEx (HTTP code 404) if no user with given name exists
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @GetMapping(
            path = "/name/{name}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    RESTOutputUser get(@PathVariable("name") String name) throws NotFoundRestEx, InternalErrorRestEx;

    /**
     * Inserts a new GSUser. Also reachable as {@code multipart/form-data} (a "user" part) via
     * {@code RESTUserServiceImpl.insertMultipart} - not declared here since one interface method can't be mapped to two
     * different request content types.
     *
     * @param user the GSUser as payload
     * @return the id of the newly created user, in plain text
     * @throws BadRequestRestEx (HTTP code 400) if parameters are illegal
     * @throws NotFoundRestEx (HTTP code 404) if the profile is not found
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @PostMapping(
            path = "/",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<Long> insert(@RequestBody RESTInputUser user)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx, ConflictRestEx;

    /**
     * Updates a GSUser. Also reachable as {@code multipart/form-data} - see {@link #insert(RESTInputUser)}.
     *
     * @param id The id of the user to update
     * @param user The new GSUser data as payload
     * @throws BadRequestRestEx (HTTP code 400) if parameters are illegal
     * @throws NotFoundRestEx (HTTP code 404) if the old user or the profile is not found
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @PutMapping(
            path = "/id/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    void update(@PathVariable("id") Long id, @RequestBody RESTInputUser user) throws BadRequestRestEx, NotFoundRestEx;

    /**
     * Updates a GSUser. Also reachable as {@code multipart/form-data} - see {@link #insert(RESTInputUser)}.
     *
     * @param name The name of the user to update
     * @param user The new GSUser data as payload
     * @throws BadRequestRestEx (HTTP code 400) if parameters are illegal
     * @throws NotFoundRestEx (HTTP code 404) if the old user or the profile is not found
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @PutMapping(
            path = "/name/{name}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    void update(@PathVariable("name") String name, @RequestBody RESTInputUser user)
            throws BadRequestRestEx, NotFoundRestEx;

    /**
     * Deletes a GSUser.
     *
     * @param name The name of the user to delete
     * @param cascade When true, also delete all the Rules referring to that user
     * @throws BadRequestRestEx (HTTP code 400) if parameters are illegal
     * @throws NotFoundRestEx (HTTP code 404) if the user is not found
     * @throws if the user is used in a rule and cascade is false
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @DeleteMapping("/name/{name}")
    ResponseEntity<String> delete(
            @PathVariable("name") String name, @RequestParam(name = "cascade", defaultValue = "false") boolean cascade)
            throws ConflictRestEx, NotFoundRestEx, InternalErrorRestEx;

    // =========================================================================
    // === Group association stuff
    // =========================================================================

    /**
     * Adds a user into a userGroup
     *
     * @param userName The name of the user to assign
     * @param groupName The name of the group the user should be added into
     * @throws BadRequestRestEx (HTTP code 400) if parameters are illegal
     * @throws NotFoundRestEx (HTTP code 404) if the user or the group are not found
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @PutMapping("/name/{userName}/group/name/{groupName}")
    void addIntoGroup(@PathVariable("userName") String userName, @PathVariable("groupName") String groupName)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    // =========================================================================
    // === Group removal stuff
    // =========================================================================

    /**
     * Remove a user from a userGroup
     *
     * @param userName The name of the user
     * @param groupName The name of the group the user should be removed from
     * @throws BadRequestRestEx (HTTP code 400) if parameters are illegal
     * @throws NotFoundRestEx (HTTP code 404) if the user or the group are not found
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @DeleteMapping("/name/{userName}/group/name/{groupName}")
    void removeFromGroup(@PathVariable("userName") String userName, @PathVariable("groupName") String groupName)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.interfaces;

import org.geofence.web.rest.api.exception.BadRequestRestEx;
import org.geofence.web.rest.api.exception.ConflictRestEx;
import org.geofence.web.rest.api.exception.InternalErrorRestEx;
import org.geofence.web.rest.api.exception.NotFoundRestEx;
import org.geofence.web.rest.api.model.RESTInputGroup;
import org.geofence.web.rest.api.model.RESTOutputGroup;
import org.geofence.web.rest.api.model.config.RESTFullUserGroupList;
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
@RequestMapping("/usergroup")
public interface RESTUserGroupService {

    /** @return a sample user list */
    @GetMapping(
            path = "/",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    RESTFullUserGroupList getList(
            @RequestParam(name = "nameLike", required = false) String nameLike,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "entries", required = false) Integer entries)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    @GetMapping("/count/{nameLike}")
    long count(@PathVariable("nameLike") String nameLike);

    @GetMapping("/count")
    long count2(@RequestParam(name = "nameLike", required = false) String nameLike);

    @GetMapping(
            path = "/name/{name}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    RESTOutputGroup get(@PathVariable("name") String name) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    /**
     * Also reachable as {@code multipart/form-data} (a "userGroup" part) via
     * {@code RESTUserGroupServiceImpl.insertMultipart} - not declared here since one interface method can't be mapped
     * to two different request content types.
     */
    @PostMapping(
            path = "/",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<Long> insert(@RequestBody RESTInputGroup group)
            throws BadRequestRestEx, ConflictRestEx, InternalErrorRestEx;

    /** Also reachable as {@code multipart/form-data} - see {@link #insert(RESTInputGroup)}. */
    @PutMapping(
            path = "/name/{name}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    void update(@PathVariable("name") String name, @RequestBody RESTInputGroup group)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    /**
     * Deletes a UserGroup.
     *
     * @param name The name of the group to delete
     * @param cascade When true, also delete all the Rules referring to that group
     * @throws BadRequestRestEx (HTTP code 400) if parameters are illegal
     * @throws NotFoundRestEx (HTTP code 404) if the group is not found
     * @throws ConflictRestEx (HTTP code 409) if any rule refers to the group and cascade is false
     * @throws InternalErrorRestEx (HTTP code 500)
     */
    @DeleteMapping("/name/{name}")
    ResponseEntity<String> delete(
            @PathVariable("name") String name, @RequestParam(name = "cascade", defaultValue = "false") boolean cascade)
            throws ConflictRestEx, NotFoundRestEx, InternalErrorRestEx;
}

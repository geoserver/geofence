/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.interfaces;

import org.geofence.web.rest.api.exception.BadRequestRestEx;
import org.geofence.web.rest.api.exception.InternalErrorRestEx;
import org.geofence.web.rest.api.exception.NotFoundRestEx;
import org.geofence.web.rest.api.model.RESTBatch;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** @author Emanuele Tajariol (etj at geo-solutions.it) */
@RequestMapping("/config")
public interface RESTConfigService {
    @GetMapping(
            path = "/backup",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    RESTBatch backup(@RequestParam(name = "includeGRUsers", defaultValue = "false") Boolean includeGRUsers);

    /**
     * Also reachable as {@code multipart/form-data} (a "batch" part) via {@code RESTConfigServiceImpl
     * .restoreMultipart} - not declared here since one interface method can't be mapped to two different request
     * content types.
     */
    @PutMapping(
            path = "/restore",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    void restore(@RequestBody RESTBatch batch) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    @PutMapping("/cleanup")
    void cleanup() throws InternalErrorRestEx;

    @GetMapping(
            path = "/backup/groups",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    RESTBatch backupGroups();

    @GetMapping(
            path = "/backup/users",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    RESTBatch backupUsers();

    @GetMapping(
            path = "/backup/instances",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    RESTBatch backupInstances();

    @GetMapping(
            path = "/backup/rules",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    RESTBatch backupRules();
}

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/** @author Emanuele Tajariol (etj at geo-solutions.it) */
@RequestMapping("/batch")
public interface RESTBatchService {

    /**
     * Also reachable as {@code multipart/form-data} (a "batch" part) via {@code RESTBatchServiceImpl .execMultipart} -
     * not declared here since one interface method can't be mapped to two different request content types.
     */
    @PostMapping(
            path = "/exec",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<String> exec(@RequestBody RESTBatch batch)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    /** Similar to exec, but not transactional. Used internally. */
    void runBatch(RESTBatch batch) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;
}

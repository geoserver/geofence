/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.interfaces;

import org.geofence.web.rest.api.annotations.FilterBean;
import org.geofence.web.rest.api.exception.BadRequestRestEx;
import org.geofence.web.rest.api.exception.InternalErrorRestEx;
import org.geofence.web.rest.api.exception.NotFoundRestEx;
import org.geofence.web.rest.api.interfaces.params.RESTRuleFilter;
import org.geofence.web.rest.api.model.RESTInputRule;
import org.geofence.web.rest.api.model.RESTOutputRule;
import org.geofence.web.rest.api.model.RESTOutputRuleList;
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

/**
 * The {@code @FilterBean RESTRuleFilter} parameters are resolved from query-string parameters using the filter type's
 * own {@code @FilterParam}-annotated fields (see {@code QueryParamBeanArgumentResolver}) - unlike the DTO body types
 * elsewhere in this API, {@code RESTRuleFilter} is never itself the JSON/XML wire format for these endpoints.
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@RequestMapping("/rules")
public interface RESTRuleService {

    /**
     * Also reachable as {@code multipart/form-data} (a "rule" part) via {@code RESTRuleServiceImpl.insertMultipart} -
     * not declared here since one interface method can't be mapped to two different request content types.
     */
    @PostMapping(
            path = "/",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<Long> insert(@RequestBody RESTInputRule rule) throws BadRequestRestEx, NotFoundRestEx;

    @GetMapping(
            path = "/id/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    RESTOutputRule get(@PathVariable("id") Long id) throws BadRequestRestEx, NotFoundRestEx;

    /** Also reachable as {@code multipart/form-data} - see {@link #insert(RESTInputRule)}. */
    @PutMapping(
            path = "/id/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    void update(@PathVariable("id") Long id, @RequestBody RESTInputRule rule) throws BadRequestRestEx, NotFoundRestEx;

    @DeleteMapping(
            path = "/id/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<String> delete(@PathVariable("id") Long id) throws BadRequestRestEx, NotFoundRestEx;

    @GetMapping(
            path = "/",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    RESTOutputRuleList get(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "entries", required = false) Integer entries,
            @RequestParam(name = "full", defaultValue = "false") boolean full,
            @FilterBean RESTRuleFilter query)
            throws BadRequestRestEx, InternalErrorRestEx;

    @GetMapping("/count")
    long count(@FilterBean RESTRuleFilter query);

    /**
     * Move the provided rules to the target priority. Rules will be sorted by their priority, first rule will be
     * updated with a priority equal to the target priority and the next ones will get an incremented priority value.
     */
    @GetMapping(path = "/move", produces = MediaType.TEXT_XML_VALUE)
    ResponseEntity<String> move(
            @RequestParam(name = "rulesIds") String rulesIds,
            @RequestParam(name = "targetPriority") Integer targetPriority)
            throws BadRequestRestEx, InternalErrorRestEx;
}

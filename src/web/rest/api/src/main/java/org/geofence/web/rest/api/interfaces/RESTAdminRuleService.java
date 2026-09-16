/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.interfaces;

import org.geofence.web.rest.api.annotations.FilterBean;
import org.geofence.web.rest.api.exception.BadRequestRestEx;
import org.geofence.web.rest.api.exception.InternalErrorRestEx;
import org.geofence.web.rest.api.exception.NotFoundRestEx;
import org.geofence.web.rest.api.interfaces.params.RESTAdminRuleFilter;
import org.geofence.web.rest.api.model.RESTInputAdminRule;
import org.geofence.web.rest.api.model.RESTOutputAdminRule;
import org.geofence.web.rest.api.model.RESTOutputAdminRuleList;
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
 * The {@code @FilterBean RESTAdminRuleFilter} parameters are resolved from query-string parameters using the filter
 * type's own {@code @FilterParam}-annotated fields (see {@code QueryParamBeanArgumentResolver}).
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@RequestMapping("/adminrules")
public interface RESTAdminRuleService {

    /**
     * Also reachable as {@code multipart/form-data} (a "rule" part) via
     * {@code RESTAdminRuleServiceImpl.insertMultipart} - not declared here since one interface method can't be mapped
     * to two different request content types.
     */
    @PostMapping(
            path = "/",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<Long> insert(@RequestBody RESTInputAdminRule rule) throws BadRequestRestEx, NotFoundRestEx;

    @GetMapping(
            path = "/id/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    RESTOutputAdminRule get(@PathVariable("id") Long id) throws BadRequestRestEx, NotFoundRestEx;

    /** Also reachable as {@code multipart/form-data} - see {@link #insert(RESTInputAdminRule)}. */
    @PutMapping(
            path = "/id/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    void update(@PathVariable("id") Long id, @RequestBody RESTInputAdminRule rule)
            throws BadRequestRestEx, NotFoundRestEx;

    @DeleteMapping(
            path = "/id/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<String> delete(@PathVariable("id") Long id) throws BadRequestRestEx, NotFoundRestEx;

    @GetMapping(
            path = "/",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    RESTOutputAdminRuleList get(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "entries", required = false) Integer entries,
            @RequestParam(name = "full", defaultValue = "false") boolean full,
            @FilterBean RESTAdminRuleFilter query)
            throws BadRequestRestEx, InternalErrorRestEx;

    @GetMapping("/count")
    long count(@FilterBean RESTAdminRuleFilter query);
}

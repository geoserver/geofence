/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.interfaces;

import org.geofence.web.rest.api.exception.BadRequestRestEx;
import org.geofence.web.rest.api.interfaces.params.RESTRuleFilter;
import org.geofence.web.rest.api.model.RESTAccessInfo;
import org.geofence.web.rest.api.model.RESTShortRuleList;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * Runtime rule-evaluation API: given a {@link RESTRuleFilter} describing a request context (user, service, layer, ...),
 * returns the resulting access grant - as opposed to the rest of this REST layer, which is admin CRUD on rules
 * themselves.
 *
 * <p>Annotated with {@code @HttpExchange}/{@code @PostExchange} rather than {@code @RequestMapping}/
 * {@code @PostMapping}: Spring MVC's {@code RequestMappingHandlerMapping} natively registers
 * {@code @HttpExchange}-family methods as server endpoints (an alternative to {@code @RequestMapping} since Spring
 * 6.1), so this single interface is also used directly as the {@code GeoFenceClient} proxy contract via
 * {@code HttpServiceProxyFactory}. JSON-only (no {@code consumes}/{@code contentType} array support on
 * {@code @HttpExchange} - it takes one value, not a list): acceptable here since this interface's only real caller,
 * {@code GeoFenceClient}, always sends JSON.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@HttpExchange("/rulereader")
public interface RESTRuleReaderService {

    @PostExchange(
            url = "/accessinfo",
            contentType = MediaType.APPLICATION_JSON_VALUE,
            accept = MediaType.APPLICATION_JSON_VALUE)
    RESTAccessInfo getAccessInfo(@RequestBody RESTRuleFilter filter) throws BadRequestRestEx;

    @PostExchange(
            url = "/adminauthorization",
            contentType = MediaType.APPLICATION_JSON_VALUE,
            accept = MediaType.APPLICATION_JSON_VALUE)
    RESTAccessInfo getAdminAuthorization(@RequestBody RESTRuleFilter filter) throws BadRequestRestEx;

    @PostExchange(
            url = "/matchingrules",
            contentType = MediaType.APPLICATION_JSON_VALUE,
            accept = MediaType.APPLICATION_JSON_VALUE)
    RESTShortRuleList getMatchingRules(@RequestBody RESTRuleFilter filter) throws BadRequestRestEx;
}

/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.interfaces;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.geofence.web.rest.api.exception.BadRequestRestEx;
import org.geofence.web.rest.api.interfaces.params.RESTRuleFilter;
import org.geofence.web.rest.api.model.RESTAccessInfo;
import org.geofence.web.rest.api.model.RESTShortRuleList;

/**
 * Runtime rule-evaluation API: given a {@link RESTRuleFilter} describing a request context (user, service, layer, ...),
 * returns the resulting access grant - as opposed to the rest of this REST layer, which is admin CRUD on rules
 * themselves.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Path("/rulereader")
public interface RESTRuleReaderService {

    @POST
    @Path("/accessinfo")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    RESTAccessInfo getAccessInfo(RESTRuleFilter filter) throws BadRequestRestEx;

    @POST
    @Path("/adminauthorization")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    RESTAccessInfo getAdminAuthorization(RESTRuleFilter filter) throws BadRequestRestEx;

    @POST
    @Path("/matchingrules")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    RESTShortRuleList getMatchingRules(RESTRuleFilter filter) throws BadRequestRestEx;
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest;


import org.geoserver.geofence.services.rest.exception.BadRequestRestEx;
import org.geoserver.geofence.services.rest.exception.InternalErrorRestEx;
import org.geoserver.geofence.services.rest.exception.NotFoundRestEx;
import org.geoserver.geofence.services.rest.model.RESTBatch;
import org.geoserver.geofence.services.rest.model.RESTOutputRuleList;
import org.geoserver.geofence.services.rest.model.RESTShortInstanceList;
import org.geoserver.geofence.services.rest.model.RESTShortUserList;
import org.geoserver.geofence.services.rest.model.config.RESTConfigurationRemapping;
import org.geoserver.geofence.services.rest.model.config.RESTFullConfiguration;
import org.geoserver.geofence.services.rest.model.config.RESTFullUserGroupList;
import org.geoserver.geofence.services.rest.model.config.RESTFullUserList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;


/**
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */

@Path("/")
public interface RESTConfigService
{

    /**
     * @deprecated misbehaves since usergroups introduction. Please use backup()
     */
    @GET
    @Path("/full")
    @Produces(MediaType.APPLICATION_XML)
    RESTFullConfiguration getConfiguration(@QueryParam("includeGFUsers")
        @DefaultValue("False")
        Boolean includeGRUsers);

    @GET
    @Path("/backup")
    @Produces(MediaType.APPLICATION_XML)
    RESTBatch backup(@QueryParam("includeGFUsers")
        @DefaultValue("False")
        Boolean includeGRUsers);

    @PUT
    @Path("/restore")
    @Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    void restore(@Multipart("batch")RESTBatch batch)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    @PUT
    @Path("/cleanup")
    void cleanup()
            throws InternalErrorRestEx;

    @GET
    @Path("/backup/groups")
    @Produces(MediaType.APPLICATION_XML)
    RESTBatch backupGroups();

    @GET
    @Path("/backup/users")
    @Produces(MediaType.APPLICATION_XML)
    RESTBatch backupUsers();

    @GET
    @Path("/backup/instances")
    @Produces(MediaType.APPLICATION_XML)
    RESTBatch backupInstances();

    @GET
    @Path("/backup/rules")
    @Produces(MediaType.APPLICATION_XML)
    RESTBatch backupRules();

    /**
     * @deprecated
     */
    @PUT
    @Path("/full")
    @Produces(MediaType.APPLICATION_XML)
    RESTConfigurationRemapping setConfiguration(@Multipart("configuration") RESTFullConfiguration config,
        @QueryParam("includeGRUsers")
        @DefaultValue("False")
        Boolean includeGRUsers)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    /**
     * @deprecated
     */
    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_XML)
    RESTFullUserList getUsers()
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    /**
     * @deprecated
     */
    @GET
    @Path("/groups")
    @Produces(MediaType.APPLICATION_XML)
    RESTFullUserGroupList getUserGroups()
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    //====

    /**
     * @deprecated used for testing only
     */
    @POST
    @Path("/groups")
    @Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    void setUserGroups(@Multipart("groups")RESTFullUserGroupList groups)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    /**
     * only for debug/quick insert
     * takes as input the same xml returned by the related service GET operation
     *
     * @deprecated used for testing only
     */
    @POST
    @Path("/users/short")
    @Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    void setUsers(@Multipart("users")RESTShortUserList users)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    /**
     * @deprecated used for testing only
     */
    @POST
    @Path("/instances/short")
    @Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    void setInstances(@Multipart("instances")RESTShortInstanceList instances)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    /**
     * @deprecated used for testing only
     */
    @POST
    @Path("/rules/short")
    @Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    void setRules(@Multipart("rules")RESTOutputRuleList rules)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

}

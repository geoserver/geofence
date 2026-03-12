/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.interfaces;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.geofence.web.rest.api.exception.BadRequestRestEx;
import org.geofence.web.rest.api.exception.InternalErrorRestEx;
import org.geofence.web.rest.api.exception.NotFoundRestEx;
import org.geofence.web.rest.api.model.RESTBatch;
import org.glassfish.jersey.media.multipart.FormDataParam;

/** @author Emanuele Tajariol (etj at geo-solutions.it) */
@Path("/batch")
public interface RESTBatchService {
    @POST
    @Path("/exec")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    Response exec(@FormDataParam("batch") RESTBatch batch) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;

    /** Similar to exec, but not transaction. Used internally. */
    void runBatch(RESTBatch batch) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx;
}

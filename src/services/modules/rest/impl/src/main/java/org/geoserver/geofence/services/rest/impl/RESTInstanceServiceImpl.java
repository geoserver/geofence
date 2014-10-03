/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.impl;

import org.geoserver.geofence.core.model.GSInstance;
import java.util.List;

import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.core.model.util.PwEncoder;
import org.geoserver.geofence.services.InstanceAdminService;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.RuleFilter.SpecialFilterType;
import org.geoserver.geofence.services.dto.ShortGroup;
import org.geoserver.geofence.services.dto.ShortInstance;
import org.geoserver.geofence.services.exception.BadRequestServiceEx;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;
import org.geoserver.geofence.services.rest.RESTGSInstanceService;
import org.geoserver.geofence.services.rest.exception.BadRequestRestEx;
import org.geoserver.geofence.services.rest.exception.ConflictRestEx;
import org.geoserver.geofence.services.rest.exception.GeoFenceRestEx;
import org.geoserver.geofence.services.rest.exception.InternalErrorRestEx;
import org.geoserver.geofence.services.rest.exception.NotFoundRestEx;
import org.geoserver.geofence.services.rest.model.RESTInputGroup;
import org.geoserver.geofence.services.rest.model.RESTInputInstance;
import org.geoserver.geofence.services.rest.model.RESTOutputInstance;
import org.geoserver.geofence.services.rest.model.RESTShortInstanceList;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RESTInstanceServiceImpl
        extends BaseRESTServiceImpl
        implements RESTGSInstanceService {

    private static final Logger LOGGER = LogManager.getLogger(RESTInstanceServiceImpl.class);
    private InstanceAdminService instanceAdminService;

    @Override
    public RESTShortInstanceList getList(String nameLike, Integer page, Integer entries) {
        List<ShortInstance> list = instanceAdminService.getList(nameLike, page, entries);
        return new RESTShortInstanceList(list);
    }

    @Override
    public long count(String nameLike) {
        return instanceAdminService.getCount(nameLike);
    }

    @Override
    public RESTOutputInstance get(Long id) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        try {
            GSInstance ret = instanceAdminService.get(id);
            return toOutputInstance(ret);
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("GSInstance not found: " + id);
            throw new NotFoundRestEx("GSInstance not found: " + id);
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    @Override
    public RESTOutputInstance get(String name) throws NotFoundRestEx, InternalErrorRestEx {
        try {
            GSInstance ret = instanceAdminService.get(name);
            return toOutputInstance(ret);
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("GSInstance not found: " + name);
            throw new NotFoundRestEx("GSInstance not found: " + name);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    @Override
    public Response insert(RESTInputInstance instance) throws NotFoundRestEx, InternalErrorRestEx, ConflictRestEx {

        // check that no group with same name exists
        boolean exists;
        try {
            instanceAdminService.get(instance.getName());
            exists = true;
        } catch (NotFoundServiceEx ex) {
            // well, ok, instance does not exist
            exists = false;
        } catch (Exception ex) {
            // something went wrong
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }

        if(exists)
            throw new ConflictRestEx("GSInstance '"+instance.getName()+"' already exists");

        // ok: insert it
        try {
            GSInstance insert = new GSInstance();
            insert.setName(instance.getName());
            insert.setDescription(instance.getDescription());
            insert.setBaseURL(instance.getBaseURL());
            insert.setUsername(instance.getUsername());
            insert.setPassword(instance.getPassword());

            Long id = instanceAdminService.insert(insert);
            return Response.status(Status.CREATED).tag(id.toString()).entity(id).build();

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    @Override
    public void update(String name, RESTInputInstance instance) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        try {
            GSInstance old = instanceAdminService.get(name);
            update(old.getId(), instance);
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("GSInstance not found: " + name);
            throw new NotFoundRestEx("GSInstance not found: " + name);
        }
    }

    @Override
    public void update(Long id, RESTInputInstance instance) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {

        try {
            GSInstance old = instanceAdminService.get(id);

            if ( (instance.getName() != null) ) {
                throw new BadRequestRestEx("Name can't be updated");
            }

            // TODO: TO BE FIXED
            // the instance update is not homogeneous with the other services
            // where a DTO is used, and null checks are performed in the service
            
            if(instance.getDescription() != null )
                old.setDescription(instance.getDescription());

            if(instance.getBaseURL() != null )
                old.setBaseURL(instance.getBaseURL());

            if(instance.getUsername() != null )
                old.setUsername(instance.getUsername());

            if(instance.getPassword() != null )
                old.setPassword(instance.getPassword());

            instanceAdminService.update(old);

        } catch (GeoFenceRestEx ex) {
            // already handled
            throw ex;
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("GSInstance not found id: " + id + ": " + ex.getMessage(), ex);
            throw new NotFoundRestEx(ex.getMessage());
        } catch (BadRequestServiceEx ex) {
            LOGGER.warn("Problems updating GSInstance id:" + id + ": " + ex.getMessage(), ex);
            throw new BadRequestRestEx(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    @Override
    public Response delete(Long id, boolean cascade) throws ConflictRestEx, NotFoundRestEx, InternalErrorRestEx {
        try {
            if ( cascade ) {
                ruleAdminService.deleteRulesByInstance(id);
            } else {
                RuleFilter filter = new RuleFilter(SpecialFilterType.ANY);
                filter.setInstance(id);
                filter.getInstance().setIncludeDefault(false);
                long cnt = ruleAdminService.count(filter);
                if ( cnt > 0 ) {
                    throw new ConflictRestEx("Existing rules reference the GSInstance " + id);
                }
            }

            if ( ! instanceAdminService.delete(id)) {
                LOGGER.warn("GSInstance not found: " + id);
                throw new NotFoundRestEx("GSInstance not found: " + id);
            }

            return Response.status(Status.OK).entity("OK\n").build();

        } catch (GeoFenceRestEx ex) { // already handled
            throw ex;
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("GSInstance not found: " + id);
            throw new NotFoundRestEx("GSInstance not found: " + id);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    @Override
    public Response delete(String name, boolean cascade) throws ConflictRestEx, NotFoundRestEx, InternalErrorRestEx {
        try {
            long id = instanceAdminService.get(name).getId();
            this.delete(id, cascade);

            return Response.status(Status.OK).entity("OK\n").build();
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("GSInstance not found: " + name);
            throw new NotFoundRestEx("GSInstance not found: " + name);
        } catch (GeoFenceRestEx ex) { // already handled
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }

    }

    // ==========================================================================
    // ==========================================================================
    private RESTOutputInstance toOutputInstance(GSInstance i) {
        RESTOutputInstance ret = new RESTOutputInstance();
        ret.setId(i.getId());
        ret.setName(i.getName());
        ret.setDescription(i.getDescription());
        ret.setBaseURL(i.getBaseURL());
        ret.setUsername(i.getUsername());
        ret.setPassword(PwEncoder.encode(i.getPassword()));
        ret.setCreationDate(i.getDateCreation().toString());
        return ret;
    }

    // ==========================================================================
    // ==========================================================================
    
//    public void setUserGroupAdminService(UserGroupAdminService service) {
//        this.userGroupAdminService = service;
//    }

    public void setInstanceAdminService(InstanceAdminService instanceAdminService) {
        this.instanceAdminService = instanceAdminService;
    }

}

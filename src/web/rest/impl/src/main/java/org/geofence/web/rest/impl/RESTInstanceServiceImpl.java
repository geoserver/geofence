/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.impl;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.model.GSInstance;
import org.geofence.core.model.util.PwEncoder;
import org.geofence.core.services.dto.RuleFilter;
import org.geofence.core.services.dto.RuleFilter.SpecialFilterType;
import org.geofence.core.services.dto.ShortInstance;
import org.geofence.core.services.exception.BadRequestServiceEx;
import org.geofence.core.services.exception.NotFoundServiceEx;
import org.geofence.web.rest.api.exception.BadRequestRestEx;
import org.geofence.web.rest.api.exception.ConflictRestEx;
import org.geofence.web.rest.api.exception.GeoFenceRestEx;
import org.geofence.web.rest.api.exception.InternalErrorRestEx;
import org.geofence.web.rest.api.exception.NotFoundRestEx;
import org.geofence.web.rest.api.interfaces.RESTGSInstanceService;
import org.geofence.web.rest.api.model.RESTInputInstance;
import org.geofence.web.rest.api.model.RESTOutputInstance;
import org.geofence.web.rest.api.model.RESTShortInstanceList;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

/** @author ETj (etj at geo-solutions.it) */
@Service
@RestController
public class RESTInstanceServiceImpl extends BaseRESTServiceImpl implements RESTGSInstanceService {

    private static final Logger LOGGER = LogManager.getLogger(RESTInstanceServiceImpl.class);

    @Override
    public RESTShortInstanceList getList(String nameLike, Integer page, Integer entries) {
        List<ShortInstance> list = instanceAdminService.getList(nameLike, page, entries);
        return new RESTShortInstanceList(
                list.stream().map(i -> RESTMapper.map(i)).toList());
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
    public ResponseEntity<Long> insert(RESTInputInstance instance)
            throws NotFoundRestEx, InternalErrorRestEx, ConflictRestEx {

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

        if (exists) throw new ConflictRestEx("GSInstance '" + instance.getName() + "' already exists");

        // ok: insert it
        try {
            GSInstance insert = new GSInstance();
            insert.setName(instance.getName());
            insert.setDescription(instance.getDescription());
            insert.setBaseURL(instance.getBaseURL());
            insert.setUsername(instance.getUsername());
            insert.setPassword(instance.getPassword());

            Long id = instanceAdminService.insert(insert);
            return ResponseEntity.status(HttpStatus.CREATED).eTag(id.toString()).body(id);

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    /**
     * Legacy {@code multipart/form-data} entry point (an "instance" part), for callers not yet sending JSON/XML bodies.
     */
    @PostMapping(path = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> insertMultipart(@RequestPart("instance") RESTInputInstance instance)
            throws NotFoundRestEx, InternalErrorRestEx, ConflictRestEx {
        return insert(instance);
    }

    @Override
    public void update(String name, RESTInputInstance instance)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        try {
            GSInstance old = instanceAdminService.get(name);
            update(old.getId(), instance);
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("GSInstance not found: " + name);
            throw new NotFoundRestEx("GSInstance not found: " + name);
        }
    }

    /** Legacy {@code multipart/form-data} entry point - see {@link #insertMultipart(RESTInputInstance)}. */
    @PutMapping(path = "/name/{name}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateMultipartByName(
            @PathVariable("name") String name, @RequestPart("instance") RESTInputInstance instance)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        update(name, instance);
    }

    @Override
    public void update(Long id, RESTInputInstance instance)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {

        try {
            GSInstance old = instanceAdminService.get(id);

            if ((instance.getName() != null)) {
                throw new BadRequestRestEx("Name can't be updated");
            }

            // TODO: TO BE FIXED
            // the instance update is not homogeneous with the other services
            // where a DTO is used, and null checks are performed in the service

            if (instance.getDescription() != null) old.setDescription(instance.getDescription());

            if (instance.getBaseURL() != null) old.setBaseURL(instance.getBaseURL());

            if (instance.getUsername() != null) old.setUsername(instance.getUsername());

            if (instance.getPassword() != null) old.setPassword(instance.getPassword());

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

    /** Legacy {@code multipart/form-data} entry point - see {@link #insertMultipart(RESTInputInstance)}. */
    @PutMapping(path = "/id/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateMultipartById(@PathVariable("id") Long id, @RequestPart("instance") RESTInputInstance instance)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        update(id, instance);
    }

    @Override
    public ResponseEntity<String> delete(Long id, boolean cascade)
            throws ConflictRestEx, NotFoundRestEx, InternalErrorRestEx {
        try {
            if (cascade) {
                ruleAdminService.deleteRulesByInstance(id);
            } else {
                RuleFilter filter = new RuleFilter(SpecialFilterType.ANY);
                filter.setInstance(id);
                filter.getInstance().setIncludeDefault(false);
                long cnt = ruleAdminService.count(filter);
                if (cnt > 0) {
                    throw new ConflictRestEx("Existing rules reference the GSInstance " + id);
                }
            }

            if (!instanceAdminService.delete(id)) {
                LOGGER.warn("GSInstance not found: " + id);
                throw new NotFoundRestEx("GSInstance not found: " + id);
            }

            return ResponseEntity.ok("OK\n");

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
    public ResponseEntity<String> delete(String name, boolean cascade)
            throws ConflictRestEx, NotFoundRestEx, InternalErrorRestEx {
        try {
            long id = instanceAdminService.get(name).getId();
            this.delete(id, cascade);

            return ResponseEntity.ok("OK\n");
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
}

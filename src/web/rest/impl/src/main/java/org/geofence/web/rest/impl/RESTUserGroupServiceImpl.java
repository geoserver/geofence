/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.impl;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.model.UserGroup;
import org.geofence.core.services.dto.RuleFilter;
import org.geofence.core.services.dto.RuleFilter.SpecialFilterType;
import org.geofence.core.services.dto.ShortGroup;
import org.geofence.core.services.exception.BadRequestServiceEx;
import org.geofence.core.services.exception.NotFoundServiceEx;
import org.geofence.web.rest.api.exception.BadRequestRestEx;
import org.geofence.web.rest.api.exception.ConflictRestEx;
import org.geofence.web.rest.api.exception.GeoFenceRestEx;
import org.geofence.web.rest.api.exception.InternalErrorRestEx;
import org.geofence.web.rest.api.exception.NotFoundRestEx;
import org.geofence.web.rest.api.interfaces.RESTUserGroupService;
import org.geofence.web.rest.api.model.RESTInputGroup;
import org.geofence.web.rest.api.model.RESTOutputGroup;
import org.geofence.web.rest.api.model.config.RESTFullUserGroupList;
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
public class RESTUserGroupServiceImpl extends BaseRESTServiceImpl implements RESTUserGroupService {

    private static final Logger LOGGER = LogManager.getLogger(RESTUserGroupServiceImpl.class);

    @Override
    public RESTFullUserGroupList getList(String nameLike, Integer page, Integer entries) {
        List<ShortGroup> list = userGroupAdminService.getList(nameLike, page, entries);
        return new RESTFullUserGroupList(
                list.stream().map(i -> RESTMapper.map(i)).toList());
    }

    @Override
    public long count(String nameLike) {
        return userGroupAdminService.getCount(nameLike);
    }

    @Override
    public long count2(String nameLike) {
        return count(nameLike);
    }

    @Override
    public RESTOutputGroup get(String name) throws NotFoundRestEx, InternalErrorRestEx {
        try {
            UserGroup ret = userGroupAdminService.get(name);
            return RESTMapper.map(ret);
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("Role not found: " + name);
            throw new NotFoundRestEx("Role not found: " + name);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<Long> insert(RESTInputGroup userGroup)
            throws NotFoundRestEx, InternalErrorRestEx, ConflictRestEx {

        // check that no group with same name exists
        boolean exists;
        try {
            userGroupAdminService.get(userGroup.getName());
            exists = true;
        } catch (NotFoundServiceEx ex) {
            // well, ok, usergroup does not exist
            exists = false;
        } catch (Exception ex) {
            // something went wrong
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }

        if (exists) throw new ConflictRestEx("Role '" + userGroup.getName() + "' already exists");

        // ok: insert it
        try {
            ShortGroup insert = new ShortGroup();
            insert.setEnabled(userGroup.isEnabled());
            insert.setExtId(userGroup.getExtId());
            insert.setName(userGroup.getName());

            Long id = userGroupAdminService.insert(insert);
            return ResponseEntity.status(HttpStatus.CREATED).eTag(id.toString()).body(id);

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    /**
     * Legacy {@code multipart/form-data} entry point (a "userGroup" part), for callers not yet sending JSON/XML bodies.
     */
    @PostMapping(path = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> insertMultipart(@RequestPart("userGroup") RESTInputGroup userGroup)
            throws NotFoundRestEx, InternalErrorRestEx, ConflictRestEx {
        return insert(userGroup);
    }

    @Override
    public void update(String name, RESTInputGroup group) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {

        try {
            UserGroup old = userGroupAdminService.get(name);

            ShortGroup newGroup = new ShortGroup();
            newGroup.setId(old.getId());

            if ((group.getExtId() != null)) {
                throw new BadRequestRestEx("ExtId can't be updated");
            }

            if ((group.getName() != null)) {
                throw new BadRequestRestEx("Name can't be updated");
            }

            if (group.isEnabled() != null) {
                newGroup.setEnabled(group.isEnabled());
            }

            userGroupAdminService.update(newGroup);

        } catch (GeoFenceRestEx ex) {
            // already handled
            throw ex;
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("Role not found: " + name + ": " + ex.getMessage(), ex);
            throw new NotFoundRestEx(ex.getMessage());
        } catch (BadRequestServiceEx ex) {
            LOGGER.warn("Problems updating role:" + name + ": " + ex.getMessage(), ex);
            throw new BadRequestRestEx(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    /**
     * Legacy {@code multipart/form-data} entry point (a "userGroup" part), for callers not yet sending JSON/XML bodies.
     */
    @PutMapping(path = "/name/{name}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateMultipart(@PathVariable("name") String name, @RequestPart("userGroup") RESTInputGroup group)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        update(name, group);
    }

    @Override
    public ResponseEntity<String> delete(String name, boolean cascade)
            throws ConflictRestEx, NotFoundRestEx, InternalErrorRestEx {
        try {
            if (cascade) {
                ruleAdminService.deleteRulesByRole(name);
            } else {
                RuleFilter filter = new RuleFilter(SpecialFilterType.ANY);
                filter.setRole(name);
                filter.getRole().setIncludeDefault(false);
                long cnt = ruleAdminService.count(filter);
                if (cnt > 0) {
                    throw new ConflictRestEx("Existing rules reference the role " + name);
                }
            }

            UserGroup role = userGroupAdminService.get(name);

            if (!userGroupAdminService.delete(role.getId())) {
                LOGGER.warn("Role not found: " + name);
                throw new NotFoundRestEx("Role not found: " + name);
            }

            return ResponseEntity.ok("OK\n");

        } catch (GeoFenceRestEx ex) { // already handled
            throw ex;
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("Role not found: " + name);
            throw new NotFoundRestEx("Role not found: " + name);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }
}

/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.impl;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
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
import org.springframework.stereotype.Service;

/** @author ETj (etj at geo-solutions.it) */
@Service
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
    public Response insert(RESTInputGroup userGroup) throws NotFoundRestEx, InternalErrorRestEx, ConflictRestEx {

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
            return Response.status(Status.CREATED).tag(id.toString()).entity(id).build();

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
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

    @Override
    public Response delete(String name, boolean cascade) throws ConflictRestEx, NotFoundRestEx, InternalErrorRestEx {
        try {
            if (cascade) {
                ruleAdminService.deleteRulesByRole(name);
            } else {
                RuleFilter filter = new RuleFilter(SpecialFilterType.ANY);
                filter.setRole(name);
                filter.getUser().setIncludeDefault(false);
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

            return Response.status(Status.OK).entity("OK\n").build();

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

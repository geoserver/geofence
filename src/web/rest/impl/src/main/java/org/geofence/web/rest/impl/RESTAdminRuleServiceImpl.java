/* (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.impl;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.model.AdminRule;
import org.geofence.core.model.enums.InsertPosition;
import org.geofence.core.services.dto.RuleFilter;
import org.geofence.core.services.dto.RuleFilter.IdNameFilter;
import org.geofence.core.services.dto.RuleFilter.SpecialFilterType;
import org.geofence.core.services.dto.RuleFilter.TextFilter;
import org.geofence.core.services.exception.BadRequestServiceEx;
import org.geofence.core.services.exception.NotFoundServiceEx;
import org.geofence.web.rest.api.exception.BadRequestRestEx;
import org.geofence.web.rest.api.exception.GeoFenceRestEx;
import org.geofence.web.rest.api.exception.InternalErrorRestEx;
import org.geofence.web.rest.api.exception.NotFoundRestEx;
import org.geofence.web.rest.api.interfaces.RESTAdminRuleService;
import org.geofence.web.rest.api.interfaces.params.RESTAdminRuleFilter;
import org.geofence.web.rest.api.model.RESTInputAdminRule;
import org.geofence.web.rest.api.model.RESTOutputAdminRule;
import org.geofence.web.rest.api.model.RESTOutputAdminRuleList;
import org.geofence.web.rest.api.model.RESTRulePosition.RESTPositionReference;
import org.geofence.web.rest.api.model.util.IdName;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** @author ETj (etj at geo-solutions.it) */
@Service
public class RESTAdminRuleServiceImpl extends BaseRESTServiceImpl implements RESTAdminRuleService {

    private static final Logger LOGGER = LogManager.getLogger(RESTAdminRuleServiceImpl.class);

    @Override
    public RESTOutputAdminRule get(Long id) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        try {
            AdminRule ret = adminRuleAdminService.get(id);
            return toOutput(ret);
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("AdminRule not found: " + id);
            throw new NotFoundRestEx("AdminRule not found: " + id);
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, value = "geofenceTransactionManager")
    public Response insert(RESTInputAdminRule inputAdminRule)
            throws NotFoundRestEx, BadRequestRestEx, InternalErrorRestEx {
        if (inputAdminRule.getPosition() == null || inputAdminRule.getPosition().getPosition() == null) {
            throw new BadRequestRestEx("Bad position: " + inputAdminRule.getPosition());
        }

        if (inputAdminRule.getGrant() == null) {
            throw new BadRequestRestEx("Missing grant type");
        }

        AdminRule rule = fromInput(inputAdminRule);

        InsertPosition position = inputAdminRule.getPosition().getPosition() == RESTPositionReference.fixedPriority
                ? InsertPosition.FIXED
                : inputAdminRule.getPosition().getPosition() == RESTPositionReference.offsetFromBottom
                        ? InsertPosition.FROM_END
                        : inputAdminRule.getPosition().getPosition() == RESTPositionReference.offsetFromTop
                                ? InsertPosition.FROM_START
                                : null;

        // ok: insert it
        try {
            Long id = adminRuleAdminService.insert(rule, position);

            return Response.status(Status.CREATED).tag(id.toString()).entity(id).build();
        } catch (BadRequestServiceEx ex) {
            LOGGER.error(ex.getMessage());
            throw new BadRequestRestEx(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    @Override
    public void update(Long id, RESTInputAdminRule rule) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {

        try {
            if ((rule.getPosition() != null)) {
                throw new BadRequestRestEx("Position can't be updated");
            }

            AdminRule old = adminRuleAdminService.get(id);
            boolean isRuleUpdated = false;

            if (rule.getUsername() != null) {
                old.setUsername(rule.getUsername().isEmpty() ? null : rule.getUsername());
                isRuleUpdated = true;
            }
            if (rule.getRolename() != null) {
                old.setRolename(rule.getRolename().isEmpty() ? null : rule.getRolename());
                isRuleUpdated = true;
            }
            if (rule.getInstance() != null) {
                IdName idname = rule.getInstance();
                old.setInstance(idname.getId() == null && idname.getName() == null ? null : getInstance(idname));
                isRuleUpdated = true;
            }

            if (rule.getWorkspace() != null) {
                old.setWorkspace(rule.getWorkspace().isEmpty() ? null : rule.getWorkspace());
                isRuleUpdated = true;
            }

            if (rule.getGrant() != null) {
                old.setAccess(RESTMapper.map(rule.getGrant()));
                isRuleUpdated = true;
            }

            // now persist the new data

            if (isRuleUpdated) {
                if (LOGGER.isDebugEnabled()) LOGGER.debug("Updating rule " + rule);
                adminRuleAdminService.update(old);
            } else {
                if (LOGGER.isDebugEnabled()) LOGGER.debug("Rule not changed " + rule);
            }

        } catch (GeoFenceRestEx ex) {
            // already handled
            throw ex;
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("AdminRule not found id: " + id + ": " + ex.getMessage(), ex);
            throw new NotFoundRestEx(ex.getMessage());
        } catch (BadRequestServiceEx ex) {
            LOGGER.warn("Problems updating AdminRule id:" + id + ": " + ex.getMessage(), ex);
            throw new BadRequestRestEx(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error("Unexpected exception: " + ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    @Override
    public Response delete(Long id) throws NotFoundRestEx, InternalErrorRestEx {
        try {
            if (!adminRuleAdminService.delete(id)) {
                LOGGER.warn("Rule not found: " + id);
                throw new NotFoundRestEx("Rule not found: " + id);
            }

            return Response.status(Status.OK).entity("OK\n").build();

        } catch (GeoFenceRestEx ex) { // already handled
            throw ex;
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("Group not found: " + id);
            throw new NotFoundRestEx("Group not found: " + id);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    // ==========================================================================

    @Override
    public RESTOutputAdminRuleList get(Integer page, Integer entries, boolean full, RESTAdminRuleFilter query)
            throws BadRequestRestEx, InternalErrorRestEx {
        RuleFilter filter = buildFilter(query);

        try {
            List<AdminRule> listFull = adminRuleAdminService.getListFull(filter, page, entries);
            return toOutput(listFull);
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    @Override
    public long count(RESTAdminRuleFilter query) throws BadRequestRestEx, InternalErrorRestEx {
        try {
            return adminRuleAdminService.count(buildFilter(query));
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    // ==========================================================================

    protected RuleFilter buildFilter(RESTAdminRuleFilter query) throws BadRequestRestEx {

        // coalesce deprecated "any" flag
        if (query.userDefault == null) query.userDefault = query.userAny;
        if (query.groupDefault == null) query.groupDefault = query.groupAny;
        if (query.instanceDefault == null) query.instanceDefault = query.instanceAny;
        if (query.workspaceDefault == null) query.workspaceDefault = query.workspaceAny;

        RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);

        setFilter(filter.getUser(), query.userName, query.userDefault);
        setFilter(filter.getRole(), query.groupName, query.groupDefault);
        setFilter(filter.getInstance(), query.instanceId, query.instanceName, query.instanceDefault);
        setFilter(filter.getWorkspace(), query.workspace, query.workspaceDefault);
        return filter;
    }

    private void setFilter(IdNameFilter filter, Long id, String name, Boolean includeDefault) throws BadRequestRestEx {

        if (id != null && name != null) {
            throw new BadRequestRestEx("Id and name can't be both defined (id:" + id + " name:" + name + ")");
        }

        if (id != null) {
            filter.setId(id);
            if (includeDefault != null) {
                filter.setIncludeDefault(includeDefault);
            }
        } else if (name != null) {
            filter.setName(name);
            if (includeDefault != null) {
                filter.setIncludeDefault(includeDefault);
            }
        } else {
            if (BooleanUtils.isTrue(includeDefault)) {
                filter.setType(SpecialFilterType.DEFAULT);
            } else {
                filter.setType(SpecialFilterType.ANY);
            }
        }
    }

    private void setFilter(TextFilter filter, String name, Boolean includeDefault) {

        if (name != null) {
            filter.setText(name);
            if (includeDefault != null) {
                filter.setIncludeDefault(includeDefault);
            }
        } else {
            if (BooleanUtils.isTrue(includeDefault)) {
                filter.setType(SpecialFilterType.DEFAULT);
            } else {
                filter.setType(SpecialFilterType.ANY);
            }
        }
    }

    // ==========================================================================

    protected RESTOutputAdminRuleList toOutput(List<AdminRule> rules) {
        RESTOutputAdminRuleList list = new RESTOutputAdminRuleList(rules.size());
        for (AdminRule rule : rules) {
            list.add(toOutput(rule));
        }
        return list;
    }

    // ==========================================================================
    protected RESTOutputAdminRule toOutput(AdminRule rule) {
        RESTOutputAdminRule out = new RESTOutputAdminRule();
        out.setId(rule.getId());
        out.setPriority(rule.getPriority());
        out.setGrant(RESTMapper.map(rule.getAccess()));

        out.setUsername(rule.getUsername());
        out.setRolename(rule.getRolename());
        if (rule.getInstance() != null) {
            out.setInstance(
                    new IdName(rule.getInstance().getId(), rule.getInstance().getName()));
        }
        out.setWorkspace(rule.getWorkspace());

        return out;
    }

    protected AdminRule fromInput(RESTInputAdminRule in) {
        AdminRule rule = new AdminRule();

        rule.setPriority(in.getPosition().getValue());

        rule.setAccess(RESTMapper.map(in.getGrant()));

        rule.setUsername(in.getUsername());
        rule.setRolename(in.getRolename());

        if (in.getInstance() != null) {
            rule.setInstance(getInstance(in.getInstance()));
        }

        rule.setWorkspace(in.getWorkspace());

        return rule;
    }
}

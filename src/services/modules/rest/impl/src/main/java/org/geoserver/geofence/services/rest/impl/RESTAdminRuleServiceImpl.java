/* (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.impl;

import java.util.List;

import org.geoserver.geofence.core.model.AdminRule;
import org.geoserver.geofence.core.model.enums.InsertPosition;

import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.RuleFilter.IdNameFilter;
import org.geoserver.geofence.services.dto.RuleFilter.TextFilter;
import org.geoserver.geofence.services.dto.RuleFilter.SpecialFilterType;
import org.geoserver.geofence.services.exception.BadRequestServiceEx;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;
import org.geoserver.geofence.services.rest.exception.BadRequestRestEx;
import org.geoserver.geofence.services.rest.exception.GeoFenceRestEx;
import org.geoserver.geofence.services.rest.exception.InternalErrorRestEx;
import org.geoserver.geofence.services.rest.exception.NotFoundRestEx;
import org.geoserver.geofence.services.rest.model.util.IdName;
import org.geoserver.geofence.services.rest.model.RESTInputAdminRule;
import org.geoserver.geofence.services.rest.model.RESTOutputAdminRule;
import org.geoserver.geofence.services.rest.model.RESTOutputAdminRuleList;
import org.geoserver.geofence.services.rest.model.RESTRulePosition.RulePosition;
import org.geoserver.geofence.services.rest.RESTAdminRuleService;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RESTAdminRuleServiceImpl
        extends BaseRESTServiceImpl
        implements RESTAdminRuleService {

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
    public Response insert(RESTInputAdminRule inputAdminRule) throws NotFoundRestEx, BadRequestRestEx, InternalErrorRestEx {

        if (inputAdminRule.getPosition() == null || inputAdminRule.getPosition().getPosition() == null) {
            throw new BadRequestRestEx("Bad position: " + inputAdminRule.getPosition());
        }

        if (inputAdminRule.getGrant() == null) {
            throw new BadRequestRestEx("Missing grant type");
        }

        AdminRule rule = fromInput(inputAdminRule);

        InsertPosition position =
                inputAdminRule.getPosition().getPosition() == RulePosition.fixedPriority ? InsertPosition.FIXED
                : inputAdminRule.getPosition().getPosition() == RulePosition.offsetFromBottom ? InsertPosition.FROM_END
                : inputAdminRule.getPosition().getPosition() == RulePosition.offsetFromTop ? InsertPosition.FROM_START : null;

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

            if (rule.getUsername()!= null) {
                old.setUsername(rule.getUsername().isEmpty()? null : rule.getUsername());
                isRuleUpdated = true;
            }
            if (rule.getRolename() != null) {
                old.setRolename(rule.getRolename().isEmpty()? null : rule.getRolename());
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
                old.setAccess(rule.getGrant());
                isRuleUpdated = true;
            }

            // now persist the new data

            if(isRuleUpdated) {
                if(LOGGER.isDebugEnabled())
                    LOGGER.debug("Updating rule " + rule);
                adminRuleAdminService.update(old);
            } else {
                if(LOGGER.isDebugEnabled())
                    LOGGER.debug("Rule not changed " + rule);
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
    // ==========================================================================

    @Override
    public RESTOutputAdminRuleList get(Integer page, Integer entries,
            boolean full,
            String userName, Boolean userDefault,
            String roleName, Boolean roleDefault,
            Long instanceId, String instanceName, Boolean instanceDefault,
            String workspace, Boolean workspaceDefault)
            throws BadRequestRestEx, InternalErrorRestEx
    {

        RuleFilter filter = buildFilter(
                userName, userDefault,
                roleName, roleDefault,
                instanceId, instanceName, instanceDefault,
                workspace, workspaceDefault);

        try {
            List<AdminRule> listFull = adminRuleAdminService.getListFull(filter, page, entries);
            return toOutput(listFull);
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    protected RuleFilter buildFilter(
            String userName, Boolean userDefault,
            String roleName, Boolean groupDefault,
            Long instanceId, String instanceName, Boolean instanceDefault,
            String workspace, Boolean workspaceDefault)
            throws BadRequestRestEx
    {
        RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);

        setFilter(filter.getUser(), userName, userDefault);
        setFilter(filter.getRole(), roleName, groupDefault);
        setFilter(filter.getInstance(), instanceId, instanceName, instanceDefault);
        setFilter(filter.getWorkspace(), workspace, workspaceDefault);
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
            if (includeDefault != null && includeDefault) {
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
            if (includeDefault != null && includeDefault) {
                filter.setType(SpecialFilterType.DEFAULT);
            } else {
                filter.setType(SpecialFilterType.ANY);
            }
        }
    }

    @Override
    public long count(
            String userName, Boolean userDefault,
            String roleName, Boolean groupDefault,
            Long instanceId, String instanceName, Boolean instanceDefault,
            String workspace, Boolean workspaceDefault)
            throws BadRequestRestEx, InternalErrorRestEx
    {
        RuleFilter filter = buildFilter(
                userName, userDefault,
                roleName, groupDefault,
                instanceId, instanceName, instanceDefault,
                workspace, workspaceDefault);

        try {
            return adminRuleAdminService.count(filter);
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }

    }

    // ==========================================================================

    protected RESTOutputAdminRuleList toOutput(List<AdminRule> rules)
    {
        RESTOutputAdminRuleList list = new RESTOutputAdminRuleList(rules.size());
        for (AdminRule rule : rules) {
            list.add(toOutput(rule));
        }
        return list;
    }

    // ==========================================================================
    protected RESTOutputAdminRule toOutput(AdminRule rule)
    {
        RESTOutputAdminRule out = new RESTOutputAdminRule();
        out.setId(rule.getId());
        out.setPriority(rule.getPriority());
        out.setGrant(rule.getAccess());

        out.setUsername(rule.getUsername());
        out.setRolename(rule.getRolename());
        if (rule.getInstance() != null) {
            out.setInstance(new IdName(rule.getInstance().getId(), rule.getInstance().getName()));
        }
        out.setWorkspace(rule.getWorkspace());

        return out;
    }

    protected AdminRule fromInput(RESTInputAdminRule in) {
        AdminRule rule = new AdminRule();

        rule.setPriority(in.getPosition().getValue());

        rule.setAccess(in.getGrant());

        rule.setUsername(in.getUsername());
        rule.setRolename(in.getRolename());

        if (in.getInstance() != null) {
            rule.setInstance(getInstance(in.getInstance()));
        }

        rule.setWorkspace(in.getWorkspace());

        return rule;
    }

}

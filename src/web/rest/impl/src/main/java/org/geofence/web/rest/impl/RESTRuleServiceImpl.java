/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.impl;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.model.LayerAttribute;
import org.geofence.core.model.LayerDetails;
import org.geofence.core.model.Rule;
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
import org.geofence.web.rest.api.interfaces.RESTRuleService;
import org.geofence.web.rest.api.interfaces.params.RESTRuleFilter;
import org.geofence.web.rest.api.model.RESTInputRule;
import org.geofence.web.rest.api.model.RESTLayerAttribute;
import org.geofence.web.rest.api.model.RESTLayerConstraints;
import org.geofence.web.rest.api.model.RESTOutputRule;
import org.geofence.web.rest.api.model.RESTOutputRuleList;
import org.geofence.web.rest.api.model.util.IdName;
import org.geofence.web.rest.utils.GeomUtils;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** @author ETj (etj at geo-solutions.it) */
@Service
public class RESTRuleServiceImpl extends BaseRESTServiceImpl implements RESTRuleService {

    private static final Logger LOGGER = LogManager.getLogger(RESTRuleServiceImpl.class);

    private static final Comparator<Rule> RULE_COMPARATOR = new Comparator<>() {
        @Override
        public int compare(final Rule lhs, Rule rhs) {
            return Long.compare(lhs.getPriority(), rhs.getPriority());
        }
    };

    @Override
    public RESTOutputRule get(Long id) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        try {
            Rule ret = ruleAdminService.get(id);
            return RESTMapper.map(ret);
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("Rule not found: " + id);
            throw new NotFoundRestEx("Rule not found: " + id);
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, value = "geofenceTransactionManager")
    public Response insert(RESTInputRule inputRule) throws NotFoundRestEx, BadRequestRestEx, InternalErrorRestEx {

        if (inputRule.getPosition() == null || inputRule.getPosition().getPosition() == null) {
            throw new BadRequestRestEx("Bad position: " + inputRule.getPosition());
        }

        if (inputRule.getGrant() == null) {
            throw new BadRequestRestEx("Missing grant type");
        }

        Rule rule = mapper.map(inputRule);
        InsertPosition position = RESTMapper.map(inputRule.getPosition().getPosition());

        // ok: insert it
        try {
            Long id = ruleAdminService.insert(rule, position);

            LayerDetails details = RESTMapper.map(inputRule.getConstraints());
            if (details != null) {
                ruleAdminService.setDetails(id, details);
            }

            return Response.status(Status.CREATED).tag(id.toString()).entity(id).build();
        } catch (BadRequestServiceEx ex) {
            LOGGER.error(ex.getMessage());
            throw new BadRequestRestEx(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage(), ex);
        }
    }

    @Override
    public void update(Long id, RESTInputRule rule) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {

        try {
            if ((rule.getGrant() != null)) {
                throw new BadRequestRestEx("GrantType can't be updated");
            }

            if ((rule.getPosition() != null)) {
                throw new BadRequestRestEx("Position can't be updated");
            }

            Rule old = ruleAdminService.get(id);
            boolean isRuleUpdated = false;
            boolean isDetailUpdated = false;

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

            if (rule.getService() != null) {
                old.setService(rule.getService().isEmpty() ? null : rule.getService());
                isRuleUpdated = true;
            }

            if (rule.getRequest() != null) {
                old.setRequest(rule.getRequest().isEmpty() ? null : rule.getRequest());
                isRuleUpdated = true;
            }

            if (rule.getSubfield() != null) {
                old.setSubfield(rule.getSubfield().isEmpty() ? null : rule.getSubfield());
                isRuleUpdated = true;
            }

            if (rule.getWorkspace() != null) {
                old.setWorkspace(rule.getWorkspace().isEmpty() ? null : rule.getWorkspace());
                isRuleUpdated = true;
            }

            if (rule.getLayer() != null) {
                old.setLayer(rule.getLayer().isEmpty() ? null : rule.getLayer());
                isRuleUpdated = true;
            }

            LayerDetails detailsOld = null;
            if (rule.getConstraints() != null) {

                RESTLayerConstraints constraintsNew = rule.getConstraints();
                detailsOld = old.getLayerDetails(); // check me : may be null?

                if (detailsOld == null) { // no previous details
                    detailsOld = new LayerDetails();
                }

                if (constraintsNew.getAllowedStyles() != null) {
                    detailsOld.setAllowedStyles(constraintsNew.getAllowedStyles());
                    isDetailUpdated = true;
                } else {
                    detailsOld.setAllowedStyles(null);
                }

                if (constraintsNew.getAttributes() != null) {
                    isDetailUpdated = true; // this update is complex: pessimistic case: it has to be updated

                    Set<LayerAttribute> attrToRemove = new HashSet<>();
                    Set<LayerAttribute> attrToAdd = new HashSet<>();

                    // find attribute by name, then copy in new datatype and accesstype
                    // if not found, attribute has to be removed
                    for (LayerAttribute oldAttrib : detailsOld.getAttributes()) {
                        boolean found = false;
                        for (RESTLayerAttribute newAttrib : constraintsNew.getAttributes()) {
                            if (newAttrib.getName().equals(oldAttrib.getName())) {
                                found = true;
                                oldAttrib.setDatatype(newAttrib.getDatatype());
                                oldAttrib.setAccess(RESTMapper.map(newAttrib.getAccess()));
                                break;
                            }
                        }
                        if (!found) {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("Attrib " + oldAttrib + " not found in update, will be removed");
                            }
                            attrToRemove.add(oldAttrib);
                        }
                    }

                    detailsOld.getAttributes().removeAll(attrToRemove);

                    // copy in new attributes
                    for (RESTLayerAttribute newAttrib : constraintsNew.getAttributes()) {
                        boolean found = false;
                        for (LayerAttribute oldAttrib : detailsOld.getAttributes()) {
                            if (newAttrib.getName().equals(oldAttrib.getName())) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("New attrib " + newAttrib + " found in update, will be added");
                            }

                            attrToAdd.add(RESTMapper.map(newAttrib));
                        }
                    }

                    detailsOld.getAttributes().addAll(attrToAdd);
                }

                if (constraintsNew.getCqlFilterRead() != null) {
                    detailsOld.setCqlFilterRead(
                            constraintsNew.getCqlFilterRead().isEmpty() ? null : constraintsNew.getCqlFilterRead());
                    isDetailUpdated = true;
                }

                if (constraintsNew.getCqlFilterWrite() != null) {
                    detailsOld.setCqlFilterWrite(
                            constraintsNew.getCqlFilterWrite().isEmpty() ? null : constraintsNew.getCqlFilterWrite());
                    isDetailUpdated = true;
                }

                if (constraintsNew.getDefaultStyle() != null) {
                    detailsOld.setDefaultStyle(
                            constraintsNew.getDefaultStyle().isEmpty() ? null : constraintsNew.getDefaultStyle());
                    isDetailUpdated = true;
                }

                if (constraintsNew.getRestrictedAreaWkt() != null) {
                    isDetailUpdated = true;
                    if (constraintsNew.getRestrictedAreaWkt().isEmpty()) {
                        detailsOld.setArea(null);
                    } else {
                        try {

                            Geometry g = GeomUtils.wktToGeom(constraintsNew.getRestrictedAreaWkt());
                            detailsOld.setArea((MultiPolygon) g);
                        } catch (ParseException ex) {
                            throw new BadRequestRestEx("Error parsing WKT:" + ex.getMessage());
                        }
                    }
                }

                if (constraintsNew.getType() != null) {
                    detailsOld.setType(RESTMapper.map(constraintsNew.getType()));
                    isDetailUpdated = true;
                }
            }

            // now persist the new data

            if (isRuleUpdated) {
                if (LOGGER.isDebugEnabled()) LOGGER.debug("Updating rule " + rule);
                ruleAdminService.update(old);
            } else {
                if (LOGGER.isDebugEnabled()) LOGGER.debug("Rule not changed " + rule);
            }

            if (isDetailUpdated) {
                if (LOGGER.isDebugEnabled()) LOGGER.debug("Updating details " + detailsOld);
                ruleAdminService.setDetails(id, detailsOld);
            } else {
                if (LOGGER.isDebugEnabled()) LOGGER.debug("Details not changed for rule " + rule);
            }

            //            LOGGER.warn("The details may not be updated");
            // TODO: chek if we need to update details in another step
            //            ruleAdminService.setDetails(id, old.getLayerDetails());

        } catch (GeoFenceRestEx ex) {
            // already handled
            throw ex;
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("Rule not found id: " + id + ": " + ex.getMessage(), ex);
            throw new NotFoundRestEx(ex.getMessage());
        } catch (BadRequestServiceEx ex) {
            LOGGER.warn("Problems updating rule id:" + id + ": " + ex.getMessage(), ex);
            throw new BadRequestRestEx(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error("Unexpected exception: " + ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage(), ex);
        }
    }

    @Override
    public Response delete(Long id) throws NotFoundRestEx, InternalErrorRestEx {
        try {
            if (!ruleAdminService.delete(id)) {
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
    public RESTOutputRuleList get(
            @QueryParam("page") Integer page,
            @QueryParam("entries") Integer entries,
            @QueryParam("full") @DefaultValue("false") boolean full,
            @BeanParam RESTRuleFilter query)
            throws BadRequestRestEx, InternalErrorRestEx {

        RuleFilter filter = buildFilter(query);

        try {
            // TODO handle full param
            List<Rule> listFull = ruleAdminService.getListFull(filter, page, entries);
            return toOutput(listFull);
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    protected RuleFilter buildFilter(RESTRuleFilter query) throws BadRequestRestEx {

        // coalesce deprecated "any" flag
        if (query.dateDefault == null) query.dateDefault = query.dateAny;
        if (query.groupDefault == null) query.groupDefault = query.groupAny;
        if (query.instanceDefault == null) query.instanceDefault = query.instanceAny;
        if (query.ipAddressDefault == null) query.ipAddressDefault = query.ipAddressAny;
        if (query.layerDefault == null) query.layerDefault = query.layerAny;
        if (query.requestDefault == null) query.requestDefault = query.requestAny;
        if (query.serviceDefault == null) query.serviceDefault = query.serviceAny;
        if (query.subfieldDefault == null) query.subfieldDefault = query.subfieldAny;
        if (query.userDefault == null) query.userDefault = query.userAny;
        if (query.workspaceDefault == null) query.workspaceDefault = query.workspaceAny;

        RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);

        setFilter(filter.getUser(), query.userName, query.userDefault);
        setFilter(filter.getRole(), query.groupName, query.groupDefault);
        setFilter(filter.getInstance(), query.instanceId, query.instanceName, query.instanceDefault);
        setFilter(filter.getSourceAddress(), query.ipAddress, query.ipAddressDefault);
        setFilter(filter.getDate(), query.date, query.dateDefault);
        setFilter(filter.getService(), query.serviceName, query.serviceDefault);
        setFilter(filter.getRequest(), query.requestName, query.requestDefault);
        setFilter(filter.getSubfield(), query.subfieldName, query.subfieldDefault);
        setFilter(filter.getWorkspace(), query.workspace, query.workspaceDefault);
        setFilter(filter.getLayer(), query.layer, query.layerDefault);
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

    @Override
    public long count(RESTRuleFilter query) throws BadRequestRestEx, InternalErrorRestEx {

        try {
            return ruleAdminService.count(buildFilter(query));
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    @Override
    public Response move(String rulesIds, Integer targetPriority) throws BadRequestRestEx, InternalErrorRestEx {

        try {
            List<Rule> rules = findRules(rulesIds);
            if (!rules.isEmpty()) {
                // shift priorities of rules with a priority equal or lower than the target priority
                ruleAdminService.shift(targetPriority, rules.size());

                // update moved rules priority
                long priority = targetPriority;
                for (Rule rule : rules) {
                    rule.setPriority(priority);
                    ruleAdminService.update(rule);
                    priority++;
                }
            }
            return Response.status(Status.OK).entity("OK\n").build();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    /** Helper method that will parse and retrieve the provided rules sorted by their priority. */
    private List<Rule> findRules(String rulesIds) {
        // Before Java8
        List<Rule> ruleList = new ArrayList<>();
        for (String id : Arrays.asList(rulesIds.split(","))) {
            Rule ru = ruleAdminService.get(Long.parseLong(id));
            if (ru != null) {
                ruleList.add(ru);
            }
        }
        ruleList.sort(RULE_COMPARATOR);
        return ruleList;
    }

    // ==========================================================================
    protected RESTOutputRuleList toOutput(List<Rule> rules) {
        return new RESTOutputRuleList(rules.stream().map(r -> RESTMapper.map(r)).toList());
    }
}

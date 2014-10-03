/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.impl;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.geoserver.geofence.core.model.LayerAttribute;
import org.geoserver.geofence.core.model.LayerDetails;
import org.geoserver.geofence.core.model.Rule;

import org.geoserver.geofence.core.model.enums.InsertPosition;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.RuleFilter.IdNameFilter;
import org.geoserver.geofence.services.dto.RuleFilter.TextFilter;
import org.geoserver.geofence.services.dto.RuleFilter.SpecialFilterType;
import org.geoserver.geofence.services.exception.BadRequestServiceEx;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;
import org.geoserver.geofence.services.rest.RESTRuleService;
import org.geoserver.geofence.services.rest.exception.BadRequestRestEx;
import org.geoserver.geofence.services.rest.exception.GeoFenceRestEx;
import org.geoserver.geofence.services.rest.exception.InternalErrorRestEx;
import org.geoserver.geofence.services.rest.exception.NotFoundRestEx;
import org.geoserver.geofence.services.rest.model.RESTInputRule;
import org.geoserver.geofence.services.rest.model.RESTInputRule.RESTRulePosition.RulePosition;
import org.geoserver.geofence.services.rest.model.RESTLayerConstraints;
import org.geoserver.geofence.services.rest.model.RESTOutputRule;
import org.geoserver.geofence.services.rest.model.RESTOutputRuleList;
import org.geoserver.geofence.services.rest.model.config.RESTFullRuleList;
import org.geoserver.geofence.services.rest.model.util.IdName;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RESTRuleServiceImpl
        extends BaseRESTServiceImpl
        implements RESTRuleService {

    private static final Logger LOGGER = LogManager.getLogger(RESTRuleServiceImpl.class);

    @Override
    public RESTOutputRule get(Long id) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        try {
            Rule ret = ruleAdminService.get(id);
            return toOutput(ret);
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

        Rule rule = fromInput(inputRule);

        InsertPosition position =
                inputRule.getPosition().getPosition() == RulePosition.fixedPriority ? InsertPosition.FIXED
                : inputRule.getPosition().getPosition() == RulePosition.offsetFromBottom ? InsertPosition.FROM_END
                : inputRule.getPosition().getPosition() == RulePosition.offsetFromTop ? InsertPosition.FROM_START : null;

        // ok: insert it
        try {
            Long id = ruleAdminService.insert(rule, position);

            LayerDetails details = detailsFromInput(inputRule);
            if (details != null) {
                ruleAdminService.setDetails(id, details);
            }

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

            if (rule.getUser() != null) {
                IdName idname = rule.getUser();
                old.setGsuser(idname.getId() == null && idname.getName() == null ? null : getUser(idname));
                isRuleUpdated = true;
            }
            if (rule.getGroup() != null) {
                IdName idname = rule.getGroup();
                old.setUserGroup(idname.getId() == null && idname.getName() == null ? null : getUserGroup(idname));
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

                if(detailsOld == null) { // no previous details
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

                    Set<LayerAttribute> attrToRemove = new HashSet<LayerAttribute>();
                    Set<LayerAttribute> attrToAdd = new HashSet<LayerAttribute>();

                    // find attribute by name, then copy in new datatype and accesstype
                    // if not found, attribute has to be removed
                    for (LayerAttribute oldAttrib : detailsOld.getAttributes()) {
                        boolean found = false;
                        for (LayerAttribute newAttrib : constraintsNew.getAttributes()) {
                            if (newAttrib.getName().equals(oldAttrib.getName())) {
                                found = true;
                                oldAttrib.setDatatype(newAttrib.getDatatype());
                                oldAttrib.setAccess(newAttrib.getAccess());
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
                    for (LayerAttribute newAttrib : constraintsNew.getAttributes()) {
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

                            attrToAdd.add(newAttrib);
                        }
                    }

                    detailsOld.getAttributes().addAll(attrToAdd);
                }

                if (constraintsNew.getCqlFilterRead() != null) {
                    detailsOld.setCqlFilterRead(constraintsNew.getCqlFilterRead().isEmpty() ? null : constraintsNew.getCqlFilterRead());
                    isDetailUpdated = true;
                }

                if (constraintsNew.getCqlFilterWrite() != null) {
                    detailsOld.setCqlFilterWrite(constraintsNew.getCqlFilterWrite().isEmpty() ? null : constraintsNew.getCqlFilterWrite());
                    isDetailUpdated = true;
                }

                if (constraintsNew.getDefaultStyle() != null) {
                    detailsOld.setDefaultStyle(constraintsNew.getDefaultStyle().isEmpty() ? null : constraintsNew.getDefaultStyle());
                    isDetailUpdated = true;
                }

                if (constraintsNew.getRestrictedAreaWkt() != null) {
                    isDetailUpdated = true;
                    if (constraintsNew.getRestrictedAreaWkt().isEmpty()) {
                        detailsOld.setArea(null);
                    } else {
                        try {
                            WKTReader reader = new WKTReader();
                            Geometry g = reader.read(constraintsNew.getRestrictedAreaWkt());
                            detailsOld.setArea((MultiPolygon) g);
                        } catch (ParseException ex) {
                            throw new BadRequestRestEx("Error parsing WKT:" + ex.getMessage());
                        }
                    }
                }

                if(constraintsNew.getType() != null) {
                    detailsOld.setType(constraintsNew.getType());
                    isDetailUpdated = true;
                }
            }

            // now persist the new data

            if(isRuleUpdated) {
                if(LOGGER.isDebugEnabled())
                    LOGGER.debug("Updating rule " + rule);
                ruleAdminService.update(old);
            } else {
                if(LOGGER.isDebugEnabled())
                    LOGGER.debug("Rule not changed " + rule);
            }

            if(isDetailUpdated) {
                if(LOGGER.isDebugEnabled())
                    LOGGER.debug("Updating details " + detailsOld);
                    ruleAdminService.setDetails(id, detailsOld);
            } else {
                if(LOGGER.isDebugEnabled())
                    LOGGER.debug("Details not changed for rule " + rule);
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
            throw new InternalErrorRestEx(ex.getMessage());
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
//    public void setUserGroupAdminService(UserGroupAdminService service) {
//        this.userGroupAdminService = service;
//    }
    @Override
    public RESTOutputRuleList get(Integer page, Integer entries,
            boolean full,
            Long userId, String userName, Boolean userDefault,
            Long groupId, String groupName, Boolean groupDefault,
            Long instanceId, String instanceName, Boolean instanceDefault,
            String serviceName, Boolean serviceDefault,
            String requestName, Boolean requestDefault,
            String workspace, Boolean workspaceDefault,
            String layer, Boolean layerDefault)
            throws BadRequestRestEx, InternalErrorRestEx {

        RuleFilter filter = buildFilter(
                userId, userName, userDefault,
                groupId, groupName, groupDefault,
                instanceId, instanceName, instanceDefault,
                serviceName, serviceDefault,
                requestName, requestDefault,
                workspace, workspaceDefault,
                layer, layerDefault);

        try {
            List<Rule> listFull = ruleAdminService.getListFull(filter, page, entries);
            return toOutput(listFull);
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    protected RuleFilter buildFilter(
            Long userId, String userName, Boolean userDefault,
            Long groupId, String groupName, Boolean groupDefault,
            Long instanceId, String instanceName, Boolean instanceDefault,
            String serviceName, Boolean serviceDefault,
            String requestName, Boolean requestDefault,
            String workspace, Boolean workspaceDefault,
            String layer, Boolean layerDefault) throws BadRequestRestEx {

        RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);

        setFilter(filter.getUser(), userId, userName, userDefault);
        setFilter(filter.getUserGroup(), groupId, groupName, groupDefault);
        setFilter(filter.getInstance(), instanceId, instanceName, instanceDefault);
        setFilter(filter.getService(), serviceName, serviceDefault);
        setFilter(filter.getRequest(), requestName, requestDefault);
        setFilter(filter.getWorkspace(), workspace, workspaceDefault);
        setFilter(filter.getLayer(), layer, layerDefault);
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
            Long userId, String userName, Boolean userDefault,
            Long groupId, String groupName, Boolean groupDefault,
            Long instanceId, String instanceName, Boolean instanceDefault,
            String serviceName, Boolean serviceDefault,
            String requestName, Boolean requestDefault,
            String workspace, Boolean workspaceDefault,
            String layer, Boolean layerDefault)
            throws BadRequestRestEx, InternalErrorRestEx {

        RuleFilter filter = buildFilter(
                userId, userName, userDefault,
                groupId, groupName, groupDefault,
                instanceId, instanceName, instanceDefault,
                serviceName, serviceDefault,
                requestName, requestDefault,
                workspace, workspaceDefault,
                layer, layerDefault);

        try {
            return ruleAdminService.count(filter);
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }

    }

    // ==========================================================================
    protected RESTOutputRuleList toOutput(List<Rule> rules) {
        RESTOutputRuleList list = new RESTOutputRuleList(rules.size());
        for (Rule rule : rules) {
            list.add(toOutput(rule));
        }
        return list;
    }
    
    // ==========================================================================
    protected RESTOutputRule toOutput(Rule rule) {
        RESTOutputRule out = new RESTOutputRule();
        out.setId(rule.getId());
        out.setPriority(rule.getPriority());
        out.setGrant(rule.getAccess());

        if (rule.getGsuser() != null) {
            out.setUser(new IdName(rule.getGsuser().getId(), rule.getGsuser().getName()));
        }
        if (rule.getUserGroup() != null) {
            out.setGroup(new IdName(rule.getUserGroup().getId(), rule.getUserGroup().getName()));
        }
        if (rule.getInstance() != null) {
            out.setInstance(new IdName(rule.getInstance().getId(), rule.getInstance().getName()));
        }
        out.setService(rule.getService());
        out.setRequest(rule.getRequest());
        out.setWorkspace(rule.getWorkspace());
        out.setLayer(rule.getLayer());


        if (rule.getLayerDetails() != null) {
            LayerDetails details = rule.getLayerDetails();
            RESTLayerConstraints constraints = new RESTLayerConstraints();
            if (details.getAllowedStyles() != null) {
                constraints.setAllowedStyles(new HashSet(details.getAllowedStyles()));
            }
            if (details.getAttributes() != null) {
                constraints.setAttributes(new HashSet(details.getAttributes()));
            }
            constraints.setCqlFilterRead(details.getCqlFilterRead());
            constraints.setCqlFilterWrite(details.getCqlFilterWrite());
            constraints.setDefaultStyle(details.getDefaultStyle());
            if (details.getArea() != null) {
                constraints.setRestrictedAreaWkt(details.getArea().toText());
            }

            constraints.setType(details.getType());

            out.setConstraints(constraints);
        }

        return out;
    }

    protected Rule fromInput(RESTInputRule in) {
        Rule rule = new Rule();

        rule.setPriority(in.getPosition().getValue());

        rule.setAccess(in.getGrant());

        if (in.getUser() != null) {
            rule.setGsuser(getUser(in.getUser()));
        }

        if (in.getGroup() != null) {
            rule.setUserGroup(getUserGroup(in.getGroup()));
        }

        if (in.getInstance() != null) {
            rule.setInstance(getInstance(in.getInstance()));
        }

        rule.setService(in.getService());
        rule.setRequest(in.getRequest());
        rule.setWorkspace(in.getWorkspace());
        rule.setLayer(in.getLayer());

        return rule;
    }

    protected LayerDetails detailsFromInput(RESTInputRule in) {
        RESTLayerConstraints constraints = in.getConstraints();
        if (constraints != null) {
            LayerDetails details = new LayerDetails();

            if (constraints.getAllowedStyles() != null) {
                details.setAllowedStyles(new HashSet(constraints.getAllowedStyles()));
            }
            if (constraints.getAttributes() != null) {
                details.setAttributes(new HashSet(constraints.getAttributes()));
            }
            details.setCqlFilterRead(constraints.getCqlFilterRead());
            details.setCqlFilterWrite(constraints.getCqlFilterWrite());
            details.setDefaultStyle(constraints.getDefaultStyle());
            if (constraints.getRestrictedAreaWkt() != null) {
                WKTReader reader = new WKTReader();
                Geometry g;
                try {
                    g = reader.read(constraints.getRestrictedAreaWkt());
                } catch (ParseException ex) {
                    throw new BadRequestRestEx("Error parsing WKT:" + ex.getMessage());
                }
                details.setArea((MultiPolygon) g);
            }

            details.setType(constraints.getType());

            return details;
        } else {
            return null;
        }
    }
    // ==========================================================================
    // ==========================================================================
}

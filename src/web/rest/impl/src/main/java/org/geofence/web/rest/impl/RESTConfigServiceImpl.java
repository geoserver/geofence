/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.model.GSInstance;
import org.geofence.core.model.GSUser;
import org.geofence.core.model.LayerDetails;
import org.geofence.core.model.Rule;
import org.geofence.core.model.RuleLimits;
import org.geofence.core.model.UserGroup;
import org.geofence.core.services.GetProviderService;
import org.geofence.core.services.InstanceAdminService;
import org.geofence.core.services.RuleAdminService;
import org.geofence.core.services.UserAdminService;
import org.geofence.core.services.UserGroupAdminService;
import org.geofence.core.services.dto.ShortGroup;
import org.geofence.core.services.exception.NotFoundServiceEx;
import org.geofence.web.rest.api.exception.BadRequestRestEx;
import org.geofence.web.rest.api.exception.InternalErrorRestEx;
import org.geofence.web.rest.api.exception.NotFoundRestEx;
import org.geofence.web.rest.api.interfaces.RESTBatchService;
import org.geofence.web.rest.api.interfaces.RESTConfigService;
import org.geofence.web.rest.api.model.RESTBatch;
import org.geofence.web.rest.api.model.RESTBatchOperation;
import org.geofence.web.rest.api.model.RESTInputGroup;
import org.geofence.web.rest.api.model.RESTInputInstance;
import org.geofence.web.rest.api.model.RESTInputRule;
import org.geofence.web.rest.api.model.RESTInputUser;
import org.geofence.web.rest.api.model.RESTLayerConstraints;
import org.geofence.web.rest.api.model.RESTOutputGroup;
import org.geofence.web.rest.api.model.RESTOutputRule;
import org.geofence.web.rest.api.model.RESTOutputRuleList;
import org.geofence.web.rest.api.model.RESTRulePosition;
import org.geofence.web.rest.api.model.RESTShortInstance;
import org.geofence.web.rest.api.model.RESTShortInstanceList;
import org.geofence.web.rest.api.model.RESTShortUser;
import org.geofence.web.rest.api.model.RESTShortUserList;
import org.geofence.web.rest.api.model.config.RESTFullUserGroupList;
import org.geofence.web.rest.api.model.util.IdName;
import org.geofence.web.rest.api.util.RESTBatchOperationFactory;
import org.geofence.web.rest.utils.InstanceCleaner;
import org.locationtech.jts.geom.MultiPolygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** @author ETj (etj at geo-solutions.it) */
@Service
public class RESTConfigServiceImpl implements RESTConfigService {

    private static final Logger LOGGER = LogManager.getLogger(RESTConfigServiceImpl.class);

    private UserAdminService userAdminService;
    private UserGroupAdminService userGroupAdminService;
    private RuleAdminService ruleAdminService;
    private InstanceAdminService instanceAdminService;
    private RESTBatchService restBatchService;
    private InstanceCleaner instanceCleaner;

    @Autowired
    private RESTMapper mapper;

    protected RESTBatch collectUsers(RESTBatch backup) {
        for (GSUser user : userAdminService.getFullList(null, null, null, true)) {
            RESTBatchOperation op = RESTBatchOperationFactory.createUserInputOp();
            RESTInputUser input = new RESTInputUser();
            op.setPayload(input);

            input.setAdmin(user.isAdmin());
            input.setEmailAddress(user.getEmailAddress());
            input.setEnabled(user.getEnabled());
            input.setExtId(user.getExtId());
            input.setFullName(user.getFullName());
            input.setName(user.getName());
            input.setPassword(user.getPassword());

            if (user.getGroups() != null) {
                input.setGroups(new ArrayList<IdName>(user.getGroups().size()));
                for (UserGroup userGroup : user.getGroups()) {
                    input.getGroups().add(new IdName(userGroup.getName()));
                }
            }
            backup.add(op);
        }
        return backup;
    }

    protected RESTBatch collectGroups(RESTBatch backup) {
        for (ShortGroup shortGroup : userGroupAdminService.getList(null, null, null)) {
            RESTBatchOperation op = RESTBatchOperationFactory.createGroupInputOp(shortGroup.getName());
            RESTInputGroup input = (RESTInputGroup) op.getPayload();
            input.setExtId(shortGroup.getExtId());
            input.setEnabled(shortGroup.isEnabled());
            backup.add(op);
        }
        return backup;
    }

    protected RESTBatch collectInstances(RESTBatch backup) {

        for (GSInstance instance : instanceAdminService.getFullList(null, null, null)) {
            RESTBatchOperation op = RESTBatchOperationFactory.createInstanceInputOp();
            RESTInputInstance input = new RESTInputInstance();
            op.setPayload(input);

            input.setBaseURL(instance.getBaseURL());
            input.setDescription(instance.getDescription());
            input.setName(instance.getName());
            input.setPassword(instance.getPassword());
            input.setUsername(instance.getUsername());

            backup.add(op);
        }
        return backup;
    }

    protected RESTBatch collectRules(RESTBatch backup) {
        for (Rule rule : ruleAdminService.getListFull(null, null, null)) {
            RESTBatchOperation op = RESTBatchOperationFactory.createRuleInputOp();
            RESTInputRule input = new RESTInputRule();
            op.setPayload(input);

            input.setGrant(RESTMapper.map(rule.getAccess()));
            input.setPosition(
                    new RESTRulePosition(RESTRulePosition.RESTPositionReference.fixedPriority, rule.getPriority()));

            if (rule.getInstance() != null)
                input.setInstanceName(rule.getInstance().getName());

            input.setRolename(rule.getRolename());
            input.setUsername(rule.getUsername());

            input.setService(rule.getService());
            input.setRequest(rule.getRequest());
            input.setSubfield(rule.getSubfield());
            input.setWorkspace(rule.getWorkspace());
            input.setLayer(rule.getLayer());

            RESTLayerConstraints constraints = new RESTLayerConstraints();

            if (rule.getRuleLimits() != null) {
                RuleLimits limits = rule.getRuleLimits();
                MultiPolygon mp = limits.getAllowedArea();

                constraints.setRestrictedAreaWkt(mp.toText());
                input.setConstraints(constraints);
            }

            if (rule.getLayerDetails() != null) {
                LayerDetails details = rule.getLayerDetails();

                constraints.setAllowedStyles(details.getAllowedStyles());
                constraints.setAttributes(details.getAttributes().stream()
                        .map(i -> RESTMapper.map(i))
                        .toList());
                constraints.setCqlFilterRead(details.getCqlFilterRead());
                constraints.setCqlFilterWrite(details.getCqlFilterWrite());
                constraints.setDefaultStyle(details.getDefaultStyle());
                constraints.setType(RESTMapper.map(details.getType()));

                input.setConstraints(constraints);
            }

            backup.add(op);
        }
        return backup;
    }

    @Override
    public RESTBatch backupGroups() {
        return collectGroups(new RESTBatch());
    }

    @Override
    public RESTBatch backupUsers() {
        return collectUsers(new RESTBatch());
    }

    @Override
    public RESTBatch backupInstances() {
        return collectInstances(new RESTBatch());
    }

    @Override
    public RESTBatch backupRules() {
        return collectRules(new RESTBatch());
    }

    @Override
    public RESTBatch backup(Boolean includeGRUsers) {
        RESTBatch backup = new RESTBatch();

        collectGroups(backup);
        collectUsers(backup);
        collectInstances(backup);
        collectRules(backup);

        if (includeGRUsers.booleanValue()) {
            LOGGER.warn("TODO::: GF users not handled");
            //            RESTFullGRUserList grUsers = new RESTFullGRUserList();
            //            grUsers.setList(grUserAdminService.getFullList(null, null, null));
            //            backup.setGrUserList(grUsers);
        }

        return backup;
    }

    @Override
    public void restore(RESTBatch batch) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        LOGGER.warn("Restoring GeoFence using batch with " + batch.getList().size() + " operations");

        instanceCleaner.removeAll();
        restBatchService.runBatch(batch);
    }

    @Override
    public void cleanup() throws InternalErrorRestEx {
        LOGGER.warn("Cleaning up GeoFence data");

        instanceCleaner.removeAll();
    }

    //    @Override
    public RESTFullUserGroupList getUserGroups() throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        List<ShortGroup> list = userGroupAdminService.getList(null, null, null);
        return new RESTFullUserGroupList(
                list.stream().map(i -> RESTMapper.map(i)).toList());
    }

    // ==========================================================================
    // ==========================================================================

    /** Simplified operation used for quick re-insertion of data extracted with a GET op in the related service */
    //    @Override
    public void setUserGroups(RESTFullUserGroupList groups)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        int okCnt = 0;
        for (RESTOutputGroup group : groups) {
            LOGGER.info("Adding group " + group);
            try {
                userGroupAdminService.insert(RESTMapper.map(group));
                okCnt++;
            } catch (Exception e) {
                LOGGER.info("Could not add group " + group + ": " + e.getMessage());
            }
        }
        LOGGER.info(okCnt + "/" + groups.getList().size() + " items inserted");
    }

    /** Simplified operation used for quick re-insertion of data extracted with a GET op in the related service */
    //    @Override
    public void setUsers(RESTShortUserList users) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        int okCnt = 0;
        for (RESTShortUser su : users) {
            LOGGER.info("Adding user " + su);
            try {
                GSUser u = new GSUser();
                u.setExtId(su.getExtId());
                u.setName(su.getUserName());
                u.setEnabled(su.isEnabled());
                // group list is not retrievable in shortuser :|

                userAdminService.insert(u);
                okCnt++;
            } catch (Exception e) {
                LOGGER.info("Could not add user " + su + ": " + e.getMessage());
            }
        }
        LOGGER.info(okCnt + "/" + users.getUserList().size() + " items inserted");
    }

    /** Simplified operation used for quick re-insertion of data extracted with a GET op in the related service */
    //    @Override
    public void setInstances(RESTShortInstanceList instances)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        int okCnt = 0;
        for (RESTShortInstance si : instances) {
            LOGGER.info("Adding instance " + si);
            try {
                GSInstance i = new GSInstance();
                i.setName(si.getName());
                i.setDescription(si.getDescription());
                i.setBaseURL(si.getUrl());
                i.setUsername("unknown");
                i.setPassword("unknown");
                instanceAdminService.insert(i);
                okCnt++;
            } catch (Exception e) {
                LOGGER.info("Could not add instance " + si + ": " + e.getMessage());
            }
        }
        LOGGER.info(okCnt + "/" + instances.getList().size() + " items inserted");
    }

    /** Simplified operation used for quick re-insertion of data extracted with a GET op in the related service */
    //    @Override
    public void setRules(RESTOutputRuleList rules) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        int okCnt = 0;

        for (RESTOutputRule in : rules) {
            try {
                Rule out = mapper.map(in);
                long ruleid = ruleAdminService.insert(out);
                okCnt++;

                if (in.getConstraints() != null) {
                    LOGGER.warn("TODO::: Constraints exist but will not be inserted for rule " + out);
                }
            } catch (Exception e) {
                LOGGER.info("Could not add rule " + in + ": " + e.getMessage());
            }
        }
        LOGGER.info(okCnt + "/" + rules.getList().size() + " items inserted");
    }

    // ==========================================================================
    // ==========================================================================
    public void setInstanceCleaner(InstanceCleaner instanceCleaner) {
        this.instanceCleaner = instanceCleaner;
    }

    // ==========================================================================
    public void setUserGroupAdminService(UserGroupAdminService service) {
        this.userGroupAdminService = service;
    }

    public void setUserAdminService(UserAdminService service) {
        this.userAdminService = service;
    }

    public void setInstanceAdminService(InstanceAdminService service) {
        this.instanceAdminService = service;
    }

    public void setRuleAdminService(RuleAdminService service) {
        this.ruleAdminService = service;
    }

    public void setRestBatchService(RESTBatchService restBatchService) {
        this.restBatchService = restBatchService;
    }

    // ==========================================================================
    class RemapperCache<TYPE, SERVICE extends GetProviderService<TYPE>> {

        private Map<Long, TYPE> cache = new HashMap<>();
        private final Map<Long, Long> idRemapper;
        private final SERVICE service;

        public RemapperCache(SERVICE service, Map<Long, Long> idRemapper) {
            this.idRemapper = idRemapper;
            this.service = service;
        }

        TYPE get(Long oldId) throws RemapperException, NotFoundRestEx {
            Long newId = idRemapper.get(oldId);
            if (newId == null) {
                LOGGER.error("Can't remap " + oldId);
                throw new RemapperException("Can't remap " + oldId);
            }

            TYPE cached = cache.get(newId);
            try {
                if (cached == null) {
                    cached = service.get(newId.longValue()); // may throw NotFoundServiceEx
                    cache.put(newId, cached);
                }

                return cached;
            } catch (NotFoundServiceEx ex) {
                LOGGER.error(ex.getMessage(), ex);
                throw new NotFoundRestEx(ex.getMessage());
            }
        }
    }

    class RemapperException extends Exception {

        public RemapperException(Throwable cause) {
            super(cause);
        }

        public RemapperException(String message, Throwable cause) {
            super(message, cause);
        }

        public RemapperException(String message) {
            super(message);
        }

        public RemapperException() {}
    }
}

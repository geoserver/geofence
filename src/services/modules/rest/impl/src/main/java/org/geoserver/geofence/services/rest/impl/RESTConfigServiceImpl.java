/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.impl;

import com.vividsolutions.jts.geom.MultiPolygon;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geoserver.geofence.core.model.GFUser;
import org.geoserver.geofence.core.model.GSInstance;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.LayerDetails;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.core.model.RuleLimits;
import org.geoserver.geofence.services.GFUserAdminService;
import org.geoserver.geofence.services.GetProviderService;
import org.geoserver.geofence.services.InstanceAdminService;
import org.geoserver.geofence.services.UserGroupAdminService;
import org.geoserver.geofence.services.RuleAdminService;
import org.geoserver.geofence.services.UserAdminService;
import org.geoserver.geofence.services.dto.ShortGroup;
import org.geoserver.geofence.services.dto.ShortInstance;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;
import org.geoserver.geofence.services.rest.RESTBatchService;
import org.geoserver.geofence.services.rest.RESTConfigService;
import org.geoserver.geofence.services.rest.exception.BadRequestRestEx;
import org.geoserver.geofence.services.rest.exception.InternalErrorRestEx;
import org.geoserver.geofence.services.rest.exception.NotFoundRestEx;
import org.geoserver.geofence.services.rest.model.RESTBatch;
import org.geoserver.geofence.services.rest.model.RESTBatchOperation;
import org.geoserver.geofence.services.rest.model.RESTInputGroup;
import org.geoserver.geofence.services.rest.model.RESTInputInstance;
import org.geoserver.geofence.services.rest.model.RESTInputRule;
import org.geoserver.geofence.services.rest.model.RESTInputRule.RESTRulePosition;
import org.geoserver.geofence.services.rest.model.RESTInputUser;
import org.geoserver.geofence.services.rest.model.RESTLayerConstraints;
import org.geoserver.geofence.services.rest.model.RESTOutputRule;
import org.geoserver.geofence.services.rest.model.RESTOutputRuleList;
import org.geoserver.geofence.services.rest.model.RESTShortInstanceList;
import org.geoserver.geofence.services.rest.model.RESTShortUser;
import org.geoserver.geofence.services.rest.model.RESTShortUserList;
import org.geoserver.geofence.services.rest.model.config.RESTConfigurationRemapping;
import org.geoserver.geofence.services.rest.model.config.RESTFullConfiguration;
import org.geoserver.geofence.services.rest.model.config.RESTFullGRUserList;
import org.geoserver.geofence.services.rest.model.config.RESTFullGSInstanceList;
import org.geoserver.geofence.services.rest.model.config.RESTFullUserGroupList;
import org.geoserver.geofence.services.rest.model.config.RESTFullRuleList;
import org.geoserver.geofence.services.rest.model.config.RESTFullUserList;
import org.geoserver.geofence.services.rest.model.util.IdName;
import org.geoserver.geofence.services.rest.model.util.RESTBatchOperationFactory;
import org.geoserver.geofence.services.rest.utils.InstanceCleaner;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RESTConfigServiceImpl implements RESTConfigService {

    private static final Logger LOGGER = LogManager.getLogger(RESTConfigServiceImpl.class);

    private UserAdminService userAdminService;
    private UserGroupAdminService userGroupAdminService;
    private RuleAdminService ruleAdminService;
    private InstanceAdminService instanceAdminService;
    private GFUserAdminService grUserAdminService;
    private RESTBatchService restBatchService;
    private InstanceCleaner instanceCleaner;

    public RESTFullConfiguration getConfiguration() {
        return getConfiguration(false);
    }

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

            if(user.getGroups() != null) {
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
            RESTInputGroup input = (RESTInputGroup)op.getPayload();
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

            input.setGrant(rule.getAccess());
            input.setPosition(new RESTRulePosition(RESTRulePosition.RulePosition.fixedPriority, rule.getPriority()));

            if(rule.getUserGroup() != null)
                input.setGroupName(rule.getUserGroup().getName());
            if(rule.getGsuser() != null)
                input.setUserName(rule.getGsuser().getName());
            if(rule.getInstance() != null)
                input.setInstanceName(rule.getInstance().getName());

            input.setService(rule.getService());
            input.setRequest(rule.getRequest());
            input.setWorkspace(rule.getWorkspace());
            input.setLayer(rule.getLayer());

            RESTLayerConstraints constraints = new RESTLayerConstraints();

            if(rule.getRuleLimits() != null ) {
                RuleLimits limits = rule.getRuleLimits();
                MultiPolygon mp = limits.getAllowedArea();

                constraints.setRestrictedAreaWkt(mp.toText());
                input.setConstraints(constraints);
            }

            if(rule.getLayerDetails() != null) {
                LayerDetails details = rule.getLayerDetails();

                constraints.setAllowedStyles(details.getAllowedStyles());
                constraints.setAttributes(details.getAttributes());
                constraints.setCqlFilterRead(details.getCqlFilterRead());
                constraints.setCqlFilterWrite(details.getCqlFilterWrite());
                constraints.setDefaultStyle(details.getDefaultStyle());
                constraints.setType(details.getType());

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

        if ( includeGRUsers.booleanValue() ) {
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


    /**
     * @deprecated misbehaves since usergroups introduction. Please use backup()
     */
    @Override
    public RESTFullConfiguration getConfiguration(Boolean includeGRUsers) {
        RESTFullConfiguration cfg = new RESTFullConfiguration();

        RESTFullUserList users = new RESTFullUserList();
        List<GSUser> userlist = userAdminService.getFullList(null, null, null);
        for (GSUser user : userlist) {
            user.setGroups(userAdminService.getUserGroups(user.getId()));
        }
        users.setList(userlist);
        cfg.setUserList(users);

        RESTFullUserGroupList profiles = new RESTFullUserGroupList();
        profiles.setList(userGroupAdminService.getList(null, null, null));
        cfg.setUserGroupList(profiles);

        RESTFullGSInstanceList instances = new RESTFullGSInstanceList();
        instances.setList(instanceAdminService.getFullList(null, null, null));
        cfg.setGsInstanceList(instances);

        RESTFullRuleList rules = new RESTFullRuleList();
        rules.setList(ruleAdminService.getListFull(null, null, null));
        cfg.setRuleList(rules);

        if ( includeGRUsers.booleanValue() ) {
            RESTFullGRUserList grUsers = new RESTFullGRUserList();
            grUsers.setList(grUserAdminService.getFullList(null, null, null));
            cfg.setGrUserList(grUsers);
        }

        return cfg;
    }

    public synchronized RESTConfigurationRemapping setConfiguration(RESTFullConfiguration config) {
        return setConfiguration(config, false);
    }

    @Override
    public synchronized RESTConfigurationRemapping setConfiguration(RESTFullConfiguration config,
            Boolean includeGRUsers) {
        LOGGER.warn("SETTING CONFIGURATION");

        if ( includeGRUsers ) {
            if ( (config.getGrUserList() == null) || (config.getGrUserList().getList() == null) || config.getGrUserList().getList().isEmpty() ) {
                throw new BadRequestRestEx("Can't restore internal users: no internal user defined");
            }
        }

        instanceCleaner.removeAll();

        RESTConfigurationRemapping remap = new RESTConfigurationRemapping();

        RemapperCache<UserGroup, UserGroupAdminService> groupCache = new RemapperCache<UserGroup, UserGroupAdminService>(userGroupAdminService, remap.getUserGroups());
        RemapperCache<GSUser, UserAdminService> userCache = new RemapperCache<GSUser, UserAdminService>(userAdminService, remap.getUsers());
        RemapperCache<GSInstance, InstanceAdminService> instanceCache =
                new RemapperCache<GSInstance, InstanceAdminService>(instanceAdminService, remap.getInstances());


        try {
            // === UserGroups
            for (ShortGroup sp : config.getUserGroupList().getList()) {
                Long oldId = sp.getId();
                long newId = userGroupAdminService.insert(sp);
                LOGGER.info("Remapping userGroup " + oldId + " -> " + newId);
                remap.getUserGroups().put(oldId, newId);
            }

            // === Users
            for (GSUser user : config.getUserList().getList()) {
                for (UserGroup userGroup : user.getGroups()) {
                    Long oldGroupId = userGroup.getId();
                    UserGroup group = groupCache.get(oldGroupId);
                    user.getGroups().add(group);
                }

                Long oldId = user.getId();
                user.setId(null);

                long newId = userAdminService.insert(user);
                LOGGER.info("Remapping user " + oldId + " -> " + newId);
                remap.getUsers().put(oldId, newId);
            }


            // === GSInstances
            for (GSInstance instance : config.getGsInstanceList().getList()) {
                Long oldId = instance.getId();
                instance.setId(null);

                long newId = instanceAdminService.insert(instance);
                LOGGER.info("Remapping gsInstance " + oldId + " -> " + newId);
                remap.getInstances().put(oldId, newId);
            }

            // === Rules
            for (Rule rule : config.getRuleList().getList()) {
                Long oldId = rule.getId();
                rule.setId(null);

                if ( rule.getUserGroup() != null ) {
                    rule.setUserGroup(groupCache.get(rule.getUserGroup().getId()));
                }

                if ( rule.getGsuser() != null ) {
                    rule.setGsuser(userCache.get(rule.getGsuser().getId()));
                }

                if ( rule.getInstance() != null ) {
                    rule.setInstance(instanceCache.get(rule.getInstance().getId()));
                }

                // the prob here is that layerdetails is a reverse reference, so only hibernate should be setting it.
                // using JAXB, it's injected, but we have to make hibernate eat it.
                LayerDetails ld = rule.getLayerDetails();
                rule.setLayerDetails(null);

                long newId = ruleAdminService.insert(rule);
                LOGGER.info("Remapping rule " + oldId + " -> " + newId);
                remap.getRules().put(oldId, newId);

                if ( ld != null ) {
                    ruleAdminService.setDetails(newId, ld);
                }
            }
        } catch (RemapperException e) {
            LOGGER.error("Exception in remapping: Configuration will be erased");
            instanceCleaner.removeAll();
            throw new BadRequestRestEx(e.getMessage());

        } catch (NotFoundRestEx e) {
            LOGGER.error("Internal exception in remapping: Configuration will be erased");
            instanceCleaner.removeAll();
            throw e;
        }

        // === Internal users
        if ( includeGRUsers ) {
            instanceCleaner.removeAllGFUsers();

            for (GFUser grUser : config.getGrUserList().getList()) {
                Long oldId = grUser.getId();
                grUser.setId(null);

                long newId = grUserAdminService.insert(grUser);
                LOGGER.info("Remapping internal user " + oldId + " -> " + newId);
                remap.remap(oldId, grUser);
            }

        }

        return remap;
    }

    @Override
    public RESTFullUserList getUsers() throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        List<GSUser> users = userAdminService.getFullList(null, null, null);

        RESTFullUserList ret = new RESTFullUserList();
        ret.setList(users);

        return ret;
    }

    // not @Override: not available as a standalone service
    public RESTFullGRUserList getGRUsers() throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        List<GFUser> users = grUserAdminService.getFullList(null, null, null);

        RESTFullGRUserList ret = new RESTFullGRUserList();
        ret.setList(users);

        return ret;
    }

    @Override
    public RESTFullUserGroupList getUserGroups() throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        List<ShortGroup> groups = userGroupAdminService.getList(null, null, null);
        return new RESTFullUserGroupList(groups);
    }

    // ==========================================================================
    // ==========================================================================

    /**
     * Simplified operation used for quick re-insertion of data extracted with a GET op in the related service
     */
    @Override
    public void setUserGroups(RESTFullUserGroupList groups) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        int okCnt = 0;
        for (ShortGroup group : groups) {
            LOGGER.info("Adding group " +group );
            try {
                userGroupAdminService.insert(group);
                okCnt++;
            } catch (Exception e) {
                LOGGER.info("Could not add group " +group +": " + e.getMessage());
            }
        }
        LOGGER.info(okCnt+"/"+groups.getList().size() + " items inserted");
    }

    /**
     * Simplified operation used for quick re-insertion of data extracted with a GET op in the related service
     */
    @Override
    public void setUsers(RESTShortUserList users) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        int okCnt = 0;
        for (RESTShortUser su : users) {
            LOGGER.info("Adding user " +su );
            try {
                GSUser u = new GSUser();
                u.setExtId(su.getExtId());
                u.setName(su.getUserName());
                u.setEnabled(su.isEnabled());
                // group list is not retrievable in shortuser :|

                userAdminService.insert(u);
                okCnt++;
            } catch (Exception e) {
                LOGGER.info("Could not add user " +su +": " + e.getMessage());
            }
        }
        LOGGER.info(okCnt+"/"+users.getUserList().size() + " items inserted");
    }

    /**
     * Simplified operation used for quick re-insertion of data extracted with a GET op in the related service
     */
    @Override
    public void setInstances(RESTShortInstanceList instances) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        int okCnt = 0;
        for (ShortInstance si : instances) {
            LOGGER.info("Adding instance " +si );
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
                LOGGER.info("Could not add instance " +si +": " + e.getMessage());
            }
        }
        LOGGER.info(okCnt+"/"+instances.getList().size() + " items inserted");
    }

    /**
     * Simplified operation used for quick re-insertion of data extracted with a GET op in the related service
     */
    @Override
    public void setRules(RESTOutputRuleList rules) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        int okCnt = 0;
        Map<String, UserGroup>  groups = new HashMap<String, UserGroup>();
        Map<String, GSUser>     users = new HashMap<String, GSUser>();
        Map<String, GSInstance> instances = new HashMap<String, GSInstance>();

        for (RESTOutputRule in : rules) {
            try {
                Rule out = new Rule();

                out.setAccess(in.getGrant());
                out.setPriority(in.getPriority());

                if (in.getGroup() != null) {
                    String name = in.getGroup().getName();
                    UserGroup ug = groups.get(name);
                    if(ug == null) {
                        ug = userGroupAdminService.get(name);
                        groups.put(name, ug);
                    }
                    out.setUserGroup(ug);
                }

                if (in.getUser() != null) {
                    String name = in.getUser().getName();
                    GSUser user = users .get(name);
                    if(user == null) {
                        user = userAdminService.get(name);
                        users.put(name, user);
                    }
                    out.setGsuser(user);
                }

                if (in.getInstance()!= null) {
                    String name = in.getInstance().getName();
                    GSInstance instance = instances.get(name);
                    if(instance == null) {
                        instance = instanceAdminService.get(name);
                        instances.put(name, instance);
                    }
                    out.setInstance(instance);
                }
                out.setService(in.getService());
                out.setRequest(in.getRequest());
                out.setWorkspace(in.getWorkspace());
                out.setLayer(in.getLayer());

                long ruleid = ruleAdminService.insert(out);
                okCnt++;

                if (in.getConstraints() != null) {
                    LOGGER.warn("TODO::: Constraints exist but will not be inserted for rule " + out);
                }
            } catch (Exception e) {
                LOGGER.info("Could not add rule " +in +": " + e.getMessage());
            }
        }
        LOGGER.info(okCnt+"/"+rules.getList().size() + " items inserted");
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

    public void setGrUserAdminService(GFUserAdminService service) {
        this.grUserAdminService = service;
    }

    public void setRestBatchService(RESTBatchService restBatchService) {
        this.restBatchService = restBatchService;
    }     

    // ==========================================================================
    class RemapperCache<TYPE, SERVICE extends GetProviderService<TYPE>> {

        private Map<Long, TYPE> cache = new HashMap<Long, TYPE>();
        private final Map<Long, Long> idRemapper;
        private final SERVICE service;

        public RemapperCache(SERVICE service, Map<Long, Long> idRemapper) {
            this.idRemapper = idRemapper;
            this.service = service;
        }

        TYPE get(Long oldId) throws RemapperException, NotFoundRestEx {
            Long newId = idRemapper.get(oldId);
            if ( newId == null ) {
                LOGGER.error("Can't remap " + oldId);
                throw new RemapperException("Can't remap " + oldId);
            }

            TYPE cached = cache.get(newId);
            try {
                if ( cached == null ) {
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

        public RemapperException() {
        }
    }
}

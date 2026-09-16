/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.misc.csv2geofence;

import jakarta.xml.bind.JAXB;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.misc.csv2geofence.config.model.Configuration;
import org.geofence.misc.csv2geofence.config.model.GeofenceConfig;
import org.geofence.misc.csv2geofence.config.model.RuleFileConfig;
import org.geofence.misc.csv2geofence.config.model.UserFileConfig;
import org.geofence.misc.csv2geofence.config.model.internal.RuleOp;
import org.geofence.misc.csv2geofence.config.model.internal.RunInfo;
import org.geofence.misc.csv2geofence.config.model.internal.UserOp;
import org.geofence.misc.csv2geofence.impl.RuleFileLoader;
import org.geofence.misc.csv2geofence.impl.RulesProcessor;
import org.geofence.misc.csv2geofence.impl.UserFileLoader;
import org.geofence.misc.csv2geofence.impl.UsersProcessor;
import org.geofence.web.rest.api.interfaces.params.RESTRuleFilter;
import org.geofence.web.rest.api.model.RESTBatch;
import org.geofence.web.rest.api.model.RESTBatchOperation;
import org.geofence.web.rest.api.model.RESTInputRule;
import org.geofence.web.rest.api.model.RESTInputUser;
import org.geofence.web.rest.api.model.RESTOutputGroup;
import org.geofence.web.rest.api.model.RESTOutputRule;
import org.geofence.web.rest.api.model.RESTOutputRuleList;
import org.geofence.web.rest.api.model.config.RESTFullUserGroupList;
import org.geofence.web.rest.api.util.RESTBatchOperationFactory;
import org.geofence.web.rest.client.GeoFenceAdminClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * Main logic.
 *
 * <p>Invoked by the main() method.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class Runner {
    private static final Logger LOGGER = LogManager.getLogger(Runner.class);

    private RunInfo runInfo;

    public Runner(RunInfo runInfo) {
        this.runInfo = runInfo;
    }

    public void run() throws IOException {

        Configuration cfg = JAXB.unmarshal(runInfo.getConfigurationFile(), Configuration.class);

        GeoFenceAdminClient geoFenceClient = createClient(cfg.getGeofenceConfig());
        if (geoFenceClient == null) System.exit(4);

        // -- load existing groups from geofence

        // map contains as key the uppercase version of the name of the group in the value
        LOGGER.info("Loading existing groups in GeoFence...");
        Map<String, String> existingGroups = retrieveGFGroups(geoFenceClient);

        // -- gather information on requested groups from files
        // map contains as key the uppercase version of the name of the group in the value
        LOGGER.info("Scanning user files for groups...");
        Map<String, String> requestedGroups = retrieveRequestedGroups(cfg.getUserFileConfig(), runInfo.getUserFiles());

        RESTBatch batch = new RESTBatch();

        // create new user groups
        List<RESTBatchOperation> groupOps = createGroups(requestedGroups, existingGroups);
        batch.getList().addAll(groupOps);

        // Process the user files
        LOGGER.info("Scanning user files for users...");
        for (File file : runInfo.getUserFiles()) {
            LOGGER.info("Processing user file '" + file + "'");
            List<RESTBatchOperation> batchOps = processUserFile(file, cfg.getUserFileConfig(), existingGroups);
            batch.getList().addAll(batchOps);
        }

        // Process the rule files
        LOGGER.info("Scanning rule files...");
        for (File file : runInfo.getRuleFiles()) {
            LOGGER.info("Processing rule file '" + file + "'");
            List<RESTBatchOperation> batchOps = processRuleFile(file, cfg.getRuleFileConfig(), existingGroups);
            batch.getList().addAll(batchOps);
        }

        // verify users
        LOGGER.info("Performing existence check on users...");
        verifyUsers(batch, geoFenceClient);
        LOGGER.info("Performing existence check on rules...");
        verifyRules(batch, geoFenceClient);

        if (runInfo.getOutputFile() != null) {
            Writer xmlWriter = null;
            xmlWriter = new FileWriter(runInfo.getOutputFile());
            LOGGER.info("Creating XML command file " + runInfo.getOutputFile());
            JAXB.marshal(batch, xmlWriter);
            xmlWriter.flush();
            xmlWriter.close();
            LOGGER.info("XML command file saved.");
        }

        if (runInfo.isSendRequested()) {
            LOGGER.info("Sending " + batch.getList().size() + " commands to GeoFence...");
            try {
                geoFenceClient.getBatchService().exec(batch);
                LOGGER.info("GeoFence data updated");
            } catch (RestClientResponseException ex) {
                LOGGER.error("GeoFence error (HTTP:" + ex.getStatusCode().value() + "): " + ex.getMessage(), ex);
                LOGGER.error("GeoFence data have not been updated");
            }
        }
    }

    private static List<RESTBatchOperation> processUserFile(
            File userFile, UserFileConfig ucfg, Map<String, String> remappedGroupNames) throws IOException {
        // load and parse file

        List<UserOp> userOps;
        try {
            UserFileLoader loader = new UserFileLoader(ucfg);
            userOps = loader.load(userFile);
            UsersProcessor processor = new UsersProcessor();
            return processor.buildUserBatchOps(userOps, remappedGroupNames);

        } catch (IOException e) {
            LOGGER.warn("Error loading file '" + userFile + "': " + e.getMessage(), e);
            throw e;
        }
    }

    private static List<RESTBatchOperation> processRuleFile(
            File ruleFile, RuleFileConfig cfg, Map<String, String> remappedGroupNames) throws IOException {
        // load and parse file

        List<RuleOp> ruleOps;
        try {
            RuleFileLoader loader = new RuleFileLoader(cfg);
            ruleOps = loader.load(ruleFile);
            RulesProcessor processor = new RulesProcessor();
            return processor.buildBatchOps(ruleOps, remappedGroupNames, cfg);

        } catch (IOException e) {
            LOGGER.warn("Error loading file '" + ruleFile + "': " + e.getMessage(), e);
            throw e;
        }
    }

    private Map<String, String> retrieveGFGroups(GeoFenceAdminClient client) {
        RESTFullUserGroupList groups = client.getUserGroupService().getList(null, null, null);
        Map<String, String> ret = new HashMap<String, String>();
        for (RESTOutputGroup shortGroup : groups.getList()) {
            String groupName = shortGroup.getName();
            String old = ret.put(groupName.toUpperCase(), groupName);
            if (old != null) {
                LOGGER.error("Group name collision in " + old + " and " + groupName);
                throw new IllegalStateException("Group name collision (" + groupName.toUpperCase() + ")");
            }
        }

        return ret;
    }

    /**
     * Collect all group names from user files.
     *
     * @param cfg
     * @param files
     * @return a Map having as keys the uppercase name
     */
    private Map<String, String> retrieveRequestedGroups(UserFileConfig cfg, List<File> files) {
        Map<String, String> upperNames = new HashMap<String, String>();

        UserFileLoader loader = new UserFileLoader(cfg);

        for (File file : files) {
            LOGGER.debug("Collecting group names from file '" + file + "'");
            List<UserOp> userOps;
            try {
                userOps = loader.load(file);
            } catch (IOException ex) {
                LOGGER.error("Error loading file " + file + ": " + ex.getMessage());
                throw new IllegalStateException("Error loading file", ex);
            }
            for (UserOp userOp : userOps) {
                for (String groupName : userOp.getGroups()) {
                    String old = upperNames.put(groupName.toUpperCase(), groupName);
                    if (old != null && !old.equals(groupName)) {
                        LOGGER.warn("Conflict in group names in files: '" + old + "' and '" + groupName + "'");
                    }
                }
            }
        }
        LOGGER.info("Collected " + upperNames.size() + " groups in " + files.size() + " files");
        return upperNames;
    }

    /** May add entries to existingGroups */
    private List<RESTBatchOperation> createGroups(
            Map<String, String> requestedGroups, Map<String, String> existingGroups) {

        List<RESTBatchOperation> ret = new ArrayList<RESTBatchOperation>();

        for (String reqUpperGroup : requestedGroups.keySet()) {
            if (!existingGroups.containsKey(reqUpperGroup)) {
                String realName = requestedGroups.get(reqUpperGroup); // the group real case
                LOGGER.warn("Adding new group '" + realName + "'");

                RESTBatchOperation op = RESTBatchOperationFactory.createGroupInputOp(realName);
                //                RESTBatchOperation op = new RESTBatchOperation();
                //                op.setService(RESTBatchOperation.ServiceName.groups);
                //                op.setType(RESTBatchOperation.TypeName.insert);
                //                RESTInputGroup group = new RESTInputGroup();
                //                group.setEnabled(Boolean.TRUE);
                //                group.setName(realName);
                //                op.setPayload(group);
                ret.add(op);

                existingGroups.put(reqUpperGroup, realName);
            }
        }

        LOGGER.info("Inserting  " + ret.size() + " new groups");
        return ret;
    }

    private GeoFenceAdminClient createClient(GeofenceConfig cfg) {
        GeoFenceAdminClient geoFenceClient = new GeoFenceAdminClient();
        geoFenceClient.setRestUrl(cfg.getRestUrl());
        geoFenceClient.setUsername(cfg.getUsername());
        geoFenceClient.setPassword(cfg.getPassword());

        try {
            geoFenceClient.getUserGroupService().count("this_is_a_simple_ping");
            return geoFenceClient;
        } catch (Exception e) {
            LOGGER.error("Can't connect to GeoFence: " + e.getMessage(), e);
            return null;
        }
    }

    private void verifyUsers(RESTBatch batch, GeoFenceAdminClient client) {
        for (RESTBatchOperation op : batch.getList()) {
            if (op.getService() == RESTBatchOperation.ServiceName.users) {

                switch (op.getType()) {
                    case insert:
                        {
                            String userName = ((RESTInputUser) op.getPayload()).getName();
                            boolean exist = existUser(client, userName);
                            if (exist)
                                LOGGER.warn("User " + userName
                                        + " already in GeoFence: operation 'insert' is likely to trigger an error.");
                        }
                        break;

                    case update:
                    case delete:
                        {
                            String userName = op.getName();
                            boolean exist = existUser(client, userName);

                            if (!exist)
                                LOGGER.warn("User " + userName + " not found in GeoFence: operation '" + op.getType()
                                        + "' is likely to trigger an error.");
                        }
                        break;
                }
            }
        }
    }

    /** Check for rule collision. Optionally augment the batch list with rule deletion operations. */
    private void verifyRules(RESTBatch batch, GeoFenceAdminClient client) {

        List<RESTBatchOperation> deleteOps = new LinkedList<RESTBatchOperation>();

        for (RESTBatchOperation op : batch.getList()) {
            if (op.getService() == RESTBatchOperation.ServiceName.rules) {

                StringBuilder sb = new StringBuilder();
                RESTInputRule rule = (RESTInputRule) op.getPayload();
                RESTRuleFilter ruleFilter = new RESTRuleFilter();

                ruleFilter.groupName = rule.getRolename();
                ruleFilter.groupDefault = false;
                sb.append("group:").append(rule.getRolename());

                ruleFilter.layer = rule.getLayer();
                ruleFilter.layerDefault = false;
                sb.append(" layer:").append(rule.getLayer());

                if (rule.getService() != null) {
                    ruleFilter.serviceName = rule.getService();
                    ruleFilter.serviceDefault = false;
                    sb.append(" service:").append(rule.getService());
                }

                if (rule.getRequest() != null) {
                    ruleFilter.requestName = rule.getRequest();
                    ruleFilter.requestDefault = false;
                    sb.append(" request:").append(rule.getRequest());
                }

                RESTOutputRuleList rulesFound = client.getRuleService().get(null, null, false, ruleFilter);
                if (!rulesFound.getList().isEmpty()) {
                    if (rulesFound.getList().size() == 1) {

                        if (runInfo.isDeleteObsoleteRules()) {
                            RESTBatchOperation ruleDelOp = RESTBatchOperationFactory.createDeleteRuleOp(
                                    rulesFound.getList().get(0).getId());
                            deleteOps.add(ruleDelOp);
                            LOGGER.debug("Replacing rule on " + sb);

                        } else {
                            LOGGER.warn("Rule " + rule
                                    + " already exists in GeoFence: operation 'insert' is likely to trigger an error.");
                            for (RESTOutputRule ruleFound : rulesFound) {
                                LOGGER.info(" - Rule found : " + ruleFound);
                            }
                        }

                    } else if (rulesFound.getList().size() > 1) {

                        LOGGER.error("Found too many rules matching an input rule");
                        LOGGER.error("Input Rule is " + rule);
                        for (RESTOutputRule ruleFound : rulesFound) {
                            LOGGER.info("  -  Rule found : " + ruleFound);
                        }
                        throw new IllegalStateException("Found too many rules matching " + rule);
                    }
                }
            }
        }

        if (!deleteOps.isEmpty()) {
            LOGGER.info("Adding " + deleteOps.size() + " rule delete ops at the top of the batch");
            LOGGER.debug("Old list size: " + batch.getList().size());
            batch.getList().addAll(0, deleteOps);
            LOGGER.debug("New list size: " + batch.getList().size());
        }
    }

    private boolean existUser(GeoFenceAdminClient client, String userName) {
        try {
            client.getUserService().get(userName);
            return true;
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                return false;
            } else {
                LOGGER.error("Error retrieving user '" + userName + "' in GeoFence: " + ex.getMessage() + "("
                        + ex.getStatusCode().value() + ")");
                throw new RuntimeException("Error retrieving user '" + userName + "' in GeoFence", ex);
            }
        }
    }
}

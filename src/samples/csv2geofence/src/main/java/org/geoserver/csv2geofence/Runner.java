/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.csv2geofence;

import org.geoserver.csv2geofence.config.model.Configuration;
import org.geoserver.csv2geofence.config.model.GeofenceConfig;
import org.geoserver.csv2geofence.config.model.RuleFileConfig;
import org.geoserver.csv2geofence.config.model.UserFileConfig;
import org.geoserver.csv2geofence.config.model.internal.RuleOp;
import org.geoserver.csv2geofence.config.model.internal.RunInfo;
import org.geoserver.csv2geofence.config.model.internal.UserOp;
import org.geoserver.csv2geofence.impl.RuleFileLoader;
import org.geoserver.csv2geofence.impl.UserFileLoader;
import org.geoserver.csv2geofence.impl.RulesProcessor;
import org.geoserver.csv2geofence.impl.UsersProcessor;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.ShortGroup;
import org.geoserver.geofence.services.rest.GeoFenceClient;
import org.geoserver.geofence.services.rest.RuleServiceHelper;
import org.geoserver.geofence.services.rest.model.RESTBatch;
import org.geoserver.geofence.services.rest.model.RESTBatchOperation;
import org.geoserver.geofence.services.rest.model.RESTInputRule;
import org.geoserver.geofence.services.rest.model.RESTInputUser;
import org.geoserver.geofence.services.rest.model.RESTOutputRule;
import org.geoserver.geofence.services.rest.model.RESTOutputRuleList;
import org.geoserver.geofence.services.rest.model.config.RESTFullUserGroupList;
import org.geoserver.geofence.services.rest.model.util.RESTBatchOperationFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXB;
import org.apache.cxf.jaxrs.client.ServerWebApplicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Main logic.
 *
 * Invoked by the main() method.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class Runner {
    private final static Logger LOGGER = LogManager.getLogger(Runner.class);

    private RunInfo runInfo;

    public Runner(RunInfo runInfo) {
        this.runInfo = runInfo;
    }

    public void run() throws IOException {

        Configuration cfg = JAXB.unmarshal(runInfo.getConfigurationFile(), Configuration.class);


        GeoFenceClient geoFenceClient = createClient(cfg.getGeofenceConfig());
        if(geoFenceClient == null)
            System.exit(4);

        //-- load existing groups from geofence

        // map contains as key the uppercase version of the name of the group in the value
        LOGGER.info("Loading existing groups in GeoFence...");
        Map<String, String> existingGroups = retrieveGFGroups(geoFenceClient);

        //-- gather information on requested groups from files
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
            LOGGER.info("Processing user file '" + file+"'");
            List<RESTBatchOperation> batchOps = processUserFile(file, cfg.getUserFileConfig(), existingGroups);
            batch.getList().addAll(batchOps);
        }

        // Process the rule files
        LOGGER.info("Scanning rule files...");
        for (File file : runInfo.getRuleFiles()) {
            LOGGER.info("Processing rule file '" + file+"'");
            List<RESTBatchOperation> batchOps = processRuleFile(file, cfg.getRuleFileConfig(), existingGroups);
            batch.getList().addAll(batchOps);
        }

        // verify users
        LOGGER.info("Performing existence check on users...");
        verifyUsers(batch, geoFenceClient);
        LOGGER.info("Performing existence check on rules...");
        verifyRules(batch, geoFenceClient);


        if( runInfo.getOutputFile() != null) {
            Writer xmlWriter = null;
            xmlWriter = new FileWriter(runInfo.getOutputFile());
            LOGGER.info("Creating XML command file " + runInfo.getOutputFile());
            JAXB.marshal(batch, xmlWriter);
            xmlWriter.flush();
            xmlWriter.close();
            LOGGER.info("XML command file saved.");
        }

        if(runInfo.isSendRequested()) {
            LOGGER.info("Sending "+batch.getList().size()+" commands to GeoFence...");
            try {
                geoFenceClient.getBatchService().exec(batch);
                LOGGER.info("GeoFence data updated");
            } catch (ServerWebApplicationException ex) {
                LOGGER.error("GeoFence error (HTTP:"+ex.getStatus()+"): " + ex.getMessage() , ex);
                LOGGER.error("GeoFence data have not been updated");
            }
        }
    }



    private static List<RESTBatchOperation> processUserFile(File userFile, UserFileConfig ucfg, Map<String,String> remappedGroupNames) throws IOException  {
        // load and parse file

        List<UserOp> userOps;
        try {
            UserFileLoader loader = new UserFileLoader(ucfg);
            userOps = loader.load(userFile);
            UsersProcessor processor = new UsersProcessor();
            return processor.buildUserBatchOps(userOps, remappedGroupNames);

        } catch (IOException e) {
            LOGGER.warn("Error loading file '"+userFile+"': " + e.getMessage(), e);
            throw e;
        }
    }

    private static List<RESTBatchOperation> processRuleFile(File ruleFile, RuleFileConfig cfg, Map<String,String> remappedGroupNames) throws IOException  {
        // load and parse file

        List<RuleOp> ruleOps;
        try {
            RuleFileLoader loader = new RuleFileLoader(cfg);
            ruleOps = loader.load(ruleFile);
            RulesProcessor processor = new RulesProcessor();
            return processor.buildBatchOps(ruleOps, remappedGroupNames, cfg);

        } catch (IOException e) {
            LOGGER.warn("Error loading file '"+ruleFile+"': " + e.getMessage(), e);
            throw e;
        }
    }

    private Map<String, String> retrieveGFGroups(GeoFenceClient client) {
        RESTFullUserGroupList groups = client.getUserGroupService().getList(null, null, null);
        Map<String, String> ret = new HashMap<String, String>();
        for (ShortGroup shortGroup : groups.getList()) {
            String groupName = shortGroup.getName();
            String old = ret.put(groupName.toUpperCase(), groupName);
            if(old != null) {
                LOGGER.error("Group name collision in " + old + " and " + groupName);
                throw new IllegalStateException("Group name collision ("+groupName.toUpperCase()+")");
            }
        }

        return ret;
    }

    /**
     * Collect all group names from user files.
     *
     *
     * @param cfg
     * @param files
     * @return a Map having as keys the uppercase name
     */
    private Map<String, String> retrieveRequestedGroups(UserFileConfig cfg, List<File> files) {
        Map<String, String> upperNames = new HashMap<String, String>();

        UserFileLoader loader = new UserFileLoader(cfg);

        for (File file : files) {
            LOGGER.debug("Collecting group names from file '" + file+"'");
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
                    if(old != null && ! old.equals(groupName)) {
                        LOGGER.warn("Conflict in group names in files: '"+old+"' and '"+groupName+"'");
                    }
                }
            }
        }
        LOGGER.info("Collected " + upperNames.size() + " groups in " + files.size() + " files");
        return upperNames;
    }

    /**
     *
     * May add entries to existingGroups
     */
    private List<RESTBatchOperation> createGroups(Map<String, String> requestedGroups, Map<String, String> existingGroups) {

        List<RESTBatchOperation> ret = new ArrayList<RESTBatchOperation>();

        for (String reqUpperGroup : requestedGroups.keySet()) {
            if(! existingGroups.containsKey(reqUpperGroup)) {
                String realName = requestedGroups.get(reqUpperGroup); // the group real case
                LOGGER.warn("Adding new group '"+realName+"'" );

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


    private GeoFenceClient createClient(GeofenceConfig cfg) {
        GeoFenceClient geoFenceClient = new GeoFenceClient();
        geoFenceClient.setGeostoreRestUrl(cfg.getRestUrl());
        geoFenceClient.setUsername(cfg.getUsername());
        geoFenceClient.setPassword(cfg.getPassword());

        try {
            geoFenceClient.getUserGroupService().count("this_is_a_simple_ping");
            return geoFenceClient;
        } catch (Exception e) {
            LOGGER.error("Can't connect to GeoFence: "+ e.getMessage(), e);
            return null;
        }
    }

    private void verifyUsers(RESTBatch batch, GeoFenceClient client) {
        for (RESTBatchOperation op : batch.getList()) {
            if(op.getService() == RESTBatchOperation.ServiceName.users) {

                switch(op.getType()) {
                    case insert:
                    {
                        String userName = ((RESTInputUser)op.getPayload()).getName();
                        boolean exist = existUser(client, userName);
                        if(exist)
                            LOGGER.warn("User " + userName + " already in GeoFence: operation 'insert' is likely to trigger an error.");
                    }
                    break;

                    case update:
                    case delete:
                    {
                        String userName = op.getName();
                        boolean exist = existUser(client, userName);

                        if( ! exist )
                            LOGGER.warn("User " + userName + " not found in GeoFence: operation '"+op.getType()+"' is likely to trigger an error.");
                    }
                    break;
                }
            }
        }
    }

    /**
     * Check for rule collision.
     * Optionally augment the batch list with rule deletion operations.
     *
     */
    private void verifyRules(RESTBatch batch, GeoFenceClient client) {

        final RuleServiceHelper helper = new RuleServiceHelper(client.getRuleService());

        List<RESTBatchOperation> deleteOps = new LinkedList<RESTBatchOperation>();

        for (RESTBatchOperation op : batch.getList()) {
            if(op.getService() == RESTBatchOperation.ServiceName.rules) {

                StringBuilder sb = new StringBuilder();
                RESTInputRule rule = (RESTInputRule)op.getPayload();
                RuleFilter ruleFilter = new RuleFilter(RuleFilter.SpecialFilterType.DEFAULT);

                ruleFilter.setUserGroup(rule.getGroup().getName());
                ruleFilter.getUserGroup().setIncludeDefault(false);
                sb.append("group:").append(rule.getGroup().getName());

                ruleFilter.setLayer(rule.getLayer());
                ruleFilter.getLayer().setIncludeDefault(false);
                sb.append(" layer:").append(rule.getLayer());

                if(rule.getService() != null) {
                    ruleFilter.setService(rule.getService());
                    ruleFilter.getService().setIncludeDefault(false);
                    sb.append(" service:").append(rule.getService());
                }

                if(rule.getRequest() != null) {
                    ruleFilter.setRequest(rule.getRequest());
                    ruleFilter.getRequest().setIncludeDefault(false);
                    sb.append(" request:").append(rule.getRequest());
                }

                RESTOutputRuleList rulesFound = helper.get(null, null, false, ruleFilter);
                if( ! rulesFound.getList().isEmpty()) {
                    if(rulesFound.getList().size() == 1) {

                        if(runInfo.isDeleteObsoleteRules()) {
                            RESTBatchOperation ruleDelOp = RESTBatchOperationFactory.createDeleteRuleOp(rulesFound.getList().get(0).getId());
                            deleteOps.add(ruleDelOp);
                            LOGGER.debug("Replacing rule on " + sb);

                        } else {
                            LOGGER.warn("Rule " + rule + " already exists in GeoFence: operation 'insert' is likely to trigger an error.");
                            for (RESTOutputRule ruleFound : rulesFound) {
                                LOGGER.info(" - Rule found : " + ruleFound);
                            }
                        }

                    } else  if(rulesFound.getList().size() > 1) {

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

        if(! deleteOps.isEmpty()) {
            LOGGER.info("Adding "+deleteOps.size()+ " rule delete ops at the top of the batch" );
            LOGGER.debug("Old list size: "+batch.getList().size() );
            batch.getList().addAll(0, deleteOps);
            LOGGER.debug("New list size: "+batch.getList().size() );
        }
    }

    private boolean existUser(GeoFenceClient client, String userName) {
        try {
            client.getUserService().get(userName);
            return true;
        } catch (ServerWebApplicationException ex) {
            if(ex.getStatus() == 404) {
                return false;
            } else {
                LOGGER.error("Error retrieving user '"+userName+"' in GeoFence: " + ex.getMessage() + "("+ex.getStatus()+")");
                throw new RuntimeException("Error retrieving user '"+userName+"' in GeoFence", ex);
            }
        }
    }
}

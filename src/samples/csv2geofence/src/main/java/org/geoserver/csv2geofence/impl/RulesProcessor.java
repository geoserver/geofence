/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.csv2geofence.impl;

import org.geoserver.csv2geofence.config.model.RuleFileConfig;
import org.geoserver.csv2geofence.config.model.RuleFileConfig.ServiceRequest;
import org.geoserver.csv2geofence.config.model.RuleFileConfig.ServiceRequest.Type;
import org.geoserver.csv2geofence.config.model.internal.RuleOp;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.services.rest.model.RESTBatchOperation;
import org.geoserver.geofence.services.rest.model.RESTInputRule;
import org.geoserver.geofence.services.rest.model.util.RESTBatchOperationFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Transforms RuleOps into RESTBatchoperations
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RulesProcessor {

    private final static Logger LOGGER = LogManager.getLogger(RulesProcessor.class);

    /**
     * @param ops
     * @param availableGroups may be augmented
     * @param ruleMapping
     * @return
     */
    public List<RESTBatchOperation> buildBatchOps(List<RuleOp> ops, Map<String, String> availableGroups, RuleFileConfig cfg) {
        List<RESTBatchOperation> ret = new ArrayList<RESTBatchOperation>(ops.size());

        for (RuleOp op : ops) {
            LOGGER.debug("Preparing for output " + op);
            List<RESTBatchOperation> restOps = buildBatchOperation(op, availableGroups, cfg);
            ret.addAll(restOps);
        }

        return ret;
    }

    /**
     * @param ruleOp
     * @param availableGroups may be augmented
     * @param ruleMapping
     * @return
     */
    protected List<RESTBatchOperation> buildBatchOperation(RuleOp ruleOp, Map<String, String> availableGroups, RuleFileConfig ruleFileConfig) {

        Map<String,List<RuleFileConfig.ServiceRequest>> ruleMapping = ruleFileConfig.getRuleMapping();
        int offsetFromBottom = ruleFileConfig.getOffsetFromBottom();


        List<RESTBatchOperation> ret = new ArrayList<RESTBatchOperation>();

        final String reqGroupName = ruleOp.getGroupName();
        if(! availableGroups.containsKey(reqGroupName.toUpperCase())) {
            LOGGER.warn("Adding new group '"+reqGroupName+"'" );
            availableGroups.put(reqGroupName.toUpperCase(), reqGroupName);
            RESTBatchOperation op = RESTBatchOperationFactory.createGroupInputOp(reqGroupName);
            ret.add(op);
        }
        final String realGroupName = availableGroups.get(ruleOp.getGroupName().toUpperCase());

        List<ServiceRequest> mapping = ruleMapping.get(ruleOp.getVerb());
        if(mapping == null) {
            LOGGER.error("Unknown verb in " + ruleOp);
            throw new IllegalArgumentException("Unknown verb in " + ruleOp);

        } else {
            for (ServiceRequest serviceRequest : mapping) {
            // running the list in reverse order since we'll add the rules using offsetFromBottom,
            // so they will result in a reversed order again
//            for (int i = mapping.size() -1; i>=0; i--) {
//                ServiceRequest serviceRequest = mapping.get(i);
                RESTBatchOperation restOp = new RESTBatchOperation();
                restOp.setService(RESTBatchOperation.ServiceName.rules);
                restOp.setType(RESTBatchOperation.TypeName.insert);

                RESTInputRule rule = new RESTInputRule();
                rule.setGroupName(realGroupName);
                rule.setLayer(ruleOp.getLayerName());
                rule.setService(serviceRequest.getService());
                rule.setRequest(serviceRequest.getRequest());
                if(serviceRequest.getGrant()==Type.allow)
                    rule.setGrant(GrantType.ALLOW);
                else if(serviceRequest.getGrant()==Type.deny)
                    rule.setGrant(GrantType.DENY);
                else {
                    throw new IllegalArgumentException("Unexpected grant type in " + serviceRequest + " for " + ruleOp);
                }
                rule.setPosition(new RESTInputRule.RESTRulePosition(RESTInputRule.RESTRulePosition.RulePosition.offsetFromBottom, offsetFromBottom));

                restOp.setPayload(rule);
                ret.add(restOp);
            }        
        }

        return ret;
    }

}

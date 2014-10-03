/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.csv2geofence.impl;

import org.geoserver.csv2geofence.config.model.internal.UserOp;
import org.geoserver.geofence.services.rest.model.RESTBatchOperation;
import org.geoserver.geofence.services.rest.model.RESTInputUser;
import org.geoserver.geofence.services.rest.model.util.IdName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Transforms UserOps into RESTBatchoperations
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class UsersProcessor {

    private final static Logger LOGGER = LogManager.getLogger(UsersProcessor.class);

    public List<RESTBatchOperation> buildUserBatchOps(List<UserOp> ops, Map<String, String> availableGroups) {
        List<RESTBatchOperation> ret = new ArrayList<RESTBatchOperation>(ops.size());

        for (UserOp userOp : ops) {
            LOGGER.debug("Preparing for output " + userOp);
            RESTBatchOperation restOp = buildBatchOperation(userOp, availableGroups);
            ret.add(restOp);
        }

        return ret;
    }

    protected RESTBatchOperation buildBatchOperation(UserOp userOp, Map<String, String> availableGroups) {

        RESTBatchOperation restOp = new RESTBatchOperation();
        restOp.setService(RESTBatchOperation.ServiceName.users);

        switch(userOp.getType()) {
            case INSERT:
                restOp.setType(RESTBatchOperation.TypeName.insert);

                // inserting a user: we need all the fields
                RESTInputUser restUser = new RESTInputUser();
                restUser.setEnabled(true);
                restUser.setEmailAddress(userOp.getMailAddress());
                restUser.setFullName(userOp.getFullName());
                restUser.setName(userOp.getUserName());
                restUser.setGroups(buildGroupList(userOp, availableGroups));

                restOp.setPayload(restUser);

                break;

            case UPDATE:
                restOp.setType(RESTBatchOperation.TypeName.update);

                // when updating, we're only reassigning groups
                restOp.setName(userOp.getUserName()); // set the key

                RESTInputUser restUpdateUser = new RESTInputUser();
                restUpdateUser.setGroups(buildGroupList(userOp, availableGroups)); // and the groups

                restOp.setPayload(restUpdateUser);

                break;

            case DELETE:
                restOp.setType(RESTBatchOperation.TypeName.delete);
                restOp.setName(userOp.getUserName());

                break;

            default:
                LOGGER.error("Unexpected operation type '"+userOp.getType()+"' for operation " + userOp);
                throw new IllegalStateException("Unexpected operation type '"+userOp.getType()+"' for operation " + userOp);                
        }

        return restOp;
    }

    protected List<IdName> buildGroupList(UserOp userOp, Map<String, String> availableGroups) {
        List<IdName> ret = new ArrayList<IdName>(userOp.getGroups().size());
        for (String groupName : userOp.getGroups()) {
            String groupRealName = availableGroups.get(groupName.toUpperCase());
            if(groupRealName == null)
                throw new IllegalStateException("Can't find group name '"+groupName+"' for " + userOp);

            ret.add(new IdName(groupRealName));
        }
        return ret;
    }


}

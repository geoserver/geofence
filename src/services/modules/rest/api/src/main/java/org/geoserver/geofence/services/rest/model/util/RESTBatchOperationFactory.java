/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model.util;

import org.geoserver.geofence.services.rest.model.RESTBatchOperation;
import org.geoserver.geofence.services.rest.model.RESTInputGroup;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RESTBatchOperationFactory {

    public static RESTBatchOperation createUserInputOp() {
        RESTBatchOperation op = new RESTBatchOperation();
        op.setService(RESTBatchOperation.ServiceName.users);
        op.setType(RESTBatchOperation.TypeName.insert);
        return op;
    }

    public static RESTBatchOperation createInstanceInputOp() {
        RESTBatchOperation op = new RESTBatchOperation();
        op.setService(RESTBatchOperation.ServiceName.instances);
        op.setType(RESTBatchOperation.TypeName.insert);
        return op;
    }

    public static RESTBatchOperation createRuleInputOp() {
        RESTBatchOperation op = new RESTBatchOperation();
        op.setService(RESTBatchOperation.ServiceName.rules);
        op.setType(RESTBatchOperation.TypeName.insert);
        return op;
    }

    public static RESTBatchOperation createUserUpdateOp(String username) {
        RESTBatchOperation op = new RESTBatchOperation();
        op.setService(RESTBatchOperation.ServiceName.users);
        op.setType(RESTBatchOperation.TypeName.update);
        op.setName(username);
        return op;
    }

    public static RESTBatchOperation createUserUpdateOp(Long userId) {
        RESTBatchOperation op = new RESTBatchOperation();
        op.setService(RESTBatchOperation.ServiceName.users);
        op.setType(RESTBatchOperation.TypeName.update);
        op.setId(userId);
        return op;
    }

    public static RESTBatchOperation createGroupInputOp(String name) {
        RESTInputGroup group = new RESTInputGroup();
        group.setEnabled(Boolean.TRUE);
        group.setName(name);

        RESTBatchOperation op = new RESTBatchOperation();
        op.setService(RESTBatchOperation.ServiceName.groups);
        op.setType(RESTBatchOperation.TypeName.insert);
        op.setPayload(group);
        
        return op;
    }

    public static RESTBatchOperation createDeleteRuleOp(Long ruleId) {
        RESTBatchOperation op = new RESTBatchOperation();
        op.setService(RESTBatchOperation.ServiceName.rules);
        op.setType(RESTBatchOperation.TypeName.delete);
        op.setId(ruleId);
        return op;
    }

}

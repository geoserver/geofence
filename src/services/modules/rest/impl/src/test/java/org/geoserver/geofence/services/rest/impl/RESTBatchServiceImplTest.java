package org.geoserver.geofence.services.rest.impl;

import org.geoserver.geofence.core.model.enums.AdminGrantType;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.services.rest.exception.BadRequestRestEx;
import org.geoserver.geofence.services.rest.model.RESTBatch;
import org.geoserver.geofence.services.rest.model.RESTBatchOperation;
import org.geoserver.geofence.services.rest.model.RESTInputAdminRule;
import org.geoserver.geofence.services.rest.model.RESTInputGroup;
import org.geoserver.geofence.services.rest.model.RESTInputRule;
import org.geoserver.geofence.services.rest.model.RESTInputUser;
import org.geoserver.geofence.services.rest.model.RESTOutputRule;
import org.geoserver.geofence.services.rest.model.RESTRulePosition;
import org.geoserver.geofence.services.rest.model.util.IdName;
import org.geoserver.geofence.services.rest.model.util.RESTBatchOperationFactory;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class RESTBatchServiceImplTest extends RESTBaseTest {


    @Test
    public void testBatchInsertAdminRule() {
        RESTInputAdminRule adminRule=new RESTInputAdminRule();
        adminRule.setPosition(new RESTRulePosition(RESTRulePosition.RulePosition.offsetFromTop,0));
        adminRule.setWorkspace("ws99");
        adminRule.setRolename("roleName00");
        adminRule.setGrant(AdminGrantType.ADMIN);
        RESTBatchOperation restBatchOperation=new RESTBatchOperation();
        restBatchOperation.setService(RESTBatchOperation.ServiceName.adminrules);
        restBatchOperation.setType(RESTBatchOperation.TypeName.insert);
        restBatchOperation.setPayload(adminRule);
        RESTInputAdminRule adminRule2=new RESTInputAdminRule();
        adminRule2.setPosition(new RESTRulePosition(RESTRulePosition.RulePosition.offsetFromTop,0));
        adminRule2.setWorkspace("ws999");
        adminRule2.setRolename("roleName00");
        adminRule2.setGrant(AdminGrantType.USER);
        RESTBatchOperation restBatchOperation2=new RESTBatchOperation();
        restBatchOperation2.setService(RESTBatchOperation.ServiceName.adminrules);
        restBatchOperation2.setType(RESTBatchOperation.TypeName.insert);
        restBatchOperation2.setPayload(adminRule2);
        RESTBatch restBatch=new RESTBatch();
        restBatch.add(restBatchOperation);
        restBatch.add(restBatchOperation2);
        assertEquals(200,restBatchService.exec(restBatch).getStatus());

        long result=restAdminRuleService.count(null,true,"roleName00",false,null,null,false,null,false);
        assertEquals(2,result);
    }

    @Test
    public void testBatchUpdateAdminRule() {
        RESTInputAdminRule adminRule=new RESTInputAdminRule();
        adminRule.setPosition(new RESTRulePosition(RESTRulePosition.RulePosition.offsetFromTop,0));
        adminRule.setWorkspace("ws99");
        adminRule.setRolename("roleName11");
        adminRule.setGrant(AdminGrantType.ADMIN);
        RESTInputAdminRule adminRule2=new RESTInputAdminRule();
        adminRule2.setPosition(new RESTRulePosition(RESTRulePosition.RulePosition.offsetFromTop,0));
        adminRule2.setWorkspace("ws999");
        adminRule2.setRolename("roleName11");
        adminRule2.setGrant(AdminGrantType.USER);
        Long id=(Long)restAdminRuleService.insert(adminRule).getEntity();
        Long id2=(Long)restAdminRuleService.insert(adminRule2).getEntity();
        RESTBatchOperation restBatchOperation=new RESTBatchOperation();
        restBatchOperation.setService(RESTBatchOperation.ServiceName.adminrules);
        restBatchOperation.setType(RESTBatchOperation.TypeName.update);
        restBatchOperation.setId(id);
        adminRule.setWorkspace("ws00");
        adminRule.setPosition(null);
        restBatchOperation.setPayload(adminRule);

        RESTBatchOperation restBatchOperation2=new RESTBatchOperation();
        restBatchOperation2.setService(RESTBatchOperation.ServiceName.adminrules);
        restBatchOperation2.setType(RESTBatchOperation.TypeName.update);
        restBatchOperation2.setId(id2);
        adminRule2.setWorkspace("ws000");
        adminRule2.setPosition(null);
        restBatchOperation2.setPayload(adminRule2);

        RESTBatch batch=new RESTBatch();
        batch.add(restBatchOperation);
        batch.add(restBatchOperation2);
        restBatchService.exec(batch);

        assertEquals("ws00",restAdminRuleService.get(id).getWorkspace());
        assertEquals("ws000",restAdminRuleService.get(id2).getWorkspace());

    }


    @Test
    public void testBatchDeleteAdminRule() {
        RESTInputAdminRule adminRule=new RESTInputAdminRule();
        adminRule.setPosition(new RESTRulePosition(RESTRulePosition.RulePosition.offsetFromTop,0));
        adminRule.setWorkspace("ws99");
        adminRule.setRolename("roleName22");
        adminRule.setGrant(AdminGrantType.ADMIN);
        RESTInputAdminRule adminRule2=new RESTInputAdminRule();
        adminRule2.setPosition(new RESTRulePosition(RESTRulePosition.RulePosition.offsetFromTop,0));
        adminRule2.setWorkspace("ws999");
        adminRule2.setRolename("roleName22");
        adminRule2.setGrant(AdminGrantType.USER);
        Long id=(Long)restAdminRuleService.insert(adminRule).getEntity();
        Long id2=(Long)restAdminRuleService.insert(adminRule2).getEntity();
        RESTBatchOperation restBatchOperation=new RESTBatchOperation();
        restBatchOperation.setService(RESTBatchOperation.ServiceName.adminrules);
        restBatchOperation.setType(RESTBatchOperation.TypeName.delete);
        restBatchOperation.setId(id);

        RESTBatchOperation restBatchOperation2=new RESTBatchOperation();
        restBatchOperation2.setService(RESTBatchOperation.ServiceName.adminrules);
        restBatchOperation2.setType(RESTBatchOperation.TypeName.delete);
        restBatchOperation2.setId(id2);

        RESTBatch batch=new RESTBatch();
        batch.add(restBatchOperation);
        batch.add(restBatchOperation2);
        restBatchService.exec(batch);
        long result=restAdminRuleService.count(null,true,"roleName22",false,null,null,false,null,false);
        assertEquals(0,result);

    }
}

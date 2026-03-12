/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.client;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.ws.rs.core.Response;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.web.rest.api.interfaces.params.RESTAdminRuleFilter;
import org.geofence.web.rest.api.interfaces.params.RESTRuleFilter;
import org.geofence.web.rest.api.model.RESTInputGroup;
import org.geofence.web.rest.api.model.RESTInputInstance;
import org.geofence.web.rest.api.model.RESTInputRule;
import org.geofence.web.rest.api.model.RESTInputUser;
import org.geofence.web.rest.api.model.RESTOutputAdminRule;
import org.geofence.web.rest.api.model.RESTOutputAdminRuleList;
import org.geofence.web.rest.api.model.RESTOutputGroup;
import org.geofence.web.rest.api.model.RESTOutputRule;
import org.geofence.web.rest.api.model.RESTOutputRuleList;
import org.geofence.web.rest.api.model.RESTRulePosition;
import org.geofence.web.rest.api.model.RESTShortInstance;
import org.geofence.web.rest.api.model.RESTShortUser;
import org.geofence.web.rest.api.model.enums.RESTGrantType;
import org.geofence.web.rest.api.model.util.IdName;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/** @author ETj (etj at geo-solutions.it) */
public class GeoFenceClientTest {
    private static final Logger LOGGER = LogManager.getLogger(GeoFenceClientTest.class);

    public GeoFenceClientTest() {}

    @BeforeEach
    public void before(TestInfo testInfo) throws Exception {
        String methodName = testInfo.getTestMethod().get().getName();
        LOGGER.info("### Running " + getClass().getSimpleName() + "::" + methodName);

        GeoFenceClient client = createClient();
        Assumptions.assumeTrue(pingGeoFence(client));

        removeAll();
    }

    protected GeoFenceClient createClient() {
        GeoFenceClient client = new GeoFenceClient();
        client.setRestUrl("http://localhost:9191/geofence/rest");
        client.setUsername("admin");
        client.setPassword("admin");

        return client;
    }

    protected void removeAll() {
        LOGGER.info("Removing GeoFence resources...");
        GeoFenceClient client = createClient();
        removeUsers(client);
        removeGroups(client);
        removeInstances(client);
        removeRules(client);
        LOGGER.info("Finished removing GeoFence resources...");
    }

    protected void removeUsers(GeoFenceClient client) {
        for (RESTShortUser su :
                client.getUserService().getList(null, null, null).getUserList()) {
            LOGGER.debug("Removing user " + su);
            client.getUserService().delete(su.getUserName(), true);
        }
    }

    protected void removeGroups(GeoFenceClient client) {
        for (RESTOutputGroup sg :
                client.getUserGroupService().getList(null, null, null).getList()) {
            LOGGER.debug("Removing group " + sg);
            client.getUserGroupService().delete(sg.getName(), true);
        }
    }

    protected void removeInstances(GeoFenceClient client) {
        for (RESTShortInstance entry :
                client.getGSInstanceService().getList(null, null, null).getList()) {
            LOGGER.debug("Removing instance " + entry);
            client.getGSInstanceService().delete(entry.getId(), true);
        }
    }

    protected void removeRules(GeoFenceClient client) {
        RESTOutputRuleList rules = client.getRuleService().get(null, null, false, new RESTRuleFilter());
        for (RESTOutputRule entry : rules.getList()) {
            LOGGER.debug("Removing rule " + entry);
            client.getRuleService().delete(entry.getId());
        }
    }

    protected void removeAdminRules(GeoFenceClient client) {
        RESTOutputAdminRuleList rules = client.getAdminRuleService().get(null, null, false, new RESTAdminRuleFilter());

        for (RESTOutputAdminRule entry : rules.getList()) {
            LOGGER.debug("Removing adminrule " + entry);
            client.getRuleService().delete(entry.getId());
        }
    }

    @Test
    public void testUserGroups() {
        GeoFenceClient client = createClient();

        for (String name : Arrays.asList("group01", "group02")) {

            RESTInputGroup i = new RESTInputGroup();
            i.setEnabled(Boolean.TRUE);
            i.setName(name);
            client.getUserGroupService().insert(i);
        }

        assertEquals(2, client.getUserGroupService().count("%"), "Bad group number");
        assertEquals(2, client.getUserGroupService().count("group%"), "Bad group number");
        assertEquals(0, client.getUserGroupService().count("zzz%"), "Bad group number");
        assertEquals(1, client.getUserGroupService().count("%p01"), "Bad group number");
    }

    @Test
    public void testGroupsRule() {
        GeoFenceClient client = createClient();

        for (String name : Arrays.asList("group01", "group02")) {

            RESTInputGroup i = new RESTInputGroup();
            i.setEnabled(Boolean.TRUE);
            i.setName(name);
            client.getUserGroupService().insert(i);
        }

        {
            RESTInputRule rule = new RESTInputRule();
            rule.setRolename("group01");
            rule.setLayer("test01");
            rule.setGrant(RESTGrantType.ALLOW);
            rule.setPosition(new RESTRulePosition(RESTRulePosition.RESTPositionReference.offsetFromBottom, 0));
            client.getRuleService().insert(rule);
        }
        {
            RESTInputRule rule = new RESTInputRule();
            rule.setRolename("group01");
            rule.setLayer("test02");
            rule.setGrant(RESTGrantType.ALLOW);
            rule.setPosition(new RESTRulePosition(RESTRulePosition.RESTPositionReference.offsetFromBottom, 0));
            client.getRuleService().insert(rule);
        }
        {
            RESTInputRule rule = new RESTInputRule();
            rule.setRolename("group02");
            rule.setLayer("test03");
            rule.setGrant(RESTGrantType.ALLOW);
            rule.setPosition(new RESTRulePosition(RESTRulePosition.RESTPositionReference.offsetFromBottom, 0));
            client.getRuleService().insert(rule);
        }
        {
            RESTInputRule rule = new RESTInputRule();
            rule.setGrant(RESTGrantType.DENY);
            rule.setPosition(new RESTRulePosition(RESTRulePosition.RESTPositionReference.offsetFromBottom, 0));
            client.getRuleService().insert(rule);
        }

        RESTOutputRuleList all = client.getRuleService().get(null, null, false, new RESTRuleFilter());
        assertNotNull(all);
        assertNotNull(all.getList());
        assertEquals(4, all.getList().size());
        for (RESTOutputRule rule : all.getList()) {
            LOGGER.debug("found rule " + rule);
        }

        RESTRuleFilter rf1 = new RESTRuleFilter();
        rf1.groupName = "group01";

        rf1.groupDefault = false;
        assertEquals(
                2, client.getRuleService().get(null, null, true, rf1).getList().size());
        rf1.groupDefault = true;
        assertEquals(
                2, client.getRuleService().get(null, null, true, rf1).getList().size());
    }

    @Test
    public void testReassign() {
        GeoFenceClient client = createClient();

        for (String name : Arrays.asList("group01", "group02", "group3")) {

            RESTInputGroup i = new RESTInputGroup();
            i.setEnabled(Boolean.TRUE);
            i.setName(name);
            client.getUserGroupService().insert(i);
        }

        {
            RESTInputUser user = new RESTInputUser();
            user.setEnabled(true);
            user.setName("pippo");
            user.setGroups(Arrays.asList(new IdName("group01")));
            client.getUserService().insert(user);
        }

        {
            assertEquals(1, client.getUserService().count("%"));
            assertEquals(1, client.getUserService().get("pippo").getGroups().size());
        }

        // add a group
        client.getUserService().addIntoGroup("pippo", "group02");
        {
            List<IdName> groups = client.getUserService().get("pippo").getGroups();
            assertTrue(groups.stream().anyMatch(g -> g.getName().equals("group01")), "group01 not found");
            assertTrue(groups.stream().anyMatch(g -> g.getName().equals("group02")), "group02 not found");
            assertEquals(2, groups.size());
        }

        // readd some group, size should not change
        client.getUserService().addIntoGroup("pippo", "group02");
        assertEquals(2, client.getUserService().get("pippo").getGroups().size());

        // remove first assinged group
        client.getUserService().removeFromGroup("pippo", "group01");
        assertEquals(1, client.getUserService().get("pippo").getGroups().size());
    }

    @Test
    public void testBaseRule() {
        GeoFenceClient client = createClient();

        String roleName = "RN0";
        String instanceName = "I0";
        String ipaddress = "10.11.12.0/24";
        String service = "S0";
        String request = "RQ0";
        String workspace = "WS0";
        String layer = "L0";

        RESTInputInstance inputInstance = new RESTInputInstance();
        inputInstance.setName(instanceName);
        inputInstance.setDescription("test instance");
        inputInstance.setBaseURL("http://localhost");
        inputInstance.setPassword("password");
        inputInstance.setUsername("username");

        client.getGSInstanceService().insert(inputInstance);

        RESTInputRule inputRule = new RESTInputRule();
        inputRule.setRolename(roleName);
        inputRule.setInstance(new IdName(instanceName));
        inputRule.setIpaddress(ipaddress);
        inputRule.setService(service);
        inputRule.setRequest(request);
        inputRule.setWorkspace(workspace);
        inputRule.setLayer(layer);
        inputRule.setGrant(RESTGrantType.ALLOW);
        inputRule.setPosition(new RESTRulePosition(RESTRulePosition.RESTPositionReference.offsetFromTop, 0));

        Response response = client.getRuleService().insert(inputRule);
        assertNotNull(response.getEntityTag());
        String id = response.getEntityTag().getValue();
        assertNotNull(id);

        RESTOutputRule outRule = client.getRuleService().get(Long.valueOf(id));
        assertNotNull(outRule);

        assertEquals(roleName, outRule.getRolename());
        assertEquals(instanceName, outRule.getInstance().getName());
        assertEquals(ipaddress, outRule.getIpaddress());
        assertEquals(service, outRule.getService());
        assertEquals(request, outRule.getRequest());
        assertEquals(workspace, outRule.getWorkspace());
        assertEquals(layer, outRule.getLayer());
        assertEquals(RESTGrantType.ALLOW, outRule.getGrant());
    }

    protected boolean pingGeoFence(GeoFenceClient client) {
        try {
            client.getUserService().count("*");
            return true;
        } catch (Exception ex) {
            LOGGER.debug("Error connecting to GeoFence", ex);
            // ... and now for an awful example of heuristic.....
            Throwable t = ex;
            while (t != null) {
                if (t instanceof ConnectException) {
                    LOGGER.warn("Testing GeoFence is offline");
                    return false;
                }
                t = t.getCause();
            }
            throw new RuntimeException("Unexpected exception: " + ex.getMessage(), ex);
        }
    }
}

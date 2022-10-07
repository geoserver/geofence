/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest;

import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.ShortGroup;
import org.geoserver.geofence.services.dto.ShortInstance;
import org.geoserver.geofence.services.rest.model.RESTInputGroup;
import org.geoserver.geofence.services.rest.model.RESTInputInstance;
import org.geoserver.geofence.services.rest.model.RESTInputRule;
import org.geoserver.geofence.services.rest.model.RESTInputUser;
import org.geoserver.geofence.services.rest.model.RESTOutputRule;
import org.geoserver.geofence.services.rest.model.RESTOutputRuleList;
import org.geoserver.geofence.services.rest.model.RESTRulePosition;
import org.geoserver.geofence.services.rest.model.RESTShortUser;
import org.geoserver.geofence.services.rest.model.util.IdName;

import java.net.ConnectException;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.geoserver.geofence.services.rest.model.RESTOutputAdminRule;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assume.*;
import static org.junit.Assert.*;
import org.junit.rules.TestName;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GeoFenceClientTest {
    private final static Logger LOGGER = LogManager.getLogger(GeoFenceClientTest.class);

    @org.junit.Rule
    public TestName name = new TestName();
    
    public GeoFenceClientTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void before() throws Exception {
        LOGGER.info("### Running " + getClass().getSimpleName() + "::" + name.getMethodName() );

        GeoFenceClient client = createClient();
        assumeTrue(pingGeoFence(client));

        removeAll();
    }

    protected GeoFenceClient createClient() {
        GeoFenceClient client = new GeoFenceClient();
        client.setGeostoreRestUrl("http://localhost:9191/geofence/rest");
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
        for (RESTShortUser su : client.getUserService().getList(null, null, null).getUserList()) {
            LOGGER.debug("Removing user " + su);
            client.getUserService().delete(su.getUserName(), true);
        }
    }

    protected void removeGroups(GeoFenceClient client) {
        for (ShortGroup sg : client.getUserGroupService().getList(null, null, null).getList()) {
            LOGGER.debug("Removing group " + sg);
            client.getUserGroupService().delete(sg.getName(), true);
        }
    }

    protected void removeInstances(GeoFenceClient client) {
        for (ShortInstance entry : client.getGSInstanceService().getList(null, null, null).getList()) {
            LOGGER.debug("Removing instance " + entry);
            client.getGSInstanceService().delete(entry.getId(), true);
        }
    }

    protected void removeRules(GeoFenceClient client) {
        RuleServiceHelper rsh = new RuleServiceHelper(client.getRuleService());
        for (RESTOutputRule entry : rsh.getAll().getList()) {
            LOGGER.debug("Removing rule " + entry);
            client.getRuleService().delete(entry.getId());
        }
    }

   protected void removeAdminRules(GeoFenceClient client) {
        AdminRuleServiceHelper rsh = new AdminRuleServiceHelper(client.getAdminRuleService());
        for (RESTOutputAdminRule entry : rsh.getAll().getList()) {
            LOGGER.debug("Removing adminrule " + entry);
            client.getRuleService().delete(entry.getId());
        }
    }

    @Test
    public void testUserGroups() {
        GeoFenceClient client = createClient();

        for(String name: Arrays.asList("group01", "group02")) {

            RESTInputGroup i = new RESTInputGroup();
            i.setEnabled(Boolean.TRUE);
            i.setName(name);
            client.getUserGroupService().insert(i);
        }

        assertEquals("Bad group number", 2, client.getUserGroupService().count("%"));
        assertEquals("Bad group number", 2, client.getUserGroupService().count("group%"));
        assertEquals("Bad group number", 0, client.getUserGroupService().count("zzz%"));
        assertEquals("Bad group number", 1, client.getUserGroupService().count("%p01"));
    }

    @Test
    public void testGroupsRule() {
        GeoFenceClient client = createClient();

        for(String name: Arrays.asList("group01", "group02")) {

            RESTInputGroup i = new RESTInputGroup();
            i.setEnabled(Boolean.TRUE);
            i.setName(name);
            client.getUserGroupService().insert(i);
        }

        {
            RESTInputRule rule = new RESTInputRule();
            rule.setRolename("group01");
            rule.setLayer("test01");
            rule.setGrant(GrantType.ALLOW);
            rule.setPosition(new RESTRulePosition(RESTRulePosition.RulePosition.offsetFromBottom, 0));
            client.getRuleService().insert(rule);
        }
        {
            RESTInputRule rule = new RESTInputRule();
            rule.setRolename("group01");
            rule.setLayer("test02");
            rule.setGrant(GrantType.ALLOW);
            rule.setPosition(new RESTRulePosition(RESTRulePosition.RulePosition.offsetFromBottom, 0));
            client.getRuleService().insert(rule);
        }
        {
            RESTInputRule rule = new RESTInputRule();
            rule.setRolename("group02");
            rule.setLayer("test03");
            rule.setGrant(GrantType.ALLOW);
            rule.setPosition(new RESTRulePosition(RESTRulePosition.RulePosition.offsetFromBottom, 0));
            client.getRuleService().insert(rule);
        }
        {
            RESTInputRule rule = new RESTInputRule();
            rule.setGrant(GrantType.DENY);
            rule.setPosition(new RESTRulePosition(RESTRulePosition.RulePosition.offsetFromBottom, 0));
            client.getRuleService().insert(rule);
        }

        RuleServiceHelper rsh = new RuleServiceHelper(client.getRuleService());
        RESTOutputRuleList all = rsh.getAll();
        assertNotNull(all);
        assertNotNull(all.getList());
        assertEquals(4, all.getList().size());
        for (RESTOutputRule rule : all.getList()) {
            LOGGER.debug("found rule " + rule);
        }

        assertEquals(2, rsh.get(null, null, true, null, new RuleFilter.TextFilter("group01", false, false), null, null, null, null, null, null).getList().size());
        assertEquals(3, rsh.get(null, null, true, null, new RuleFilter.TextFilter("group01", false, true), null, null, null, null, null, null).getList().size());
    }

    @Test
    public void testReassign() {
        GeoFenceClient client = createClient();
        
        for(String name: Arrays.asList("group01", "group02", "group3")) {

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
            assertTrue("group01 not found", groups.stream().anyMatch(g -> g.getName().equals("group01")));
            assertTrue("group02 not found", groups.stream().anyMatch(g -> g.getName().equals("group02")));
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
        inputRule.setGrant(GrantType.ALLOW);
        inputRule.setPosition(new RESTRulePosition(RESTRulePosition.RulePosition.offsetFromTop, 0));

        Response response = client.getRuleService().insert(inputRule);
        assertNotNull(response.getEntityTag());
        String id = response.getEntityTag().getValue();
        assertNotNull(id);

        RESTOutputRule outRule = client.getRuleService().get(Long.parseLong(id));
        assertNotNull(outRule);

        assertEquals(roleName, outRule.getRolename());
        assertEquals(instanceName, outRule.getInstance().getName());
        assertEquals(ipaddress, outRule.getIpaddress());
        assertEquals(service, outRule.getService());
        assertEquals(request, outRule.getRequest());
        assertEquals(workspace, outRule.getWorkspace());
        assertEquals(layer, outRule.getLayer());
        assertEquals(GrantType.ALLOW, outRule.getGrant());
    }

    protected boolean pingGeoFence(GeoFenceClient client) {
        try {
            client.getUserService().count("*");
            return true;
        } catch (Exception ex) {
            LOGGER.debug("Error connecting to GeoFence", ex);
            //... and now for an awful example of heuristic.....
            Throwable t = ex;
            while(t!=null) {
                if(t instanceof ConnectException) {
                    LOGGER.warn("Testing GeoFence is offline");
                    return false;
                }
                t = t.getCause();
            }
            throw new RuntimeException("Unexpected exception: " + ex.getMessage(), ex);
        }
    }

}

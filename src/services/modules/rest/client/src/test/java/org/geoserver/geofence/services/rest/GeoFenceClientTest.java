/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest;

import org.geoserver.geofence.services.rest.RuleServiceHelper;
import org.geoserver.geofence.services.rest.GeoFenceClient;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.ShortGroup;
import org.geoserver.geofence.services.dto.ShortInstance;
import org.geoserver.geofence.services.rest.model.RESTInputGroup;
import org.geoserver.geofence.services.rest.model.RESTInputRule;
import org.geoserver.geofence.services.rest.model.RESTInputUser;
import org.geoserver.geofence.services.rest.model.RESTOutputRule;
import org.geoserver.geofence.services.rest.model.RESTOutputRuleList;
import org.geoserver.geofence.services.rest.model.RESTShortUser;
import org.geoserver.geofence.services.rest.model.util.IdName;
import java.net.ConnectException;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assume.*;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GeoFenceClientTest {
    private final static Logger LOGGER = LogManager.getLogger(GeoFenceClientTest.class);

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
            client.getUserService().delete(su.getId(), true);
        }
    }

    protected void removeGroups(GeoFenceClient client) {
        for (ShortGroup sg : client.getUserGroupService().getList(null, null, null).getList()) {
            LOGGER.debug("Removing group " + sg);
            client.getUserGroupService().delete(sg.getId(), true);
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
            rule.setGroupName("group01");
            rule.setLayer("test01");
            rule.setGrant(GrantType.ALLOW);
            rule.setPosition(new RESTInputRule.RESTRulePosition(RESTInputRule.RESTRulePosition.RulePosition.offsetFromBottom, 0));
            client.getRuleService().insert(rule);
        }
        {
            RESTInputRule rule = new RESTInputRule();
            rule.setGroupName("group01");
            rule.setLayer("test02");
            rule.setGrant(GrantType.ALLOW);
            rule.setPosition(new RESTInputRule.RESTRulePosition(RESTInputRule.RESTRulePosition.RulePosition.offsetFromBottom, 0));
            client.getRuleService().insert(rule);
        }
        {
            RESTInputRule rule = new RESTInputRule();
            rule.setGroupName("group02");
            rule.setLayer("test03");
            rule.setGrant(GrantType.ALLOW);
            rule.setPosition(new RESTInputRule.RESTRulePosition(RESTInputRule.RESTRulePosition.RulePosition.offsetFromBottom, 0));
            client.getRuleService().insert(rule);
        }
        {
            RESTInputRule rule = new RESTInputRule();
            rule.setGrant(GrantType.DENY);
            rule.setPosition(new RESTInputRule.RESTRulePosition(RESTInputRule.RESTRulePosition.RulePosition.offsetFromBottom, 0));
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

        assertEquals(2, rsh.get(null, null, true, null, new RuleFilter.IdNameFilter("group01", false), null, null, null, null, null).getList().size());
        assertEquals(3, rsh.get(null, null, true, null, new RuleFilter.IdNameFilter("group01", true), null, null, null, null, null).getList().size());
    }

    @Test
    @Ignore
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
        assertEquals(2, client.getUserService().get("pippo").getGroups().size());

        // readd some group, size should not change
        client.getUserService().addIntoGroup("pippo", "group02");
        assertEquals(2, client.getUserService().get("pippo").getGroups().size());

        // remove first assinged group
        client.getUserService().removeFromGroup("pippo", "group01");
        assertEquals(1, client.getUserService().get("pippo").getGroups().size());
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

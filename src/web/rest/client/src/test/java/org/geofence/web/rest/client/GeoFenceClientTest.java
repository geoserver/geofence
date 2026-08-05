/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.ConnectException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.web.rest.api.interfaces.params.RESTAdminRuleFilter;
import org.geofence.web.rest.api.interfaces.params.RESTRuleFilter;
import org.geofence.web.rest.api.model.RESTAccessInfo;
import org.geofence.web.rest.api.model.RESTBatch;
import org.geofence.web.rest.api.model.RESTInputAdminRule;
import org.geofence.web.rest.api.model.RESTInputGroup;
import org.geofence.web.rest.api.model.RESTInputInstance;
import org.geofence.web.rest.api.model.RESTInputRule;
import org.geofence.web.rest.api.model.RESTInputUser;
import org.geofence.web.rest.api.model.RESTOutputAdminRule;
import org.geofence.web.rest.api.model.RESTOutputRule;
import org.geofence.web.rest.api.model.RESTOutputRuleList;
import org.geofence.web.rest.api.model.RESTRulePosition;
import org.geofence.web.rest.api.model.RESTShortRuleList;
import org.geofence.web.rest.api.model.enums.RESTAdminGrantType;
import org.geofence.web.rest.api.model.enums.RESTGrantType;
import org.geofence.web.rest.api.model.util.IdName;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Live-server integration test - skipped (via {@link Assumptions}) if no GeoFence server is reachable at
 * {@code createClient()}'s URL, same as before this class's Jersey-to-Spring-{@code HttpServiceProxyFactory} rewrite.
 *
 * <p>The first three tests only exercise {@link org.geofence.web.rest.api.interfaces.RESTRuleReaderService} - the only
 * service {@link GeoFenceClient} exposes, matching its only real caller (GeoServer's {@code RestRuleReaderService}).
 * The admin-CRUD tests below ({@code testUserGroups}, {@code testGroupsRule}, {@code testReassign},
 * {@code testBaseRule}) exercise {@link GeoFenceAdminClient}'s hand-rolled {@code RestClient} adapters.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GeoFenceClientTest {
    private static final Logger LOGGER = LogManager.getLogger(GeoFenceClientTest.class);

    public GeoFenceClientTest() {}

    private static final String REST_URL = "http://localhost:9191/geofence/rest";

    @BeforeEach
    public void before(TestInfo testInfo) {
        String methodName = testInfo.getTestMethod().get().getName();
        LOGGER.info("### Running " + getClass().getSimpleName() + "::" + methodName);

        Assumptions.assumeTrue(pingGeoFence(createClient()));

        // start each test from a clean instance, so the "no matching rule" assertions below hold regardless of
        // whatever the webapp seeded at startup (GeoFenceClient only exposes the rule reader, so cleanup goes through
        // GeoFenceAdminClient's one bodyless call)
        createAdminClient().removeAll();
    }

    protected GeoFenceClient createClient() {
        GeoFenceClient client = new GeoFenceClient();
        client.setRestUrl(REST_URL);
        client.setUsername("admin");
        client.setPassword("admin");

        return client;
    }

    protected GeoFenceAdminClient createAdminClient() {
        GeoFenceAdminClient client = new GeoFenceAdminClient();
        client.setRestUrl(REST_URL);
        client.setUsername("admin");
        client.setPassword("admin");

        return client;
    }

    protected boolean pingGeoFence(GeoFenceClient client) {
        try {
            client.getRuleReaderService().getAccessInfo(new RESTRuleFilter());
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

    @Test
    public void testGetAccessInfoDenyWhenNoMatchingRule() {
        GeoFenceClient client = createClient();

        RESTRuleFilter filter = new RESTRuleFilter();
        filter.userName = "no-such-user-" + UUID.randomUUID();

        RESTAccessInfo accessInfo = client.getRuleReaderService().getAccessInfo(filter);
        assertNotNull(accessInfo);
        assertEquals(RESTGrantType.DENY, accessInfo.getGrant());
    }

    @Test
    public void testGetAdminAuthorizationNoAdminRightsWhenNoMatchingRule() {
        GeoFenceClient client = createClient();

        RESTRuleFilter filter = new RESTRuleFilter();
        filter.userName = "no-such-user-" + UUID.randomUUID();

        // admin-authorization defaults to ALLOW (access isn't admin-restricted) but with no admin rights, when no
        // admin rule grants them - distinct from getAccessInfo, which defaults to DENY on no match
        RESTAccessInfo accessInfo = client.getRuleReaderService().getAdminAuthorization(filter);
        assertNotNull(accessInfo);
        assertFalse(accessInfo.isAdminRights());
    }

    @Test
    public void testGetMatchingRulesEmptyWhenNoMatchingRule() {
        GeoFenceClient client = createClient();

        RESTRuleFilter filter = new RESTRuleFilter();
        filter.userName = "no-such-user-" + UUID.randomUUID();

        RESTShortRuleList rules = client.getRuleReaderService().getMatchingRules(filter);
        assertNotNull(rules);
        assertEquals(0, rules.getRuleList().size());
    }

    // ==========================================================================
    // Admin-CRUD round-trips - re-enable once GeoFenceAdminClient's write services are implemented (they currently
    // throw UnsupportedOperationException, so these would fail; kept compiling against the stubbed getters).

    @Test
    public void testUserGroups() {
        GeoFenceAdminClient client = createAdminClient();

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
        GeoFenceAdminClient client = createAdminClient();

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
        // groupDefault=true also includes the catch-all DENY rule (rolename unset - a "default" rule for any group)
        rf1.groupDefault = true;
        assertEquals(
                3, client.getRuleService().get(null, null, true, rf1).getList().size());
    }

    @Test
    public void testReassign() {
        GeoFenceAdminClient client = createAdminClient();

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
        GeoFenceAdminClient client = createAdminClient();

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

        Long id = client.getRuleService().insert(inputRule).getBody();
        assertNotNull(id);

        RESTOutputRule outRule = client.getRuleService().get(id);
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

    /**
     * Covers the {@code GeoFenceAdminClient} methods the tests above don't exercise: update/delete/getList on
     * instances, groups, users and rules, {@code move}, the full {@code RESTAdminRuleService} CRUD, and
     * {@code RESTConfigService}/{@code RESTBatchService}'s remaining bodyless-response endpoints.
     */
    @Test
    public void testRemainingAdminOperations() {
        GeoFenceAdminClient client = createAdminClient();

        // --- instances: insert/getList/get/update/delete ---
        RESTInputInstance instance = new RESTInputInstance();
        instance.setName("instanceA");
        instance.setBaseURL("http://localhost/a");
        instance.setUsername("username");
        instance.setPassword("password");
        Long instanceId = client.getGSInstanceService().insert(instance).getBody();
        assertNotNull(instanceId);

        assertEquals(
                1,
                client.getGSInstanceService()
                        .getList(null, null, null)
                        .getList()
                        .size());
        assertEquals("instanceA", client.getGSInstanceService().get(instanceId).getName());
        assertEquals("instanceA", client.getGSInstanceService().get("instanceA").getName());

        // name is intentionally omitted - RESTInstanceServiceImpl.update(Long, ...) rejects a non-null name in the
        // payload (id-based update can't rename), unlike every other partial-update endpoint in this API
        RESTInputInstance updatedInstance = new RESTInputInstance();
        updatedInstance.setBaseURL("http://localhost/updated");
        updatedInstance.setUsername("username");
        updatedInstance.setPassword("password");
        client.getGSInstanceService().update(instanceId, updatedInstance);
        assertEquals(
                "http://localhost/updated",
                client.getGSInstanceService().get(instanceId).getBaseURL());

        client.getGSInstanceService().delete(instanceId, false);
        assertEquals(0, client.getGSInstanceService().count("%"));

        // --- groups: get/update/delete ---
        RESTInputGroup group = new RESTInputGroup();
        group.setName("groupA");
        group.setEnabled(true);
        client.getUserGroupService().insert(group);
        assertTrue(client.getUserGroupService().get("groupA").isEnabled());

        // name is intentionally omitted - update() rejects a non-null name in the payload, same as GSInstance's
        RESTInputGroup groupUpdate = new RESTInputGroup();
        groupUpdate.setEnabled(false);
        client.getUserGroupService().update("groupA", groupUpdate);
        assertFalse(client.getUserGroupService().get("groupA").isEnabled());

        client.getUserGroupService().delete("groupA", false);
        assertEquals(0, client.getUserGroupService().count("%"));

        // --- users: getList/update/delete ---
        RESTInputUser user = new RESTInputUser();
        user.setName("userA");
        user.setEnabled(true);
        client.getUserService().insert(user);
        assertEquals(
                1,
                client.getUserService().getList(null, null, null).getUserList().size());

        // name is intentionally omitted - update() rejects a non-null name in the payload, same as GSInstance's
        RESTInputUser userUpdate = new RESTInputUser();
        userUpdate.setEnabled(false);
        client.getUserService().update("userA", userUpdate);
        assertFalse(client.getUserService().get("userA").isEnabled());

        client.getUserService().delete("userA", false);
        assertEquals(0, client.getUserService().count("%"));

        // --- rules: get/update/delete/move ---
        RESTInputRule rule1 = new RESTInputRule();
        rule1.setLayer("layerA");
        rule1.setGrant(RESTGrantType.ALLOW);
        rule1.setPosition(new RESTRulePosition(RESTRulePosition.RESTPositionReference.offsetFromTop, 0));
        Long rule1Id = client.getRuleService().insert(rule1).getBody();

        RESTInputRule rule2 = new RESTInputRule();
        rule2.setLayer("layerB");
        rule2.setGrant(RESTGrantType.ALLOW);
        rule2.setPosition(new RESTRulePosition(RESTRulePosition.RESTPositionReference.offsetFromBottom, 0));
        Long rule2Id = client.getRuleService().insert(rule2).getBody();

        assertEquals(
                2,
                client.getRuleService()
                        .get(null, null, false, new RESTRuleFilter())
                        .getList()
                        .size());

        // grant/position are intentionally omitted - update() rejects a non-null value for either (both have their
        // own dedicated move/grant-change semantics elsewhere), unlike every other field which is a plain partial
        // update
        RESTInputRule ruleUpdate = new RESTInputRule();
        ruleUpdate.setLayer("layerA-renamed");
        client.getRuleService().update(rule1Id, ruleUpdate);
        assertEquals("layerA-renamed", client.getRuleService().get(rule1Id).getLayer());

        // move rule2 to priority 1 - rule1 (currently at priority 1) shifts down
        client.getRuleService().move(String.valueOf(rule2Id), 1);
        assertEquals(1, client.getRuleService().get(rule2Id).getPriority().intValue());

        client.getRuleService().delete(rule2Id);
        assertEquals(
                1,
                client.getRuleService()
                        .get(null, null, false, new RESTRuleFilter())
                        .getList()
                        .size());

        // clean up before the backup section below - RESTBatchOperation.payload is a still-unresolved JSON
        // polymorphism gap (tracked separately), which a non-empty rules/groups/users/instances backup would hit
        client.getRuleService().delete(rule1Id);

        // --- admin rules: full CRUD ---
        RESTInputAdminRule adminRule = new RESTInputAdminRule();
        adminRule.setRolename("adminRoleA");
        adminRule.setGrant(RESTAdminGrantType.ADMIN);
        adminRule.setPosition(new RESTRulePosition(RESTRulePosition.RESTPositionReference.offsetFromTop, 0));
        Long adminRuleId = client.getAdminRuleService().insert(adminRule).getBody();
        assertNotNull(adminRuleId);

        RESTOutputAdminRule outAdminRule = client.getAdminRuleService().get(adminRuleId);
        assertEquals("adminRoleA", outAdminRule.getRolename());
        assertEquals(RESTAdminGrantType.ADMIN, outAdminRule.getGrant());

        assertEquals(1, client.getAdminRuleService().count(new RESTAdminRuleFilter()));
        assertEquals(
                1,
                client.getAdminRuleService()
                        .get(null, null, false, new RESTAdminRuleFilter())
                        .getList()
                        .size());

        // position is intentionally omitted - update() rejects a non-null position (moving is a separate concern)
        RESTInputAdminRule adminRuleUpdate = new RESTInputAdminRule();
        adminRuleUpdate.setRolename("adminRoleA-renamed");
        adminRuleUpdate.setGrant(RESTAdminGrantType.USER);
        client.getAdminRuleService().update(adminRuleId, adminRuleUpdate);
        assertEquals(
                "adminRoleA-renamed",
                client.getAdminRuleService().get(adminRuleId).getRolename());
        assertEquals(
                RESTAdminGrantType.USER,
                client.getAdminRuleService().get(adminRuleId).getGrant());

        client.getAdminRuleService().delete(adminRuleId);
        assertEquals(0, client.getAdminRuleService().count(new RESTAdminRuleFilter()));

        // --- config: backup endpoints (empty instance, so no polymorphic RESTBatchOperation payloads to trip up
        // JSON (de)serialization - see the RESTBatchService polymorphism gap tracked separately) ---
        assertNotNull(client.getConfigService().backup(false));
        assertNotNull(client.getConfigService().backupGroups());
        assertNotNull(client.getConfigService().backupUsers());
        assertNotNull(client.getConfigService().backupInstances());
        assertNotNull(client.getConfigService().backupRules());

        // --- batch: exec with an empty batch ---
        assertNotNull(client.getBatchService().exec(new RESTBatch()));
    }
}

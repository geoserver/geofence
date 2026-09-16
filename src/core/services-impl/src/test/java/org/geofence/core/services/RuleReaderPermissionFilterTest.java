/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import org.geofence.core.model.GSInstance;
import org.geofence.core.model.Rule;
import org.geofence.core.model.UserGroup;
import org.geofence.core.model.enums.GrantType;
import org.geofence.core.services.dto.PermsResult;
import org.geofence.core.services.dto.RuleFilter;
import org.geofence.core.services.dto.RuleFilter.FilterType;
import org.geofence.core.services.dto.RuleFilter.SpecialFilterType;
import org.geofence.core.services.util.PermsResultBuilder;
import org.geofence.core.services.util.PermsResultInternal;
import org.geotools.api.filter.Filter;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link RuleReaderService#getPermissionFilter(RuleFilter)}, covering the rule combinations that affect the
 * resulting CQL filter and accessible resources.
 *
 * <p>The valid filter configurations for {@code getPermissionFilter} are:
 *
 * <ul>
 *   <li>user: DEFAULT (anonymous) or NAMEVALUE + includeDefault
 *   <li>role: ANY (resolved from user service) or NAMEVALUE (explicit CSV list)
 *   <li>service, request, subfield, workspace, layer: must all remain ANY (not set)
 * </ul>
 */
public class RuleReaderPermissionFilterTest extends ServiceTestBase {

    // -------------------------------------------------------------------------
    // Helper methods for building valid RuleFilter instances
    // -------------------------------------------------------------------------

    /** Anonymous user, any role (rules resolved from user service). */
    private RuleFilter buildAnonAnyRoleFilter() {
        RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);
        filter.getUser().setType(SpecialFilterType.DEFAULT);
        // role stays ANY
        return filter;
    }

    /** Anonymous user, explicit role(s) given as a comma-separated string. */
    private RuleFilter buildAnonRoleFilter(String roles) {
        RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);
        filter.getUser().setType(SpecialFilterType.DEFAULT);
        filter.getRole().setText(roles);
        return filter;
    }

    /**
     * Named user, role resolved from the user's groups (role=ANY). The user must exist in the DB with its UserGroup
     * associations.
     */
    private RuleFilter buildUserAnyRoleFilter(String username) {
        RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);
        filter.getUser().setText(username);
        filter.getUser().setIncludeDefault(true);
        // role stays ANY -> roles will be resolved from GSUser.usergroups
        return filter;
    }

    // -------------------------------------------------------------------------
    // Tests: basic rule combinations
    // -------------------------------------------------------------------------

    @Test
    public void testNoRules_returnsExclude() {
        PermsResult result = ruleReaderService.getPermissionFilter(buildAnonAnyRoleFilter());

        assertEquals("EXCLUDE", result.getCqlFilter());
        assertTrue(result.getAccessibleResources().isEmpty());
    }

    @Test
    public void testGlobalAllow_returnsInclude() {
        // ALLOW with no constraints -> grants access to everything
        ruleAdminService.insert(new Rule(10, null, null, null, null, null, null, null, null, null, GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildAnonAnyRoleFilter());

        assertEquals("INCLUDE", result.getCqlFilter());
        assertTrue(result.getAccessibleResources().contains("*:*"));
    }

    @Test
    public void testWorkspaceWideAllow_returnsWorkspaceFilter() {
        // ALLOW on whole workspace (no layer constraint)
        ruleAdminService.insert(new Rule(10, null, null, null, null, null, null, null, "ws1", null, GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildAnonAnyRoleFilter());

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue(resources.contains("ws1:*"));
        assertFalse(resources.contains("*:*")); // not a global grant

        String cql = result.getCqlFilter();
        assertTrue(cql.contains("workspace = 'ws1'"), "Expected workspace filter in CQL: " + cql);
    }

    @Test
    public void testWorkspaceLayerAllow_returnsWorkspaceAndLayerFilter() {
        // ALLOW on a specific workspace+layer
        ruleAdminService.insert(new Rule(10, null, null, null, null, null, null, null, "ws1", "l1", GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildAnonAnyRoleFilter());

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue(resources.contains("ws1:l1"));
        assertFalse(resources.contains("ws1:*"));

        String cql = result.getCqlFilter();
        assertTrue(cql.contains("workspace = 'ws1'"), "Expected workspace filter: " + cql);
        assertTrue(cql.contains("layer = 'l1'"), "Expected layer filter: " + cql);
    }

    @Test
    public void testMultipleLayersSameWorkspace_returnsBothLayers() {
        // Two specific layer ALLOWs in the same workspace
        ruleAdminService.insert(new Rule(10, null, null, null, null, null, null, null, "ws1", "l1", GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, null, null, null, null, null, null, null, "ws1", "l2", GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildAnonAnyRoleFilter());

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue(resources.contains("ws1:l1"));
        assertTrue(resources.contains("ws1:l2"));
        assertFalse(resources.contains("ws1:*"), "Should not be a full workspace grant");
    }

    @Test
    public void testMultipleWorkspaces_returnsAllWorkspaces() {
        // ALLOWs for different workspaces
        ruleAdminService.insert(new Rule(10, null, null, null, null, null, null, null, "ws1", null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, null, null, null, null, null, null, null, "ws2", "l2", GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildAnonAnyRoleFilter());

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue(resources.contains("ws1:*"));
        assertTrue(resources.contains("ws2:l2"));
    }

    // -------------------------------------------------------------------------
    // Tests: DENY rules interaction
    // -------------------------------------------------------------------------

    @Test
    public void testHighPriorityDenyBlocksSubsequentAllow() {
        // Full-block DENY (no service, no request) at higher priority (lower number) blocks ALLOW
        ruleAdminService.insert(new Rule(10, null, null, null, null, null, null, null, null, null, GrantType.DENY));
        ruleAdminService.insert(new Rule(20, null, null, null, null, null, null, null, "ws1", "l1", GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildAnonAnyRoleFilter());

        assertEquals("EXCLUDE", result.getCqlFilter());
        assertTrue(result.getAccessibleResources().isEmpty());
    }

    @Test
    public void testLowPriorityDenyDoesNotBlockEarlierAllow() {
        // ALLOW at higher priority (lower number) is NOT blocked by a later DENY
        ruleAdminService.insert(new Rule(10, null, null, null, null, null, null, null, "ws1", "l1", GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, null, null, null, null, null, null, null, null, null, GrantType.DENY));

        PermsResult result = ruleReaderService.getPermissionFilter(buildAnonAnyRoleFilter());

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue(resources.contains("ws1:l1"));
    }

    @Test
    public void testWorkspaceDenyPunchesHolesInWorkspaceAllow() {
        // DENY (priority 10) for a specific layer in ws1, ALLOW (priority 20) for whole ws1
        // -> workspace access granted except for the denied layer
        ruleAdminService.insert(
                new Rule(10, null, null, null, null, null, null, null, "ws1", "l_denied", GrantType.DENY));
        ruleAdminService.insert(new Rule(20, null, null, null, null, null, null, null, "ws1", null, GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildAnonAnyRoleFilter());

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue(resources.contains("ws1:*"), "Workspace wildcard expected");
        assertTrue(resources.contains("ws1:!l_denied"), "Denied layer exclusion expected");

        String cql = result.getCqlFilter();
        assertTrue(cql.contains("workspace = 'ws1'"), "CQL should allow ws1: " + cql);
        assertTrue(cql.contains("layer = 'l_denied'"), "CQL should exclude l_denied: " + cql);
        assertTrue(cql.contains("NOT"), "CQL should negate denied layer: " + cql);
    }

    @Test
    public void testDeniedLayerGrantedByOtherRoleHealsExclusion() {
        // Role p1 has a DENY for l_denied and a workspace-wide ALLOW for ws1 ->
        //   the p1 group result has a hole: {ws1:*, ws1:!l_denied}
        // Role p2 has an explicit ALLOW for l_denied in ws1 ->
        //   the p2 group grants {ws1:l_denied}
        // After merging both roles, the !l_denied exclusion should be healed because
        // the raw "l_denied" grant from p2 cancels the "!l_denied" exclusion from p1.
        ruleAdminService.insert(
                new Rule(10, null, "p1", null, null, null, null, null, "ws1", "l_denied", GrantType.DENY));
        ruleAdminService.insert(new Rule(20, null, "p1", null, null, null, null, null, "ws1", null, GrantType.ALLOW));
        ruleAdminService.insert(
                new Rule(30, null, "p2", null, null, null, null, null, "ws1", "l_denied", GrantType.ALLOW));

        RuleFilter filter = buildAnonRoleFilter("p1,p2");
        PermsResult result = ruleReaderService.getPermissionFilter(filter);

        SortedSet<String> resources = result.getAccessibleResources();
        // p2's explicit grant heals p1's exclusion marker
        assertFalse(
                resources.contains("ws1:!l_denied"),
                "Exclusion should be healed when layer is granted by another role");
        assertTrue(resources.contains("ws1:*"), "Workspace wildcard should remain");
    }

    @Test
    public void testPartialDenyWithServiceConstraintDoesNotBlockAllow() {
        // DENY restricted to a specific service (partial block) -> does NOT block catalog-level ALLOWs
        ruleAdminService.insert(new Rule(10, null, null, null, null, "WMS", null, null, null, null, GrantType.DENY));
        ruleAdminService.insert(new Rule(20, null, null, null, null, null, null, null, "ws1", "l1", GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildAnonAnyRoleFilter());

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue(resources.contains("ws1:l1"), "Partial DENY should not block the ALLOW");
    }

    @Test
    public void testPartialDenyWithRequestConstraintDoesNotBlockAllow() {
        // DENY restricted to a specific request (partial block) -> does NOT block catalog-level ALLOWs
        ruleAdminService.insert(new Rule(10, null, null, null, null, null, "GetMap", null, null, null, GrantType.DENY));
        ruleAdminService.insert(new Rule(20, null, null, null, null, null, null, null, "ws1", "l1", GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildAnonAnyRoleFilter());

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue(resources.contains("ws1:l1"), "Partial DENY should not block the ALLOW");
    }

    // -------------------------------------------------------------------------
    // Tests: multiple roles
    // -------------------------------------------------------------------------

    @Test
    public void testMultipleRoles_permissionsAreMergedWithOr() {
        // Each role grants access to a different workspace
        ruleAdminService.insert(new Rule(10, null, "p1", null, null, null, null, null, "ws1", null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, null, "p2", null, null, null, null, null, "ws2", null, GrantType.ALLOW));

        RuleFilter filter = buildAnonRoleFilter("p1,p2");
        PermsResult result = ruleReaderService.getPermissionFilter(filter);

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue(resources.contains("ws1:*"), "Role p1 should grant ws1");
        assertTrue(resources.contains("ws2:*"), "Role p2 should grant ws2");
    }

    @Test
    public void testDenyInOneRoleDoesNotAffectOtherRole() {
        // Role p1 has a DENY that blocks its own ALLOW; role p2 independently allows ws2
        ruleAdminService.insert(new Rule(10, null, "p1", null, null, null, null, null, null, null, GrantType.DENY));
        ruleAdminService.insert(new Rule(20, null, "p1", null, null, null, null, null, "ws1", null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(30, null, "p2", null, null, null, null, null, "ws2", null, GrantType.ALLOW));

        RuleFilter filter = buildAnonRoleFilter("p1,p2");
        PermsResult result = ruleReaderService.getPermissionFilter(filter);

        SortedSet<String> resources = result.getAccessibleResources();
        // p1's ALLOW is blocked by p1's DENY (higher priority), but p2's ALLOW survives
        assertFalse(resources.contains("ws1:*"), "p1 ws1 should be blocked");
        assertTrue(resources.contains("ws2:*"), "p2 ws2 should be accessible");
    }

    @Test
    public void testDefaultRulesIncludedWithRoleFilter() {
        // A rule with no role (default), plus a role-specific rule: both should be returned
        ruleAdminService.insert(
                new Rule(10, null, "p1", null, null, null, null, null, "ws_role", null, GrantType.ALLOW));
        ruleAdminService.insert(
                new Rule(20, null, null, null, null, null, null, null, "ws_default", null, GrantType.ALLOW));

        // role p1 filter includes default rules (includeDefault=true on the role filter)
        RuleFilter filter = buildAnonRoleFilter("p1");
        PermsResult result = ruleReaderService.getPermissionFilter(filter);

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue(resources.contains("ws_role:*"), "Role-specific rule should be included");
        assertTrue(resources.contains("ws_default:*"), "Default (no-role) rule should also be included");
    }

    @Test
    public void testRoleOneGlobalAllowOverridesRoleSpecificResult() {
        // Role p1 has a global ALLOW -> result should be INCLUDE regardless of other roles
        ruleAdminService.insert(new Rule(10, null, "p1", null, null, null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, null, "p2", null, null, null, null, null, "ws2", null, GrantType.ALLOW));

        RuleFilter filter = buildAnonRoleFilter("p1,p2");
        PermsResult result = ruleReaderService.getPermissionFilter(filter);

        assertEquals("INCLUDE", result.getCqlFilter(), "Global ALLOW in any role should produce INCLUDE");
        assertTrue(result.getAccessibleResources().contains("*:*"));
    }

    // -------------------------------------------------------------------------
    // Tests: user specified, roles resolved from GSUser.usergroups
    // -------------------------------------------------------------------------

    @Test
    public void testUserWithSingleGroup_rolesResolvedFromUserGroups() {
        // User u1 belongs to role p1; a workspace ALLOW exists for p1.
        // Filter specifies only the user (role=ANY) -> the system resolves p1 from the DB.
        UserGroup role1 = createRole("p1");
        createUser("u1", role1);

        ruleAdminService.insert(new Rule(10, null, "p1", null, null, null, null, null, "ws1", null, GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildUserAnyRoleFilter("u1"));

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue(resources.contains("ws1:*"), "User's group p1 should grant ws1");
    }

    @Test
    public void testUserWithMultipleGroups_allGroupsContributeToPermissions() {
        // User u1 belongs to roles p1 and p2; each role grants a different workspace.
        // Filter specifies only the user -> both roles are resolved and OR-merged.
        UserGroup role1 = createRole("p1");
        UserGroup role2 = createRole("p2");
        createUser("u1", role1, role2);

        ruleAdminService.insert(new Rule(10, null, "p1", null, null, null, null, null, "ws1", null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, null, "p2", null, null, null, null, null, "ws2", null, GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildUserAnyRoleFilter("u1"));

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue(resources.contains("ws1:*"), "Role p1 should contribute ws1");
        assertTrue(resources.contains("ws2:*"), "Role p2 should contribute ws2");
    }

    @Test
    public void testUserWithNoGroups_onlyDefaultRulesApply() {
        // User u_nogroup has no group memberships.
        // When role=ANY, the resolver returns an empty set ->
        // the system falls back to DEFAULT (null-role) rules only.
        createUser("u_nogroup");

        ruleAdminService.insert(
                new Rule(10, null, "p1", null, null, null, null, null, "ws_role", null, GrantType.ALLOW));
        ruleAdminService.insert(
                new Rule(20, null, null, null, null, null, null, null, "ws_default", null, GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildUserAnyRoleFilter("u_nogroup"));

        SortedSet<String> resources = result.getAccessibleResources();
        assertFalse(resources.contains("ws_role:*"), "Role-specific rule should not apply to a group-less user");
        assertTrue(resources.contains("ws_default:*"), "Default (null-role) rule should apply");
    }

    @Test
    public void testUserNotInDb_onlyDefaultRulesApply() {
        // A username not present in the DB also has no resolved roles ->
        // only default (null-role) rules should apply.
        ruleAdminService.insert(
                new Rule(10, null, "p1", null, null, null, null, null, "ws_role", null, GrantType.ALLOW));
        ruleAdminService.insert(
                new Rule(20, null, null, null, null, null, null, null, "ws_default", null, GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildUserAnyRoleFilter("unknown_user"));

        SortedSet<String> resources = result.getAccessibleResources();
        assertFalse(resources.contains("ws_role:*"), "Role-specific rule should not apply for an unknown user");
        assertTrue(resources.contains("ws_default:*"), "Default (null-role) rule should apply");
    }

    @Test
    public void testUserGroupDenyDoesNotAffectOtherUsersGroups() {
        // u1 belongs to p1 only; u2 belongs to p2 only.
        // A DENY for p2 should not affect u1's result.
        UserGroup role1 = createRole("p1");
        UserGroup role2 = createRole("p2");
        createUser("u1", role1);
        createUser("u2", role2);

        ruleAdminService.insert(new Rule(10, null, "p1", null, null, null, null, null, "ws1", null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, null, "p2", null, null, null, null, null, null, null, GrantType.DENY));

        PermsResult resultU1 = ruleReaderService.getPermissionFilter(buildUserAnyRoleFilter("u1"));
        PermsResult resultU2 = ruleReaderService.getPermissionFilter(buildUserAnyRoleFilter("u2"));

        assertTrue(resultU1.getAccessibleResources().contains("ws1:*"), "u1 (p1) should have ws1");
        assertEquals("EXCLUDE", resultU2.getCqlFilter(), "u2 (p2) should be fully denied");
    }

    @Test
    public void testUserDefaultRulesIncludedViaIncludeDefault() {
        // u1 belongs to p1; rules exist for both p1 and null-role (default).
        // The filter has user=u1, role=ANY with includeDefault=true,
        // so both the role-specific and the default rule should be included.
        UserGroup role1 = createRole("p1");
        createUser("u1", role1);

        ruleAdminService.insert(
                new Rule(10, null, "p1", null, null, null, null, null, "ws_role", null, GrantType.ALLOW));
        ruleAdminService.insert(
                new Rule(20, null, null, null, null, null, null, null, "ws_default", null, GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildUserAnyRoleFilter("u1"));

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue(resources.contains("ws_role:*"), "Role-specific ws should be accessible");
        assertTrue(resources.contains("ws_default:*"), "Default ws should also be accessible (includeDefault=true)");
    }

    @Test
    public void testTwoUsersInDifferentGroupsGetDifferentPermissions() {
        // u1 belongs to p1, u2 belongs to p2; each role grants a different workspace.
        // Querying each user separately should return only that user's accessible workspaces.
        UserGroup role1 = createRole("p1");
        UserGroup role2 = createRole("p2");
        createUser("u1", role1);
        createUser("u2", role2);

        ruleAdminService.insert(new Rule(10, null, "p1", null, null, null, null, null, "ws1", null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, null, "p2", null, null, null, null, null, "ws2", null, GrantType.ALLOW));

        PermsResult resultU1 = ruleReaderService.getPermissionFilter(buildUserAnyRoleFilter("u1"));
        PermsResult resultU2 = ruleReaderService.getPermissionFilter(buildUserAnyRoleFilter("u2"));

        assertTrue(resultU1.getAccessibleResources().contains("ws1:*"), "u1 should see ws1");
        assertFalse(resultU1.getAccessibleResources().contains("ws2:*"), "u1 should not see ws2");

        assertFalse(resultU2.getAccessibleResources().contains("ws1:*"), "u2 should not see ws1");
        assertTrue(resultU2.getAccessibleResources().contains("ws2:*"), "u2 should see ws2");
    }

    // -------------------------------------------------------------------------
    // Tests: filter validation
    // -------------------------------------------------------------------------

    @Test
    public void testValidation_userTypeAny_throwsException() {
        // user type=ANY is not acceptable for getPermissionFilter
        RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);
        assertEquals(FilterType.ANY, filter.getUser().getType(), "Precondition: user type should be ANY");

        try {
            ruleReaderService.getPermissionFilter(filter);
            fail("Expected IllegalArgumentException for user type=ANY");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testValidation_roleTypeDefault_throwsException() {
        // role type=DEFAULT is not acceptable for getPermissionFilter
        RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);
        filter.getUser().setType(SpecialFilterType.DEFAULT); // valid user
        filter.getRole().setType(SpecialFilterType.DEFAULT); // invalid role

        try {
            ruleReaderService.getPermissionFilter(filter);
            fail("Expected IllegalArgumentException for role type=DEFAULT");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testValidation_workspaceSet_throwsException() {
        RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);
        filter.getUser().setType(SpecialFilterType.DEFAULT);
        filter.getWorkspace().setText("ws1"); // must remain ANY

        try {
            ruleReaderService.getPermissionFilter(filter);
            fail("Expected IllegalArgumentException when workspace is set");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testValidation_layerSet_throwsException() {
        RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);
        filter.getUser().setType(SpecialFilterType.DEFAULT);
        filter.getLayer().setText("l1"); // must remain ANY

        try {
            ruleReaderService.getPermissionFilter(filter);
            fail("Expected IllegalArgumentException when layer is set");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testValidation_serviceSet_throwsException() {
        RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);
        filter.getUser().setType(SpecialFilterType.DEFAULT);
        filter.getService().setText("WMS"); // must remain ANY

        try {
            ruleReaderService.getPermissionFilter(filter);
            fail("Expected IllegalArgumentException when service is set");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testValidation_requestSet_throwsException() {
        RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);
        filter.getUser().setType(SpecialFilterType.DEFAULT);
        filter.getRequest().setText("GetMap"); // must remain ANY

        try {
            ruleReaderService.getPermissionFilter(filter);
            fail("Expected IllegalArgumentException when request is set");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testValidation_subfieldSet_throwsException() {
        RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);
        filter.getUser().setType(SpecialFilterType.DEFAULT);
        filter.getSubfield().setText("attr1"); // must remain ANY

        try {
            ruleReaderService.getPermissionFilter(filter);
            fail("Expected IllegalArgumentException when subfield is set");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    private GSInstance createInstance(String name) {
        GSInstance instance = new GSInstance();
        instance.setName(name);
        instance.setBaseURL("http://" + name + "/geoserver");
        instance.setUsername("admin");
        instance.setPassword("geoserver");
        instance.setDescription("test instance " + name);
        instanceAdminService.insert(instance);
        return instance;
    }

    @Test
    public void testInstanceFilterIsRespected_crossInstanceLeak() {
        GSInstance instanceA = createInstance("instA");
        GSInstance instanceB = createInstance("instB");

        // ALLOW for instance A on ws_a:*
        ruleAdminService.insert(
                new Rule(10, null, null, instanceA, null, null, null, null, "ws_a", null, GrantType.ALLOW));
        // ALLOW for instance B on ws_b:* (should NOT be visible from instance A's perspective)
        ruleAdminService.insert(
                new Rule(20, null, null, instanceB, null, null, null, null, "ws_b", null, GrantType.ALLOW));

        RuleFilter filter = buildAnonAnyRoleFilter();
        filter.setInstance("instA");

        PermsResult result = ruleReaderService.getPermissionFilter(filter);

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue(resources.contains("ws_a:*"), "Instance A's grant should appear");
        assertFalse(
                resources.contains("ws_b:*"),
                "Instance B's grant should NOT leak into instance A's permission filter - "
                        + "if this assertion fails, the cross-instance leak bug is confirmed");
    }

    @Test
    public void testGlobalAllowRespectsHigherPriorityWorkspaceDeny() {
        // Higher priority: full-block DENY for a specific (workspace, layer)
        ruleAdminService.insert(
                new Rule(10, null, null, null, null, null, null, null, "secret", "top_secret", GrantType.DENY));
        // Lower priority: global ALLOW
        ruleAdminService.insert(new Rule(20, null, null, null, null, null, null, null, null, null, GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildAnonAnyRoleFilter());

        SortedSet<String> resources = result.getAccessibleResources();
        String cql = result.getCqlFilter();

        assertFalse(
                "INCLUDE".equals(cql) && resources.contains("*:*") && resources.size() == 1,
                "Global ALLOW should not produce a bare INCLUDE when a higher-priority "
                        + "workspace-specific DENY exists - if this assertion fails, the "
                        + "global-ALLOW-drops-DENY-holes bug is confirmed: result is '" + cql + "'");

        assertTrue(
                cql.contains("secret")
                        && cql.contains("top_secret")
                        && cql.toUpperCase().contains("NOT"),
                "CQL should NOT (workspace='secret' AND layer='top_secret') somewhere - got: " + cql);
    }

    @Test
    public void testLowerPriorityGlobalDenyDoesNotOverrideHigherPriorityAllows() {
        PermsResultBuilder builder = new PermsResultBuilder();

        // Rule 10: Priority 10 - Broad WMS ALLOW
        //        Rule rule10 = new Rule(10, null, null, null, null, "WMS", null, null, null, null, GrantType.ALLOW);
        Rule rule10 = new Rule(10, GrantType.ALLOW).setService("WMS");
        //        rule10.setId(1206L);

        // Rule 1209: Priority 60 - Specific S1:R1 ALLOW on w1:l1
        //        Rule rule60 = new Rule(60, null, null, null, null, "S1", "R1", null, "w1", "l1", GrantType.ALLOW);
        Rule rule60 = new Rule(60, GrantType.ALLOW)
                .setService("S1")
                .setRequest("R1")
                .setWorkspace("w1")
                .setLayer("l1");
        //        rule60.setId(1209L);

        // Rule 1207: Priority 100 - Catch-all DENY (lower priority)
        //        Rule rule90 = new Rule(90, null, null, null, null, null, null, null, null, null, GrantType.DENY);
        Rule rule90 = new Rule(90, GrantType.DENY);
        //        rule90.setId(1207L);

        List<Rule> sortedRules = Arrays.asList(rule10, rule60, rule90);

        // Execute computation
        PermsResultInternal result = builder.computePerms(sortedRules);

        Filter filter = result.getFilter();
        var resources = result.getAccessibleResources();

        // 1. Verify the CQL Filter is NOT EXCLUDE or NOT (INCLUDE)
        assertNotEquals(Filter.EXCLUDE, filter, "Filter should NOT be EXCLUDE");
        assertEquals(Filter.INCLUDE, filter, "Filter should simplify to INCLUDE");

        // 2. Verify the human-friendly resource map contains global access
        assertTrue(resources.containsKey("*"), "Resource map should contain global workspace '*'");
        Set<String> globalLayers = resources.get("*");
        assertNotNull(globalLayers, "Global layers should not be null");
        assertTrue(globalLayers.contains("*"), "Global layers should contain wildcard '*'");
        assertFalse(globalLayers.contains("!null"), "Global layers should NOT contain exclusion '!null'");
    }
}

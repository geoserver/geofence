/* (c) 2025 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import java.util.SortedSet;
import org.geoserver.geofence.core.model.GSInstance;
import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.services.dto.PermsResult;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.RuleFilter.FilterType;
import org.geoserver.geofence.services.dto.RuleFilter.SpecialFilterType;
import org.junit.Test;

/**
 * Tests for the {@link RuleReaderService#getPermissionFilter(RuleFilter)} method,
 * covering the various rule combinations that affect the resulting CQL filter
 * and accessible resources.
 *
 * <p>The valid filter configurations for {@code getPermissionFilter} are:
 * <ul>
 *   <li>user: DEFAULT (anonymous) or NAMEVALUE + includeDefault</li>
 *   <li>role: ANY (resolved from user service) or NAMEVALUE (explicit CSV list)</li>
 *   <li>service, request, subfield, workspace, layer: must all remain ANY (not set)</li>
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
        // ALLOW with no constraints → grants access to everything
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
        assertTrue("Expected workspace filter in CQL: " + cql, cql.contains("workspace = 'ws1'"));
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
        assertTrue("Expected workspace filter: " + cql, cql.contains("workspace = 'ws1'"));
        assertTrue("Expected layer filter: " + cql, cql.contains("layer = 'l1'"));
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
        assertFalse("Should not be a full workspace grant", resources.contains("ws1:*"));
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
        // → workspace access granted except for the denied layer
        ruleAdminService.insert(new Rule(10, null, null, null, null, null, null, null, "ws1", "l_denied", GrantType.DENY));
        ruleAdminService.insert(new Rule(20, null, null, null, null, null, null, null, "ws1", null, GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildAnonAnyRoleFilter());

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue("Workspace wildcard expected", resources.contains("ws1:*"));
        assertTrue("Denied layer exclusion expected", resources.contains("ws1:!l_denied"));

        String cql = result.getCqlFilter();
        assertTrue("CQL should allow ws1: " + cql, cql.contains("workspace = 'ws1'"));
        assertTrue("CQL should exclude l_denied: " + cql, cql.contains("layer = 'l_denied'"));
        assertTrue("CQL should negate denied layer: " + cql, cql.contains("NOT"));
    }

    @Test
    public void testDeniedLayerGrantedByOtherRoleHealsExclusion() {
        // Role p1 has a DENY for l_denied and a workspace-wide ALLOW for ws1 →
        //   the p1 group result has a hole: {ws1:*, ws1:!l_denied}
        // Role p2 has an explicit ALLOW for l_denied in ws1 →
        //   the p2 group grants {ws1:l_denied}
        // After merging both roles, the !l_denied exclusion should be healed because
        // the raw "l_denied" grant from p2 cancels the "!l_denied" exclusion from p1.
        ruleAdminService.insert(new Rule(10, null, "p1", null, null, null, null, null, "ws1", "l_denied", GrantType.DENY));
        ruleAdminService.insert(new Rule(20, null, "p1", null, null, null, null, null, "ws1", null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(30, null, "p2", null, null, null, null, null, "ws1", "l_denied", GrantType.ALLOW));

        RuleFilter filter = buildAnonRoleFilter("p1,p2");
        PermsResult result = ruleReaderService.getPermissionFilter(filter);

        SortedSet<String> resources = result.getAccessibleResources();
        // p2's explicit grant heals p1's exclusion marker
        assertFalse("Exclusion should be healed when layer is granted by another role",
                    resources.contains("ws1:!l_denied"));
        assertTrue("Workspace wildcard should remain", resources.contains("ws1:*"));
    }

    @Test
    public void testPartialDenyWithServiceConstraintDoesNotBlockAllow() {
        // DENY restricted to a specific service (partial block) → does NOT block catalog-level ALLOWs
        ruleAdminService.insert(new Rule(10, null, null, null, null, "WMS", null, null, null, null, GrantType.DENY));
        ruleAdminService.insert(new Rule(20, null, null, null, null, null, null, null, "ws1", "l1", GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildAnonAnyRoleFilter());

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue("Partial DENY should not block the ALLOW", resources.contains("ws1:l1"));
    }

    @Test
    public void testPartialDenyWithRequestConstraintDoesNotBlockAllow() {
        // DENY restricted to a specific request (partial block) → does NOT block catalog-level ALLOWs
        ruleAdminService.insert(new Rule(10, null, null, null, null, null, "GetMap", null, null, null, GrantType.DENY));
        ruleAdminService.insert(new Rule(20, null, null, null, null, null, null, null, "ws1", "l1", GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildAnonAnyRoleFilter());

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue("Partial DENY should not block the ALLOW", resources.contains("ws1:l1"));
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
        assertTrue("Role p1 should grant ws1", resources.contains("ws1:*"));
        assertTrue("Role p2 should grant ws2", resources.contains("ws2:*"));
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
        assertFalse("p1 ws1 should be blocked", resources.contains("ws1:*"));
        assertTrue("p2 ws2 should be accessible", resources.contains("ws2:*"));
    }

    @Test
    public void testDefaultRulesIncludedWithRoleFilter() {
        // A rule with no role (default), plus a role-specific rule: both should be returned
        ruleAdminService.insert(new Rule(10, null, "p1", null, null, null, null, null, "ws_role", null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, null, null, null, null, null, null, null, "ws_default", null, GrantType.ALLOW));

        // role p1 filter includes default rules (includeDefault=true on the role filter)
        RuleFilter filter = buildAnonRoleFilter("p1");
        PermsResult result = ruleReaderService.getPermissionFilter(filter);

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue("Role-specific rule should be included", resources.contains("ws_role:*"));
        assertTrue("Default (no-role) rule should also be included", resources.contains("ws_default:*"));
    }

    @Test
    public void testRoleOneGlobalAllowOverridesRoleSpecificResult() {
        // Role p1 has a global ALLOW → result should be INCLUDE regardless of other roles
        ruleAdminService.insert(new Rule(10, null, "p1", null, null, null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, null, "p2", null, null, null, null, null, "ws2", null, GrantType.ALLOW));

        RuleFilter filter = buildAnonRoleFilter("p1,p2");
        PermsResult result = ruleReaderService.getPermissionFilter(filter);

        assertEquals("Global ALLOW in any role should produce INCLUDE", "INCLUDE", result.getCqlFilter());
        assertTrue(result.getAccessibleResources().contains("*:*"));
    }

    /**
     * Named user, role resolved from the user's groups (role=ANY).
     * The user must exist in the DB with its UserGroup associations.
     */
    private RuleFilter buildUserAnyRoleFilter(String username) {
        RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);
        filter.getUser().setText(username);
        filter.getUser().setIncludeDefault(true);
        // role stays ANY → roles will be resolved from GSUser.usergroups
        return filter;
    }

    // -------------------------------------------------------------------------
    // Tests: user specified, roles resolved from GSUser.usergroups
    // -------------------------------------------------------------------------

    @Test
    public void testUserWithSingleGroup_rolesResolvedFromUserGroups() {
        // User u1 belongs to role p1; a workspace ALLOW exists for p1.
        // Filter specifies only the user (role=ANY) → the system resolves p1 from the DB.
        UserGroup role1 = createRole("p1");
        createUser("u1", role1);

        ruleAdminService.insert(new Rule(10, null, "p1", null, null, null, null, null, "ws1", null, GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildUserAnyRoleFilter("u1"));

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue("User's group p1 should grant ws1", resources.contains("ws1:*"));
    }

    @Test
    public void testUserWithMultipleGroups_allGroupsContributeToPermissions() {
        // User u1 belongs to roles p1 and p2; each role grants a different workspace.
        // Filter specifies only the user → both roles are resolved and OR-merged.
        UserGroup role1 = createRole("p1");
        UserGroup role2 = createRole("p2");
        createUser("u1", role1, role2);

        ruleAdminService.insert(new Rule(10, null, "p1", null, null, null, null, null, "ws1", null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, null, "p2", null, null, null, null, null, "ws2", null, GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildUserAnyRoleFilter("u1"));

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue("Role p1 should contribute ws1", resources.contains("ws1:*"));
        assertTrue("Role p2 should contribute ws2", resources.contains("ws2:*"));
    }

    @Test
    public void testUserWithNoGroups_onlyDefaultRulesApply() {
        // User u_nogroup has no group memberships.
        // When role=ANY, the resolver returns an empty set →
        // the system falls back to DEFAULT (null-role) rules only.
        createUser("u_nogroup");

        ruleAdminService.insert(new Rule(10, null, "p1", null, null, null, null, null, "ws_role", null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, null, null, null, null, null, null, null, "ws_default", null, GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildUserAnyRoleFilter("u_nogroup"));

        SortedSet<String> resources = result.getAccessibleResources();
        assertFalse("Role-specific rule should not apply to a group-less user", resources.contains("ws_role:*"));
        assertTrue("Default (null-role) rule should apply", resources.contains("ws_default:*"));
    }

    @Test
    public void testUserNotInDb_onlyDefaultRulesApply() {
        // A username not present in the DB also has no resolved roles →
        // only default (null-role) rules should apply.
        ruleAdminService.insert(new Rule(10, null, "p1", null, null, null, null, null, "ws_role", null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, null, null, null, null, null, null, null, "ws_default", null, GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildUserAnyRoleFilter("unknown_user"));

        SortedSet<String> resources = result.getAccessibleResources();
        assertFalse("Role-specific rule should not apply for an unknown user", resources.contains("ws_role:*"));
        assertTrue("Default (null-role) rule should apply", resources.contains("ws_default:*"));
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

        assertTrue("u1 (p1) should have ws1", resultU1.getAccessibleResources().contains("ws1:*"));
        assertEquals("u2 (p2) should be fully denied", "EXCLUDE", resultU2.getCqlFilter());
    }

    @Test
    public void testUserDefaultRulesIncludedViaIncludeDefault() {
        // u1 belongs to p1; rules exist for both p1 and null-role (default).
        // The filter has user=u1, role=ANY with includeDefault=true,
        // so both the role-specific and the default rule should be included.
        UserGroup role1 = createRole("p1");
        createUser("u1", role1);

        ruleAdminService.insert(new Rule(10, null, "p1", null, null, null, null, null, "ws_role", null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, null, null, null, null, null, null, null, "ws_default", null, GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildUserAnyRoleFilter("u1"));

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue("Role-specific ws should be accessible", resources.contains("ws_role:*"));
        assertTrue("Default ws should also be accessible (includeDefault=true)", resources.contains("ws_default:*"));
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

        assertTrue("u1 should see ws1", resultU1.getAccessibleResources().contains("ws1:*"));
        assertFalse("u1 should not see ws2", resultU1.getAccessibleResources().contains("ws2:*"));

        assertFalse("u2 should not see ws1", resultU2.getAccessibleResources().contains("ws1:*"));
        assertTrue("u2 should see ws2", resultU2.getAccessibleResources().contains("ws2:*"));
    }

    // -------------------------------------------------------------------------
    // Tests: filter validation
    // -------------------------------------------------------------------------

    @Test
    public void testValidation_userTypeAny_throwsException() {
        // user type=ANY is not acceptable for getPermissionFilter
        RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);
        assertEquals("Precondition: user type should be ANY", FilterType.ANY, filter.getUser().getType());

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
        ruleAdminService.insert(new Rule(10, null, null, instanceA, null, null, null, null, "ws_a", null, GrantType.ALLOW));
        // ALLOW for instance B on ws_b:* (should NOT be visible from instance A's perspective)
        ruleAdminService.insert(new Rule(20, null, null, instanceB, null, null, null, null, "ws_b", null, GrantType.ALLOW));

        RuleFilter filter = buildAnonAnyRoleFilter();
        filter.setInstance("instA");

        PermsResult result = ruleReaderService.getPermissionFilter(filter);

        SortedSet<String> resources = result.getAccessibleResources();
        assertTrue("Instance A's grant should appear", resources.contains("ws_a:*"));
        assertFalse("Instance B's grant should NOT leak into instance A's permission filter — "
                + "if this assertion fails, the cross-instance leak bug is confirmed",
                resources.contains("ws_b:*"));
    }

    @Test
    public void testGlobalAllowRespectsHigherPriorityWorkspaceDeny() {
        // Higher priority: full-block DENY for a specific (workspace, layer)
        ruleAdminService.insert(new Rule(10, null, null, null, null, null, null, null, "secret", "top_secret", GrantType.DENY));
        // Lower priority: global ALLOW
        ruleAdminService.insert(new Rule(20, null, null, null, null, null, null, null, null, null, GrantType.ALLOW));

        PermsResult result = ruleReaderService.getPermissionFilter(buildAnonAnyRoleFilter());

        SortedSet<String> resources = result.getAccessibleResources();
        String cql = result.getCqlFilter();

        assertFalse("Global ALLOW should not produce a bare INCLUDE when a higher-priority "
                + "workspace-specific DENY exists — if this assertion fails, the "
                + "global-ALLOW-drops-DENY-holes bug is confirmed: result is '" + cql + "'",
                "INCLUDE".equals(cql) && resources.contains("*:*") && resources.size() == 1);

        assertTrue("CQL should NOT (workspace='secret' AND layer='top_secret') somewhere — got: " + cql,
                cql.contains("secret") && cql.contains("top_secret") && cql.toUpperCase().contains("NOT"));
    }
    
}

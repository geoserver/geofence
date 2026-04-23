/* (c) 2025 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import java.util.SortedSet;
import org.geoserver.geofence.core.model.Rule;
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
        ruleAdminService.insert(new Rule(10, null, "p1",  null, null, null, null, null, "ws_role",    null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, null, null,  null, null, null, null, null, "ws_default", null, GrantType.ALLOW));

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
}

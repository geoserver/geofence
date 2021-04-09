/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import java.util.List;
import org.geoserver.geofence.core.model.AdminRule;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.core.model.enums.AdminGrantType;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.RuleFilter.SpecialFilterType;
import org.geoserver.geofence.services.dto.ShortAdminRule;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;
import org.junit.Test;

/** @author ETj (etj at geo-solutions.it) */
public class AdminRuleAdminServiceImplTest extends ServiceTestBase {

    @Test
    public void testInsertDeleteAdminRule() throws NotFoundServiceEx {

        UserGroup profile = createRole(getName());
        AdminRule rule = new AdminRule();
        rule.setRolename(getName());
        rule.setAccess(AdminGrantType.ADMIN);
        adminruleAdminService.insert(rule);
        adminruleAdminService.get(rule.getId()); // will throw if not found
        assertTrue("Could not delete rule", adminruleAdminService.delete(rule.getId()));
    }

    @Test
    public void testUpdateAdminRule() throws Exception {
        UserGroup p1 = createRole("p1");
        UserGroup p2 = createRole("p2");

        AdminRule rule = new AdminRule(10, null, "p1", null, null, "w1", AdminGrantType.ADMIN);
        adminruleAdminService.insert(rule);

        {
            AdminRule loaded = adminruleAdminService.get(rule.getId());
            assertNotNull(loaded);

            assertEquals("p1", loaded.getRolename());
            assertEquals("w1", loaded.getWorkspace());

            loaded.setRolename("p2");
            loaded.setWorkspace("w2");

            adminruleAdminService.update(loaded);
        }
        {
            AdminRule loaded = adminruleAdminService.get(rule.getId());
            assertNotNull(loaded);

            assertEquals("p2", loaded.getRolename());
            assertEquals("w2", loaded.getWorkspace());
        }
    }

    @Test
    public void testGetAllAdminRules() {
        assertEquals(0, adminruleAdminService.getAll().size());

        UserGroup p1 = createRole("p1");

        AdminRule r1 = new AdminRule(10, null, "p1", null, null, "w1", AdminGrantType.USER);
        AdminRule r2 = new AdminRule(20, null, "p1", null, null, "w2", AdminGrantType.USER);
        AdminRule r3 = new AdminRule(30, null, "p1", null, null, "w3", AdminGrantType.ADMIN);

        adminruleAdminService.insert(r1);
        adminruleAdminService.insert(r2);
        adminruleAdminService.insert(r3);

        assertEquals(3, adminruleAdminService.getAll().size());
    }

    @Test
    public void testGetAdminRules() {
        assertEquals(0, adminruleAdminService.count(new RuleFilter(SpecialFilterType.ANY)));

        UserGroup p1 = createRole("p1");
        UserGroup p2 = createRole("p2");

        AdminRule r1 = new AdminRule(10, null, "p1", null, null, "w1", AdminGrantType.USER);
        AdminRule r2 = new AdminRule(20, null, "p2", null, null, "w2", AdminGrantType.USER);
        AdminRule r3 = new AdminRule(30, null, "p1", null, null, "w3", AdminGrantType.USER);
        AdminRule r4 = new AdminRule(40, null, "p1", null, null, null, AdminGrantType.USER);

        adminruleAdminService.insert(r1);
        adminruleAdminService.insert(r2);
        adminruleAdminService.insert(r3);
        adminruleAdminService.insert(r4);

        assertNotNull(p1.getId());
        assertNotNull(p2.getId());

        assertEquals(4, adminruleAdminService.count(new RuleFilter(SpecialFilterType.ANY)));
        assertEquals(
                3,
                adminruleAdminService.count(new RuleFilter(SpecialFilterType.ANY).setRole("p1")));
        assertEquals(
                1,
                adminruleAdminService.count(new RuleFilter(SpecialFilterType.ANY).setRole("p2")));
        assertEquals(
                4,
                adminruleAdminService.count(
                        new RuleFilter(SpecialFilterType.ANY).setService("s1")));
        assertEquals(
                4,
                adminruleAdminService.count(
                        new RuleFilter(SpecialFilterType.ANY).setService("ZZ")));

        RuleFilter f1 = new RuleFilter(SpecialFilterType.ANY).setWorkspace("w1");
        f1.getWorkspace().setIncludeDefault(true);
        assertEquals(2, adminruleAdminService.count(f1));
        f1.getWorkspace().setIncludeDefault(false);
        assertEquals(1, adminruleAdminService.count(f1));
    }

    @Test
    public void testShift() {
        assertEquals(0, adminruleAdminService.getCountAll());

        AdminRule r1 = new AdminRule(10, null, null, null, null, "w1", AdminGrantType.ADMIN);
        AdminRule r2 = new AdminRule(20, null, null, null, null, "w2", AdminGrantType.ADMIN);
        AdminRule r3 = new AdminRule(30, null, null, null, null, "w3", AdminGrantType.ADMIN);
        AdminRule r4 = new AdminRule(40, null, null, null, null, "w4", AdminGrantType.ADMIN);

        adminruleAdminService.insert(r1);
        adminruleAdminService.insert(r2);
        adminruleAdminService.insert(r3);
        adminruleAdminService.insert(r4);

        int n = adminruleAdminService.shift(20, 5);
        assertEquals(3, n);

        RuleFilter f = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        f.setWorkspace("w3");
        List<ShortAdminRule> loaded = adminruleAdminService.getList(f, null, null);
        assertEquals(1, loaded.size());
        assertEquals(35, loaded.get(0).getPriority());
    }

    @Test
    public void testSwap() {
        assertEquals(0, adminruleAdminService.getCountAll());

        AdminRule r1 = new AdminRule(10, null, null, null, null, "w1", AdminGrantType.ADMIN);
        AdminRule r2 = new AdminRule(20, null, null, null, null, "w2", AdminGrantType.ADMIN);
        AdminRule r3 = new AdminRule(30, null, null, null, null, "w3", AdminGrantType.ADMIN);

        adminruleAdminService.insert(r1);
        adminruleAdminService.insert(r2);
        adminruleAdminService.insert(r3);

        adminruleAdminService.swap(r1.getId(), r2.getId());

        RuleFilter f = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        List<ShortAdminRule> loaded = adminruleAdminService.getList(f, null, null);
        assertEquals(3, loaded.size());
        assertEquals(r2.getId(), loaded.get(0).getId());
        assertEquals(r1.getId(), loaded.get(1).getId());
        assertEquals(r3.getId(), loaded.get(2).getId());

        assertEquals(10, loaded.get(0).getPriority());
        assertEquals(20, loaded.get(1).getPriority());
        assertEquals(30, loaded.get(2).getPriority());
    }

    @Test
    public void testGetByPriority() {
        assertEquals(0, adminruleAdminService.getAll().size());

        AdminRule r1 = new AdminRule(10, null, null, null, null, "w1", AdminGrantType.ADMIN);
        AdminRule r2 = new AdminRule(20, null, null, null, null, "w2", AdminGrantType.ADMIN);
        AdminRule r3 = new AdminRule(30, null, null, null, null, "w3", AdminGrantType.ADMIN);
        AdminRule r4 = new AdminRule(40, null, null, null, null, "w4", AdminGrantType.ADMIN);
        AdminRule r5 = new AdminRule(50, null, null, null, null, "w5", AdminGrantType.ADMIN);

        adminruleAdminService.insert(r1);
        adminruleAdminService.insert(r2);
        adminruleAdminService.insert(r3);
        adminruleAdminService.insert(r4);
        adminruleAdminService.insert(r5);

        assertEquals(5, adminruleAdminService.getAll().size());

        // single existing rule
        ShortAdminRule res1 = adminruleAdminService.getRuleByPriority(20);
        assertNotNull(res1);
        assertEquals("w2", res1.getWorkspace());

        // single non-existing rule
        ShortAdminRule resNull = adminruleAdminService.getRuleByPriority(21);
        assertNull(resNull);

        // all rules, first is not the requested priority
        assertEquals(5, adminruleAdminService.getRulesByPriority(0, null, null).size());
        // all rules, first is the requested priority
        assertEquals(5, adminruleAdminService.getRulesByPriority(10, null, null).size());
        // first rule should be not included
        assertEquals(4, adminruleAdminService.getRulesByPriority(20, null, null).size());
        // paged request amid the list
        assertEquals(2, adminruleAdminService.getRulesByPriority(20, 0, 2).size());
        // paged request at the end of the list
        assertEquals(1, adminruleAdminService.getRulesByPriority(50, 0, 2).size());
        // empty page
        assertEquals(0, adminruleAdminService.getRulesByPriority(50, 100, 2).size());
    }
}

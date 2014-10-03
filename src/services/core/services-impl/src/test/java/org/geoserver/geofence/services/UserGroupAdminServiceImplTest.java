/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.core.model.Rule;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.services.dto.ShortGroup;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class UserGroupAdminServiceImplTest extends ServiceTestBase {

    public UserGroupAdminServiceImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testInsertDelete() throws NotFoundServiceEx {

        UserGroup p = createUserGroup(getName());
        userGroupAdminService.get(p.getId()); // will throw if not found
        assertTrue("Could not delete group", userGroupAdminService.delete(p.getId()));
    }

    @Test
    public void testDeleteCascadeGroup() throws NotFoundServiceEx {

        UserGroup group = createUserGroup(getName());
        Rule rule = new Rule();
        rule.setUserGroup(group);
        rule.setAccess(GrantType.ALLOW);
        ruleAdminService.insert(rule);

        {
            Rule loaded = ruleAdminService.get(rule.getId()); // will throw if not found
            assertNotNull(loaded);
        }

        try {
            userGroupAdminService.delete(group.getId());
            fail("Group reference not trapped");
        } catch (Exception e) {
            LOGGER.info("Exception properly trapped ("+e.getClass().getSimpleName()+")");
        }

        ruleAdminService.delete(rule.getId());
        userGroupAdminService.delete(group.getId());
    }


    @Test
    public void testUpdate() throws Exception {
        removeAllUserGroups();

        UserGroup group = createUserGroup("u1");

        boolean oldEnabled;

        {
            UserGroup loaded = userGroupAdminService.get(group.getId());
            assertNotNull(loaded);

            assertEquals("u1", loaded.getName());
            oldEnabled = loaded.getEnabled();

            loaded.setEnabled(!oldEnabled);

            ShortGroup sg = new ShortGroup(loaded);
            userGroupAdminService.update(sg);
        }
        {
            UserGroup loaded = userGroupAdminService.get(group.getId());
            assertNotNull(loaded);

            assertEquals((boolean)!oldEnabled, (boolean)loaded.getEnabled());
        }
    }

    @Test
    public void testGetAll() {
        assertEquals(0, userGroupAdminService.getList(null,null,null).size());

        createUserGroup("u1");
        createUserGroup("u2");
        createUserGroup("u3");

        assertEquals(3, userGroupAdminService.getList(null,null,null).size());
    }

    @Test
    public void testGetCount() {
        assertEquals(0, userGroupAdminService.getCount(null));

        createUserGroup("u10");
        createUserGroup("u20");
        createUserGroup("u30");
        UserGroup u99 = createUserGroup("u99");

        assertEquals(4, userGroupAdminService.getCount(null));
        assertEquals(4, userGroupAdminService.getCount("u%"));
        assertEquals(3, userGroupAdminService.getCount("%0"));

        List<ShortGroup> glist = userGroupAdminService.getList("%9",null,null);
        assertEquals(1, glist.size());
        assertEquals("u99", glist.get(0).getName());
        assertEquals((Long)u99.getId(), (Long)glist.get(0).getId());
    }

//    @Test
//    public void testGetProps() {
//
//        assertEquals(0, userGroupAdminService.getCount(null));
//
//        UserGroup p = createUserGroup(getName());
//        Map<String,String> props = new HashMap<String, String>();
//        props.put("k1", "v1");
//        props.put("k2", "v2");
//        userGroupAdminService.setCustomProps(p.getId(), props);
//
//        assertEquals(1, userGroupAdminService.getCount(null));
//        assertEquals(1, userGroupAdminService.getList(null, null, null).size());
//        assertEquals(1, userGroupAdminService.getFullList(null, null, null).size());
//    }

}

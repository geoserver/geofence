/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.geofence.core.model.UserGroup;
import org.geofence.core.services.dto.ShortGroup;
import org.geofence.core.services.exception.NotFoundServiceEx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/** @author ETj (etj at geo-solutions.it) */
public class UserGroupAdminServiceImplTest extends ServiceTestBase {

    public UserGroupAdminServiceImplTest() {}

    @Test
    public void testInsertDelete(TestInfo testInfo) throws NotFoundServiceEx {
        String methodName = testInfo.getTestMethod().get().getName();

        UserGroup p = createRole(methodName);
        userGroupAdminService.get(p.getId()); // will throw if not found
        assertTrue(userGroupAdminService.delete(p.getId()), "Could not delete group");
    }

    @Test
    public void testUpdate() throws Exception {
        removeAllUserGroups();

        UserGroup group = createRole("u1");

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

            assertEquals((boolean) !oldEnabled, (boolean) loaded.getEnabled());
        }
    }

    @Test
    public void testGetAll() {
        assertEquals(0, userGroupAdminService.getList(null, null, null).size());

        createRole("u1");
        createRole("u2");
        createRole("u3");

        assertEquals(3, userGroupAdminService.getList(null, null, null).size());
    }

    @Test
    public void testGetCount() {
        assertEquals(0, userGroupAdminService.getCount(null));

        createRole("u10");
        createRole("u20");
        createRole("u30");
        UserGroup u99 = createRole("u99");

        assertEquals(4, userGroupAdminService.getCount(null));
        assertEquals(4, userGroupAdminService.getCount("u%"));
        assertEquals(3, userGroupAdminService.getCount("%0"));

        List<ShortGroup> glist = userGroupAdminService.getList("%9", null, null);
        assertEquals(1, glist.size());
        assertEquals("u99", glist.get(0).getName());
        assertEquals((Long) u99.getId(), (Long) glist.get(0).getId());
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

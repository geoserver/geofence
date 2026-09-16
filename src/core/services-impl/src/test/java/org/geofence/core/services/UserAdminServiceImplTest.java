/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.geofence.core.model.GSUser;
import org.geofence.core.model.UserGroup;
import org.geofence.core.services.dto.ShortUser;
import org.geofence.core.services.exception.NotFoundServiceEx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/** @author ETj (etj at geo-solutions.it) */
public class UserAdminServiceImplTest extends ServiceTestBase {

    public UserAdminServiceImplTest() {}

    @Test
    public void testInsertDeleteUpdateUser(TestInfo testInfo) throws NotFoundServiceEx {
        String methodName = testInfo.getTestMethod().get().getName();

        UserGroup g1 = createRole(methodName + "_1");
        UserGroup g2 = createRole(methodName + "_2");
        UserGroup g3 = createRole(methodName + "_3");

        GSUser user = new GSUser();
        user.setName(methodName);
        user.getGroups().add(g1);
        user.getGroups().add(g2);
        userAdminService.insert(user);

        {
            GSUser loaded = userAdminService.getFull(user.getName());
            assertNotNull(loaded);
            assertEquals(2, loaded.getGroups().size());

            Set<Long> ids = extractId(loaded.getGroups());
            assertTrue(ids.contains(g1.getId()));
            assertTrue(ids.contains(g2.getId()));

            // add a new group
            loaded.getGroups().add(g3);
            userAdminService.update(loaded);
        }

        {
            GSUser loaded = userAdminService.getFull(user.getName());
            assertNotNull(loaded);
            assertEquals(3, loaded.getGroups().size());

            Set<Long> ids = extractId(loaded.getGroups());
            assertTrue(ids.contains(g1.getId()));
            assertTrue(ids.contains(g2.getId()));
            assertTrue(ids.contains(g3.getId()));

            assertTrue(loaded.getGroups().remove(g1), "Could not remove group association");
            userAdminService.update(loaded);
        }

        {
            GSUser loaded = userAdminService.getFull(user.getName());
            assertNotNull(loaded);
            assertEquals(2, loaded.getGroups().size());

            Set<Long> ids = extractId(loaded.getGroups());
            assertTrue(ids.contains(g2.getId()));
            assertTrue(ids.contains(g3.getId()));
        }

        assertTrue(userAdminService.delete(user.getId()), "Could not delete user");
    }

    protected Set<Long> extractId(Set<UserGroup> set) {
        Set<Long> ret = new HashSet<Long>();
        for (UserGroup userGroup : set) {
            ret.add(userGroup.getId());
        }
        return ret;
    }

    @Test
    public void testBadGet() throws Exception {
        try {
            userAdminService.get(-1l); // will throw if not found
            fail("Exception not trapped");
        } catch (NotFoundServiceEx e) {
            LOGGER.info("Exception properly trapped");
        }
    }

    @Test
    public void testUpdateUser() throws Exception {
        UserGroup ug1 = createRole("p1");
        GSUser user = createUser("u1", ug1);

        UserGroup ug2 = createRole("p2");
        final String NEWNAME = "NEWNAME";

        {
            GSUser loaded = userAdminService.getFull(user.getName());
            assertNotNull(loaded);

            assertEquals("u1", loaded.getName());
            assertEquals(1, loaded.getGroups().size());
            assertEquals("p1", loaded.getGroups().iterator().next().getName());

            loaded.getGroups().add(ug2);
            loaded.setName(NEWNAME);

            userAdminService.update(loaded);
        }
        {
            GSUser loaded = userAdminService.getFull(NEWNAME);
            assertNotNull(loaded);

            assertEquals(user.getId(), loaded.getId());
            assertEquals(2, loaded.getGroups().size());
        }
    }

    @Test
    public void testUpdateUserGroups() throws Exception {

        UserGroup ug1 = createRole("g1");
        UserGroup ug2 = createRole("g2");
        UserGroup ug3 = createRole("g3");

        Long id = createUser("u1", ug1).getId();

        {
            GSUser loaded = userAdminService.getFull("u1");
            assertNotNull(loaded);

            assertEquals("u1", loaded.getName());
            assertEquals(1, loaded.getGroups().size());
            assertEquals("g1", loaded.getGroups().iterator().next().getName());

            loaded.getGroups().add(ug2);

            userAdminService.update(loaded);
        }
        {
            GSUser loaded = userAdminService.getFull("u1");
            assertNotNull(loaded);

            assertEquals(2, loaded.getGroups().size());
            assertTrue(hasGroups(loaded, "g1", "g2"));

            loaded.getGroups().remove(ug1);
            loaded.getGroups().add(ug3);
            userAdminService.update(loaded);
        }
        {
            GSUser loaded = userAdminService.getFull("u1");
            assertNotNull(loaded);

            assertEquals(2, loaded.getGroups().size());
            assertTrue(hasGroups(loaded, "g2", "g3"));
        }
    }

    protected boolean hasGroups(GSUser user, String... groupName) {
        Set<String> names = new HashSet<>();
        for (UserGroup userGroup : user.getGroups()) {
            names.add(userGroup.getName());
        }

        return names.containsAll(Arrays.asList(groupName));
    }

    @Test
    public void testGetAllUsers() {
        assertEquals(0, userAdminService.getList(null, null, null).size());

        createUserAndGroup("u1");
        createUserAndGroup("u2");
        createUserAndGroup("u3");

        assertEquals(3, userAdminService.getList(null, null, null).size());
    }

    @Test
    public void testGetUsersCount() {
        assertEquals(0, userAdminService.getCount(null));

        createUserAndGroup("u10");
        createUserAndGroup("u20");
        createUserAndGroup("u30");
        GSUser u99 = createUserAndGroup("u99");

        assertEquals(4, userAdminService.getCount(null));
        assertEquals(4, userAdminService.getCount("u%"));
        assertEquals(3, userAdminService.getCount("%0"));

        List<ShortUser> users = userAdminService.getList("%9", null, null);
        assertEquals(1, users.size());
        assertEquals("u99", users.get(0).getName());
        assertEquals(u99.getId(), (Long) users.get(0).getId());
    }
}

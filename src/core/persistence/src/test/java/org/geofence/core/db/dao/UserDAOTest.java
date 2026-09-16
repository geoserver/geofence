/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import org.geofence.core.model.GSUser;
import org.geofence.core.model.UserGroup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/** @author ETj (etj at geo-solutions.it) */
public class UserDAOTest extends BaseDAOTest {

    @Test
    public void testPersistUser(TestInfo testInfo) throws Exception {

        long id;
        {
            GSUser user = createUserAndGroup(testInfo.getTestMethod().get().getName());
            userDAO.persist(user);
            id = user.getId();
        }

        // test save & load
        {
            GSUser loaded = userDAO.find(id);
            assertNotNull(loaded, "Can't retrieve user");
        }

        userDAO.removeById(id);
        assertNull(userDAO.find(id), "User not deleted");
    }

    @Test
    public void testGroups(TestInfo testInfo) throws Exception {

        Long gid1, gid2;
        Long uid1;
        {
            UserGroup g1 = createUserGroup(testInfo.getTestMethod().get().getName() + "1");
            gid1 = g1.getId();

            UserGroup g2 = createUserGroup(testInfo.getTestMethod().get().getName() + "2");
            gid2 = g2.getId();

            GSUser u1 = createUser("u0", g1);
            userDAO.persist(u1);
            uid1 = u1.getId();
            assertNotNull(uid1);
        }

        {
            GSUser loaded = userDAO.getFull("u0");
            assertNotNull(loaded, "Can't retrieve user");
            Set<UserGroup> grps = loaded.getGroups();
            assertEquals(1, grps.size(), "Bad number of usergroups");
            assertEquals(gid1, grps.iterator().next().getId(), "Bad assigned usergroup");

            // add another group
            UserGroup g2 = userGroupDAO.find(gid2);
            assertNotNull(g2);
            loaded.setGroups(grps);
            loaded.getGroups().add(g2);
            userDAO.merge(loaded);
        }

        {
            GSUser loaded = userDAO.getFull("u0");
            assertNotNull(loaded, "Can't retrieve user");
            Set<UserGroup> grps = loaded.getGroups();
            assertEquals(2, grps.size(), "Bad number of usergroups");
        }
    }
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.geofence.core.model.UserGroup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/** @author ETj (etj at geo-solutions.it) */
public class UserGroupDAOTest extends BaseDAOTest {

    @Test
    public void testPersist(TestInfo testInfo) throws Exception {

        long id;
        {
            UserGroup group = createUserGroup(testInfo.getTestMethod().get().getName());
            id = group.getId();
        }

        // test save & load
        {
            UserGroup loaded = userGroupDAO.find(id);
            assertNotNull(loaded, "Can't retrieve userGroup");
            assertEquals(testInfo.getTestMethod().get().getName(), loaded.getName());
        }

        userGroupDAO.removeById(id);
        assertNull(userGroupDAO.find(id), "User not deleted");
    }

    @Test
    public void testMerge(TestInfo testInfo) throws Exception {

        long id;
        {
            UserGroup group = createUserGroup(testInfo.getTestMethod().get().getName());
            id = group.getId();
        }

        {
            UserGroup loaded = userGroupDAO.find(id);
            assertNotNull(loaded, "Can't retrieve userGroup");
            assertEquals(testInfo.getTestMethod().get().getName(), loaded.getName());

            loaded.setName("aNewName");
            userGroupDAO.merge(loaded);
        }

        {
            UserGroup loaded = userGroupDAO.find(id);
            assertEquals("aNewName", loaded.getName());
        }

        userGroupDAO.removeById(id);
        assertNull(userGroupDAO.find(id), "User not deleted");
    }

    //    @Test
    //    public void testSearchByProp() throws Exception {
    //
    //        final long id1;
    //        {
    //            UserGroup profile = createUserGroup("p1");
    //            profile.getCustomProps().put("xid", "one");
    //            userGroupDAO.merge(profile);
    //            id1 = profile.getId();
    //        }
    //
    //        {
    //            UserGroup profile = createUserGroup("p2");
    //            profile.getCustomProps().put("xid", "two");
    //            userGroupDAO.merge(profile);
    //        }
    //        {
    //            UserGroup profile = createUserGroup("p3");
    //            profile.getCustomProps().put("xid", "two");
    //            userGroupDAO.merge(profile);
    //        }
    //
    //        {
    //            // make sure props are in there
    //            Map<String, String> customProps = userGroupDAO.getCustomProps(id1);
    //            assertEquals("one", customProps.get("xid"));
    //        }
    //
    //        // none of these will work
    ////        Search search = new Search(UserGroup.class);
    ////        search.addFilterEqual("customProps.xid", "one");
    ////        search.addFilterEqual("customProps.key", "xid");
    ////        search.addFilterEqual("customProps.index", "xid");
    ////        search.addFilterEqual("customProps.propkey", "xid");
    ////        search.addFilterSome("customProps", Filter.equal("index", "xid"));
    ////        search.addFilterSome("customProps", Filter.equal("key", "xid"));
    ////        search.addFilterSome("customProps", Filter.equal("propkey", "xid"));
    ////        search.addFilterEqual("propvalue", "xid");  // Could not find property 'propvalue' on class class
    // org.geofence.core.model.UserGroup
    ////        search.addFilterSome("customProps", Filter.equal("xid", "one"));
    ////        search.addFilterEqual("index(customProps)", "xid");
    //
    //        {
    //            List<UserGroup> f1 = userGroupDAO.searchByCustomProp("xid", "one");
    //            assertEquals(1, f1.size());
    //            assertEquals("p1", f1.get(0).getName());
    //        }
    //
    //        {
    //            List<UserGroup> f1 = userGroupDAO.searchByCustomProp("xid", "two");
    //            assertEquals(2, f1.size());
    //        }
    //
    ////        List found = userGroupDAO.search(search);
    ////        LOGGER.info("Found " + found);
    ////        assertEquals(1, found.size());
    //
    //    }

}

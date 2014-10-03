/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.services.dto.ShortUser;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.geoserver.geofence.services.exception.NotFoundServiceEx;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class UserAdminServiceImplTest extends ServiceTestBase {

    public UserAdminServiceImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testInsertDeleteUpdateUser() throws NotFoundServiceEx {

        UserGroup g1 = createUserGroup(getName()+"_1");
        UserGroup g2 = createUserGroup(getName()+"_2");
        UserGroup g3 = createUserGroup(getName()+"_3");

        GSUser user = new GSUser();
        user.setName( getName() );
        user.getGroups().add(g1);
        user.getGroups().add(g2);
        userAdminService.insert(user);

        {
            GSUser loaded = userAdminService.getFull(user.getId());
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
            GSUser loaded = userAdminService.getFull(user.getId());
            assertNotNull(loaded);
            assertEquals(3, loaded.getGroups().size());

            Set<Long> ids = extractId(loaded.getGroups());
            assertTrue(ids.contains(g1.getId()));
            assertTrue(ids.contains(g2.getId()));
            assertTrue(ids.contains(g3.getId()));

            assertTrue("Could not remove group association", loaded.getGroups().remove(g1));
            userAdminService.update(loaded);
        }

        {
            GSUser loaded = userAdminService.getFull(user.getId());
            assertNotNull(loaded);
            assertEquals(2, loaded.getGroups().size());

            Set<Long> ids = extractId(loaded.getGroups());
            assertTrue(ids.contains(g2.getId()));
            assertTrue(ids.contains(g3.getId()));
        }

        assertTrue("Could not delete user", userAdminService.delete(user.getId()));
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
        UserGroup ug1 = createUserGroup("p1");
        GSUser user = createUser("u1", ug1);

        UserGroup ug2 = createUserGroup("p2");
        final String NEWNAME = "NEWNAME";

        {
            GSUser loaded = userAdminService.getFull(user.getId());
            assertNotNull(loaded);

            assertEquals("u1", loaded.getName());
            assertEquals(1, loaded.getGroups().size());
            assertEquals("p1", loaded.getGroups().iterator().next().getName());

            loaded.getGroups().add(ug2);
            loaded.setName(NEWNAME);

//            WKTReader wktReader = new WKTReader();
//            String allowedArea = "MULTIPOLYGON (((414699.55 130160.43, 414701.83 130149.9, 414729.2 130155.7, 414729.2 130155.7, 414733.25 130149.8, 414735.1 130140.9, 414743.75 130142.7, 414740.6 130158.15, 414742.15 130158.5, 414739.65 130169.25, 414728.05 130166.65, 414727.77 130167.93, 414724.52 130167.19, 414717.65 130165.63, 414717.85 130164.45, 414699.55 130160.43)))";
//            MultiPolygon the_geom = (MultiPolygon) wktReader.read(allowedArea);
//            the_geom.setSRID(4326);
//            loaded.setAllowedArea(the_geom);

            userAdminService.update(loaded);
        }
        {
            GSUser loaded = userAdminService.getFull(user.getId());
            assertNotNull(loaded);

            assertEquals(NEWNAME, loaded.getName());
            assertEquals(2, loaded.getGroups().size());
//            assertEquals("p2", loaded.getGroups().getName());

//            assertNotNull(loaded.getAllowedArea());
//            assertEquals(4326, loaded.getAllowedArea().getSRID());
        }
    }

    @Test
    public void testUpdateUserGroups() throws Exception {

        UserGroup ug1 = createUserGroup("g1");
        UserGroup ug2 = createUserGroup("g2");
        UserGroup ug3 = createUserGroup("g3");


        Long id;        
        {
            id = createUser("u1", ug1).getId();
        }


        {
            GSUser loaded = userAdminService.getFull(id);
            assertNotNull(loaded);

            assertEquals("u1", loaded.getName());
            assertEquals(1, loaded.getGroups().size());
            assertEquals("g1", loaded.getGroups().iterator().next().getName());

            loaded.getGroups().add(ug2);

            userAdminService.update(loaded);
        }
        {
            GSUser loaded = userAdminService.getFull(id);
            assertNotNull(loaded);

            assertEquals(2, loaded.getGroups().size());
            assertTrue(hasGroups(loaded, "g1","g2"));

            loaded.getGroups().remove(ug1);
            loaded.getGroups().add(ug3);
            userAdminService.update(loaded);
        }
        {
            GSUser loaded = userAdminService.getFull(id);
            assertNotNull(loaded);

            assertEquals(2, loaded.getGroups().size());
            assertTrue(hasGroups(loaded, "g2","g3"));
        }
    }

    protected boolean hasGroups(GSUser user, String ... groupName) {
        Set<String> names = new HashSet<String>();
        for (UserGroup userGroup : user.getGroups()) {
            names.add(userGroup.getName());
        }
        
        return names.containsAll(Arrays.asList(groupName));
    }

    @Test
    public void testGetAllUsers() {
        assertEquals(0, userAdminService.getList(null,null,null).size());

        createUserAndGroup("u1");
        createUserAndGroup("u2");
        createUserAndGroup("u3");

        assertEquals(3, userAdminService.getList(null,null,null).size());
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

        List<ShortUser> users = userAdminService.getList("%9",null,null);
        assertEquals(1, users.size());
        assertEquals("u99", users.get(0).getName());
        assertEquals((Long)u99.getId(), (Long)users.get(0).getId());
    }

}

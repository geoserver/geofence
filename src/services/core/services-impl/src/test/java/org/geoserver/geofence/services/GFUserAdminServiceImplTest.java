/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.services.dto.ShortUser;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.geoserver.geofence.core.model.GFUser;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GFUserAdminServiceImplTest extends ServiceTestBase {

    public GFUserAdminServiceImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testInsertDeleteUser() throws NotFoundServiceEx {

        GFUser user = createGFUser(getName());
        gfUserAdminService.get(user.getId()); // will throw if not found
        assertTrue("Could not delete user", gfUserAdminService.delete(user.getId()));
    }

    @Test
    public void testUpdateUser() throws Exception {
        GFUser user = createGFUser("u1");

        final String NEWNAME = "NEWNAME";

        {
            GFUser loaded = gfUserAdminService.get(user.getId());
            assertNotNull(loaded);

            assertEquals("u1", loaded.getName());

            loaded.setName(NEWNAME);

            gfUserAdminService.update(loaded);
        }
        {
            GFUser loaded = gfUserAdminService.get(user.getId());
            assertNotNull(loaded);

            assertEquals(NEWNAME, loaded.getName());
        }
    }

    @Test
    public void testGetAllUsers() {
        assertEquals(0, gfUserAdminService.getList(null,null,null).size());

        createGFUser("u1");
        createGFUser("u2");
        createGFUser("u3");

        assertEquals(3, gfUserAdminService.getList(null,null,null).size());
    }

    @Test
    public void testGetUsersCount() {
        assertEquals(0, gfUserAdminService.getCount(null));

        createGFUser("u10");
        createGFUser("u20");
        createGFUser("u30");
        GFUser u99 = createGFUser("u99");

        assertEquals(4, gfUserAdminService.getCount(null));
        assertEquals(4, gfUserAdminService.getCount("u%"));
        assertEquals(3, gfUserAdminService.getCount("%0"));

        List<ShortUser> users = gfUserAdminService.getList("%9",null,null);
        assertEquals(1, users.size());
        assertEquals("u99", users.get(0).getName());
        assertEquals((Long)u99.getId(), (Long)users.get(0).getId());
    }

}

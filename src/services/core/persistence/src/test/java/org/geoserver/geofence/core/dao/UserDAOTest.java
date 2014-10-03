/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao;

import com.vividsolutions.jts.geom.MultiPolygon;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.UserGroup;
import java.util.Set;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * 
 * @author ETj (etj at geo-solutions.it)
 */
public class UserDAOTest extends BaseDAOTest {

    @Test
    public void testPersistUser() throws Exception {

        long id;
        {
            GSUser user = createUserAndGroup(name.getMethodName());
            userDAO.persist(user);
            id = user.getId();
        }

        // test save & load
        {
            GSUser loaded = userDAO.find(id);
            assertNotNull("Can't retrieve user", loaded);

//            assertNull(loaded.getAllowedArea());
//            loaded.setAllowedArea(buildMultiPolygon());
//            userDAO.merge(loaded);
        }

//        {
//            GSUser loaded2 = userDAO.find(id);
//            assertNotNull("Can't retrieve user", loaded2);
//            assertNotNull(loaded2.getAllowedArea());
//        }

        userDAO.removeById(id);
        assertNull("User not deleted", userDAO.find(id));
    }

//    @Test
//    public void testUpdateSRID() throws Exception {
//
//        final int srid2 = 4326;
//        final int srid1 = 8307;
//
//        long id;
//        {
//            GSUser user = createUserAndGroup(getName());
//            MultiPolygon mp = buildMultiPolygon();
//            mp.setSRID(srid1);
//            user.setAllowedArea(mp);
//            userDAO.persist(user);
//            id = user.getId();
//        }
//
//        // test save & load
//        {
//            GSUser loaded = userDAO.find(id);
//            assertNotNull("Can't retrieve user", loaded);
//            assertEquals("bad SRID", srid1, loaded.getAllowedArea().getSRID());
//
//            MultiPolygon mp = buildMultiPolygon();
//            mp.setSRID(srid2);
//            loaded.setAllowedArea(mp);
//            assertEquals("SRID not set", srid2, loaded.getAllowedArea().getSRID());
//
//            userDAO.merge(loaded);
//        }
//
//        {
//            GSUser loaded = userDAO.find(id);
//            assertNotNull("Can't retrieve user", loaded);
//            assertEquals("SRID not updated", srid2, loaded.getAllowedArea().getSRID());
//        }
//
//        userDAO.removeById(id);
//        assertNull("User not deleted", userDAO.find(id));
//    }


    @Test
    public void testGroups() throws Exception {

        Long gid1, gid2;
        Long uid1;
        {
            UserGroup g1 = createUserGroup(name.getMethodName()+"1");
            gid1 = g1.getId();
            
            UserGroup g2 = createUserGroup(name.getMethodName()+"2");
            gid2 = g2.getId();

            GSUser u1 = createUser("u0", g1);
            userDAO.persist(u1);
            uid1= u1.getId();
            assertNotNull(uid1);
        }

        {
            GSUser loaded = userDAO.find(uid1);
            assertNotNull("Can't retrieve user", loaded);
            Set<UserGroup> grps = userDAO.getGroups(uid1);
            assertEquals("Bad number of usergroups", 1, grps.size());
            assertEquals("Bad assigned usergroup", gid1, grps.iterator().next().getId());

            // add another group
            UserGroup g2 = userGroupDAO.find(gid2);
            assertNotNull(g2);
            loaded.setGroups(grps);
            loaded.getGroups().add(g2);
            userDAO.merge(loaded);
        }

        {
            GSUser loaded = userDAO.find(uid1);
            assertNotNull("Can't retrieve user", loaded);
            Set<UserGroup> grps = userDAO.getGroups(uid1);
            assertEquals("Bad number of usergroups", 2, grps.size());
        }
    }

}
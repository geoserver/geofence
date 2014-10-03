/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao;

import org.geoserver.geofence.core.model.GFUser;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * 
 * @author ETj (etj at geo-solutions.it)
 */
public class GFUserDAOTest extends BaseDAOTest {

    @Test
    public void testPersistUser() throws Exception {

        removeAllGRUsers();

        long id;
        {
            GFUser user = new GFUser();
            user.setName(name.getMethodName());
            gfUserDAO.persist(user);
            id = user.getId();
        }

        // test save & load
        {
            GFUser loaded = gfUserDAO.find(id);
            assertNotNull("Can't retrieve user", loaded);
        }

        gfUserDAO.removeById(id);
        assertNull("User not deleted", gfUserDAO.find(id));
    }

}
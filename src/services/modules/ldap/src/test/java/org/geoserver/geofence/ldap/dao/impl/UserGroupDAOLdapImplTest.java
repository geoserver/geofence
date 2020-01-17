/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.ldap.dao.impl;

import static org.junit.Assert.*;
import org.geoserver.geofence.core.model.UserGroup;

import java.util.List;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public class UserGroupDAOLdapImplTest extends BaseDAOTest
{

    @Test
    public void testFindAll()
    {
        List<UserGroup> groups = userGroupDAO.findAll();
        assertTrue("No groups", groups.size() > 0);
        UserGroup group = groups.get(0);
        assertTrue("Empty group name", group.getName().length() > 0);


        Set<String> expected = new HashSet<>(Arrays.asList(new String[]{"adminGroup", "parent", "other", "otherGroup", "destination"}));
        Set<String> found = new HashSet<>();
        for (UserGroup g : groups) {
            LOGGER.debug("Found group " + g);
            found.add(g.getName());
        }
        assertEquals("Bad group set found", expected, found);
    }

    @Test
    public void testFind()
    {
        UserGroup group = userGroupDAO.find(1l);
        assertNull("Find by id should be disabled in LDAP", group);
    }

    @Test
    public void testSearch()
    {
        assertNotNull(userGroupDAO.get("adminGroup"));        

        List<UserGroup> groups = userGroupDAO.search("adminGroup", null, null);
        assertEquals(1, groups.size());
        assertEquals("adminGroup", groups.get(0).getName());
    }

    @Test
    public void testCount()
    {
        assertEquals(5, userGroupDAO.countByNameLike(null));
    }
    
    @Test
    public void testSearchPagination()
    {
        List<UserGroup> groups = userGroupDAO.search(null, null, null);
        assertEquals(5, groups.size());
        
        groups = userGroupDAO.search(null, 0,2);
        assertEquals(2, groups.size());
        
        groups = userGroupDAO.search(null, 1, 2);
        assertEquals(2, groups.size());
        
        groups = userGroupDAO.search(null, 2, 2);
        assertEquals(1, groups.size());
    }
}

/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.ldap.dao.impl;

import static org.junit.Assert.*;
import org.geoserver.geofence.core.model.UserGroup;

import java.util.List;

import org.junit.Test;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

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


        Set<String> expected = new HashSet<>(Arrays.asList(new String[]{"adminGroup", "other", "otherGroup", "destination"}));
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
        Search search = new Search();
        search.addFilter(new Filter("groupname", "adminGroup"));

        List<UserGroup> groups = userGroupDAO.search(search);
        assertTrue(groups.size() == 1);
        UserGroup group = groups.get(0);
        assertEquals("adminGroup", group.getName());
    }

    @Test
    public void testCount()
    {
        assertEquals(4, userGroupDAO.count(new Search()));
    }
}

/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.ldap.dao.impl;

import static org.junit.Assert.*;
import org.geoserver.geofence.core.model.GSUser;

import java.util.List;

import org.junit.Test;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import java.util.HashSet;
import java.util.Set;
import org.geoserver.geofence.core.model.UserGroup;

/**
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public class GSUserDAOLdapImplTest extends BaseDAOTest
{

    @Test
    public void testFindAll()
    {
        List<GSUser> users = userDAO.findAll();
        assertTrue(users.size() > 0);
        GSUser user = users.get(0);
        assertTrue(user.getName().length() > 0);
    }

    @Test
    public void testFind()
    {
        GSUser user = userDAO.find(1l);
        assertNull(user);
    }

    @Test
    public void testGetFullByName()
    {
        final String USERNAME = "admin";
        GSUser user = userDAO.getFull(USERNAME);
        assertNotNull(user);
        LOGGER.info("Searching for '" + USERNAME + "', found " + user);
        assertEquals(USERNAME, user.getName());
    }

    @Test
    public void testCount()
    {
        assertTrue(userDAO.count(new Search()) > 0);
    }

    @Test
    public void testSearch_admin()
    {
        Search search = new Search();
        search.addFilter(new Filter("username", "admin"));
        List<GSUser> users = userDAO.search(search);
        assertTrue(users.size() > 0);
        GSUser user = users.get(0);
        assertTrue(user.getName().length() > 0);
    }

    @Test
    public void testSearch_groups()
    {
        Search search = new Search();
        search.addFilter(new Filter("username", "destination1"));
        List<GSUser> users = userDAO.search(search);
        assertTrue(users.size() == 1);
        GSUser user = users.get(0);
        assertTrue(user.getName().equals("destination1"));
    }

    @Test
    public void test_getFullByName_groups()
    {
        GSUser user = userDAO.getFull("destination2");
        assertNotNull(user);
        assertEquals("destination2", user.getName());
        assertEquals(2, user.getGroups().size());
        
        Set<String> gnames = new HashSet<>();
        for (UserGroup g : user.getGroups()) {
            LOGGER.debug("group : " + g.getName());
            gnames.add(g.getName());
        }

        assertTrue(gnames.contains("destination"));
        assertTrue(gnames.contains("otherGroup"));
    }
    
    @Test
    public void test_getFullByName_hierarchicalGroups()
    {
        ((GSUserDAOLdapImpl)userDAO).setEnableHierarchicalGroups(true);
        ((GSUserDAOLdapImpl)userDAO).setMemberFilter("member={0}");
        ((GSUserDAOLdapImpl)userDAO).setNestedMemberFilter("member={0}");
        try {
            GSUser user = userDAO.getFull("destination2");
            
            assertNotNull(user);
            assertEquals("destination2", user.getName());
            assertEquals(3, user.getGroups().size());
            
            Set<String> gnames = new HashSet<>();
            for (UserGroup g : user.getGroups()) {
                LOGGER.debug("group : " + g.getName());
                gnames.add(g.getName());
            }
    
            assertTrue(gnames.contains("destination"));
            assertTrue(gnames.contains("otherGroup"));
            assertTrue(gnames.contains("parent"));
        } finally {
            ((GSUserDAOLdapImpl)userDAO).setEnableHierarchicalGroups(false);
            ((GSUserDAOLdapImpl)userDAO).setMemberFilter(null);
            ((GSUserDAOLdapImpl)userDAO).setNestedMemberFilter(null);
        }
    }

}

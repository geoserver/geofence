/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.core.dao.util;

import org.geoserver.geofence.core.dao.GSUserDAO;
import org.geoserver.geofence.core.dao.impl.GSUserDAOImpl;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ETj <etj at geo-solutions.it>
 */
public class DaoRegistryTest
{

    public DaoRegistryTest()
    {
    }

    @Test
    public void testGetDefault()
    {
        GeofenceDaoRegistry registry = new GeofenceDaoRegistry("DEFAULTTYPE");
        registry.addRegistrar(new GeofenceDaoRegistry.DaoRegistrar("DEFAULTTYPE", new GSUserDAOImpl()));

        GSUserDAO dao = registry.getDao(GSUserDAO.class); // should not throw
    }

    @Test
    public void testGet()
    {
        GeofenceDaoRegistry registry = new GeofenceDaoRegistry("DEFAULTTYPE");
        registry.addRegistrar(new GeofenceDaoRegistry.DaoRegistrar("DEFAULTTYPE", new GSUserTestDAO("t1")));
        registry.addRegistrar(new GeofenceDaoRegistry.DaoRegistrar("TYPE1", new GSUserTestDAO("t2")));

        {
            GSUserDAO dao = registry.getDao(GSUserDAO.class);
            assertTrue(dao instanceof GSUserTestDAO);
            assertEquals("t1", ((GSUserTestDAO)dao).name);
        }

        registry.setSelectedType("TYPE1");

        {
            GSUserDAO dao = registry.getDao(GSUserDAO.class);
            assertTrue(dao instanceof GSUserTestDAO);
            assertEquals("t2", ((GSUserTestDAO)dao).name);
        }
    }

    @Test
    public void testGetMissingDefault()
    {
        GeofenceDaoRegistry registry = new GeofenceDaoRegistry("DEFAULTTYPE");

        try {
            GSUserDAO dao = registry.getDao(GSUserDAO.class);
            fail("Missing DAO not trapped");
        } catch (Exception e) {
        }
    }

    private static class GSUserTestDAO extends GSUserDAOImpl {

        private String name;

        public GSUserTestDAO(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }
    }
}

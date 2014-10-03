/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao;

import org.geoserver.geofence.core.dao.UserGroupDAO;
import org.geoserver.geofence.core.dao.RuleLimitsDAO;
import org.geoserver.geofence.core.dao.RuleDAO;
import org.geoserver.geofence.core.dao.GSUserDAO;
import org.geoserver.geofence.core.dao.LayerDetailsDAO;
import org.geoserver.geofence.core.dao.GFUserDAO;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.geoserver.geofence.core.model.GFUser;
import org.geoserver.geofence.core.model.UserGroup;
import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.Rule;

import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.rules.TestName;
import org.junit.Test;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public abstract class BaseDAOTest  {
    protected final Logger LOGGER;

    @org.junit.Rule
    public TestName name = new TestName();

    protected static GSUserDAO userDAO;
    protected static GFUserDAO gfUserDAO;
    protected static UserGroupDAO userGroupDAO;
    protected static RuleDAO ruleDAO;
    protected static LayerDetailsDAO detailsDAO;
    protected static RuleLimitsDAO limitsDAO;

    protected static ClassPathXmlApplicationContext ctx = null;

    public BaseDAOTest() {
        LOGGER = LogManager.getLogger(getClass());

        synchronized(BaseDAOTest.class) {
            if(ctx == null) {
                String[] paths = {
                        "applicationContext.xml"
//                         ,"applicationContext-test.xml"
                };
                ctx = new ClassPathXmlApplicationContext(paths);

                userDAO = (GSUserDAO)ctx.getBean("gsUserDAO");
                gfUserDAO = (GFUserDAO)ctx.getBean("gfUserDAO");
                userGroupDAO = (UserGroupDAO)ctx.getBean("userGroupDAO");
                ruleDAO = (RuleDAO)ctx.getBean("ruleDAO");
                detailsDAO = (LayerDetailsDAO)ctx.getBean("layerDetailsDAO");
                limitsDAO = (RuleLimitsDAO)ctx.getBean("ruleLimitsDAO");
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        LOGGER.info("################ Running " + getClass().getSimpleName() + "::" + name.getMethodName() );

        removeAll();
        LOGGER.info("##### Ending setup for " + name.getMethodName() + " ###----------------------");
    }

    @Test
    public void testCheckDAOs() {

        assertNotNull(userDAO);
        assertNotNull(gfUserDAO);
        assertNotNull(userGroupDAO);
        assertNotNull(ruleDAO);
        assertNotNull(detailsDAO);
    }

    protected void removeAll() {
        removeAllRules();
        removeAllUsers(); 
        removeAllUserGroups();
    }

    protected void removeAllUsers() {
        List<GSUser> list = userDAO.findAll();
        for (GSUser item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = userDAO.remove(item);
            assertTrue("User not removed", ret);
        }

        assertEquals("Users have not been properly deleted", 0, userDAO.count(null));
    }

    protected void removeAllGRUsers() {
        List<GFUser> list = gfUserDAO.findAll();
        for (GFUser item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = gfUserDAO.remove(item);
            assertTrue("User not removed", ret);
        }

        assertEquals("GRUsers have not been properly deleted", 0, gfUserDAO.count(null));
    }

    protected void removeAllRules() {
        List<Rule> list = ruleDAO.findAll();
        for (Rule item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = ruleDAO.remove(item);
            assertTrue("Rule not removed", ret);
        }

        assertEquals("Rules have not been properly deleted", 0, ruleDAO.count(null));
    }

    protected void removeAllUserGroups() {
        List<UserGroup> list = userGroupDAO.findAll();
        for (UserGroup item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = userGroupDAO.remove(item);
            assertTrue("UserGroup not removed", ret);
        }

        assertEquals("UserGroups have not been properly deleted", 0, userGroupDAO.count(null));
    }

    protected GSUser createUser(String base, UserGroup userGroup) {

        GSUser user = new GSUser();
        user.setName( base );
        user.getGroups().add(userGroup);
        return user;
    }

    protected UserGroup createUserGroup(String base) {

        UserGroup group = new UserGroup();
        group.setName(base);
        userGroupDAO.persist(group);
        return group;
    }

    protected GSUser createUserAndGroup(String base) {

        UserGroup group = createUserGroup(base);
        return createUser(base, group);
    }


    protected final static String MULTIPOLYGONWKT = "MULTIPOLYGON(((48.6894038 62.33877482, 48.7014874 62.33877482, 48.7014874 62.33968662, 48.6894038 62.33968662, 48.6894038 62.33877482)))";
    protected final static String POLYGONWKT = "POLYGON((48.6894038 62.33877482, 48.7014874 62.33877482, 48.7014874 62.33968662, 48.6894038 62.33968662, 48.6894038 62.33877482))";

    protected MultiPolygon buildMultiPolygon() {
        try {
            WKTReader reader = new WKTReader();
            MultiPolygon mp = (MultiPolygon) reader.read(MULTIPOLYGONWKT);
            mp.setSRID(4326);
            return mp;
        } catch (ParseException ex) {
            throw new RuntimeException("Unexpected exception: " + ex.getMessage(), ex);
        }
    }

    protected Polygon buildPolygon() {
        try {
            WKTReader reader = new WKTReader();
            Polygon mp = (Polygon) reader.read(POLYGONWKT);
            mp.setSRID(4326);
            return mp;
        } catch (ParseException ex) {
            throw new RuntimeException("Unexpected exception: " + ex.getMessage(), ex);
        }
    }

    //==========================================================================

}

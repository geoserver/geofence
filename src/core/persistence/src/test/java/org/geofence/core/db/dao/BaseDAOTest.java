/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.db.BaseContainerTest;
import org.geofence.core.db.config.GeofencePersistenceConfig;
import org.geofence.core.model.GSUser;
import org.geofence.core.model.Rule;
import org.geofence.core.model.UserGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/** @author ETj (etj at geo-solutions.it) */
@SpringJUnitConfig(classes = GeofencePersistenceConfig.class)
public abstract class BaseDAOTest extends BaseContainerTest {
    protected final Logger LOGGER;

    @Autowired
    protected GSUserDAO userDAO;

    @Autowired
    protected UserGroupDAO userGroupDAO;

    @Autowired
    protected RuleDAO ruleDAO;

    @Autowired
    protected LayerDetailsDAO detailsDAO;

    @Autowired
    protected RuleLimitsDAO limitsDAO;

    public BaseDAOTest() {
        LOGGER = LogManager.getLogger(getClass());
    }

    @BeforeEach
    public void setUp(TestInfo testInfo) throws Exception {
        String methodName = testInfo.getTestMethod().get().getName();
        LOGGER.info("################ Running " + getClass().getSimpleName() + "::" + methodName);

        removeAll();
        LOGGER.info("##### Ending setup for " + methodName + " ###----------------------");
    }

    @Test
    public void testCheckDAOs() {

        assertNotNull(userDAO);
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
            userDAO.remove(item);
            assertNull(userDAO.find(item.getId()), "User not removed");
        }

        assertEquals(0, userDAO.countByNameLike(null), "Users have not been properly deleted");
    }

    protected void removeAllRules() {
        List<Rule> list = ruleDAO.findAll();
        for (Rule item : list) {
            LOGGER.info("Removing " + item);
            ruleDAO.remove(item);
            assertNull(ruleDAO.find(item.getId()), "Rule not removed");
        }

        assertEquals(0, ruleDAO.count(null), "Rules have not been properly deleted");
    }

    protected void removeAllUserGroups() {
        List<UserGroup> list = userGroupDAO.findAll();
        for (UserGroup item : list) {
            LOGGER.info("Removing " + item);
            userGroupDAO.remove(item);
            assertNull(userGroupDAO.find(item.getId()), "UserGroup not removed");
        }

        assertEquals(0, userGroupDAO.countByNameLike(null), "UserGroups have not been properly deleted");
    }

    protected GSUser createUser(String base, UserGroup userGroup) {

        GSUser user = new GSUser();
        user.setName(base);
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

    protected static final String MULTIPOLYGONWKT =
            "MULTIPOLYGON(((48.6894038 62.33877482, 48.7014874 62.33877482, 48.7014874 62.33968662, 48.6894038 62.33968662, 48.6894038 62.33877482)))";
    protected static final String POLYGONWKT =
            "POLYGON((48.6894038 62.33877482, 48.7014874 62.33877482, 48.7014874 62.33968662, 48.6894038 62.33968662, 48.6894038 62.33877482))";

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
}

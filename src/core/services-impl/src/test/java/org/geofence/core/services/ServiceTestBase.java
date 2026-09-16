/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.db.BaseContainerTest;
import org.geofence.core.model.GSInstance;
import org.geofence.core.model.GSUser;
import org.geofence.core.model.UserGroup;
import org.geofence.core.services.config.GeofenceServiceConfig;
import org.geofence.core.services.dto.ShortAdminRule;
import org.geofence.core.services.dto.ShortGroup;
import org.geofence.core.services.dto.ShortRule;
import org.geofence.core.services.dto.ShortUser;
import org.geofence.core.services.exception.NotFoundServiceEx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/** @author ETj (etj at geo-solutions.it) */
@SpringJUnitConfig(classes = GeofenceServiceConfig.class)
public class ServiceTestBase extends BaseContainerTest {

    protected final Logger LOGGER = LogManager.getLogger(getClass());

    @Autowired
    protected UserAdminService userAdminService;

    @Autowired
    protected UserGroupAdminService userGroupAdminService;

    @Autowired
    protected InstanceAdminService instanceAdminService;

    @Autowired
    protected RuleAdminService ruleAdminService;

    @Autowired
    protected AdminRuleAdminService adminruleAdminService;

    @Autowired
    protected RuleReaderService ruleReaderService;

    public ServiceTestBase() {
        //        LOGGER = LogManager.getLogger(getClass());

    }

    @BeforeEach
    protected void setUp(TestInfo testInfo) throws Exception {
        String methodName = testInfo.getTestMethod().get().getName();
        LOGGER.info("############################################ Running "
                + getClass().getSimpleName() + "::" + methodName);
        removeAll();
    }

    public void testCheckServices() {
        assertNotNull(userAdminService);
        assertNotNull(userGroupAdminService);
        assertNotNull(instanceAdminService);
        assertNotNull(ruleAdminService);
        assertNotNull(adminruleAdminService);
    }

    protected void removeAll() throws NotFoundServiceEx {
        LOGGER.info("***** removeAll()");
        removeAllRules();
        removeAllAdminRules();
        removeAllUsers();
        removeAllUserGroups();
        removeAllInstances();
    }

    protected void removeAllRules() throws NotFoundServiceEx {
        List<ShortRule> list = ruleAdminService.getAll();
        for (ShortRule item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = ruleAdminService.delete(item.getId());
            assertTrue(ret, "Rule not removed");
        }

        assertEquals(0, ruleAdminService.getCountAll(), "Rules have not been properly deleted");
    }

    protected void removeAllAdminRules() throws NotFoundServiceEx {
        List<ShortAdminRule> list = adminruleAdminService.getAll();
        for (ShortAdminRule item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = adminruleAdminService.delete(item.getId());
            assertTrue(ret, "AdminRule not removed");
        }

        assertEquals(0, adminruleAdminService.getCountAll(), "AdminRules have not been properly deleted");
    }

    protected void removeAllUsers() throws NotFoundServiceEx {
        List<ShortUser> list = userAdminService.getList(null, null, null);
        for (ShortUser item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = userAdminService.delete(item.getId());
            assertTrue(ret, "User not removed");
        }

        assertEquals(0, userAdminService.getCount(null), "Users have not been properly deleted");
    }

    protected void removeAllUserGroups() throws NotFoundServiceEx {
        List<ShortGroup> list = userGroupAdminService.getList(null, null, null);
        for (ShortGroup item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = userGroupAdminService.delete(item.getId());
            assertTrue(ret, "UserGroup not removed");
        }

        assertEquals(0, userGroupAdminService.getCount(null), "UserGroups have not been properly deleted");
    }

    protected void removeAllInstances() throws NotFoundServiceEx {
        List<GSInstance> list = instanceAdminService.getAll();
        for (GSInstance item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = instanceAdminService.delete(item.getId());
            assertTrue(ret, "GSInstance not removed");
        }

        assertEquals(0, instanceAdminService.getCount(null), "Instances have not been properly deleted");
    }

    protected GSUser createUser(String base, UserGroup... groups) {

        GSUser user = new GSUser();
        user.setName(base);
        user.getGroups().addAll(Arrays.asList(groups));
        userAdminService.insert(user);
        return user;
    }

    protected UserGroup createRole(String base) {

        ShortGroup sgroup = new ShortGroup();
        sgroup.setName(base);
        long id = userGroupAdminService.insert(sgroup);
        try {
            return userGroupAdminService.get(id);
        } catch (NotFoundServiceEx ex) {
            throw new RuntimeException("Should never happen (" + id + ")", ex);
        }
    }

    protected GSUser createUserAndGroup(String base) {

        UserGroup group = createRole(base);
        return createUser(base, group);
    }

    protected MultiPolygon parseMultiPolygon(String wkt) {
        try {
            WKTReader wktReader = new WKTReader();
            MultiPolygon the_geom = (MultiPolygon) wktReader.read(wkt);
            the_geom.setSRID(4326);
            return the_geom;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Unparsabe WKT", e);
        }
    }
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.services.UserGroupAdminService;
import org.geoserver.geofence.services.GFUserAdminService;
import org.geoserver.geofence.services.RuleAdminService;
import org.geoserver.geofence.services.RuleReaderService;
import org.geoserver.geofence.services.InstanceAdminService;
import org.geoserver.geofence.services.UserAdminService;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.geoserver.geofence.core.model.GFUser;
import org.geoserver.geofence.core.model.GSInstance;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.services.dto.ShortGroup;
import org.geoserver.geofence.services.dto.ShortRule;
import org.geoserver.geofence.services.dto.ShortUser;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class ServiceTestBase extends TestCase {

    protected final Logger LOGGER = LogManager.getLogger(getClass());

    protected static UserAdminService userAdminService;
    protected static GFUserAdminService gfUserAdminService;
    protected static UserGroupAdminService userGroupAdminService;
    protected static InstanceAdminService instanceAdminService;
    protected static RuleAdminService ruleAdminService;
    protected static RuleReaderService ruleReaderService;

    protected static ClassPathXmlApplicationContext ctx = null;

    public ServiceTestBase() {
//        LOGGER = LogManager.getLogger(getClass());

        synchronized(ServiceTestBase.class) {
            if(ctx == null) {
                String[] paths = {
                        "classpath*:applicationContext.xml"
//                         ,"applicationContext-test.xml"
                };
                ctx = new ClassPathXmlApplicationContext(paths);

                userAdminService     = (UserAdminService)ctx.getBean("userAdminService");
                gfUserAdminService   = (GFUserAdminService)ctx.getBean("gfUserAdminService");
                userGroupAdminService  = (UserGroupAdminService)ctx.getBean("userGroupAdminService");
                instanceAdminService = (InstanceAdminService)ctx.getBean("instanceAdminService");
                ruleAdminService     = (RuleAdminService)ctx.getBean("ruleAdminService");
                ruleReaderService    = (RuleReaderService)ctx.getBean("ruleReaderService");
            }
        }
    }

    @Override
    protected void setUp() throws Exception {
        LOGGER.info("############################################ Running " + getClass().getSimpleName() + "::" + getName() );
        super.setUp();
        removeAll();
    }

    public void testCheckServices() {
        assertNotNull(userAdminService);
        assertNotNull(gfUserAdminService);
        assertNotNull(userGroupAdminService);
        assertNotNull(instanceAdminService);
        assertNotNull(ruleAdminService);
    }

    protected void removeAll() throws NotFoundServiceEx {
        LOGGER.info("***** removeAll()");
        removeAllRules();
        removeAllUsers();
        removeAllGRUsers();
        removeAllUserGroups();
        removeAllInstances();
    }

    protected void removeAllRules() throws NotFoundServiceEx {
        List<ShortRule> list = ruleAdminService.getAll();
        for (ShortRule item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = ruleAdminService.delete(item.getId());
            assertTrue("Rule not removed", ret);
        }

        assertEquals("Rules have not been properly deleted", 0, ruleAdminService.getCountAll());
    }

    protected void removeAllUsers() throws NotFoundServiceEx {
        List<ShortUser> list = userAdminService.getList(null,null,null);
        for (ShortUser item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = userAdminService.delete(item.getId());
            assertTrue("User not removed", ret);
        }

        assertEquals("Users have not been properly deleted", 0, userAdminService.getCount(null));
    }

    protected void removeAllGRUsers() throws NotFoundServiceEx {
        List<ShortUser> list = gfUserAdminService.getList(null,null,null);
        for (ShortUser item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = gfUserAdminService.delete(item.getId());
            assertTrue("User not removed", ret);
        }

        assertEquals("GRUsers have not been properly deleted", 0, gfUserAdminService.getCount(null));
    }

    protected void removeAllUserGroups() throws NotFoundServiceEx {
        List<ShortGroup> list = userGroupAdminService.getList(null,null,null);
        for (ShortGroup item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = userGroupAdminService.delete(item.getId());
            assertTrue("UserGroup not removed", ret);
        }

        assertEquals("UserGroups have not been properly deleted", 0, userGroupAdminService.getCount(null));
    }

    protected void removeAllInstances() throws NotFoundServiceEx {
        List<GSInstance> list = instanceAdminService.getAll();
        for (GSInstance item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = instanceAdminService.delete(item.getId());
            assertTrue("GSInstance not removed", ret);
        }

        assertEquals("Instances have not been properly deleted", 0, instanceAdminService.getCount(null));
    }

    protected GSUser createUser(String base, UserGroup ... groups) {

        GSUser user = new GSUser();
        user.setName( base );
        user.getGroups().addAll(Arrays.asList(groups));
        userAdminService.insert(user);
        return user;
    }

    protected GFUser createGFUser(String base) {

        GFUser user = new GFUser();
        user.setName( base );
        gfUserAdminService.insert(user);
        return user;
    }

    protected UserGroup createUserGroup(String base) {

        ShortGroup sgroup = new ShortGroup();
        sgroup.setName(base);
        long id = userGroupAdminService.insert(sgroup);
        try {
            return userGroupAdminService.get(id);
        } catch (NotFoundServiceEx ex) {
            throw new RuntimeException("Should never happen ("+id+")", ex);
        }
    }

    protected GSUser createUserAndGroup(String base) {

        UserGroup group = createUserGroup(base);
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

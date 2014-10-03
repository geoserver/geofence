/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.servicetest;

import org.geoserver.geofence.core.model.GSInstance;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.LayerAttribute;
import org.geoserver.geofence.core.model.LayerDetails;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.core.model.RuleLimits;
import org.geoserver.geofence.core.model.enums.AccessType;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.services.InstanceAdminService;
import org.geoserver.geofence.services.UserGroupAdminService;
import org.geoserver.geofence.services.RuleAdminService;
import org.geoserver.geofence.services.RuleReaderService;
import org.geoserver.geofence.services.UserAdminService;
import org.geoserver.geofence.services.dto.AccessInfo;
import org.geoserver.geofence.services.dto.ShortGroup;
import org.geoserver.geofence.services.dto.ShortRule;
import org.geoserver.geofence.services.dto.ShortUser;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.io.WKTReader;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.RuleFilter.SpecialFilterType;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class MainTest implements InitializingBean, ApplicationContextAware {

    private final static Logger LOGGER = LogManager.getLogger(MainTest.class);

    private XmlWebApplicationContext applicationContext;

    private UserAdminService userAdminService;
    private UserGroupAdminService userGroupAdminService;
    private InstanceAdminService instanceAdminService;
    private RuleAdminService ruleAdminService;
    private RuleReaderService ruleReaderService;

    protected final static String MULTIPOLYGONWKT = "MULTIPOLYGON(((48 62, 48 63, 49 63, 49 62, 48 62)))";

    public void afterPropertiesSet() throws Exception {
        /***********************************************************************
         *
         * WARNING, READ CAREFULLY BEFORE CHANGING ANYTHING IN THIS SETUP
         *
         * This test setup is used for the ResorceAccessManager integration tests,
         * which expect the webtest to be running in Jetty with these exact contents.
         * If you need to add more or modify the contents please also make sure
         * you're not breaking the build in those tests.
         * If you blinding modify the class and I find the tests got broken
         * this is the destiny that awaits you:
         * http://en.wikipedia.org/wiki/Impalement
         *
         * Signed: Andrea Vlad Dracul Aime
         *
         ***********************************************************************/

        LOGGER.info("===== RESETTING DB DATA =====");
        removeAll();

        LOGGER.info("===== Creating Profiles (not actually needed while testing GS) =====");
        ShortGroup shortProfile = new ShortGroup();
        shortProfile.setName("basic");
        long pid1 = userGroupAdminService.insert(shortProfile);
        UserGroup p1 = userGroupAdminService.get(pid1);

        ShortGroup shortProfile2 = new ShortGroup();
        shortProfile2.setName("advanced");
        long pid2 = userGroupAdminService.insert(shortProfile2);
        UserGroup p2 = userGroupAdminService.get(pid2);


        LOGGER.info("===== Creating Users =====");
        GSUser cite = createUser("cite");
        cite.getGroups().add(p1);
        userAdminService.insert(cite);

        GSUser wmsUser = createUser("wmsuser");
        wmsUser.getGroups().add(p1);
        userAdminService.insert(wmsUser);

        GSUser areaUser = createUser("area");
        areaUser.getGroups().add(p1);
        userAdminService.insert(areaUser);

        GSUser uStates = createUser("u-states");
        uStates.getGroups().add(p1);
        userAdminService.insert(uStates);

        LOGGER.info("===== Creating Rules =====");

        LayerDetails ld1 = new LayerDetails();
        ld1.getAllowedStyles().add("style1");
        ld1.getAllowedStyles().add("style2");
        ld1.getAttributes().add(new LayerAttribute("attr1", AccessType.NONE));
        ld1.getAttributes().add(new LayerAttribute("attr2", AccessType.READONLY));
        ld1.getAttributes().add(new LayerAttribute("attr3", AccessType.READWRITE));

        int priority = 0;

        /* Cite user rules */
        // allow user cite full control over the cite workspace
        ruleAdminService.insert(new Rule(priority++, cite, null, null, null, null, null, "cite", null, GrantType.ALLOW));
        // allow only getmap, getcapatbilities and reflector usage on workspace sf
        ruleAdminService.insert((new Rule(priority++, cite, null, null, null, "wms", "GetMap", "sf", null, GrantType.ALLOW)));
        ruleAdminService.insert((new Rule(priority++, cite, null, null, null, "wms", "GetCapabilities", "sf", null, GrantType.ALLOW)));
        ruleAdminService.insert((new Rule(priority++, cite, null, null, null, "wms", "reflect", "sf", null, GrantType.ALLOW)));
        // allow only GetMap and GetFeature the topp workspace

        /* wms user rules */
        ruleAdminService.insert((new Rule(priority++, wmsUser, null, null, null, "wms", null, null, null, GrantType.ALLOW)));

        /* all powerful but only in a restricted area */
        Rule areaRestriction = new Rule(priority++, areaUser, null, null, null, null, null, null, null, GrantType.LIMIT);
        RuleLimits limits = new RuleLimits();
        limits.setAllowedArea((MultiPolygon) new WKTReader().read(MULTIPOLYGONWKT));
        long ruleId = ruleAdminService.insert(areaRestriction);
        ruleAdminService.setLimits(ruleId, limits);
        ruleAdminService.insert((new Rule(priority++, areaUser, null, null, null, null, null, null, null, GrantType.ALLOW)));

        /* some users for interactive testing with the default data directory */
        // uStates can do whatever, but only on topp:states
        ruleAdminService.insert(new Rule(priority++, uStates, null, null, null, null, null, "topp", "states", GrantType.ALLOW));

        // deny everything else
        ruleAdminService.insert(new Rule(priority++, null, null, null,  null, null, null, null, null, GrantType.DENY));
        new Thread(new Runnable() {

            @Override
            public void run() {
                boolean success = false;
                int cnt = 5;

                while( ! success && cnt-->0) {
                    try{
                        LOGGER.info("Waiting 5 secs...");
                        Thread.sleep(5000);

                        LOGGER.info("Trying creating spring remoting client...");
                        instantiateAndRunSpringRemoting();

                        success = true;

                    } catch (InterruptedException ex) {
                    }catch(Exception e) {
                        LOGGER.warn("Failed creating spring remoting client..." + e.getMessage());
                    }
                }
            }
        }).start();


        try {
            LOGGER.info("===== User List =====");

            List<ShortUser> users = userAdminService.getList(null,null,null);
            for (ShortUser loop : users) {
                LOGGER.info("   User -> " + loop);
            }

            LOGGER.info("===== Rules =====");
            List<ShortRule> rules = ruleAdminService.getAll();
            for (ShortRule shortRule : rules) {
                LOGGER.info("   Rule -> " + shortRule);
            }



        } finally {
        }
    }


    public void instantiateAndRunSpringRemoting() {
        HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean = new HttpInvokerProxyFactoryBean();
        httpInvokerProxyFactoryBean.setServiceInterface(org.geoserver.geofence.services.RuleReaderService.class);
        httpInvokerProxyFactoryBean.setServiceUrl("http://localhost:9191/geofence/remoting/RuleReader");
        httpInvokerProxyFactoryBean.afterPropertiesSet();
        RuleReaderService rrs = (RuleReaderService) httpInvokerProxyFactoryBean.getObject();

        RuleFilter filter1 = new RuleFilter(SpecialFilterType.DEFAULT, true)
                .setUser("pippo")
                .setInstance("gs1")
                .setService("WMS");
        AccessInfo accessInfo = rrs.getAccessInfo(filter1);
        LOGGER.info(accessInfo);

        RuleFilter filter2 = new RuleFilter(SpecialFilterType.DEFAULT, true)
                .setUser("pippo")
                .setInstance("gs1")
                .setService("WCS");
        AccessInfo accessInfo2 = rrs.getAccessInfo(filter2);
        LOGGER.info(accessInfo2);
    }

    //==========================================================================

    protected GSUser createUser(String baseName) {
        GSUser user = new GSUser();
        user.setName(baseName);
        return user;
    }

    //==========================================================================

    protected void removeAll() throws NotFoundServiceEx {
        LOGGER.info("***** removeAll()");
        removeAllRules();
        removeAllUsers();
        removeAllProfiles();
        removeAllInstances();
    }

    protected void removeAllRules() throws NotFoundServiceEx {
        List<ShortRule> list = ruleAdminService.getAll();
        for (ShortRule item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = ruleAdminService.delete(item.getId());
            if(!ret)
                throw new IllegalStateException("Rule not removed");
        }

        if( ruleAdminService.getCountAll() != 0)
                throw new IllegalStateException("Rules have not been properly deleted");
    }

    protected void removeAllUsers() throws NotFoundServiceEx {
        List<ShortUser> list = userAdminService.getList(null,null,null);
        for (ShortUser item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = userAdminService.delete(item.getId());
            if(!ret)
                throw new IllegalStateException("User not removed");
        }

        if( userAdminService.getCount(null) != 0)
                throw new IllegalStateException("Users have not been properly deleted");
    }

    protected void removeAllProfiles() throws NotFoundServiceEx {
        List<ShortGroup> list = userGroupAdminService.getList(null,null,null);
        for (ShortGroup item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = userGroupAdminService.delete(item.getId());
            if(!ret)
                throw new IllegalStateException("Group not removed");
        }

        if( userGroupAdminService.getCount(null) != 0)
                throw new IllegalStateException("Groups have not been properly deleted");
    }

    protected void removeAllInstances() throws NotFoundServiceEx {
        List<GSInstance> list = instanceAdminService.getAll();
        for (GSInstance item : list) {
            LOGGER.info("Removing " + item);
            boolean ret = instanceAdminService.delete(item.getId());
            if(!ret)
                throw new IllegalStateException("GSInstance not removed");

        }

        if( instanceAdminService.getCount(null) != 0)
                throw new IllegalStateException("Instances have not been properly deleted");
    }

    //==========================================================================

    public void setInstanceAdminService(InstanceAdminService instanceAdminService) {
        this.instanceAdminService = instanceAdminService;
    }

    public void setUserGroupAdminService(UserGroupAdminService userGroupAdminService) {
        this.userGroupAdminService = userGroupAdminService;
    }

    public void setRuleAdminService(RuleAdminService ruleAdminService) {
        this.ruleAdminService = ruleAdminService;
    }

    public void setUserAdminService(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    public void setRuleReaderService(RuleReaderService ruleReaderService) {
        this.ruleReaderService = ruleReaderService;
    }


    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        this.applicationContext = (XmlWebApplicationContext)ac;

    }

}

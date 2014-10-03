/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.impl;

import org.geoserver.geofence.services.dto.ShortGroup;
import org.geoserver.geofence.services.rest.RESTRuleService;
import org.geoserver.geofence.services.rest.RESTUserGroupService;
import org.geoserver.geofence.services.rest.RESTUserService;
import org.geoserver.geofence.services.rest.model.RESTOutputRule;
import org.geoserver.geofence.services.rest.model.RESTOutputRuleList;
import org.geoserver.geofence.services.rest.model.RESTShortUser;
import org.geoserver.geofence.services.rest.model.RESTShortUserList;
import org.geoserver.geofence.services.rest.model.config.RESTFullUserGroupList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static org.junit.Assert.*;
import org.junit.rules.TestName;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public abstract class RESTBaseTest {
    private static final Logger LOGGER = LogManager.getLogger(RESTBaseTest.class);

    @org.junit.Rule public TestName name = new TestName();

    protected static ClassPathXmlApplicationContext ctx = null;

    protected static RESTUserService restUserService;
    protected static RESTUserGroupService restUserGroupService;
    protected static RESTRuleService restRuleService;

    public RESTBaseTest() {

        synchronized(RESTBaseTest.class) {
            if(ctx == null) {
                String[] paths = {
                        "classpath*:applicationContext.xml"
//                         ,"applicationContext-test.xml"
                };
                ctx = new ClassPathXmlApplicationContext(paths);


                for(String name : ctx.getBeanDefinitionNames()) {
                    if(name.startsWith("rest") )
                        LOGGER.warn("  BEAN ===> " + name);                    
                }

                restUserService       = (RESTUserService)ctx.getBean("restUserService");
                restUserGroupService  = (RESTUserGroupService)ctx.getBean("restUserGroupService");
                restRuleService       = (RESTRuleService)ctx.getBean("restRuleService");
            }
            
            assertNotNull(restUserService);
            assertNotNull(restUserGroupService);
            assertNotNull(restRuleService);
        }
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void before() throws Exception {
        LOGGER.info("");
        LOGGER.info("============================== TEST " + name.getMethodName());
        LOGGER.info("");

        RESTOutputRuleList rules = restRuleService.get(null, null, false, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        for (RESTOutputRule rule : rules) {
            LOGGER.warn("Removing " + rule);
            restRuleService.delete(rule.getId());
        }


        RESTShortUserList users = restUserService.getList(null, null, null);
        for (RESTShortUser user : users) {
            LOGGER.warn("Removing " + user);
            restUserService.delete(user.getId(), true);            
        }
        RESTFullUserGroupList userGroups = restUserGroupService.getList(null, null, null);
        for (ShortGroup group : userGroups) {
            LOGGER.warn("Removing " + group);
            restUserGroupService.delete(group.getId(), true);
        }

        LOGGER.info("----------------- ending cleaning tasks ------------- ");

    }


}

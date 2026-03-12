/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.db.BaseContainerTest;
import org.geofence.web.rest.api.interfaces.RESTRuleService;
import org.geofence.web.rest.api.interfaces.RESTUserGroupService;
import org.geofence.web.rest.api.interfaces.RESTUserService;
import org.geofence.web.rest.api.interfaces.params.RESTRuleFilter;
import org.geofence.web.rest.api.model.RESTOutputGroup;
import org.geofence.web.rest.api.model.RESTOutputRule;
import org.geofence.web.rest.api.model.RESTOutputRuleList;
import org.geofence.web.rest.api.model.RESTShortUser;
import org.geofence.web.rest.api.model.RESTShortUserList;
import org.geofence.web.rest.api.model.config.RESTFullUserGroupList;
import org.geofence.web.rest.config.GeofenceRESTConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/** @author ETj (etj at geo-solutions.it) */
@SpringJUnitConfig(classes = GeofenceRESTConfig.class)
public abstract class RESTBaseTest extends BaseContainerTest {
    private static final Logger LOGGER = LogManager.getLogger(RESTBaseTest.class);

    @Autowired
    protected RESTUserService restUserService;

    @Autowired
    protected RESTUserGroupService restUserGroupService;

    @Autowired
    protected RESTRuleService restRuleService;

    public RESTBaseTest() {}

    @BeforeEach
    public void before(TestInfo testInfo) throws Exception {
        String methodName = testInfo.getTestMethod().get().getName();
        LOGGER.info("");
        LOGGER.info("============================== TEST " + methodName);
        LOGGER.info("");

        RESTRuleFilter rf = new RESTRuleFilter();
        RESTOutputRuleList rules = restRuleService.get(null, null, false, rf);
        for (RESTOutputRule rule : rules) {
            LOGGER.warn("Removing " + rule);
            restRuleService.delete(rule.getId());
        }

        RESTShortUserList users = restUserService.getList(null, null, null);
        for (RESTShortUser user : users) {
            LOGGER.warn("Removing " + user);
            restUserService.delete(user.getUserName(), true);
        }
        RESTFullUserGroupList roles = restUserGroupService.getList(null, null, null);
        for (RESTOutputGroup role : roles) {
            LOGGER.warn("Removing " + role);
            restUserGroupService.delete(role.getName(), true);
        }

        LOGGER.info("----------------- ending cleaning tasks ------------- ");
    }
}

/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.geofence.web.rest.api.interfaces.RESTRuleReaderService;
import org.geofence.web.rest.api.interfaces.params.RESTRuleFilter;
import org.geofence.web.rest.api.model.RESTInputRule;
import org.geofence.web.rest.api.model.RESTRulePosition;
import org.geofence.web.rest.api.model.RESTShortRuleList;
import org.geofence.web.rest.api.model.enums.RESTGrantType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** @author ETj (etj at geo-solutions.it) */
public class RESTRuleReaderServiceImplTest extends RESTBaseTest {

    @Autowired
    RESTRuleReaderService restRuleReaderService;

    private Long insertRule(String userName, String workspace, String layer, RESTGrantType grant) {
        RESTInputRule rule = new RESTInputRule();
        rule.setPosition(new RESTRulePosition(RESTRulePosition.RESTPositionReference.offsetFromTop, 0));
        rule.setGrant(grant);
        rule.setUsername(userName);
        rule.setWorkspace(workspace);
        rule.setLayer(layer);
        return (Long) restRuleService.insert(rule).getBody();
    }

    @Test
    public void testGetAccessInfoAllow() {
        insertRule("user0", "topp", "states", RESTGrantType.ALLOW);

        RESTRuleFilter filter = new RESTRuleFilter();
        filter.userName = "user0";
        filter.workspace = "topp";
        filter.layer = "states";

        var accessInfo = restRuleReaderService.getAccessInfo(filter);
        assertNotNull(accessInfo);
        assertEquals(RESTGrantType.ALLOW, accessInfo.getGrant());
        assertFalse(accessInfo.isAdminRights());
    }

    @Test
    public void testGetAccessInfoDenyWhenNoMatchingRule() {
        RESTRuleFilter filter = new RESTRuleFilter();
        filter.userName = "someone-with-no-rules";

        var accessInfo = restRuleReaderService.getAccessInfo(filter);
        assertNotNull(accessInfo);
        assertEquals(RESTGrantType.DENY, accessInfo.getGrant());
    }

    @Test
    public void testGetAdminAuthorization() {
        RESTRuleFilter filter = new RESTRuleFilter();
        filter.userName = "user0";
        filter.workspace = "topp";

        var accessInfo = restRuleReaderService.getAdminAuthorization(filter);
        assertNotNull(accessInfo);
        assertEquals(RESTGrantType.ALLOW, accessInfo.getGrant());
        assertFalse(accessInfo.isAdminRights());
    }

    @Test
    public void testGetMatchingRules() {
        insertRule("user0", "topp", "states", RESTGrantType.ALLOW);
        insertRule("user0", "topp", "roads", RESTGrantType.LIMIT);

        RESTRuleFilter filter = new RESTRuleFilter();
        filter.userName = "user0";
        filter.workspace = "topp";

        RESTShortRuleList rules = restRuleReaderService.getMatchingRules(filter);
        assertEquals(2, rules.getRuleList().size());
    }
}

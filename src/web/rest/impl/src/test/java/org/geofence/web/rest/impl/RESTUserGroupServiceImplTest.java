/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.web.rest.api.exception.ConflictRestEx;
import org.geofence.web.rest.api.model.RESTInputGroup;
import org.geofence.web.rest.api.model.RESTInputRule;
import org.geofence.web.rest.api.model.RESTInputUser;
import org.geofence.web.rest.api.model.RESTOutputUser;
import org.geofence.web.rest.api.model.RESTRulePosition;
import org.geofence.web.rest.api.model.enums.RESTGrantType;
import org.geofence.web.rest.api.model.util.IdName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

/** @author ETj (etj at geo-solutions.it) */
public class RESTUserGroupServiceImplTest extends RESTBaseTest {
    private static final Logger LOGGER = LogManager.getLogger(RESTUserGroupServiceImplTest.class);

    @Test
    public void testInsert() {
        RESTInputGroup group = new RESTInputGroup();
        group.setName("g1");
        ResponseEntity<Long> res = restUserGroupService.insert(group);
        long gid1 = res.getBody();

        RESTInputUser user = new RESTInputUser();
        user.setName("user0");
        user.setEnabled(Boolean.TRUE);
        user.setGroups(new ArrayList<>());
        user.getGroups().add(new IdName("g1"));

        ResponseEntity<Long> userResp = restUserService.insert(user);
        Long id = userResp.getBody();

        {
            RESTOutputUser out = restUserService.get("user0");
            assertNotNull(out);
            assertEquals("user0", out.getName());
        }
    }

    @Test
    public void testInsertDup() {

        {
            RESTInputGroup group1 = new RESTInputGroup();
            group1.setName("g1");
            restUserGroupService.insert(group1);
        }

        LOGGER.info("Inserting dup");
        try {
            RESTInputGroup group2 = new RESTInputGroup();
            group2.setName("g1");
            restUserGroupService.insert(group2);
            fail("409 not trapped");
        } catch (ConflictRestEx e) {
            LOGGER.info("Exception properly trapped");
        }
    }

    @Test
    public void testDeleteUnreferencedGroupNoCascade() {
        RESTInputGroup group = new RESTInputGroup();
        group.setName("g1");
        restUserGroupService.insert(group);

        // a catch-all rule (no rolename) must NOT count as referencing "g1" - the reference check needs
        // includeDefault=false on the *role* filter component, not (as the bug had it) on the user one
        RESTInputRule catchAll = new RESTInputRule();
        catchAll.setGrant(RESTGrantType.DENY);
        catchAll.setPosition(new RESTRulePosition(RESTRulePosition.RESTPositionReference.offsetFromBottom, 0));
        restRuleService.insert(catchAll);

        // no rule specifically references "g1" - a non-cascade delete must succeed, not falsely 409
        restUserGroupService.delete("g1", false);

        assertEquals(0, restUserGroupService.count("g1"));
    }

    @Test
    public void testDeleteReferencedGroupNoCascadeConflicts() {
        RESTInputGroup group = new RESTInputGroup();
        group.setName("g1");
        restUserGroupService.insert(group);

        RESTInputRule rule = new RESTInputRule();
        rule.setRolename("g1");
        rule.setGrant(RESTGrantType.ALLOW);
        rule.setPosition(new RESTRulePosition(RESTRulePosition.RESTPositionReference.offsetFromBottom, 0));
        restRuleService.insert(rule);

        try {
            restUserGroupService.delete("g1", false);
            fail("409 not trapped");
        } catch (ConflictRestEx e) {
            LOGGER.info("Exception properly trapped");
        }
    }
}

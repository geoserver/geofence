/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.impl;

import org.geoserver.geofence.services.rest.model.RESTInputUser;
import org.geoserver.geofence.services.rest.model.RESTInputRule;
import org.geoserver.geofence.services.rest.model.RESTOutputRule;
import org.geoserver.geofence.services.rest.model.RESTInputGroup;
import org.geoserver.geofence.services.rest.model.RESTLayerConstraints;
import org.geoserver.geofence.core.model.LayerAttribute;
import org.geoserver.geofence.core.model.enums.AccessType;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.services.rest.exception.BadRequestRestEx;
import org.geoserver.geofence.services.rest.exception.ConflictRestEx;
import org.geoserver.geofence.services.rest.model.util.IdName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RESTRuleServiceImplTest extends RESTBaseTest {
    private static final Logger LOGGER = LogManager.getLogger(RESTRuleServiceImplTest.class);


    @Test
    public void testInsert() {
        RESTInputGroup group = new RESTInputGroup();
        group.setName("g1");
        Response res = restUserGroupService.insert(group);
        long gid1 = (Long)res.getEntity();


        RESTInputUser user = new RESTInputUser();
        user.setName("user0");
        user.setEnabled(Boolean.TRUE);
        user.setGroups(new ArrayList<IdName>());
        user.getGroups().add(new IdName("g1"));
        Response userResp = restUserService.insert(user);
        Long id = (Long)userResp.getEntity();

        RESTInputRule rule = new RESTInputRule();
        rule.setUser(new IdName("user0"));

        try{
            restRuleService.insert(rule).getEntity();
            fail("Missing position not trapped");
        } catch (BadRequestRestEx e) {
            LOGGER.info("Exception properly trapped");
        }

        rule.setPosition(new RESTInputRule.RESTRulePosition(RESTInputRule.RESTRulePosition.RulePosition.offsetFromTop, 0));        
        rule.setGrant(GrantType.ALLOW);

        Long rid = (Long)restRuleService.insert(rule).getEntity();
        assertNotNull(rid);

        {
            RESTOutputRule out = restRuleService.get(rid);
            assertNotNull(out);
            assertEquals("user0", out.getUser().getName());
            assertNull(out.getConstraints());
        }
    }

    @Test
    public void testMissingLayerOnConstraints() {

        RESTInputRule rule = new RESTInputRule();
        rule.setPosition(new RESTInputRule.RESTRulePosition(RESTInputRule.RESTRulePosition.RulePosition.offsetFromTop, 0));
        rule.setGrant(GrantType.ALLOW);

        RESTLayerConstraints constraints = new RESTLayerConstraints();
        rule.setConstraints(constraints);

        try {
            restRuleService.insert(rule).getEntity();
            fail("Missing layer not trapped");
        } catch (BadRequestRestEx e) {
            LOGGER.info("Exception properly trapped");
        }
        rule.setLayer("l0");
        Long rid = (Long)restRuleService.insert(rule).getEntity();
        assertNotNull(rid);
    }

    @Test
    public void testUpdateToNull() {
        RESTInputRule rule = new RESTInputRule();
        rule.setPosition(new RESTInputRule.RESTRulePosition(RESTInputRule.RESTRulePosition.RulePosition.offsetFromTop, 0));
        rule.setGrant(GrantType.ALLOW);
        rule.setService("s0");
        rule.setWorkspace("w0");
        rule.setLayer("l0");
        Long rid = (Long)restRuleService.insert(rule).getEntity();
        assertNotNull(rid);

        {
            RESTOutputRule out = restRuleService.get(rid);
            assertNotNull(out);
            assertEquals("S0", out.getService()); // upper case?!?
            assertEquals("w0", out.getWorkspace());
            assertEquals("l0", out.getLayer());
            
            RESTInputRule up = new RESTInputRule();
            up.setLayer("l1");
            up.setWorkspace("");

            restRuleService.update(rid, up);
        }
        {
            RESTOutputRule out = restRuleService.get(rid);
            assertNotNull(out);
            assertEquals("S0", out.getService());  // not changed
            assertNull(out.getWorkspace());       // nulled
            assertEquals("l1", out.getLayer());  // changed
        }
    }

    @Test
    public void testEmptyDetails() {
        RESTInputRule rule = new RESTInputRule();
        rule.setPosition(new RESTInputRule.RESTRulePosition(RESTInputRule.RESTRulePosition.RulePosition.offsetFromTop, 0));
        rule.setGrant(GrantType.ALLOW);
        rule.setLayer("l0");
        Long rid = (Long)restRuleService.insert(rule).getEntity();
        assertNotNull(rid);

        {
            RESTOutputRule out = restRuleService.get(rid);
            assertNotNull(out);
            LOGGER.debug("Constraints: "+out.getConstraints());
            assertNull(out.getConstraints());

            // Try to update not existing constr

            RESTInputRule up = new RESTInputRule();
            RESTLayerConstraints constraints = new RESTLayerConstraints();
            constraints.setAllowedStyles(new HashSet<String>(Arrays.asList("r1","r2","r3")));
            up.setConstraints(constraints);
            restRuleService.update(rid, up);
        }
        {
            RESTOutputRule out = restRuleService.get(rid);
            assertNotNull(out);
            LOGGER.debug("Constraints: "+out.getConstraints());
            assertNotNull(out.getConstraints());
            assertNotNull(out.getConstraints().getAllowedStyles());
            assertEquals(new HashSet<String>(Arrays.asList("r1","r2","r3")), out.getConstraints().getAllowedStyles());
        }
    }

    @Test
    public void testDetails() {

        Long rid;

        {
            RESTInputRule rule = new RESTInputRule();
            rule.setPosition(new RESTInputRule.RESTRulePosition(RESTInputRule.RESTRulePosition.RulePosition.offsetFromTop, 0));
            rule.setGrant(GrantType.ALLOW);
            rule.setWorkspace("w0");
            rule.setLayer("l0");

            RESTLayerConstraints constraints = new RESTLayerConstraints();
            constraints.setAllowedStyles(new HashSet<String>(Arrays.asList("s1","s2")));
            rule.setConstraints(constraints);

            rid = (Long)restRuleService.insert(rule).getEntity();
            assertNotNull(rid);
        }

        {
            RESTOutputRule out = restRuleService.get(rid);
            assertNotNull(out);
            LOGGER.debug("Constraints " + out.getConstraints());
            assertNotNull(out.getConstraints());
            assertNotNull(out.getConstraints().getAllowedStyles());
            assertEquals(2, out.getConstraints().getAllowedStyles().size());

            // update allowedStyles only
            RESTInputRule up = new RESTInputRule();
            RESTLayerConstraints constraints = new RESTLayerConstraints();
            constraints.setAllowedStyles(new HashSet<String>(Arrays.asList("r1","r2","r3")));
            up.setConstraints(constraints);
            restRuleService.update(rid, up);
        }

        {
            RESTOutputRule out = restRuleService.get(rid);
            assertNotNull(out);
            assertEquals("l0", out.getLayer());
            LOGGER.debug("Constraints " + out.getConstraints());
            assertNotNull(out.getConstraints());
            assertNotNull(out.getConstraints().getAllowedStyles());
            assertEquals(new HashSet<String>(Arrays.asList("r1","r2","r3")), out.getConstraints().getAllowedStyles());

            RESTInputRule up = new RESTInputRule();
            RESTLayerConstraints constraints = new RESTLayerConstraints();
            constraints.setAllowedStyles(new HashSet<String>(Arrays.asList("r1","r2")));
            up.setConstraints(constraints);
            restRuleService.update(rid, up);
        }
    }

    @Test
    public void testAttribs() {

        Long rid;
        int ccnt = 0;

        {
            RESTInputRule rule = new RESTInputRule();
            rule.setPosition(new RESTInputRule.RESTRulePosition(RESTInputRule.RESTRulePosition.RulePosition.fixedPriority, 42));
            rule.setGrant(GrantType.ALLOW);
            rule.setWorkspace("w0");
            rule.setLayer("l0");

            RESTLayerConstraints constraints = new RESTLayerConstraints();
            constraints.setAttributes(new HashSet<LayerAttribute>(Arrays.asList(
                    new LayerAttribute("attr1", AccessType.NONE),
                    new LayerAttribute("attr2", AccessType.READWRITE)
                    )));
            rule.setConstraints(constraints);

            rid = (Long)restRuleService.insert(rule).getEntity();
            assertNotNull(rid);
        }

        {
            LOGGER.debug("=== CHECK "+ ccnt++);
            RESTOutputRule out = restRuleService.get(rid);
            assertNotNull(out);
            LOGGER.debug("Constraints " + out.getConstraints());
            assertNotNull(out.getConstraints());
            assertEquals(new HashSet<LayerAttribute>(Arrays.asList(
                                new LayerAttribute("attr1", AccessType.NONE),
                                new LayerAttribute("attr2", AccessType.READWRITE)
                                )),
                    out.getConstraints().getAttributes());
        }

        String allowedArea = "MULTIPOLYGON (((4146.5 1301.4, 4147.5 1301.1, 4147.8 1301.4, 4146.5 1301.4)))";

        // update allowedStyles only
        {
            RESTInputRule up = new RESTInputRule();
            RESTLayerConstraints constraints = new RESTLayerConstraints();
            constraints.setRestrictedAreaWkt(allowedArea);

            up.setConstraints(constraints);
            restRuleService.update(rid, up);
        }

        {
            LOGGER.debug("=== CHECK "+ ccnt++);
            RESTOutputRule out = restRuleService.get(rid);
            assertNotNull(out);
            LOGGER.debug("Constraints " + out.getConstraints());
            assertNotNull(out.getConstraints());
            assertEquals(allowedArea, out.getConstraints().getRestrictedAreaWkt());
            // check that attribs have not been changed
            assertEquals(new HashSet<LayerAttribute>(Arrays.asList(
                                new LayerAttribute("attr1", AccessType.NONE),
                                new LayerAttribute("attr2", AccessType.READWRITE)
                                )),
                    out.getConstraints().getAttributes());
        }


    }

}

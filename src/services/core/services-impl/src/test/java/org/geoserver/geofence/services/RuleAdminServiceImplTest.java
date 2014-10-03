/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.core.model.LayerAttribute;
import org.geoserver.geofence.core.model.LayerDetails;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.core.model.RuleLimits;
import org.geoserver.geofence.core.model.enums.AccessType;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.RuleFilter.SpecialFilterType;
import org.geoserver.geofence.services.dto.ShortRule;
import org.geoserver.geofence.services.exception.BadRequestServiceEx;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;
import java.util.Arrays;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RuleAdminServiceImplTest extends ServiceTestBase {

    public RuleAdminServiceImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testInsertDeleteRule() throws NotFoundServiceEx {

        UserGroup profile = createUserGroup(getName());
        Rule rule = new Rule();
        rule.setUserGroup(profile);
        rule.setAccess(GrantType.ALLOW);
        ruleAdminService.insert(rule);
        ruleAdminService.get(rule.getId()); // will throw if not found
        assertTrue("Could not delete rule", ruleAdminService.delete(rule.getId()));
    }

    @Test
    public void testUpdateRule() throws Exception {
        UserGroup p1 = createUserGroup("p1");
        UserGroup p2 = createUserGroup("p2");

        Rule rule = new Rule(10, null, p1,null,null, "s1", "r1", "w1", "l1", GrantType.ALLOW);
        ruleAdminService.insert(rule);

        {
            Rule loaded = ruleAdminService.get(rule.getId());
            assertNotNull(loaded);

            assertEquals("p1", loaded.getUserGroup().getName());
            assertEquals("S1", loaded.getService());
            assertEquals("l1", loaded.getLayer());

            loaded.setUserGroup(p2);
            loaded.setService("s2");
            loaded.setLayer("l2");

            ruleAdminService.update(loaded);
        }
        {
            Rule loaded = ruleAdminService.get(rule.getId());
            assertNotNull(loaded);

            assertEquals("p2", loaded.getUserGroup().getName());
            assertEquals("S2", loaded.getService());
            assertEquals("l2", loaded.getLayer());
        }
    }

    @Test
    public void testGetAllRules() {
        assertEquals(0, ruleAdminService.getAll().size());

        UserGroup p1 = createUserGroup("p1");

        Rule r1 = new Rule(10, null, p1, null,null, "s1", "r1", "w1", "l1", GrantType.ALLOW);
        Rule r2 = new Rule(20, null, p1, null,null, "s2", "r2", "w2", "l2", GrantType.ALLOW);
        Rule r3 = new Rule(30, null, p1, null,null, "s3", "r3", "w3", "l3", GrantType.ALLOW);

        ruleAdminService.insert(r1);
        ruleAdminService.insert(r2);
        ruleAdminService.insert(r3);

        assertEquals(3, ruleAdminService.getAll().size());
    }

    @Test
    public void testGetRules() {
        assertEquals(0, ruleAdminService.count(new RuleFilter(SpecialFilterType.ANY)));

        UserGroup p1 = createUserGroup("p1");
        UserGroup p2 = createUserGroup("p2");

        Rule r1 = new Rule(10, null, p1, null,null,      "s1", "r1", "w1", "l1", GrantType.ALLOW);
        Rule r2 = new Rule(20, null, p2, null,null,      "s1", "r2", "w2", "l2", GrantType.ALLOW);
        Rule r3 = new Rule(30, null, p1, null,null,      "s3", "r3", "w3", "l3", GrantType.ALLOW);
        Rule r4 = new Rule(40, null, p1, null,null,      null, null, null, null, GrantType.ALLOW);

        ruleAdminService.insert(r1);
        ruleAdminService.insert(r2);
        ruleAdminService.insert(r3);
        ruleAdminService.insert(r4);

       assertNotNull(p1.getId());
       assertNotNull(p2.getId());

        assertEquals(4, ruleAdminService.count(new RuleFilter(SpecialFilterType.ANY)));
        assertEquals(3, ruleAdminService.count(new RuleFilter(SpecialFilterType.ANY).setUserGroup(p1.getId())));
        assertEquals(3, ruleAdminService.count(new RuleFilter(SpecialFilterType.ANY).setUserGroup(p1.getName())));
        assertEquals(1, ruleAdminService.count(new RuleFilter(SpecialFilterType.ANY).setUserGroup(p2.getId())));
        assertEquals(3, ruleAdminService.count(new RuleFilter(SpecialFilterType.ANY).setService("s1")));
        assertEquals(1, ruleAdminService.count(new RuleFilter(SpecialFilterType.ANY).setService("ZZ")));
        RuleFilter f1 = new RuleFilter(SpecialFilterType.ANY).setService("s1");
        f1.getService().setIncludeDefault(true);
        assertEquals(3, ruleAdminService.count(f1));
        f1.getService().setIncludeDefault(false);
        assertEquals(2, ruleAdminService.count(f1));
    }

    @Test
    public void testGetFixedRule() {
        assertEquals(0, ruleAdminService.count(new RuleFilter(SpecialFilterType.ANY)));

        UserGroup p1 = createUserGroup("p1");
        UserGroup p2 = createUserGroup("p2");

        Rule r1 = new Rule(10, null, p1, null,null,      "s1", "r1", "w1", "l1", GrantType.ALLOW);
        Rule r2 = new Rule(20, null, p2, null,null,      "s1", "r2", "w2", "l2", GrantType.ALLOW);
        Rule r3 = new Rule(30, null, p1, null,null,      "s3", "r3", "w3", "l3", GrantType.ALLOW);
        Rule r4 = new Rule(40, null, p1, null,null,      null, null, null, null, GrantType.ALLOW);

        ruleAdminService.insert(r1);
        ruleAdminService.insert(r2);
        ruleAdminService.insert(r3);
        ruleAdminService.insert(r4);

        assertNull(ruleAdminService.getRule(new RuleFilter(SpecialFilterType.DEFAULT)));

        RuleFilter f1 = new RuleFilter(SpecialFilterType.DEFAULT, false);
        f1.setUserGroup("p1");
        assertNotNull(ruleAdminService.getRule(f1));
        f1.setUserGroup("p99");
        assertNull(ruleAdminService.getRule(f1));
        f1.setUserGroup("p1");
        f1.setService("s3");
        assertNull(ruleAdminService.getRule(f1));
        f1.setRequest("r3");
        assertNull(ruleAdminService.getRule(f1));
        f1.setWorkspace("w3");
        assertNull(ruleAdminService.getRule(f1));
        f1.setLayer("l3");
        assertNotNull(ruleAdminService.getRule(f1));
    }


    public void testRuleLimits() throws NotFoundServiceEx {
        final Long id;

        {
            Rule r1 = new Rule(10, null, null, null,null,      "s1", "r1", "w1", "l1", GrantType.LIMIT);
            ruleAdminService.insert(r1);
            id = r1.getId();
        }

        // save limits and check it has been saved
        final Long lid1;
        {
            RuleLimits limits = new RuleLimits();
            ruleAdminService.setLimits(id, limits);
            lid1 = limits.getId();
            assertNotNull(lid1);
        }

        // check limits have been set in Rule
        {
            Rule loaded = ruleAdminService.get(id);
            assertNotNull(loaded.getRuleLimits());
            assertEquals(lid1, loaded.getRuleLimits().getId());
            LOGGER.info("Found " + loaded + " --> " + loaded.getRuleLimits());
        }

        // set new limits
        final Long lid2;
        {
            RuleLimits limits = new RuleLimits();
            ruleAdminService.setLimits(id, limits);
            lid2 = limits.getId();
            assertNotNull(lid2);
        }

        // remove limits
        {
            Rule loaded = ruleAdminService.get(id);
            assertNotNull(loaded.getRuleLimits());
            ruleAdminService.setLimits(id, null);

            Rule loaded2 = ruleAdminService.get(id);
            assertNull(loaded2.getRuleLimits());
        }

        // remove Rule and cascade on Limits
        {
            RuleLimits limits = new RuleLimits();
            ruleAdminService.setLimits(id, limits);
            Rule loaded = ruleAdminService.get(id);
            assertNotNull(loaded.getRuleLimits());

            ruleAdminService.delete(id);
        }

    }

    public void testRuleLimitsErrors() throws NotFoundServiceEx {

        try {
            RuleLimits limits = new RuleLimits();
            ruleAdminService.setLimits(-10L, limits);
            fail("Failed recognising not existent rule");
        } catch (NotFoundServiceEx e) {
            // OK
        }

        final Long id;
        Rule r1 = new Rule(10, null, null, null,null,      "s1", "r1", "w1", "l1", GrantType.ALLOW);
        ruleAdminService.insert(r1);
        id = r1.getId();

        try {
            RuleLimits limits = new RuleLimits();
            ruleAdminService.setLimits(id, limits);
            fail("Failed recognising bad rule type");
        } catch (BadRequestServiceEx e) {
            // OK
        }

    }

    public void testRuleDetails() throws NotFoundServiceEx {
        final Long id;

        {
            Rule r1 = new Rule(10, null, null, null,null,      "s1", "r1", "w1", "l1", GrantType.ALLOW);
            ruleAdminService.insert(r1);
            id = r1.getId();
        }

        // save details and check it has been saved
        final Long lid1;
        {
            LayerDetails details = new LayerDetails();
            details.getAllowedStyles().add("FIRST_style1");
            details.getAttributes().add(new LayerAttribute("FIRST_attr1", AccessType.NONE));
            ruleAdminService.setDetails(id, details);
            lid1 = details.getId();
            assertNotNull(lid1);
        }

        // check details have been set in Rule
        {
            Rule loaded = ruleAdminService.get(id);
            LayerDetails details = loaded.getLayerDetails();
            assertNotNull(details);
            assertEquals(lid1, details.getId());
            assertEquals(1, details.getAttributes().size());
            assertEquals(new HashSet<String>(Arrays.asList("FIRST_style1")), details.getAllowedStyles());
            LOGGER.info("Found " + loaded + " --> " + loaded.getLayerDetails());
        }

        // set new details
        final Long lid2;
        {
            LayerDetails details = new LayerDetails();
            details.getAttributes().add(new LayerAttribute("attr1", AccessType.NONE));
            details.getAttributes().add(new LayerAttribute("attr2", AccessType.READONLY));
            details.getAttributes().add(new LayerAttribute("attr3", AccessType.READWRITE));

            assertEquals(3, details.getAttributes().size());

            // way [1] to update allowed styles: setAllowedStyles
            Set<String> styles = new HashSet<String>(Arrays.asList("style1X","style2X"));
            ruleAdminService.setAllowedStyles(id, styles);
//            details.setAllowedStyles(styles);

            details.setAllowedStyles(null); // we're setting the list to null because we dont want to update
            ruleAdminService.setDetails(id, details);
            lid2 = details.getId();
            assertNotNull(lid2);
        }

        // check details
        {
            Rule loaded = ruleAdminService.get(id);
            LayerDetails details = loaded.getLayerDetails();
            assertNotNull(details);
            for (LayerAttribute layerAttribute : details.getAttributes()) {
                LOGGER.error(layerAttribute);
            }

            assertEquals(3, details.getAttributes().size());
            assertEquals(new HashSet<String>(Arrays.asList("style1X","style2X")), details.getAllowedStyles());
        }

        // remove details
        {
            assertNotNull(ruleAdminService.get(id).getLayerDetails());
            ruleAdminService.setDetails(id, null);

            Rule loaded2 = ruleAdminService.get(id);
            assertNull(loaded2.getLayerDetails());
        }

        // remove Rule and cascade on details
        {
            LayerDetails details = new LayerDetails();
            ruleAdminService.setDetails(id, details);
            Rule loaded = ruleAdminService.get(id);
            assertNotNull(loaded.getLayerDetails());

            ruleAdminService.delete(id);
        }

    }

    public void testAllowedStyles() throws NotFoundServiceEx {
        final Long id;

        {
            Rule r1 = new Rule(10, null, null, null,null,      "s1", "r1", "w1", "l1", GrantType.ALLOW);
            ruleAdminService.insert(r1);
            id = r1.getId();
        }

        // save details and check it has been saved
        final Long lid1;
        {
            LayerDetails details = new LayerDetails();
            details.getAllowedStyles().add("style_A_1");
            ruleAdminService.setDetails(id, details);
            lid1 = details.getId();
            assertNotNull(lid1);
        }

        // check details have been set in Rule
        {
            Rule loaded = ruleAdminService.get(id);
            LayerDetails details = loaded.getLayerDetails();
            assertNotNull(details);
            assertEquals(lid1, details.getId());
            assertEquals(new HashSet<String>(Arrays.asList("style_A_1")), details.getAllowedStyles());
            LOGGER.info("Found " + loaded + " --> " + loaded.getLayerDetails());
        }

        // set new details, leaving the style as is
        final Long lid2;
        {
            LayerDetails old = ruleAdminService.get(id).getLayerDetails();
            old.setDefaultStyle("ds1");
            old.setAllowedStyles(null); // setting this on null, will not change the existing style list

            ruleAdminService.setDetails(id, old); // mh
            lid2 = old.getId();
            assertNotNull(lid2);
        }

        // check
        {
            Rule loaded = ruleAdminService.get(id);
            LayerDetails details = loaded.getLayerDetails();
            assertNotNull(details);
            assertEquals(lid2, details.getId());
            assertEquals("ds1", details.getDefaultStyle());
            assertEquals(new HashSet<String>(Arrays.asList("style_A_1")), details.getAllowedStyles()); // not changed

            LOGGER.info("Found " + loaded + " --> " + loaded.getLayerDetails());
        }

        // set new details, and allowedStyles
        {
            LayerDetails old = ruleAdminService.get(id).getLayerDetails();
            old.setAllowedStyles(new HashSet<String>(Arrays.asList("style_B_1","style_B_2")));

            ruleAdminService.setDetails(id, old); // mh
        }

        // check
        {
            Rule loaded = ruleAdminService.get(id);
            LayerDetails details = loaded.getLayerDetails();
            assertNotNull(details);
            assertEquals(lid2, details.getId());
            assertEquals("ds1", details.getDefaultStyle());
            assertEquals(new HashSet<String>(Arrays.asList("style_B_1","style_B_2")), details.getAllowedStyles());

            LOGGER.info("Found " + loaded + " --> " + loaded.getLayerDetails());
        }

        // set new allowed styles directly
        {
            ruleAdminService.setAllowedStyles(id, new HashSet<String>(Arrays.asList("style_C_1","style_C_2","style_C_3")));
        }

        // check
        {
            Rule loaded = ruleAdminService.get(id);
            LayerDetails details = loaded.getLayerDetails();
            assertNotNull(details);
            assertEquals(new HashSet<String>(Arrays.asList("style_C_1","style_C_2","style_C_3")), details.getAllowedStyles());

            LOGGER.info("Found " + loaded + " --> " + loaded.getLayerDetails());
        }
    }

    public void testAttribs() throws NotFoundServiceEx {
        final Long id;

        {
            Rule r1 = new Rule(10, null, null, null,null,      "s1", "r1", "w1", "l1", GrantType.ALLOW);
            ruleAdminService.insert(r1);
            id = r1.getId();
        }

        // save details and check it has been saved
        final Long lid1;
        {
            LayerDetails details = new LayerDetails();
            details.setAttributes(new HashSet<LayerAttribute>(Arrays.asList(
                    new LayerAttribute("attr1", AccessType.NONE),
                    new LayerAttribute("attr2", AccessType.READWRITE)
                    )));
            ruleAdminService.setDetails(id, details);
            lid1 = details.getId();
            assertNotNull(lid1);
        }

        // check attribs have been properly set
        {
            Rule loaded = ruleAdminService.get(id);
            LayerDetails details = loaded.getLayerDetails();
            LOGGER.debug("Reloaded details 1: " + details);
            assertNotNull(details);
            assertEquals(lid1, details.getId());

            assertEquals(new HashSet<LayerAttribute>(Arrays.asList(
                            new LayerAttribute("attr1", AccessType.NONE),
                            new LayerAttribute("attr2", AccessType.READWRITE)
                            )),
                    details.getAttributes());
        }

        String allowedArea = "MULTIPOLYGON (((4146.5 1301.4, 4147.5 1301.1, 4147.8 1301.4, 4146.5 1301.4)))";

        // set new area in details, leaving the attribs as is
        final Long lid2;
        {
            LayerDetails old = ruleAdminService.get(id).getLayerDetails();
            old.setDefaultStyle("ds1");

            old.setArea(parseMultiPolygon(allowedArea));

            ruleAdminService.setDetails(id, old);
            lid2 = old.getId();
            assertNotNull(lid2);
        }

        // check
        {
            Rule loaded = ruleAdminService.get(id);
            LayerDetails details = loaded.getLayerDetails();
            LOGGER.debug("Reloaded details 2: " + details);
            assertNotNull(details);
            assertEquals(lid2, details.getId());
            assertEquals("ds1", details.getDefaultStyle());
            assertTrue(parseMultiPolygon(allowedArea).equals(details.getArea())); // assertEquals is not reliable here
            
            // these should not have changed
            assertEquals(new HashSet<LayerAttribute>(Arrays.asList(
                            new LayerAttribute("attr1", AccessType.NONE),
                            new LayerAttribute("attr2", AccessType.READWRITE)
                            )),
                    details.getAttributes());                       
        }
    }

    public void testRuleDetailsErrors() throws NotFoundServiceEx {

        try {
            LayerDetails details = new LayerDetails();
            ruleAdminService.setDetails(-10L, details);
            fail("Failed recognising not existent rule");
        } catch (NotFoundServiceEx e) {
            // OK
        }

        try {
            Rule r1 = new Rule(10, null, null, null,null,      "s1", "r1", "w1", "l1", GrantType.DENY);
            ruleAdminService.insert(r1);
            Long id1 = r1.getId();

            LayerDetails details = new LayerDetails();
            ruleAdminService.setDetails(id1, details);
            fail("Failed recognising bad rule type");
        } catch (BadRequestServiceEx e) {
            // OK
        }

        try {
            Rule r1 = new Rule(10, null, null, null,null,      "s1", "r1", "w1", null, GrantType.ALLOW);
            ruleAdminService.insert(r1);
            Long id1 = r1.getId();

            LayerDetails details = new LayerDetails();
            ruleAdminService.setDetails(id1, details);
            fail("Failed recognising bad rule constraints");
        } catch (BadRequestServiceEx e) {
            // OK
        }

    }

    public void testRuleDetailsProps() throws NotFoundServiceEx {
        final Long id;
        final Long lid1;

        {
            Rule r1 = new Rule(10, null, null, null,null,      "s1", "r1", "w1", "l1", GrantType.ALLOW);
            ruleAdminService.insert(r1);
            id = r1.getId();

            LayerDetails details = new LayerDetails();
            ruleAdminService.setDetails(id, details);
            lid1 = details.getId();
            assertNotNull(lid1);
        }

        // check props have been set in LD
        {
            Rule loaded = ruleAdminService.get(id);
            assertNotNull(loaded.getLayerDetails());
        }

        // set new details, old map should be retained
        final Long lid2;
        {
            LayerDetails details = new LayerDetails();
            ruleAdminService.setDetails(id, details);
            lid2 = details.getId();
            assertNotNull(lid2);
        }

        // remove details
        {
            Rule loaded = ruleAdminService.get(id);
            assertNotNull(loaded.getLayerDetails());
            ruleAdminService.setDetails(id, null);
            // props should be cascaded
        }

        // remove Rule and cascade on details
        {
            LayerDetails details = new LayerDetails();
            ruleAdminService.setDetails(id, details);

            Rule loaded = ruleAdminService.get(id);
            assertNotNull(loaded.getLayerDetails());

            ruleAdminService.delete(id);
        }

    }


    @Test
    public void testShift() {
        assertEquals(0, ruleAdminService.getCountAll());

        Rule r1 = new Rule(10, null, null, null,null,      "s1", "r1", "w1", "l1", GrantType.ALLOW);
        Rule r2 = new Rule(20, null, null, null,null,      "s2", "r2", "w2", "l2", GrantType.ALLOW);
        Rule r3 = new Rule(30, null, null, null,null,      "s3", "r3", "w3", "l3", GrantType.ALLOW);
        Rule r4 = new Rule(40, null, null, null,null,      "s4", "r3", "w3", "l3", GrantType.ALLOW);

        ruleAdminService.insert(r1);
        ruleAdminService.insert(r2);
        ruleAdminService.insert(r3);
        ruleAdminService.insert(r4);

        int n = ruleAdminService.shift(20, 5);
        assertEquals(3, n);

        RuleFilter f = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        f.setService("s3");
        List<ShortRule> loaded = ruleAdminService.getList(f, null, null);
        assertEquals(1, loaded.size());
        assertEquals(35, loaded.get(0).getPriority());
    }

    @Test
    public void testSwap() {
        assertEquals(0, ruleAdminService.getCountAll());

        Rule r1 = new Rule(10, null, null, null,null,      "s1", "r1", "w1", "l1", GrantType.ALLOW);
        Rule r2 = new Rule(20, null, null, null,null,      "s2", "r2", "w2", "l2", GrantType.ALLOW);
        Rule r3 = new Rule(30, null, null, null,null,      "s3", "r3", "w3", "l3", GrantType.ALLOW);

        ruleAdminService.insert(r1);
        ruleAdminService.insert(r2);
        ruleAdminService.insert(r3);

        ruleAdminService.swap(r1.getId(), r2.getId());

        RuleFilter f = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        List<ShortRule> loaded = ruleAdminService.getList(f, null, null);
        assertEquals(3, loaded.size());
        assertEquals(r2.getId(), loaded.get(0).getId());
        assertEquals(r1.getId(), loaded.get(1).getId());
        assertEquals(r3.getId(), loaded.get(2).getId());

        assertEquals(10, loaded.get(0).getPriority());
        assertEquals(20, loaded.get(1).getPriority());
        assertEquals(30, loaded.get(2).getPriority());
    }




}

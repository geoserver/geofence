/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao;

import com.googlecode.genericdao.search.Search;
import com.vividsolutions.jts.geom.MultiPolygon;
import static org.geoserver.geofence.core.dao.BaseDAOTest.ruleDAO;
import org.geoserver.geofence.core.dao.util.SearchUtil;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.IPAddressRange;
import org.geoserver.geofence.core.model.LayerAttribute;
import org.geoserver.geofence.core.model.LayerDetails;
import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.core.model.enums.AccessType;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.core.model.enums.InsertPosition;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RuleDAOTest extends BaseDAOTest {


    private Rule createRule() {
        GSUser user = createUserAndGroup("rule_test");
        userDAO.persist(user);

        Rule rule = new Rule();
        rule.setGsuser(user);
        rule.setPriority(0);
        rule.setAccess(GrantType.ALLOW);
        ruleDAO.persist(rule);
        return rule;
    }

    @Test
    public void testPersistRule() throws Exception {

        Long uid;
        Long rid;
        {
            Rule rule = createRule();
            rid = rule.getId();
            uid = rule.getGsuser().getId();
        }

        // test save & load
        {
            Rule loaded = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", loaded);

            assertNull(loaded.getLayerDetails());
            assertEquals(uid, loaded.getGsuser().getId());
            assertEquals(GrantType.ALLOW, loaded.getAccess());

            loaded.setAccess(GrantType.DENY);
            ruleDAO.merge(loaded);
        }

        {
            Rule loaded2 = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", loaded2);
            assertEquals(GrantType.DENY, loaded2.getAccess());
        }

        ruleDAO.removeById(rid);
        userDAO.removeById(uid);
        assertNull("Rule not deleted", ruleDAO.find(rid));
    }

    @Test
    public void testPersistLayerDetails() throws Exception {

        long rid = createRule().getId();

        // add details
        {
            Rule loaded = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", loaded);

            assertNull(loaded.getLayerDetails());

            LayerDetails details = new LayerDetails();
            details.setRule(loaded);
            details.setDefaultStyle("default");
            details.getAttributes().add(new LayerAttribute("a1", AccessType.NONE));
            details.getAttributes().add(new LayerAttribute("a2", AccessType.READONLY));
            details.getAttributes().add(new LayerAttribute("a3", AccessType.READWRITE));
            details.getAttributes().add(new LayerAttribute("a4", AccessType.READWRITE));

            detailsDAO.persist(details);
        }

        // check everything's fine
        {
            Rule loaded2 = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", loaded2);
            LayerDetails details = loaded2.getLayerDetails();
            assertNotNull(details);
            assertEquals("default", details.getDefaultStyle());
            assertEquals(4, details.getAttributes().size());

        }

        // try removing the details
        {
            Rule loaded2 = ruleDAO.find(rid);
            LayerDetails details = loaded2.getLayerDetails();
            assertNotNull(details);

            detailsDAO.remove(details);
        }
    }
    @Test
    public void testAttributeNullAccessType() throws Exception {

        long rid = createRule().getId();

        // add details
        {
            Rule loaded = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", loaded);

            assertNull(loaded.getLayerDetails());

            LayerDetails details = new LayerDetails();
            details.setRule(loaded);
            details.setDefaultStyle("default");
            details.getAttributes().add(new LayerAttribute("a1", null));

            try {
                detailsDAO.persist(details);
            } catch (Exception e) {
            }
        }
    }

    @Test
    public void testChangeLayerDetails() throws Exception {

        long rid = createRule().getId();

        // create rule and details
        {
            Rule loaded = ruleDAO.find(rid);

            assertNull(loaded.getLayerDetails());

            LayerDetails details = new LayerDetails();
            details.setRule(loaded);
            details.setDefaultStyle("default");
            details.getAllowedStyles().add("s1");
            details.getAttributes().add(new LayerAttribute("a1", AccessType.NONE));
            detailsDAO.persist(details);
        }

        {
            Rule loaded = ruleDAO.find(rid);
            LayerDetails oldDetails = loaded.getLayerDetails();
            assertNotNull(oldDetails);

            // remove old details
            detailsDAO.remove(oldDetails);
        }

        // create new details
        {
            Rule loaded = ruleDAO.find(rid);
            assertNull(loaded.getLayerDetails());

            LayerDetails details = new LayerDetails();
            details.setRule(loaded);
            details.setDefaultStyle("default2");
            details.getAllowedStyles().add("s2");
            details.getAttributes().add(new LayerAttribute("z1", AccessType.NONE));
            details.getAttributes().add(new LayerAttribute("z2", AccessType.READONLY));
            details.getAttributes().add(new LayerAttribute("z3", AccessType.READWRITE));
            detailsDAO.persist(details);
        }

    }

    @Test
    public void testLayerDetailsPK() {

        long rid = createRule().getId();

        // add details from detailsDAO
        {
            Rule loaded = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", loaded);

            assertNull(loaded.getLayerDetails());

            LayerDetails details = new LayerDetails();
            details.setRule(loaded);
            details.setDefaultStyle("default");
            details.getAttributes().add(new LayerAttribute("a1", AccessType.NONE));
            details.getAttributes().add(new LayerAttribute("a1", AccessType.READONLY));

            try {
                detailsDAO.persist(details);
                fail("Dup attribute name not recognised");
            } catch (Exception e) {
            }
        }

    }


    public void testRuleDetails()  {

//        RuleAdminServiceImpl ruleAdminService = new RuleAdminServiceImpl();
//        ruleAdminService.setRuleDAO(ruleDAO);
//        ruleAdminService.setRuleLimitsDAO(limitsDAO);
//        ruleAdminService.setLayerDetailsDAO(detailsDAO);

        final Long id;

        {
            Rule r1 = new Rule(10, null, null, null, null,     "s1", "r1", "w1", "l1", GrantType.ALLOW);
//            ruleAdminService.insert(r1);
            ruleDAO.persist(r1);
            id = r1.getId();
        }

        // save details and check it has been saved
//        final Long lid1;
//        {
//            LayerDetails details = new LayerDetails();
//            details.getAllowedStyles().add("FIRST_style1");
//            details.getAttributes().add(new LayerAttribute("FIRST_attr1", AccessType.NONE));
////            ruleAdminService.setDetails(id, details);
//            setDetails(id, details);
//            lid1 = details.getId();
//            assertNotNull(lid1);
//        }
//
//        // check details have been set in Rule
//        {
////            Rule loaded = ruleAdminService.get(id);
//            Rule loaded = ruleDAO.find(id);
//            LayerDetails details = loaded.getLayerDetails();
//            assertNotNull(details);
//            assertEquals(lid1, details.getId());
//            assertEquals(1, details.getAttributes().size());
//            assertEquals(1, details.getAllowedStyles().size());
//            LOGGER.info("Found " + loaded + " --> " + loaded.getLayerDetails());
//        }

        // set new details
        final Long lid2;
        {
            LayerDetails details = new LayerDetails();
            details.getAllowedStyles().add("style1");
            details.getAllowedStyles().add("style2");
            details.getAttributes().add(new LayerAttribute("attr1", AccessType.NONE));
            details.getAttributes().add(new LayerAttribute("attr2", AccessType.READONLY));
            details.getAttributes().add(new LayerAttribute("attr3", AccessType.READWRITE));

            assertEquals(3, details.getAttributes().size());

//            ruleAdminService.setDetails(id, details);
            setDetails(id, details);
            lid2 = details.getId();
            assertNotNull(lid2);
        }

        // check details
        {
//            Rule loaded = ruleAdminService.get(id);
            Rule loaded = ruleDAO.find(id);
            LayerDetails details = loaded.getLayerDetails();
            assertNotNull(details);
            for (LayerAttribute layerAttribute : details.getAttributes()) {
                LOGGER.error(layerAttribute);
            }

            assertEquals(3, details.getAttributes().size());
            assertEquals(2, details.getAllowedStyles().size());
            assertTrue(details.getAllowedStyles().contains("style1"));
        }

        // add geom
        {
            Rule loaded = ruleDAO.find(id);
            LayerDetails details = loaded.getLayerDetails();

            MultiPolygon mpoly = buildMultiPolygon();
            details.setArea(mpoly);
            setDetails(id, details);
        }

        // check geom
        {
            Rule loaded = ruleDAO.find(id);
            LayerDetails details = loaded.getLayerDetails();
            assertNotNull(details);
            assertNotNull(details.getArea());
        }


        // remove details
        {
//            assertNotNull(ruleAdminService.get(id).getLayerDetails());
//            ruleAdminService.setDetails(id, null);
//
//            Rule loaded2 = ruleAdminService.get(id);
//            assertNull(loaded2.getLayerDetails());
            assertNotNull(ruleDAO.find(id).getLayerDetails());
            setDetails(id, null);

            Rule loaded2 = ruleDAO.find(id);
            assertNull(loaded2.getLayerDetails());
        }

        // remove Rule and cascade on details
        {
            LayerDetails details = new LayerDetails();
//            ruleAdminService.setDetails(id, details);
            setDetails(id, details);
//            Rule loaded = ruleAdminService.get(id);
            Rule loaded = ruleDAO.find(id);
            assertNotNull(loaded.getLayerDetails());

//            ruleAdminService.delete(id);
            ruleDAO.removeById(id);
        }

    }

//    @Override
    public void setDetails(Long ruleId, LayerDetails details) {
        Rule rule = ruleDAO.find(ruleId);
        if(rule == null)
            throw new RuntimeException("Rule not found");

        if(rule.getLayer() == null && details != null)
            throw new RuntimeException("Rule does not refer to a fixed layer");

        if(rule.getAccess() != GrantType.ALLOW && details != null)
            throw new RuntimeException("Rule is not of ALLOW type");


        // remove old details if any
        if(rule.getLayerDetails() != null) {
            detailsDAO.remove(rule.getLayerDetails());
        }

        rule = ruleDAO.find(ruleId);
        if(rule.getLayerDetails() != null)
            throw new IllegalStateException("LayerDetails (1) should be null");

        if(detailsDAO.find(ruleId) != null)
            throw new IllegalStateException("LayerDetails (2) should be null");

        if(! detailsDAO.findAll().isEmpty()) {
            LOGGER.error("LayerDetails (3) should be null --> " + detailsDAO.findAll());
            throw new IllegalStateException("LayerDetails (3) should be null --> " + detailsDAO.findAll().size());
        }


        if(details != null) {
            LOGGER.info("Setting details " + details
                    + "for " + rule);
            details.setRule(rule);
            detailsDAO.persist(details);
            // restore old properties
//            if(oldProps != null) { // always it is, add a size check
//                LOGGER.info("Restoring " + oldProps.size() + " props from older LayerDetails (id:"+ruleId+")");
//                detailsDAO.setCustomProps(ruleId, oldProps);
//            }
        } else {
            LOGGER.info("Removing details "
//                    + (oldProps.isEmpty()?"":"and " + oldProps + " props ")
                    + "for " + rule);
        }
    }


    @Test
    public void testDupRuleTest() throws Exception {

        {
            Rule rule1 = new Rule(10, null, null, null, null, "s", null, null, null, GrantType.ALLOW);
            Rule rule2 = new Rule(10, null, null, null, null, "s", null, null, null, GrantType.ALLOW);

            ruleDAO.persist(rule1);

            try {
                ruleDAO.persist(rule2);
                fail("Dup'd rule not detected");
            } catch (Exception e) {
                // ok
            }

        }
    }

    @Test
    public void testDupRule2Test() throws Exception {
        {
            Rule rule1 = new Rule(10, null, null, null, new IPAddressRange("1.2.3.4/32"), "s", null, null, null, GrantType.ALLOW);
            Rule rule2 = new Rule(10, null, null, null, new IPAddressRange("1.2.3.4/32"), "s", null, null, null, GrantType.ALLOW);

            ruleDAO.persist(rule1);

            try {
                ruleDAO.persist(rule2);
                fail("Dup'd rule not detected (addressRange)");
            } catch (Exception e) {
                // ok
            }
        }
    }

    @Test
    public void testDupRule3Test() throws Exception {
        {
            Rule rule1 = new Rule(10, null, null, null, new IPAddressRange("1.2.3.5/32"), "s", null, null, null, GrantType.ALLOW);
            Rule rule2 = new Rule(10, null, null, null, new IPAddressRange("1.2.3.4/32"), "s", null, null, null, GrantType.ALLOW);

            ruleDAO.persist(rule1);

            try {
                ruleDAO.persist(rule2);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Invalid dup'd rule (addressRange)");
            }
        }
    }


    @Test
    public void testShift() {
        assertEquals(0, ruleDAO.count(new Search(Rule.class)));

        Rule r1 = new Rule(10, null, null, null, null,     "s1", "r1", "w1", "l1", GrantType.ALLOW);
        Rule r2 = new Rule(20, null, null, null, null,     "s2", "r2", "w2", "l2", GrantType.ALLOW);
        Rule r3 = new Rule(30, null, null, null, null,     "s3", "r3", "w3", "l3", GrantType.ALLOW);
        Rule r4 = new Rule(40, null, null, null, null,     "s4", "r3", "w3", "l3", GrantType.ALLOW);

        ruleDAO.persist(r1);
        ruleDAO.persist(r2);
        ruleDAO.persist(r3);
        ruleDAO.persist(r4);

        int n = ruleDAO.shift(20, 5);
        assertEquals(3, n);

        Search s = new Search(Rule.class);
        s.addFilterEqual("service", "s3");
        List<Rule> loaded = ruleDAO.search(s);
        assertEquals(1, loaded.size());
        assertEquals(35, loaded.get(0).getPriority());

        // perform another shift: since there are no rule in there, the shift sould be skipped
        n = ruleDAO.shift(20, 5);
        assertEquals(-1, n);
    }

    @Test
    public void testSwap() {
        assertEquals(0, ruleDAO.count(new Search(Rule.class)));

        Rule r1 = new Rule(10, null, null, null, null,     "s1", "r1", "w1", "l1", GrantType.ALLOW);
        Rule r2 = new Rule(20, null, null, null, null,     "s2", "r2", "w2", "l2", GrantType.ALLOW);
        Rule r3 = new Rule(30, null, null, null, null,     "s3", "r3", "w3", "l3", GrantType.ALLOW);

        ruleDAO.persist(r1);
        ruleDAO.persist(r2);
        ruleDAO.persist(r3);

        ruleDAO.swap(r1.getId(), r2.getId());

        assertEquals(20, ruleDAO.find(r1.getId()).getPriority());
        assertEquals(10, ruleDAO.find(r2.getId()).getPriority());
        assertEquals(30, ruleDAO.find(r3.getId()).getPriority());
    }

    @Test
    public void testPersistRulePosition() throws Exception {

        long id1;
        {
            assertEquals(0, ruleDAO.count(new Search(Rule.class)));
            Rule rule1 = new Rule(1000, null, null, null, null, "s", null, null, null, GrantType.ALLOW);
            ruleDAO.persist(rule1, InsertPosition.FROM_START);
            id1 = rule1.getId();
        }

        {
            Rule loaded = ruleDAO.find(id1);
            assertNotNull(loaded);
            assertEquals(1, loaded.getPriority());
        }


        ruleDAO.persist(new Rule(10, null, null, null, null, "s10", null, null, null, GrantType.ALLOW));
        ruleDAO.persist(new Rule(20, null, null, null, null, "s20", null, null, null, GrantType.ALLOW));

        {
            assertEquals(3, ruleDAO.count(new Search(Rule.class)));
            Rule rule1 = new Rule(1000, null, null, null, null, "sZ", null, null, null, GrantType.ALLOW);
            long pri = ruleDAO.persist(rule1, InsertPosition.FROM_START);
            assertEquals(21, pri);
        }

        {
            Rule rule1 = new Rule(1, null, null, null, null, "second", null, null, null, GrantType.ALLOW);
            long pri = ruleDAO.persist(rule1, InsertPosition.FROM_START);
            assertEquals(10, pri);
        }

        {
            Rule rule1 = new Rule(0, null, null, null, null, "last", null, null, null, GrantType.ALLOW);
            long pri = ruleDAO.persist(rule1, InsertPosition.FROM_END);
            assertEquals(23, pri);
        }

        {
            Rule rule1 = new Rule(1, null, null, null, null, "last2", null, null, null, GrantType.ALLOW);
            long pri = ruleDAO.persist(rule1, InsertPosition.FROM_END);
            assertEquals(23, pri);
        }


    }


    @Test
    public void testIPRangeTest() throws Exception {

        Long uid;
        Long rid;
        {

            GSUser user = createUserAndGroup("rule_test");
            userDAO.persist(user);

            Rule rule = new Rule();
            rule.setGsuser(user);
            rule.setPriority(0);
            rule.setAddressRange(new IPAddressRange("10.11.0.0/16"));
            rule.setAccess(GrantType.ALLOW);
            ruleDAO.persist(rule);

            rid = rule.getId();
            uid = rule.getGsuser().getId();
        }

        // test save & load by id
        {
            Rule loaded = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", loaded);

            assertNotNull(loaded.getAddressRange());
            assertEquals("10.11.0.0/16", loaded.getAddressRange().getCidrSignature());
        }

        //test search
        {
            Search s = new Search(Rule.class);

            SearchUtil.addAddressRangeSearch(s, new IPAddressRange("10.11.0.0/16"));

            List<Rule> loadedList = ruleDAO.search(s);
            assertEquals(1, loadedList.size());

            Rule loaded = loadedList.get(0);

            // DO UPDATE
            loaded.setAddressRange(new IPAddressRange("111.222.223.0/24"));
            ruleDAO.merge(loaded);
        }

        // test update
        {
            Rule loaded = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", loaded);

            assertNotNull(loaded.getAddressRange());
            assertEquals("111.222.223.0/24", loaded.getAddressRange().getCidrSignature());

            // DO REMOVE
            loaded.setAddressRange(null);
            ruleDAO.merge(loaded);
        }

        // test remove
        {
            Rule loaded = ruleDAO.find(rid);
            assertNotNull("Can't retrieve rule", loaded);

            assertNull(loaded.getAddressRange());
        }
    }

}


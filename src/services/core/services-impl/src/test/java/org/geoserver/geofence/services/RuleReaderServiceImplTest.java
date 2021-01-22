/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.core.model.*;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.geoserver.geofence.core.model.enums.AccessType;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.services.dto.AccessInfo;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.RuleFilter.SpecialFilterType;
import org.geoserver.geofence.services.dto.ShortRule;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.geoserver.geofence.core.model.enums.AdminGrantType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RuleReaderServiceImplTest extends ServiceTestBase {

    public RuleReaderServiceImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }


    @Test
    public void testGetRulesForUsersAndGroup() {

        RuleFilter filter;

        filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        assertEquals(0, ruleAdminService.count(filter));

        UserGroup p1 = createRole("p1");
        UserGroup p2 = createRole("p2");


        String u1 = "TestUser1";
        String u2 = "TestUser2";
        String u3 = "TestUser3";

        GSUser user1 = new GSUser();
        user1.setName(u1);
        user1.getGroups().add(p1);

        GSUser user2 = new GSUser();
        user2.setName(u2);
        user2.getGroups().add(p2);

        UserGroup g3a = createRole("g3a");
        UserGroup g3b = createRole("g3b");
        GSUser user3 = new GSUser();
        user3.setName(u3);
        user3.getGroups().add(g3a);
        user3.getGroups().add(g3b);

        userAdminService.insert(user1);
        userAdminService.insert(user2);
        userAdminService.insert(user3);

        ruleAdminService.insert(new Rule(10, u1,  "p1", null, null,     "s1", "r1", "w1", "l1", GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, u2,  "p2", null, null,     "s1", "r2", "w2", "l2", GrantType.ALLOW));
        ruleAdminService.insert(new Rule(30, u1,  "p1", null, null,     "s3", "r3", "w3", "l3", GrantType.ALLOW));
        ruleAdminService.insert(new Rule(40, u1,  "p1", null, null,     null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(50, null,"g3a", null,null,     null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(60, null,"g3b", null,null,     null, null, null, null, GrantType.ALLOW));

        assertEquals(3, ruleReaderService.getMatchingRules(u1,  "*",  "Z","*",  "*", "*","*","*").size());
        assertEquals(3, ruleReaderService.getMatchingRules("*", "p1", "Z","*",  "*", "*","*","*").size());
        assertEquals(1, ruleReaderService.getMatchingRules(u1,  "*",  "Z","*", null, null,null,null).size());
        assertEquals(0, ruleReaderService.getMatchingRules("*", "Z",  "Z","*", null, null,null,null).size());
        assertEquals(1, ruleReaderService.getMatchingRules(u1,  "*",  "Z","*", null, null,null,null).size());
        assertEquals(1, ruleReaderService.getMatchingRules(u1,  "*",  "Z","*", null, null,null,null).size());
        assertEquals(1, ruleReaderService.getMatchingRules(u2,  "*",  "Z","*",  "*", "*","*","*").size());
        assertEquals(1, ruleReaderService.getMatchingRules("*", "p2", "Z","*",  "*", "*","*","*").size());
        assertEquals(2, ruleReaderService.getMatchingRules(u1,  "*",  "Z","*",  "s1", "*","*","*").size());
        assertEquals(2, ruleReaderService.getMatchingRules("*", "p1", "Z","*",  "s1", "*","*","*").size());
        assertEquals(2, ruleReaderService.getMatchingRules(u3,  "*",  "Z","*",  "s1", "*","*","*").size());
    }

    private static RuleFilter createFilter(String userName, String groupName, String service) {
        RuleFilter filter;
        filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        if(userName != null)
            filter.setUser(userName);
        if(groupName != null)
            filter.setRole(groupName);
        if(service != null)
            filter.setService(service);
        return filter;
    }


    @Test
    public void testGetRulesForGroupOnly() {

        RuleFilter filter;
        filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        assertEquals(0, ruleAdminService.count(filter));

        UserGroup g1 = createRole("p1");
        UserGroup g2 = createRole("p2");

        Rule r1 = new Rule(10, null, "p1", null,null,      "s1", "r1", "w1", "l1", GrantType.ALLOW);
        Rule r2 = new Rule(20, null, "p2", null,null,      "s1", "r2", "w2", "l2", GrantType.ALLOW);
        Rule r3 = new Rule(30, null, "p1", null,null,      "s3", "r3", "w3", "l3", GrantType.ALLOW);
        Rule r4 = new Rule(40, null, "p1", null,null,      null, null, null, null, GrantType.ALLOW);

        ruleAdminService.insert(r1);
        ruleAdminService.insert(r2);
        ruleAdminService.insert(r3);
        ruleAdminService.insert(r4);

        assertEquals(4, ruleReaderService.getMatchingRules("*","*","*", "*",  "*",  "*","*","*").size());
        assertEquals(3, ruleReaderService.getMatchingRules("*","*","*", "*",  "s1", "*","*","*").size());
        assertEquals(1, ruleReaderService.getMatchingRules("*","*","*", "*",  "ZZ", "*","*","*").size());

        assertEquals(3, ruleReaderService.getMatchingRules("*","p1","*", "*",  "*",  "*","*","*").size());
        assertEquals(2, ruleReaderService.getMatchingRules("*","p1","*", "*",  "s1", "*","*","*").size());
        assertEquals(1, ruleReaderService.getMatchingRules("*","p1","*", "*",  "ZZ", "*","*","*").size());

        assertEquals(1, ruleReaderService.getMatchingRules("*","p2","*", "*",  "*",  "*","*","*").size());
        assertEquals(1, ruleReaderService.getMatchingRules("*","p2","*", "*",  "s1", "*","*","*").size());
        assertEquals(0, ruleReaderService.getMatchingRules("*","p2","*", "*",  "ZZ", "*","*","*").size());

        filter = createFilter(null, g1.getName(), null);
        assertEquals(3, ruleReaderService.getMatchingRules(filter).size());

        filter = createFilter((String)null, null, "s3");
        assertEquals(2, ruleReaderService.getMatchingRules(filter).size());
    }

    @Test
    public void testGetInfo() {
        assertEquals(0, ruleAdminService.count(new RuleFilter(RuleFilter.SpecialFilterType.ANY)));

        List<Rule> rules = new ArrayList<>();

        rules.add(new Rule(100+rules.size(), null, null, null,null,   "WCS", null, null, null, GrantType.ALLOW));
        rules.add(new Rule(100+rules.size(), null, null, null,null,   "s1", "r2", "w2", "l2", GrantType.ALLOW));
        rules.add(new Rule(100+rules.size(), null, null, null,null,   "s3", "r3", "w3", "l3", GrantType.ALLOW));
        rules.add(new Rule(100+rules.size(), null, null, null,null,    null, null, null, null, GrantType.DENY));

        for (Rule rule : rules) {
            ruleAdminService.insert(rule);
        }

        assertEquals(4, ruleAdminService.count(new RuleFilter(RuleFilter.SpecialFilterType.ANY)));

        RuleFilter baseFilter = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        baseFilter.setUser("u0");
        baseFilter.setRole("p0");
        baseFilter.setInstance("i0");
        baseFilter.setService("WCS");
        baseFilter.setRequest(RuleFilter.SpecialFilterType.ANY);
        baseFilter.setWorkspace("W0");
        baseFilter.setLayer("l0");


        AccessInfo accessInfo;

        {
            RuleFilter ruleFilter = new RuleFilter(baseFilter);
            ruleFilter.setUser(SpecialFilterType.ANY);

            assertEquals(2, ruleReaderService.getMatchingRules(ruleFilter).size());
            assertEquals(GrantType.ALLOW, ruleReaderService.getAccessInfo(ruleFilter).getGrant());
        }
        {
            RuleFilter ruleFilter = new RuleFilter(baseFilter);
            ruleFilter.setRole(SpecialFilterType.ANY);

            assertEquals(2, ruleReaderService.getMatchingRules(ruleFilter).size());
            assertEquals(GrantType.ALLOW, ruleReaderService.getAccessInfo(ruleFilter).getGrant());
        }
        {
            RuleFilter ruleFilter = new RuleFilter(baseFilter);
            ruleFilter.setUser(SpecialFilterType.ANY);
            ruleFilter.setService("UNMATCH");

            assertEquals(1, ruleReaderService.getMatchingRules(ruleFilter).size());
            assertEquals(GrantType.DENY, ruleReaderService.getAccessInfo(ruleFilter).getGrant());
        }
        {
            RuleFilter ruleFilter = new RuleFilter(baseFilter);
            ruleFilter.setRole(SpecialFilterType.ANY);
            ruleFilter.setService("UNMATCH");

            assertEquals(1, ruleReaderService.getMatchingRules(ruleFilter).size());
            assertEquals(GrantType.DENY, ruleReaderService.getAccessInfo(ruleFilter).getGrant());
        }
    }

    @Test
    public void testResolveLazy() {
        assertEquals(0, ruleAdminService.count(new RuleFilter(RuleFilter.SpecialFilterType.ANY)));

        List<Rule> rules = new ArrayList<>();

        rules.add(new Rule(100+rules.size(), null, null, null,null,   "WCS", null, null, null, GrantType.ALLOW));
        rules.add(new Rule(100+rules.size(), null, null, null,null,   "s1", "r2", "w2", "l2", GrantType.ALLOW));

        for (Rule rule : rules) {
            if(rule != null)
                ruleAdminService.insert(rule);
        }

        LayerDetails details = new LayerDetails();
        details.setRule(rules.get(1));
        ruleAdminService.setDetails(rules.get(1).getId(), details);

        assertEquals(2, ruleAdminService.count(new RuleFilter(RuleFilter.SpecialFilterType.ANY)));

        AccessInfo accessInfo;

        {
            RuleFilter ruleFilter = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
            ruleFilter.setService("s1");
            ruleFilter.setLayer("l2");

            assertEquals(2, ruleReaderService.getMatchingRules(new RuleFilter(RuleFilter.SpecialFilterType.ANY)).size());
            List<ShortRule> matchingRules = ruleReaderService.getMatchingRules(ruleFilter);
            LOGGER.info("Matching rules: " + matchingRules);
            assertEquals(1, matchingRules.size());
            accessInfo = ruleReaderService.getAccessInfo(ruleFilter);
            assertEquals(GrantType.ALLOW, accessInfo.getGrant());
            assertNull(accessInfo.getAreaWkt());
        }
    }

    @Test
    public void testNoDefault() {
        assertEquals(0, ruleAdminService.count(new RuleFilter(SpecialFilterType.ANY)));

        ruleAdminService.insert(new Rule(0, null, null, null,null,   "WCS", null, null, null, GrantType.ALLOW));

        assertEquals(1, ruleReaderService.getMatchingRules("u0","*","i0",null, "WCS", null,"W0","l0").size());
        assertEquals(GrantType.ALLOW, ruleReaderService.getAccessInfo("u0","*","i0",null, "WCS", null,"W0","l0").getGrant());

        assertEquals(1, ruleReaderService.getMatchingRules("*","p0","i0",null, "WCS", null,"W0","l0").size());
        assertEquals(GrantType.ALLOW, ruleReaderService.getAccessInfo("*","p0","i0",null, "WCS", null,"W0","l0").getGrant());

        assertEquals(0, ruleReaderService.getMatchingRules("u0","*","i0",null, "UNMATCH", null,"W0","l0").size());
        assertEquals(GrantType.DENY, ruleReaderService.getAccessInfo("u0","*","i0",null, "UNMATCH", null,"W0","l0").getGrant());

        assertEquals(0, ruleReaderService.getMatchingRules("*","p0","i0",null, "UNMATCH", null,"W0","l0").size());
        assertEquals(GrantType.DENY, ruleReaderService.getAccessInfo("*","p0","i0",null, "UNMATCH", null,"W0","l0").getGrant());
    }

    @Test
    public void testGroups() {
        assertEquals(0, ruleAdminService.getCountAll());

        UserGroup g1 = createRole("p1");
        UserGroup g2 = createRole("p2");

        GSUser u1 = createUser("u1", g1);
        GSUser u2 = createUser("u2", g2);

        List<Rule> rules = new ArrayList<>();
        rules.add(new Rule(rules.size()+10, null, "p1", null, null,     "s1", "r1", "w1", "l1", GrantType.ALLOW));
        rules.add(new Rule(rules.size()+10, null, "p1", null, null,     null, null, null, null, GrantType.DENY));

        for (Rule rule : rules) {
            ruleAdminService.insert(rule);
        }

        LOGGER.info("SETUP ENDED, STARTING TESTS");
        //===

        assertEquals(rules.size(), ruleAdminService.getCountAll());

        {
            RuleFilter filter;
            filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
            filter.setUser(u1.getName());
            assertEquals(2, ruleReaderService.getMatchingRules(filter).size());
            filter.setService("s1");
            assertEquals(2, ruleReaderService.getMatchingRules(filter).size());
            assertEquals(GrantType.ALLOW, ruleReaderService.getAccessInfo(filter).getGrant());

            filter.setService("s2");
            assertEquals(1, ruleReaderService.getMatchingRules(filter).size());
            assertEquals(GrantType.DENY, ruleReaderService.getAccessInfo(filter).getGrant());
        }

        {
            RuleFilter filter;
            filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
            filter.setUser(u2.getName());
            assertEquals(0, ruleReaderService.getMatchingRules(filter).size());
            assertEquals(GrantType.DENY, ruleReaderService.getAccessInfo(filter).getGrant());

        }
    }

    @Test
    public void testGroupOrder01() throws UnknownHostException {
        assertEquals(0, ruleAdminService.getCountAll());

        UserGroup g1 = createRole("p1");
        UserGroup g2 = createRole("p2");

        GSUser u1 = createUser("u1", g1);
        GSUser u2 = createUser("u2", g2);

        List<Rule> rules = new ArrayList<Rule>();
        rules.add(new Rule(rules.size()+10, null, "p1", null,null,      null, null, null, null, GrantType.ALLOW));
        rules.add(new Rule(rules.size()+10, null, "p2", null,null,      null, null, null, null, GrantType.DENY));

        for (Rule rule : rules) {
            ruleAdminService.insert(rule);
        }

        LOGGER.info("SETUP ENDED, STARTING TESTS");
        //===

        assertEquals(rules.size(), ruleAdminService.getCountAll());

        RuleFilter filterU1 = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        filterU1.setUser(u1.getName());

        RuleFilter filterU2 = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        filterU2.setUser(u2.getName());

        assertEquals(1, ruleReaderService.getMatchingRules(filterU1).size());
        assertEquals(1, ruleReaderService.getMatchingRules(filterU2).size());

        assertEquals(GrantType.ALLOW, ruleReaderService.getAccessInfo(filterU1).getGrant());
        assertEquals(GrantType.DENY, ruleReaderService.getAccessInfo(filterU2).getGrant());
    }

    @Test
    public void testGroupOrder02() {
        assertEquals(0, ruleAdminService.getCountAll());

        UserGroup g1 = createRole("p1");
        UserGroup g2 = createRole("p2");

        GSUser u1 = createUser("u1", g1);
        GSUser u2 = createUser("u2", g2);

        List<Rule> rules = new ArrayList<Rule>();
        rules.add(new Rule(rules.size()+10, null, "p2", null,null,      null, null, null, null, GrantType.DENY));
        rules.add(new Rule(rules.size()+10, null, "p1", null,null,      null, null, null, null, GrantType.ALLOW));

        for (Rule rule : rules) {
            ruleAdminService.insert(rule);
        }

        LOGGER.info("SETUP ENDED, STARTING TESTS");
        //===

        assertEquals(rules.size(), ruleAdminService.getCountAll());

        RuleFilter filterU1;
        filterU1 = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        filterU1.setUser(u1.getName());

        RuleFilter filterU2;
        filterU2 = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        filterU2.setUser(u2.getName());


        assertEquals(1, ruleReaderService.getMatchingRules(filterU1).size());
        assertEquals(1, ruleReaderService.getMatchingRules(filterU2).size());

        assertEquals(GrantType.ALLOW, ruleReaderService.getAccessInfo(filterU1).getGrant());
        assertEquals(GrantType.DENY, ruleReaderService.getAccessInfo(filterU2).getGrant());
    }

    protected MultiPolygon buildMultiPolygon(String multip) {
        try {
            WKTReader reader = new WKTReader();
            MultiPolygon mp = (MultiPolygon) reader.read(multip);
            mp.setSRID(4326);
            return mp;
        } catch (ParseException ex) {
            throw new RuntimeException("Unexpected exception: " + ex.getMessage(), ex);
        }
    }

    @Test
    public void testAttrib() throws NotFoundServiceEx {
        assertEquals(0, ruleAdminService.getCountAll());

        {
            UserGroup g1 = createRole("g1");
            UserGroup g2 = createRole("g2");
            UserGroup g3 = createRole("g3");
            UserGroup g4 = createRole("g4");

            createUser("u1", g1);
            createUser("u2", g2);
            createUser("u12", g1, g2);
            createUser("u13", g1, g3);
            createUser("u14", g1, g4);

            int pri = 0;
            {
                Rule r1 = new Rule(pri++, null, "g1", null,null,      null, null, null, "l1", GrantType.ALLOW);
                ruleAdminService.insert(r1);

                LayerDetails d1 = new LayerDetails();
                d1.getAllowedStyles().add("style01");
                d1.getAllowedStyles().add("style02");
                d1.getAttributes().add(new LayerAttribute("att1", "String", AccessType.NONE));
                d1.getAttributes().add(new LayerAttribute("att2", "String", AccessType.READONLY));
                d1.getAttributes().add(new LayerAttribute("att3", "String", AccessType.READWRITE));

                ruleAdminService.setDetails(r1.getId(), d1);
            }
            {
                Rule r1 = new Rule(pri++, null, "g2", null,null,      null, null, null, "l1", GrantType.ALLOW);
                ruleAdminService.insert(r1);

                LayerDetails d1 = new LayerDetails();
                d1.getAllowedStyles().add("style02");
                d1.getAllowedStyles().add("style03");
                d1.getAttributes().add(new LayerAttribute("att1", "String", AccessType.READONLY));
                d1.getAttributes().add(new LayerAttribute("att2", "String", AccessType.READWRITE));
                d1.getAttributes().add(new LayerAttribute("att3", "String", AccessType.NONE));

                ruleAdminService.setDetails(r1.getId(), d1);
            }
            {
                Rule r1 = new Rule(pri++, null, "g3", null,null,      null, null, null, "l1", GrantType.ALLOW);
                ruleAdminService.insert(r1);

                LayerDetails d1 = new LayerDetails();

                ruleAdminService.setDetails(r1.getId(), d1);
            }
            {
                Rule r1 = new Rule(pri++, null, "g4", null,null,      null, null, null, "l1", GrantType.DENY);
                ruleAdminService.insert(r1);
            }
        }

        LOGGER.info("SETUP ENDED, STARTING TESTS========================================");

        assertEquals(4, ruleAdminService.getCountAll());

        //===

        // TEST u1
        {
            RuleFilter filterU1;
            filterU1 = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
            filterU1.setUser("u1");

            LOGGER.info("getMatchingRules ========================================");
            assertEquals(1, ruleReaderService.getMatchingRules(filterU1).size());

            LOGGER.info("getAccessInfo ========================================");
            AccessInfo accessInfo = ruleReaderService.getAccessInfo(filterU1);
            assertEquals(GrantType.ALLOW, accessInfo.getGrant());
        }

        // TEST u2
        {
            RuleFilter filter;
            filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
            filter.setUser("u2");
            filter.setLayer("l1");

            assertEquals(1, ruleReaderService.getMatchingRules(filter).size());

            AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
            assertEquals(GrantType.ALLOW, accessInfo.getGrant());
            assertNotNull(accessInfo.getAttributes());
            assertEquals(3, accessInfo.getAttributes().size());
            assertEquals(
                    new HashSet(Arrays.asList(
                        new LayerAttribute("att1", "String", AccessType.READONLY),
                        new LayerAttribute("att2", "String", AccessType.READWRITE),
                        new LayerAttribute("att3", "String", AccessType.NONE))),
                    accessInfo.getAttributes());

            assertEquals(2, accessInfo.getAllowedStyles().size());
        }

        // TEST u3
        // merging attributes at higher access level
        // merging styles
        {
            RuleFilter filter;
            filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
            filter.setUser("u12");
            filter.setLayer("l1");

            assertEquals(2, ruleReaderService.getMatchingRules(filter).size());

            AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
            assertEquals(GrantType.ALLOW, accessInfo.getGrant());
            assertNotNull(accessInfo.getAttributes());
            assertEquals(3, accessInfo.getAttributes().size());
            assertEquals(
                    new HashSet(Arrays.asList(
                        new LayerAttribute("att1", "String", AccessType.READONLY),
                        new LayerAttribute("att2", "String", AccessType.READWRITE),
                        new LayerAttribute("att3", "String", AccessType.READWRITE))),
                    accessInfo.getAttributes());

            assertEquals(3, accessInfo.getAllowedStyles().size());
        }

        // TEST u4
        // merging attributes to full access
        // unconstraining styles

        {
            RuleFilter filter;
            filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
            filter.setUser("u13");
            filter.setLayer("l1");

            assertEquals(2, ruleReaderService.getMatchingRules(filter).size());

            AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
            assertEquals(GrantType.ALLOW, accessInfo.getGrant());
            LOGGER.info("attributes: " + accessInfo.getAttributes());
            assertTrue(accessInfo.getAttributes().isEmpty());
//            assertEquals(3, accessInfo.getAttributes().size());
//            assertEquals(
//                    new HashSet(Arrays.asList(
//                        new LayerAttribute("att1", "String", AccessType.READONLY),
//                        new LayerAttribute("att2", "String", AccessType.READWRITE),
//                        new LayerAttribute("att3", "String", AccessType.READWRITE))),
//                    accessInfo.getAttributes());

            assertTrue(accessInfo.getAllowedStyles().isEmpty());
        }
    }

    /**
     * Added for issue #23
     */
    @Test
    public void testNullAllowableStyles() throws NotFoundServiceEx {
        assertEquals(0, ruleAdminService.getCountAll());

        {
            UserGroup g1 = createRole("g1");
            UserGroup g2 = createRole("g2");

            GSUser u1 = createUser("u1", g1, g2);

            // no details for first rule
            {
                Rule r1 = new Rule(30, null, "g2", null,null,      null, null, null, "l1", GrantType.ALLOW);
                ruleAdminService.insert(r1);
            }
            // some allowed styles for second rule
            {
                Rule r1 = new Rule(40, null, "g1", null,null,      null, null, null, "l1", GrantType.ALLOW);
                ruleAdminService.insert(r1);

                LayerDetails d1 = new LayerDetails();
                d1.getAllowedStyles().add("style01");
                d1.getAllowedStyles().add("style02");

                ruleAdminService.setDetails(r1.getId(), d1);
            }
        }

        LOGGER.info("SETUP ENDED, STARTING TESTS========================================");

        assertEquals(2, ruleAdminService.getCountAll());

        //===

        // TEST u1
        {
            RuleFilter filterU1;
            filterU1 = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
            filterU1.setUser("u1");

            LOGGER.info("getMatchingRules ========================================");
            assertEquals(2, ruleReaderService.getMatchingRules(filterU1).size());

            LOGGER.info("getAccessInfo ========================================");
            AccessInfo accessInfo = ruleReaderService.getAccessInfo(filterU1);
            assertEquals(GrantType.ALLOW, accessInfo.getGrant());

            assertTrue(accessInfo.getAllowedStyles().isEmpty());
        }

    }

    @Test
    public void testIPAddress() {

        RuleFilter filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        assertEquals(0, ruleAdminService.count(filter));

        UserGroup g1 = createRole("g1");
        UserGroup g2 = createRole("g2");

        IPAddressRange ip10 = new IPAddressRange("10.10.100.0/24");
        IPAddressRange ip192 = new IPAddressRange("192.168.0.0/16");

        Rule r1 = new Rule(10, null, "g1", null,ip10,      "s1", "r1", "w1", "l1", GrantType.ALLOW);
        Rule r2 = new Rule(20, null, "g2", null,ip10,      "s1", "r2", "w2", "l2", GrantType.ALLOW);
        Rule r3 = new Rule(30, null, "g1", null,ip192,      "s3", "r3", "w3", "l3", GrantType.ALLOW);
        Rule r4 = new Rule(40, null, "g1", null,null,      null, null, null, null, GrantType.ALLOW);

        ruleAdminService.insert(r1);
        ruleAdminService.insert(r2);
        ruleAdminService.insert(r3);
        ruleAdminService.insert(r4);

        // test without address filtering

        assertEquals(4, ruleReaderService.getMatchingRules("*","*", "*",  "*",  "*", "*","*","*").size());
        assertEquals(3, ruleReaderService.getMatchingRules("*","g1","*",  "*",  "*", "*","*","*").size());
        assertEquals(1, ruleReaderService.getMatchingRules("*","g2","*",  "*",  "*", "*","*","*").size());
        assertEquals(2, ruleReaderService.getMatchingRules("*","g1","*",  "*",  "s1", "*","*","*").size());
        assertEquals(1, ruleReaderService.getMatchingRules("*","*", "*",  "*",  "ZZ", "*","*","*").size());

        // test with  address filtering
        assertEquals(3, ruleReaderService.getMatchingRules("*","*", "*", "10.10.100.4", "*", "*","*","*").size());
        assertEquals(2, ruleReaderService.getMatchingRules("*","g1","*", "10.10.100.4", "*", "*","*","*").size());
        assertEquals(1, ruleReaderService.getMatchingRules("*","*", "*", "10.10.1.4",   "*", "*","*","*").size());
        assertEquals(2, ruleReaderService.getMatchingRules("*","*", "*", "192.168.1.1", "*", "*","*","*").size());
        assertEquals(1, ruleReaderService.getMatchingRules("*","*", "*",  null,         "*", "*","*","*").size());

        assertEquals(0, ruleReaderService.getMatchingRules("*","*","*","BAD",  "*", "*","*","*").size());
    }
    
    @Test
    public void testGetRulesForUserOnly() {
        RuleFilter filter;

        filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        assertEquals(0, ruleAdminService.count(filter));

        UserGroup g1 = createRole("g1");
        UserGroup g2 = createRole("g2");

        String u1 = "TestUser1";
        String u2 = "TestUser2";
        String u3 = "TestUser3";

        GSUser user1 = new GSUser();
        user1.setName(u1);
        user1.getGroups().add(g1);

        GSUser user2 = new GSUser();
        user2.setName(u2);
        user2.getGroups().add(g2);

        UserGroup g3a = createRole("g3a");
        UserGroup g3b = createRole("g3b");
        GSUser user3 = new GSUser();
        user3.setName(u3);
        user3.getGroups().add(g3a);
        user3.getGroups().add(g3b);

        userAdminService.insert(user1);
        userAdminService.insert(user2);
        userAdminService.insert(user3);

        ruleAdminService.insert(new Rule(10, u1, "g1", null, null,     "s1", "r1", "w1", "l1", GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, u2, "g2", null, null,     "s1", "r2", "w2", "l2", GrantType.ALLOW));
        ruleAdminService.insert(new Rule(30, u1, "g1", null, null,     "s3", "r3", "w3", "l3", GrantType.ALLOW));
        ruleAdminService.insert(new Rule(40, u1, "g1", null, null,     null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(50, null,  "g3a", null,null,      null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(60, null,  "g3b", null,null,      null, null, null, null, GrantType.ALLOW));

        filter = createFilter(u1, null, null);
        assertEquals(3, ruleReaderService.getMatchingRules(filter).size());

        filter = createFilter(u1, null, "s1");
        assertEquals(2, ruleReaderService.getMatchingRules(filter).size());

        filter = createFilter(u1, null, "s3");
        assertEquals(2, ruleReaderService.getMatchingRules(filter).size());
        
        filter = createFilter("anonymous", null, null);
        assertEquals(0, ruleReaderService.getMatchingRules(filter).size());
    }

    @Test
    public void testAdminRules() {


        GSUser user = createUser("auth00");

        ruleAdminService.insert(new Rule(10, user.getName(), null, null, null,     "s1", "r1", "w1", "l1", GrantType.ALLOW));

        RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);
        filter.setWorkspace("w1");

        AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
        assertEquals(GrantType.ALLOW, accessInfo.getGrant());
        assertFalse(accessInfo.getAdminRights());

        // let's add a USER adminrule

        adminruleAdminService.insert(new AdminRule(20, user.getName(), null, null, null, null, AdminGrantType.USER));

        accessInfo = ruleReaderService.getAccessInfo(filter);
        assertEquals(GrantType.ALLOW, accessInfo.getGrant());
        assertFalse(accessInfo.getAdminRights());

        // let's add an ADMIN adminrule on workspace w1

        adminruleAdminService.insert(new AdminRule(10, user.getName(), null, null, null, "w1", AdminGrantType.ADMIN));

        accessInfo = ruleReaderService.getAccessInfo(filter);
        assertEquals(GrantType.ALLOW, accessInfo.getGrant());
        assertTrue(accessInfo.getAdminRights());
    }

    @Test
    public void testRuleLimitsAllowedAreaSRIDIsPreserved() throws NotFoundServiceEx, ParseException {
        // test that the original SRID is present in the allowedArea wkt representation,
        // when retrieving it from the AccessInfo object
        Long id =null;
        Long id2=null;
        try {
            {
                Rule r1 = new Rule(10, null, null, null, null, "s1", "r1", "w1", "l1", GrantType.LIMIT);
                ruleAdminService.insert(r1);
                id = r1.getId();
            }

            {
                Rule r2 = new Rule(11, null, null, null, null, "s1", "r1", "w1", "l1", GrantType.ALLOW);
                id2 = ruleAdminService.insert(r2);
            }

            // save limits and check it has been saved
            {
                RuleLimits limits = new RuleLimits();
                String wkt = "MULTIPOLYGON(((0.0016139656066815888 -0.0006386457758059581,0.0019599705696027314 -0.0006386457758059581,0.0019599705696027314 -0.0008854090051601674,0.0016139656066815888 -0.0008854090051601674,0.0016139656066815888 -0.0006386457758059581)))";
                Geometry allowedArea = new WKTReader().read(wkt);
                allowedArea.setSRID(3857);
                limits.setAllowedArea((MultiPolygon) allowedArea);
                ruleAdminService.setLimits(id, limits);
            }

            {
                RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);
                filter.setWorkspace("w1");
                filter.setService("s1");
                filter.setRequest("r1");
                filter.setLayer("l1");
                AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
                String[] wktAr = accessInfo.getAreaWkt().split(";");
                assertEquals("SRID=3857", wktAr[0]);

            }
        } finally {

            if(id!=null)
                ruleAdminService.delete(id);
            if (id2!=null)
                ruleAdminService.delete(id2);

        }
    }


    @Test
    public void testRuleLimitsAllowedAreaReprojectionWithDifferentSrid() throws NotFoundServiceEx, ParseException {
        // test that the original SRID is present in the allowedArea wkt representation,
        // when retrieving it from the AccessInfo object
        Long id =null;
        Long id2=null;
        Long id3=null;
        try {
            {
                Rule r1 = new Rule(999, null, null, null, null, "s1", "r1", "w1", "l1", GrantType.ALLOW);
                ruleAdminService.insert(r1);
                id = r1.getId();
            }

            {
                Rule r2 = new Rule(11, null, null, null, null, "s1", "r1", "w1", "l1", GrantType.LIMIT);
                id2 = ruleAdminService.insert(r2);
            }

            // save limits and check it has been saved
            {
                RuleLimits limits = new RuleLimits();
                String wkt="MultiPolygon (((1680529.71478682174347341 4849746.00902365241199732, 1682436.7076464940328151 4849731.7422441728413105, 1682446.21883281995542347 4849208.62699576932936907, 1680524.95919364970177412 4849279.96089325752109289, 1680529.71478682174347341 4849746.00902365241199732)))";
                Geometry allowedArea = new WKTReader().read(wkt);
                allowedArea.setSRID(3003);
                limits.setAllowedArea((MultiPolygon) allowedArea);
                ruleAdminService.setLimits(id2, limits);
            }

            {
                Rule r3 = new Rule(12, null, null, null, null, "s1", "r1", "w1", "l1", GrantType.LIMIT);
                id3 = ruleAdminService.insert(r3);
            }

            // save limits and check it has been saved
            {
                RuleLimits limits = new RuleLimits();
                String wkt="MultiPolygon (((680588.67850254673976451 4850060.34823693986982107, 681482.71827003755606711 4850469.32878803834319115, 682633.56349697941914201 4849499.20374245755374432, 680588.67850254673976451 4850060.34823693986982107)))";
                Geometry allowedArea = new WKTReader().read(wkt);
                allowedArea.setSRID(23032);
                limits.setAllowedArea((MultiPolygon) allowedArea);
                ruleAdminService.setLimits(id3, limits);
            }

            {
                RuleFilter filter = new RuleFilter(SpecialFilterType.ANY, true);
                filter.setWorkspace("w1");
                filter.setService("s1");
                filter.setRequest("r1");
                filter.setLayer("l1");
                AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
                String[] wktAr = accessInfo.getAreaWkt().split(";");
                assertEquals("SRID=3003", wktAr[0]);

            }
        } finally {

            if(id!=null)
                ruleAdminService.delete(id);
            if (id2!=null)
                ruleAdminService.delete(id2);
            if(id3!=null)
                ruleAdminService.delete(id3);

        }
    }

}

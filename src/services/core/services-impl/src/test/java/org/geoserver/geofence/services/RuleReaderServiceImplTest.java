/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.core.model.*;
import org.geoserver.geofence.core.model.enums.*;
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
import java.util.Set;
import java.util.stream.Collectors;

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

        ruleAdminService.insert(new Rule(10, u1,  "p1", null, null,     "s1", "r1", null, "w1", "l1", GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, u2,  "p2", null, null,     "s1", "r2", null, "w2", "l2", GrantType.ALLOW));
        ruleAdminService.insert(new Rule(30, u1,  "p1", null, null,     "s3", "r3", null, "w3", "l3", GrantType.ALLOW));
        ruleAdminService.insert(new Rule(40, u1,  "p1", null, null,     null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(50, null,"g3a", null,null,     null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(60, null,"g3b", null,null,     null, null, null, null, null, GrantType.ALLOW));

        assertEquals(3, getMatchingRules(u1,  "*",  "Z","*",  "*", "*","*","*").size());
        assertEquals(3, getMatchingRules("*", "p1", "Z","*",  "*", "*","*","*").size());
        assertEquals(1, getMatchingRules(u1,  "*",  "Z","*", null, null,null,null).size());
        assertEquals(0, getMatchingRules("*", "Z",  "Z","*", null, null,null,null).size());
        assertEquals(1, getMatchingRules(u1,  "*",  "Z","*", null, null,null,null).size());
        assertEquals(1, getMatchingRules(u1,  "*",  "Z","*", null, null,null,null).size());
        assertEquals(1, getMatchingRules(u2,  "*",  "Z","*",  "*", "*","*","*").size());
        assertEquals(1, getMatchingRules("*", "p2", "Z","*",  "*", "*","*","*").size());
        assertEquals(2, getMatchingRules(u1,  "*",  "Z","*",  "s1", "*","*","*").size());
        assertEquals(2, getMatchingRules("*", "p1", "Z","*",  "s1", "*","*","*").size());
        assertEquals(2, getMatchingRules(u3,  "*",  "Z","*",  "s1", "*","*","*").size());
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

        Rule r1 = new Rule(10, null, "p1", null,null,      "s1", "r1", null, "w1", "l1", GrantType.ALLOW);
        Rule r2 = new Rule(20, null, "p2", null,null,      "s1", "r2", null, "w2", "l2", GrantType.ALLOW);
        Rule r3 = new Rule(30, null, "p1", null,null,      "s3", "r3", null, "w3", "l3", GrantType.ALLOW);
        Rule r4 = new Rule(40, null, "p1", null,null,      null, null, null, null, null, GrantType.ALLOW);

        ruleAdminService.insert(r1);
        ruleAdminService.insert(r2);
        ruleAdminService.insert(r3);
        ruleAdminService.insert(r4);

        assertEquals(4, getMatchingRules("*","*","*", "*",  "*",  "*","*","*").size());
        assertEquals(3, getMatchingRules("*","*","*", "*",  "s1", "*","*","*").size());
        assertEquals(1, getMatchingRules("*","*","*", "*",  "ZZ", "*","*","*").size());

        assertEquals(3, getMatchingRules("*","p1","*", "*",  "*",  "*","*","*").size());
        assertEquals(2, getMatchingRules("*","p1","*", "*",  "s1", "*","*","*").size());
        assertEquals(1, getMatchingRules("*","p1","*", "*",  "ZZ", "*","*","*").size());

        assertEquals(1, getMatchingRules("*","p2","*", "*",  "*",  "*","*","*").size());
        assertEquals(1, getMatchingRules("*","p2","*", "*",  "s1", "*","*","*").size());
        assertEquals(0, getMatchingRules("*","p2","*", "*",  "ZZ", "*","*","*").size());

        filter = createFilter(null, g1.getName(), null);
        assertEquals(3, ruleReaderService.getMatchingRules(filter).size());

        filter = createFilter((String)null, null, "s3");
        assertEquals(2, ruleReaderService.getMatchingRules(filter).size());
    }

    @Test
    public void testGetInfo() {
        assertEquals(0, ruleAdminService.count(new RuleFilter(RuleFilter.SpecialFilterType.ANY)));

        List<Rule> rules = new ArrayList<>();

        rules.add(new Rule(100+rules.size(), null, null, null,null,   "WCS", null, null,  null, null, GrantType.ALLOW));
        rules.add(new Rule(100+rules.size(), null, null, null,null,   "s1", "r2",  null, "w2", "l2", GrantType.ALLOW));
        rules.add(new Rule(100+rules.size(), null, null, null,null,   "s3", "r3",  null, "w3", "l3", GrantType.ALLOW));
        rules.add(new Rule(100+rules.size(), null, null, null,null,    null, null, null,  null, null, GrantType.DENY));

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

        rules.add(new Rule(100+rules.size(), null, null, null,null,   "WCS", null, null, null, null, GrantType.ALLOW));
        rules.add(new Rule(100+rules.size(), null, null, null,null,   "s1", "r2",  null, "w2", "l2", GrantType.ALLOW));

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

        ruleAdminService.insert(new Rule(0, null, null, null,null, "WCS", null,null, null, null, GrantType.ALLOW));

        assertEquals(1, getMatchingRules("u0","*","i0",null, "WCS", null,"W0","l0").size());
        assertEquals(GrantType.ALLOW, getAccessInfo("u0","*","i0",null, "WCS", null,"W0","l0").getGrant());

        assertEquals(1, getMatchingRules("*","p0","i0",null, "WCS", null,"W0","l0").size());
        assertEquals(GrantType.ALLOW, getAccessInfo("*","p0","i0",null, "WCS", null,"W0","l0").getGrant());

        assertEquals(0, getMatchingRules("u0","*","i0",null, "UNMATCH", null,"W0","l0").size());
        assertEquals(GrantType.DENY, getAccessInfo("u0","*","i0",null, "UNMATCH", null,"W0","l0").getGrant());

        assertEquals(0, getMatchingRules("*","p0","i0",null, "UNMATCH", null,"W0","l0").size());
        assertEquals(GrantType.DENY, getAccessInfo("*","p0","i0",null, "UNMATCH", null,"W0","l0").getGrant());
    }

    @Test
    public void testGroups() {
        assertEquals(0, ruleAdminService.getCountAll());

        UserGroup g1 = createRole("p1");
        UserGroup g2 = createRole("p2");

        GSUser u1 = createUser("u1", g1);
        GSUser u2 = createUser("u2", g2);

        List<Rule> rules = new ArrayList<>();
        rules.add(new Rule(rules.size()+10, null, "p1", null, null,     "s1", "r1", null, "w1", "l1", GrantType.ALLOW));
        rules.add(new Rule(rules.size()+10, null, "p1", null, null,     null, null, null, null, null, GrantType.DENY));

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
        rules.add(new Rule(rules.size()+10, null, "p1", null,null,      null, null, null, null, null, GrantType.ALLOW));
        rules.add(new Rule(rules.size()+10, null, "p2", null,null,      null, null, null, null, null, GrantType.DENY));

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
        rules.add(new Rule(rules.size()+10, null, "p2", null,null,      null, null, null, null, null, GrantType.DENY));
        rules.add(new Rule(rules.size()+10, null, "p1", null,null,      null, null, null, null, null, GrantType.ALLOW));

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
                Rule r1 = new Rule(pri++, null, "g1", null,null, null, null,null, null, "l1", GrantType.ALLOW);
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
                Rule r1 = new Rule(pri++, null, "g2", null,null, null, null, null, null, "l1", GrantType.ALLOW);
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
                Rule r1 = new Rule(pri++, null, "g3", null,null,      null, null, null, null, "l1", GrantType.ALLOW);
                ruleAdminService.insert(r1);

                LayerDetails d1 = new LayerDetails();

                ruleAdminService.setDetails(r1.getId(), d1);
            }
            {
                Rule r1 = new Rule(pri++, null, "g4", null,null, null, null, null, null, "l1", GrantType.DENY);
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
                Rule r1 = new Rule(30, null, "g2", null,null,      null, null, null, null, "l1", GrantType.ALLOW);
                ruleAdminService.insert(r1);
            }
            // some allowed styles for second rule
            {
                Rule r1 = new Rule(40, null, "g1", null,null,      null, null, null, null, "l1", GrantType.ALLOW);
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

        Rule r1 = new Rule(10, null, "g1", null,ip10,      "s1", "r1", null, "w1", "l1", GrantType.ALLOW);
        Rule r2 = new Rule(20, null, "g2", null,ip10,      "s1", "r2", null, "w2", "l2", GrantType.ALLOW);
        Rule r3 = new Rule(30, null, "g1", null,ip192,     "s3", "r3", null, "w3", "l3", GrantType.ALLOW);
        Rule r4 = new Rule(40, null, "g1", null,null,      null, null, null, null, null, GrantType.ALLOW);

        ruleAdminService.insert(r1);
        ruleAdminService.insert(r2);
        ruleAdminService.insert(r3);
        ruleAdminService.insert(r4);

        // test without address filtering

        assertEquals(4, getMatchingRules("*","*", "*",  "*",  "*",  "*","*","*").size());
        assertEquals(3, getMatchingRules("*","g1","*",  "*",  "*",  "*","*","*").size());
        assertEquals(1, getMatchingRules("*","g2","*",  "*",  "*",  "*","*","*").size());
        assertEquals(2, getMatchingRules("*","g1","*",  "*",  "s1", "*","*","*").size());
        assertEquals(1, getMatchingRules("*","*", "*",  "*",  "ZZ", "*","*","*").size());

        // test with  address filtering
        assertEquals(3, getMatchingRules("*","*", "*", "10.10.100.4", "*", "*","*","*").size());
        assertEquals(2, getMatchingRules("*","g1","*", "10.10.100.4", "*", "*","*","*").size());
        assertEquals(1, getMatchingRules("*","*", "*", "10.10.1.4",   "*", "*","*","*").size());
        assertEquals(2, getMatchingRules("*","*", "*", "192.168.1.1", "*", "*","*","*").size());
        assertEquals(1, getMatchingRules("*","*", "*",  null,         "*", "*","*","*").size());

        assertEquals(0, getMatchingRules("*","*","*","BAD",  "*", "*","*","*").size());
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

        ruleAdminService.insert(new Rule(10, u1, "g1", null, null,     "s1", "r1", null, "w1", "l1", GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, u2, "g2", null, null,     "s1", "r2", null, "w2", "l2", GrantType.ALLOW));
        ruleAdminService.insert(new Rule(30, u1, "g1", null, null,     "s3", "r3", null, "w3", "l3", GrantType.ALLOW));
        ruleAdminService.insert(new Rule(40, u1, "g1", null, null,     null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(50, null,  "g3a", null,null,  null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(60, null,  "g3b", null,null,  null, null, null, null, null, GrantType.ALLOW));

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

        ruleAdminService.insert(new Rule(10, user.getName(), null, null, null,     "s1", "r1", null, "w1", "l1", GrantType.ALLOW));

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
    public void testMultiRoles() {

        RuleFilter filter;

        filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        assertEquals(0, ruleAdminService.count(filter));

        UserGroup p1 = createRole("p1");
        UserGroup p2 = createRole("p2");
        UserGroup p3 = createRole("p3");

        String u1  = "TestUser1";
        String u2  = "TestUser2";
        String u3 = "TestUser3";

        GSUser user1 = new GSUser();
        user1.setName(u1);
        user1.getGroups().add(p1);

        GSUser user2 = new GSUser();
        user2.setName(u2);
        user2.getGroups().add(p2);

        GSUser user12 = new GSUser();
        user12.setName(u3);
        user12.getGroups().add(p1);
        user12.getGroups().add(p2);

        userAdminService.insert(user1);
        userAdminService.insert(user2);
        userAdminService.insert(user12);

        ruleAdminService.insert(new Rule(10, u1,   "p1",  null, null,  "s1", "r1", null, "w1", "l1", GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, u2,   "p2",  null, null,  "s1", "r2", null, "w2", "l2", GrantType.ALLOW));
        ruleAdminService.insert(new Rule(30, u1,    null, null, null,  null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(40, u2,    null, null, null,  null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(50, u3,    null, null, null,  null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(51, u3,   "p1",  null, null,  null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(52, u3,   "p2",  null, null,  null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(60, null, "p1",  null, null,  null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(70, null, "p2",  null, null,  null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(80, null, "p3",  null, null,  null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(901, u1,  "p2",  null, null,  null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(902, u2,  "p1",  null, null,  null, null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(999, null, null, null, null,  null, null, null, null, null, GrantType.ALLOW));

        
        assertRules(createFilter("*",  "*"), new Integer[]{10,20,30,40,50,51,52,60,70,80,901,902,999});
        assertRules(createFilter("*", null), new Integer[]{30,40,50,999});
        assertRules(createFilter("*", "NO"), new Integer[]{30,40,50,999});
        assertRules(createFilter("*", "p1"),    new Integer[]{10,30,40,50,51,60,902,999});
        assertRules(createFilter("*", "p1,NO"), new Integer[]{10,30,40,50,51,60,902,999});
        assertRules(createFilter("*", "p1,p2"),    new Integer[]{10,20,30,40,50,51,52,60,70,901,902,999});
        assertRules(createFilter("*", "p1,p2,NO"), new Integer[]{10,20,30,40,50,51,52,60,70,901,902,999});
        
        assertRules(createFilter(null,  "*"), new Integer[]{60,70,80,999});
        assertRules(createFilter(null, null), new Integer[]{999});
        assertRules(createFilter(null, "NO"), new Integer[]{999});
        assertRules(createFilter(null, "p1"),    new Integer[]{60,999});
        assertRules(createFilter(null, "p1,NO"), new Integer[]{60,999});        
        assertRules(createFilter(null, "p1,p2"),    new Integer[]{60,70,999});        
        assertRules(createFilter(null, "p1,p2,NO"), new Integer[]{60,70,999});        
        
        assertRules(createFilter("NO",  "*"), new Integer[]{999});
        assertRules(createFilter("NO", null), new Integer[]{999});
        assertRules(createFilter("NO", "NO"), new Integer[]{999});
        assertRules(createFilter("NO","p1"),    new Integer[]{999});        
        assertRules(createFilter("NO","p1,NO"), new Integer[]{999});                
        assertRules(createFilter("NO","p1,p2"),    new Integer[]{999});                
        assertRules(createFilter("NO","p1,p2,NO"), new Integer[]{999});                
        
        assertRules(createFilter(u1,  "*"), new Integer[]{10,30,60,999});
        assertRules(createFilter(u1, null), new Integer[]{30,999});
        assertRules(createFilter(u1, "NO"), new Integer[]{30,999});        
        assertRules(createFilter(u1, "p1"),       new Integer[]{10,30,60,999});
        assertRules(createFilter(u1, "p1,NO"),    new Integer[]{10,30,60,999});
        assertRules(createFilter(u1, "p1,p2"),    new Integer[]{10,30,60,999});
        assertRules(createFilter(u1, "p1,p2,NO"), new Integer[]{10,30,60,999});
        
        assertRules(createFilter(u3,  "*"), new Integer[]{50,51,52,60,70,999});
        assertRules(createFilter(u3, null), new Integer[]{50,999});
        assertRules(createFilter(u3, "NO"), new Integer[]{50,999});        
        assertRules(createFilter(u3, "p1"),       new Integer[]{50,51,60,999});
        assertRules(createFilter(u3, "p2"),       new Integer[]{50,52,70,999});
        assertRules(createFilter(u3, "p1,NO"),    new Integer[]{50,51,60,999});
        assertRules(createFilter(u3, "p1,p2"),    new Integer[]{50,51,52,60,70,999});
        assertRules(createFilter(u3, "p1,p2,p3"), new Integer[]{50,51,52,60,70,999});
        assertRules(createFilter(u3, "p1,p2,NO"), new Integer[]{50,51,52,60,70,999});
    }

    
    private RuleFilter createFilter(String userName, String groupName) {
        return new RuleFilter(userName, groupName, "*", "*", "*", "*", "*", "*", "*");
    }
    
    private void assertRules(RuleFilter filter, Integer[] expectedPriorities) {
        RuleFilter origFilter = filter.clone();
        List<ShortRule> rules = ruleReaderService.getMatchingRules(filter);
        
        Set<Long> pri = rules.stream()
                .map(r -> r.getPriority())
                .collect(Collectors.toSet());
        Set<Long> exp = Arrays.asList(expectedPriorities).stream()
                .map(i -> i.longValue())
                .collect(Collectors.toSet());
        assertEquals("Bad rule set selected for filter " + origFilter, exp, pri);        
    }

    private List<ShortRule> getMatchingRules(
                    String userName, String profileName, String instanceName,
                    String sourceAddress,
                    String service, String request,
                    String workspace, String layer) {

        return ruleReaderService.getMatchingRules(
                new RuleFilter(userName, profileName, instanceName, sourceAddress, 
                        service, request, null, workspace, layer));
    }

    private AccessInfo getAccessInfo(String userName, String roleName, String instanceName,
            String sourceAddress,
            String service, String request, 
            String workspace, String layer) {
        return ruleReaderService.getAccessInfo(
                new RuleFilter(userName, roleName, instanceName, sourceAddress, 
                        service, request, null, workspace, layer));
    }
    
}

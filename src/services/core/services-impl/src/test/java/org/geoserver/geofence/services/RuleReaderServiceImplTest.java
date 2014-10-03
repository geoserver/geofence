/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.IPAddressRange;
import org.geoserver.geofence.core.model.LayerAttribute;
import org.geoserver.geofence.core.model.LayerDetails;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.core.model.Rule;
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

        UserGroup g1 = createUserGroup("p1");
        UserGroup g2 = createUserGroup("p2");

        GSUser user1 = new GSUser();
        user1.setName("TestUser1");
        user1.getGroups().add(g1);

        GSUser user2 = new GSUser();
        user2.setName("TestUser2");
        user2.getGroups().add(g2);

        UserGroup g3a = createUserGroup("g3a");
        UserGroup g3b = createUserGroup("g3b");
        GSUser user3 = new GSUser();
        user3.setName("TestUser3");
        user3.getGroups().add(g3a);
        user3.getGroups().add(g3b);

        userAdminService.insert(user1);
        userAdminService.insert(user2);
        userAdminService.insert(user3);

        ruleAdminService.insert(new Rule(10, user1, g1, null, null,     "s1", "r1", "w1", "l1", GrantType.ALLOW));
        ruleAdminService.insert(new Rule(20, user2, g2, null, null,     "s1", "r2", "w2", "l2", GrantType.ALLOW));
        ruleAdminService.insert(new Rule(30, user1, g1, null, null,     "s3", "r3", "w3", "l3", GrantType.ALLOW));
        ruleAdminService.insert(new Rule(40, user1, g1, null, null,     null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(50, null,  g3a, null,null,      null, null, null, null, GrantType.ALLOW));
        ruleAdminService.insert(new Rule(60, null,  g3b, null,null,      null, null, null, null, GrantType.ALLOW));

        assertEquals(3, ruleReaderService.getMatchingRules(user1.getName(),g1.getName(),"Z","*",  "*", "*","*","*").size());
        assertEquals(0, ruleReaderService.getMatchingRules(user1.getName(),"Z",         "Z","*", null, null,null,null).size());
        assertEquals(1, ruleReaderService.getMatchingRules(user1.getName(),"*",         "Z","*", null, null,null,null).size());
        assertEquals(1, ruleReaderService.getMatchingRules(user1.getName(),"*",         "*","*", null, null,null,null).size());
        assertEquals(1, ruleReaderService.getMatchingRules(user2.getName(),g2.getName(),"","*",  "*", "*","*","*").size());
        assertEquals(2, ruleReaderService.getMatchingRules(user1.getName(),g1.getName(),"","*",  "s1", "*","*","*").size());
        assertEquals(2, ruleReaderService.getMatchingRules(user3.getName(),"*"         ,"","*",  "s1", "*","*","*").size());
//        assertEquals(1, ruleReaderService.getMatchingRules(user1.getName(),"","",            null, null,null,null).size());
//        assertEquals(3, ruleReaderService.getMatchingRules(user1.getName(),g1.getName(),"",  "*", "*","*","*").size());
//        assertEquals(1, ruleReaderService.getMatchingRules(user2.getName(),g2.getName(),"",  "*", "*","*","*").size());
//        assertEquals(2, ruleReaderService.getMatchingRules(user1.getName(),g1.getName(),"",  "s1", "*","*","*").size());
//        assertEquals(1, ruleReaderService.getMatchingRules(user1.getName(),"","",            "ZZ", "*","*","*").size());


        filter = createFilter(user1.getId(), g1.getId(), null);
        assertEquals(3, ruleReaderService.getMatchingRules(filter).size());

        filter = createFilter(user1.getName(), g1.getName(), null);
        assertEquals(3, ruleReaderService.getMatchingRules(filter).size());

        filter = createFilter(user1.getId(), g1.getId(), "s1");
        assertEquals(2, ruleReaderService.getMatchingRules(filter).size());

        filter = createFilter(user1.getId(), null, "s3");
        assertEquals(2, ruleReaderService.getMatchingRules(filter).size());

    }

    private static RuleFilter createFilter(Long userId, Long groupId, String service) {
        RuleFilter filter;
        filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        if(userId != null)
            filter.setUser(userId);
        if(groupId != null)
            filter.setUserGroup(groupId);
        if(service != null)
            filter.setService(service);
        return filter;
    }
    private static RuleFilter createFilter(String userName, String groupName, String service) {
        RuleFilter filter;
        filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        if(userName != null)
            filter.setUser(userName);
        if(groupName != null)
            filter.setUserGroup(groupName);
        if(service != null)
            filter.setService(service);
        return filter;
    }


    @Test
    public void testGetRulesForGroupOnly() {

        RuleFilter filter;
        filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        assertEquals(0, ruleAdminService.count(filter));

        UserGroup g1 = createUserGroup("p1");
        UserGroup g2 = createUserGroup("p2");

        Rule r1 = new Rule(10, null, g1, null,null,      "s1", "r1", "w1", "l1", GrantType.ALLOW);
        Rule r2 = new Rule(20, null, g2, null,null,      "s1", "r2", "w2", "l2", GrantType.ALLOW);
        Rule r3 = new Rule(30, null, g1, null,null,      "s3", "r3", "w3", "l3", GrantType.ALLOW);
        Rule r4 = new Rule(40, null, g1, null,null,      null, null, null, null, GrantType.ALLOW);

        ruleAdminService.insert(r1);
        ruleAdminService.insert(r2);
        ruleAdminService.insert(r3);
        ruleAdminService.insert(r4);

        assertEquals(0, ruleReaderService.getMatchingRules("","","",          "*",  null, null,null,null).size());
        assertEquals(3, ruleReaderService.getMatchingRules("",g1.getName(),"","*",  "*", "*","*","*").size());
        assertEquals(1, ruleReaderService.getMatchingRules("",g2.getName(),"","*",  "*", "*","*","*").size());
        assertEquals(2, ruleReaderService.getMatchingRules("",g1.getName(),"","*",  "s1", "*","*","*").size());
        assertEquals(0, ruleReaderService.getMatchingRules("","","",          "*",  "ZZ", "*","*","*").size());


        filter = createFilter(null, g1.getId(), null);
        assertEquals(3, ruleReaderService.getMatchingRules(filter).size());

        filter = createFilter(null, g1.getName(), null);
        assertEquals(3, ruleReaderService.getMatchingRules(filter).size());

        filter = createFilter(null, g1.getId(), "s1");
        assertEquals(2, ruleReaderService.getMatchingRules(filter).size());

        filter = createFilter((String)null, null, "s3");
        assertEquals(2, ruleReaderService.getMatchingRules(filter).size());
    }

    @Test
    public void testGetInfo() {
        assertEquals(0, ruleAdminService.count(new RuleFilter(RuleFilter.SpecialFilterType.ANY)));

        int pri = -1;
        List<Rule> rules = new ArrayList<Rule>();

        rules.add(new Rule(100+rules.size(), null, null, null,null,   "WCS", null, null, null, GrantType.ALLOW));
        rules.add(new Rule(100+rules.size(), null, null, null,null,   "s1", "r2", "w2", "l2", GrantType.ALLOW));
        rules.add(new Rule(100+rules.size(), null, null, null,null,   "s3", "r3", "w3", "l3", GrantType.ALLOW));
        rules.add(new Rule(100+rules.size(), null, null, null,null,    null, null, null, null, GrantType.DENY));

        for (Rule rule : rules) {
            if(rule != null)
                ruleAdminService.insert(rule);
        }

        assertEquals(4, ruleAdminService.count(new RuleFilter(RuleFilter.SpecialFilterType.ANY)));

        AccessInfo accessInfo;

        {
            RuleFilter ruleFilter = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
            ruleFilter.setUser("u0");
            ruleFilter.setUserGroup("p0");
            ruleFilter.setInstance("i0");
            ruleFilter.setService("WCS");
            ruleFilter.setRequest(RuleFilter.SpecialFilterType.ANY);
            ruleFilter.setWorkspace("W0");
            ruleFilter.setLayer("l0");

            assertEquals(4, ruleReaderService.getMatchingRules(new RuleFilter(RuleFilter.SpecialFilterType.ANY)).size());
            List<ShortRule> matchingRules = ruleReaderService.getMatchingRules(ruleFilter);
            LOGGER.info("Matching rules: " + matchingRules);
            assertEquals(2, matchingRules.size());
            accessInfo = ruleReaderService.getAccessInfo(ruleFilter);
            assertEquals(GrantType.ALLOW, accessInfo.getGrant());
            assertNull(accessInfo.getAreaWkt());
        }
        {
            RuleFilter ruleFilter = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
            ruleFilter.setUser("u0");
            ruleFilter.setUserGroup("p0");
            ruleFilter.setInstance("i0");
            ruleFilter.setService("UNMATCH");
            ruleFilter.setRequest(RuleFilter.SpecialFilterType.ANY);
            ruleFilter.setWorkspace("W0");
            ruleFilter.setLayer("l0");

            assertEquals(1, ruleReaderService.getMatchingRules(ruleFilter).size());
            accessInfo = ruleReaderService.getAccessInfo(ruleFilter);
            assertEquals(GrantType.DENY, accessInfo.getGrant());
        }
    }

    @Test
    public void testResolveLazy() {
        assertEquals(0, ruleAdminService.count(new RuleFilter(RuleFilter.SpecialFilterType.ANY)));

        List<Rule> rules = new ArrayList<Rule>();

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

        int pri = -1;
        Rule rules[] = new Rule[100];

        pri++; rules[pri] = new Rule(pri, null, null, null,null,   "WCS", null, null, null, GrantType.ALLOW);

        for (Rule rule : rules) {
            if(rule != null)
                ruleAdminService.insert(rule);
        }

        AccessInfo accessInfo;

        assertEquals(1, ruleReaderService.getMatchingRules("u0","p0","i0",null, "WCS", null,"W0","l0").size());
        accessInfo = ruleReaderService.getAccessInfo("u0","p0","i0",null, "WCS", null,"W0","l0");
        assertEquals(GrantType.ALLOW, accessInfo.getGrant());

        assertEquals(0, ruleReaderService.getMatchingRules("u0","p0","i0",null, "UNMATCH", null,"W0","l0").size());
        accessInfo = ruleReaderService.getAccessInfo("u0","p0","i0",null, "UNMATCH", null,"W0","l0");
        assertEquals(GrantType.DENY, accessInfo.getGrant());
    }

    @Test
    public void testGroups() {
        assertEquals(0, ruleAdminService.getCountAll());

        UserGroup g1 = createUserGroup("p1");
        UserGroup g2 = createUserGroup("p2");

        GSUser u1 = createUser("u1", g1);
        GSUser u2 = createUser("u2", g2);

        List<Rule> rules = new ArrayList<Rule>();
        rules.add(new Rule(rules.size()+10, null, g1, null, null,     "s1", "r1", "w1", "l1", GrantType.ALLOW));
        rules.add(new Rule(rules.size()+10, null, g1, null, null,     null, null, null, null, GrantType.DENY));

        for (Rule rule : rules) {
            ruleAdminService.insert(rule);
        }

        LOGGER.info("SETUP ENDED, STARTING TESTS");
        //===

        assertEquals(rules.size(), ruleAdminService.getCountAll());

        {
            RuleFilter filter;
            filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
            filter.setUser(u1.getId());
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
            filter.setUser(u2.getId());
            filter.setUserGroup(g1.getId());
            assertEquals(0, ruleReaderService.getMatchingRules(filter).size());
            assertEquals(GrantType.DENY, ruleReaderService.getAccessInfo(filter).getGrant());

        }
    }

    @Test
    public void testGroupOrder01() throws UnknownHostException {
        assertEquals(0, ruleAdminService.getCountAll());

        UserGroup g1 = createUserGroup("p1");
        UserGroup g2 = createUserGroup("p2");

        GSUser u1 = createUser("u1", g1);
        GSUser u2 = createUser("u2", g2);

        List<Rule> rules = new ArrayList<Rule>();
        rules.add(new Rule(rules.size()+10, null, g1, null,null,      null, null, null, null, GrantType.ALLOW));
        rules.add(new Rule(rules.size()+10, null, g2, null,null,      null, null, null, null, GrantType.DENY));

        for (Rule rule : rules) {
            ruleAdminService.insert(rule);
        }

        LOGGER.info("SETUP ENDED, STARTING TESTS");
        //===

        assertEquals(rules.size(), ruleAdminService.getCountAll());

        RuleFilter filterU1 = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        filterU1.setUser(u1.getId());

        RuleFilter filterU2 = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        filterU2.setUser(u2.getId());

        assertEquals(1, ruleReaderService.getMatchingRules(filterU1).size());
        assertEquals(1, ruleReaderService.getMatchingRules(filterU2).size());

        assertEquals(GrantType.ALLOW, ruleReaderService.getAccessInfo(filterU1).getGrant());
        assertEquals(GrantType.DENY, ruleReaderService.getAccessInfo(filterU2).getGrant());
    }

    @Test
    public void testGroupOrder02() {
        assertEquals(0, ruleAdminService.getCountAll());

        UserGroup g1 = createUserGroup("p1");
        UserGroup g2 = createUserGroup("p2");

        GSUser u1 = createUser("u1", g1);
        GSUser u2 = createUser("u2", g2);

        List<Rule> rules = new ArrayList<Rule>();
        rules.add(new Rule(rules.size()+10, null, g2, null,null,      null, null, null, null, GrantType.DENY));
        rules.add(new Rule(rules.size()+10, null, g1, null,null,      null, null, null, null, GrantType.ALLOW));

        for (Rule rule : rules) {
            ruleAdminService.insert(rule);
        }

        LOGGER.info("SETUP ENDED, STARTING TESTS");
        //===

        assertEquals(rules.size(), ruleAdminService.getCountAll());

        RuleFilter filterU1;
        filterU1 = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        filterU1.setUser(u1.getId());

        RuleFilter filterU2;
        filterU2 = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
        filterU2.setUser(u2.getId());


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

//    @Test
//    public void testArea() throws NotFoundServiceEx, ParseException {
//        assertEquals(0, ruleAdminService.getCountAll());
//
//        final String MULTIPOLYGONWKT0 = "MULTIPOLYGON(((10 0, 0 -10, -10 0, 0 10, 10 0)))";
//        final String MULTIPOLYGONWKT1 = "MULTIPOLYGON(((6 6, 6 -6, -6 -6 , -6 6, 6 6)))";
//
//        UserGroup g1 = createUserGroup("p1");
//        GSUser u1 = createGFUser("u1", g1);
//        u1.setAllowedArea(buildMultiPolygon(MULTIPOLYGONWKT0));
//        userAdminService.update(u1);
//
//        Rule r0 = new Rule(10, u1,   g1, null,      null, null, null, null, GrantType.LIMIT);
//        Rule r1 = new Rule(20, null, g1, null,      null, null, null, null, GrantType.ALLOW);
//
//
//        ruleAdminService.insert(r0);
//        ruleAdminService.insert(r1);
//
//        RuleLimits limits = new RuleLimits();
//        limits.setAllowedArea(buildMultiPolygon(MULTIPOLYGONWKT1));
//        ruleAdminService.setLimits(r0.getId(), limits);
//
//        LOGGER.info("SETUP ENDED, STARTING TESTS");
//
//        assertEquals(2, ruleAdminService.getCountAll());
//
//        //===
//
//        RuleFilter filterU1;
//        filterU1 = new RuleFilter(RuleFilter.SpecialFilterType.ANY);
//        filterU1.setUser(u1.getId());
//
//
//        assertEquals(2, ruleReaderService.getMatchingRules(filterU1).size());
//
//        AccessInfo accessInfo = ruleReaderService.getAccessInfo(filterU1);
//        assertEquals(GrantType.ALLOW, accessInfo.getGrant());
////        assertNotNull(accessInfo.getArea());
////        assertEquals(9, accessInfo.getArea().getNumPoints());
//
//        assertNotNull(accessInfo.getAreaWkt());
//        GeometryAdapter ga = new GeometryAdapter();
//        Geometry area = ga.unmarshal(accessInfo.getAreaWkt());
//        assertEquals(9, area.getNumPoints());
//    }

    @Test
    public void testAttrib() throws NotFoundServiceEx {
        assertEquals(0, ruleAdminService.getCountAll());

        {
            UserGroup g1 = createUserGroup("g1");
            UserGroup g2 = createUserGroup("g2");
            UserGroup g3 = createUserGroup("g3");
            UserGroup g4 = createUserGroup("g4");

            GSUser u1 = createUser("u1", g1);
            GSUser u2 = createUser("u2", g2);
            GSUser u3 = createUser("u3", g1, g2);
            GSUser u4 = createUser("u4", g1, g3);
            GSUser u5 = createUser("u5", g1, g4);

            {
                Rule r1 = new Rule(20, null, g1, null,null,      null, null, null, "l1", GrantType.ALLOW);
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
                Rule r1 = new Rule(20, null, g2, null,null,      null, null, null, "l1", GrantType.ALLOW);
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
                Rule r1 = new Rule(20, null, g3, null,null,      null, null, null, "l1", GrantType.ALLOW);
                ruleAdminService.insert(r1);

                LayerDetails d1 = new LayerDetails();

                ruleAdminService.setDetails(r1.getId(), d1);
            }
            {
                Rule r1 = new Rule(20, null, g4, null,null,      null, null, null, "l1", GrantType.DENY);
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
            filter.setUser("u3");
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
            filter.setUser("u4");
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
            UserGroup g1 = createUserGroup("g1");
            UserGroup g2 = createUserGroup("g2");

            GSUser u1 = createUser("u1", g1, g2);

            // no details for first rule
            {
                Rule r1 = new Rule(30, null, g2, null,null,      null, null, null, "l1", GrantType.ALLOW);
                ruleAdminService.insert(r1);
            }
            // some allowed styles for second rule
            {
                Rule r1 = new Rule(40, null, g1, null,null,      null, null, null, "l1", GrantType.ALLOW);
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

        UserGroup g1 = createUserGroup("g1");
        UserGroup g2 = createUserGroup("g2");

        IPAddressRange ip10 = new IPAddressRange("10.10.100.0/24");
        IPAddressRange ip192 = new IPAddressRange("192.168.0.0/16");

        Rule r1 = new Rule(10, null, g1, null,ip10,      "s1", "r1", "w1", "l1", GrantType.ALLOW);
        Rule r2 = new Rule(20, null, g2, null,ip10,      "s1", "r2", "w2", "l2", GrantType.ALLOW);
        Rule r3 = new Rule(30, null, g1, null,ip192,      "s3", "r3", "w3", "l3", GrantType.ALLOW);
        Rule r4 = new Rule(40, null, g1, null,null,      null, null, null, null, GrantType.ALLOW);

        ruleAdminService.insert(r1);
        ruleAdminService.insert(r2);
        ruleAdminService.insert(r3);
        ruleAdminService.insert(r4);

        // test without address filtering

        assertEquals(0, ruleReaderService.getMatchingRules("","","",          "*",  null, null,null,null).size());
        assertEquals(3, ruleReaderService.getMatchingRules("",g1.getName(),"","*",  "*", "*","*","*").size());
        assertEquals(1, ruleReaderService.getMatchingRules("",g2.getName(),"","*",  "*", "*","*","*").size());
        assertEquals(2, ruleReaderService.getMatchingRules("",g1.getName(),"","*",  "s1", "*","*","*").size());
        assertEquals(0, ruleReaderService.getMatchingRules("","","",          "*",  "ZZ", "*","*","*").size());

        // test with  address filtering
        assertEquals(3, ruleReaderService.getMatchingRules("*","*","*","10.10.100.4",  "*", "*","*","*").size());
        assertEquals(2, ruleReaderService.getMatchingRules("*","g1","*","10.10.100.4",  "*", "*","*","*").size());
        assertEquals(1, ruleReaderService.getMatchingRules("*","*","*","10.10.1.4",  "*", "*","*","*").size());
        assertEquals(2, ruleReaderService.getMatchingRules("*","*","*","192.168.1.1",  "*", "*","*","*").size());
        assertEquals(1, ruleReaderService.getMatchingRules("*","*","*",null,  "*", "*","*","*").size());

        assertEquals(0, ruleReaderService.getMatchingRules("*","*","*","BAD",  "*", "*","*","*").size());
    }

}

package org.geoserver.geofence.services;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.core.model.RuleLimits;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.core.model.enums.CatalogMode;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.core.model.enums.SpatialFilterType;
import static org.geoserver.geofence.services.ServiceTestBase.ruleAdminService;
import static org.geoserver.geofence.services.ServiceTestBase.ruleReaderService;
import org.geoserver.geofence.services.dto.AccessInfo;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

/**
 *
 */
public class RuleReaderServiceImpl_GeomTest extends ServiceTestBase {

    @Test
    public void testRuleLimitsAllowedAreaSRIDIsPreserved() throws NotFoundServiceEx, ParseException {
        // test that the original SRID is present in the allowedArea wkt representation,
        // when retrieving it from the AccessInfo object
        Long id = null;
        Long id2 = null;
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
                RuleFilter filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY, true);
                filter.setWorkspace("w1");
                filter.setService("s1");
                filter.setRequest("r1");
                filter.setLayer("l1");
                AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
                String[] wktAr = accessInfo.getAreaWkt().split(";");
                assertEquals("SRID=3857", wktAr[0]);

            }
        } finally {

            if (id != null) {
                ruleAdminService.delete(id);
            }
            if (id2 != null) {
                ruleAdminService.delete(id2);
            }

        }
    }

    @Test
    public void testRuleLimitsAllowedAreaReprojectionWithDifferentSrid() throws NotFoundServiceEx, ParseException {
        // test that the original SRID is present in the allowedArea wkt representation,
        // when retrieving it from the AccessInfo object
        Long id = null;
        Long id2 = null;
        Long id3 = null;
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
                String wkt = "MultiPolygon (((1680529.71478682174347341 4849746.00902365241199732, 1682436.7076464940328151 4849731.7422441728413105, 1682446.21883281995542347 4849208.62699576932936907, 1680524.95919364970177412 4849279.96089325752109289, 1680529.71478682174347341 4849746.00902365241199732)))";
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
                String wkt = "MultiPolygon (((680588.67850254673976451 4850060.34823693986982107, 681482.71827003755606711 4850469.32878803834319115, 682633.56349697941914201 4849499.20374245755374432, 680588.67850254673976451 4850060.34823693986982107)))";
                Geometry allowedArea = new WKTReader().read(wkt);
                allowedArea.setSRID(23032);
                limits.setAllowedArea((MultiPolygon) allowedArea);
                ruleAdminService.setLimits(id3, limits);
            }

            {
                RuleFilter filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY, true);
                filter.setWorkspace("w1");
                filter.setService("s1");
                filter.setRequest("r1");
                filter.setLayer("l1");
                AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
                String[] wktAr = accessInfo.getAreaWkt().split(";");
                assertEquals("SRID=3003", wktAr[0]);

            }
        } finally {

            if (id != null) {
                ruleAdminService.delete(id);
            }
            if (id2 != null) {
                ruleAdminService.delete(id2);
            }
            if (id3 != null) {
                ruleAdminService.delete(id3);
            }

        }
    }

    public void testRuleSpatialFilterTypeClipSameGroup() throws ParseException {

        // test that when we have two rules referring to the same group
        // one having a filter type Intersects and the other one having filter type Clip
        // the result is a clip area obtained by the intersection of the two.
        UserGroup g1 = createRole("group11");
        UserGroup g2 = createRole("group12");
        GSUser user = createUser("auth11", g1, g2);

        ruleAdminService.insert(new Rule(9999, null, null, null, null, "s11", "r11", "w11", "l11", GrantType.ALLOW));
        long id = ruleAdminService.insert(new Rule(10, user.getName(), "group11", null, null, "s11", "r11", "w11", "l11", GrantType.LIMIT));
        RuleLimits limits = new RuleLimits();
        limits.setSpatialFilterType(SpatialFilterType.CLIP);
        limits.setCatalogMode(CatalogMode.HIDE);
        String areaWKT = "MultiPolygon (((-1.93327272727272859 5.5959090909090925, 2.22727272727272707 5.67609090909091041, 2.00454545454545441 4.07245454545454599, -1.92436363636363761 4.54463636363636425, -1.92436363636363761 4.54463636363636425, -1.93327272727272859 5.5959090909090925)))";
        MultiPolygon area = (MultiPolygon) new WKTReader().read(areaWKT);
        limits.setAllowedArea(area);
        ruleAdminService.setLimits(id, limits);

        long id2 = ruleAdminService.insert(new Rule(11, user.getName(), "group12", null, null, "s11", "r11", "w11", "l11", GrantType.LIMIT));
        RuleLimits limits2 = new RuleLimits();
        limits2.setSpatialFilterType(SpatialFilterType.INTERSECT);
        limits2.setCatalogMode(CatalogMode.HIDE);
        String areaWKT2 = "MultiPolygon (((-1.78181818181818308 5.95227272727272894, -0.16927272727272813 5.4711818181818197, 1.97781818181818148 3.81409090909090986, 1.93327272727272748 2.05009090909090919, -2.6638181818181832 2.64700000000000069, -1.78181818181818308 5.95227272727272894)))";
        MultiPolygon area2 = (MultiPolygon) new WKTReader().read(areaWKT2);
        limits2.setAllowedArea(area2);
        ruleAdminService.setLimits(id2, limits2);
        RuleFilter filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY, true);
        filter.setWorkspace("w11");
        filter.setLayer("l11");

        AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
        assertEquals(GrantType.ALLOW, accessInfo.getGrant());
        assertFalse(accessInfo.getAdminRights());

        // area in same group, the result should an itersection of the
        // two allowed area as a clip geometry.
        Geometry testArea = area.intersection(area2);
        testArea.normalize();
        assertNull(accessInfo.getAreaWkt());
        assertNotNull(accessInfo.getClipAreaWkt());

        Geometry resultArea = (new WKTReader().read(accessInfo.getClipAreaWkt()));
        resultArea.normalize();
        assertTrue(testArea.equalsExact(resultArea, 10.0E-15));
    }

    @Test
    public void testRuleSpatialFilterTypeIntersectsSameGroup() throws ParseException {

        // test that when we have two rules referring to the same group
        // both having a filter type Intersects
        // the result is an intersect area obtained by the intersection of the two.
        UserGroup g1 = createRole("group13");
        UserGroup g2 = createRole("group14");
        GSUser user = createUser("auth12", g1, g2);

        ruleAdminService.insert(new Rule(9999, null, null, null, null, "s11", "r11", "w11", "l11", GrantType.ALLOW));
        long id = ruleAdminService.insert(new Rule(13, user.getName(), "group13", null, null, "s11", "r11", "w11", "l11", GrantType.LIMIT));
        RuleLimits limits = new RuleLimits();
        limits.setSpatialFilterType(SpatialFilterType.INTERSECT);
        limits.setCatalogMode(CatalogMode.HIDE);
        String areaWKT = "MultiPolygon (((-1.93327272727272859 5.5959090909090925, 2.22727272727272707 5.67609090909091041, 2.00454545454545441 4.07245454545454599, -1.92436363636363761 4.54463636363636425, -1.92436363636363761 4.54463636363636425, -1.93327272727272859 5.5959090909090925)))";
        MultiPolygon area = (MultiPolygon) new WKTReader().read(areaWKT);
        limits.setAllowedArea(area);
        ruleAdminService.setLimits(id, limits);

        long id2 = ruleAdminService.insert(new Rule(14, user.getName(), "group14", null, null, "s11", "r11", "w11", "l11", GrantType.LIMIT));
        RuleLimits limits2 = new RuleLimits();
        limits2.setSpatialFilterType(SpatialFilterType.INTERSECT);
        limits2.setCatalogMode(CatalogMode.HIDE);
        String areaWKT2 = "MultiPolygon (((-1.78181818181818308 5.95227272727272894, -0.16927272727272813 5.4711818181818197, 1.97781818181818148 3.81409090909090986, 1.93327272727272748 2.05009090909090919, -2.6638181818181832 2.64700000000000069, -1.78181818181818308 5.95227272727272894)))";
        MultiPolygon area2 = (MultiPolygon) new WKTReader().read(areaWKT2);
        limits2.setAllowedArea(area2);
        ruleAdminService.setLimits(id2, limits2);
        RuleFilter filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY, true);
        filter.setWorkspace("w11");
        filter.setLayer("l11");

        AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
        assertEquals(GrantType.ALLOW, accessInfo.getGrant());
        assertFalse(accessInfo.getAdminRights());

        // area in same group, the result should an itersection of the
        // two allowed area as an intersects geometry.
        Geometry testArea = area.intersection(area2);
        testArea.normalize();
        assertNull(accessInfo.getClipAreaWkt());
        assertNotNull(accessInfo.getAreaWkt());

        Geometry resultArea = (new WKTReader().read(accessInfo.getAreaWkt()));
        resultArea.normalize();
        assertTrue(testArea.equalsExact(resultArea, 10.0E-15));
    }

    @Test
    public void testRuleSpatialFilterTypeEnlargeAccess() throws ParseException {
        // test the access enalargement behaviour with the SpatialFilterType.
        // the user belongs to two groups. One with an allowedArea of type intersects,
        // the other one with an allowed area of type clip. They should be returned
        // separately in the final rule.

        UserGroup g1 = createRole("group22");
        UserGroup g2 = createRole("group23");
        GSUser user = createUser("auth22", g1, g2);

        ruleAdminService.insert(new Rule(999, null, null, null, null, "s22", "r22", "w22", "l22", GrantType.ALLOW));

        long id = ruleAdminService.insert(new Rule(15, null, "group22", null, null, "s22", "r22", "w22", "l22", GrantType.LIMIT));
        RuleLimits limits = new RuleLimits();
        limits.setSpatialFilterType(SpatialFilterType.INTERSECT);
        limits.setCatalogMode(CatalogMode.HIDE);
        String areaWKT = "MultiPolygon (((-1.93327272727272859 5.5959090909090925, 2.22727272727272707 5.67609090909091041, 2.00454545454545441 4.07245454545454599, -1.92436363636363761 4.54463636363636425, -1.92436363636363761 4.54463636363636425, -1.93327272727272859 5.5959090909090925)))";
        MultiPolygon area = (MultiPolygon) new WKTReader().read(areaWKT);
        limits.setAllowedArea(area);
        ruleAdminService.setLimits(id, limits);

        long id2 = ruleAdminService.insert(new Rule(16, null, "group23", null, null, "s22", "r22", "w22", "l22", GrantType.LIMIT));
        RuleLimits limits2 = new RuleLimits();
        limits2.setSpatialFilterType(SpatialFilterType.CLIP);
        limits2.setCatalogMode(CatalogMode.HIDE);
        String areaWKT2 = "MultiPolygon (((-1.78181818181818308 5.95227272727272894, -0.16927272727272813 5.4711818181818197, 1.97781818181818148 3.81409090909090986, 1.93327272727272748 2.05009090909090919, -2.6638181818181832 2.64700000000000069, -1.78181818181818308 5.95227272727272894)))";
        MultiPolygon area2 = (MultiPolygon) new WKTReader().read(areaWKT2);
        limits2.setAllowedArea(area2);
        ruleAdminService.setLimits(id2, limits2);
        RuleFilter filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY, true);
        filter.setWorkspace("w22");
        filter.setLayer("l22");
        filter.setUser(user.getName());
        AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
        assertEquals(GrantType.ALLOW, accessInfo.getGrant());
        assertFalse(accessInfo.getAdminRights());

        // we got a user in two groups one with an intersect spatialFilterType
        // and the other with a clip spatialFilterType. The two area should haven
        // been kept separated
        assertNotNull(accessInfo.getAreaWkt());
        assertNotNull(accessInfo.getClipAreaWkt());

        // the intersects should be equal to the originally defined
        // allowed area
        Geometry intersects = new WKTReader().read(accessInfo.getAreaWkt());
        intersects.normalize();
        assertTrue(intersects.equalsExact(area, 10.0E-15));

        Geometry clip = new WKTReader().read(accessInfo.getClipAreaWkt());
        clip.normalize();
        area2.normalize();
        assertTrue(clip.equalsExact(area2, 10.0E-15));
    }

    @Test
    public void testRuleSpatialFilterTypeFourRules() throws ParseException {
        // the user belongs to two groups and there are two rules for each group:
        // INTERSECTS and CLIP for the first, and CLIP CLIP for the second.
        // The expected result is only one allowedArea of type clip
        // obtained by the intersection of the firs two, united with the intersection of the second two.
        // the first INTERSECTS is resolve as CLIP because during constraint resolution the more restrictive
        // type is chosen.

        UserGroup g1 = createRole("group31");
        UserGroup g2 = createRole("group32");
        GSUser user = createUser("auth33", g1, g2);

        WKTReader reader = new WKTReader();

        ruleAdminService.insert(new Rule(999, null, null, null, null, "s22", "r22", "w22", "l22", GrantType.ALLOW));

        long id = ruleAdminService.insert(new Rule(17, null, "group31", null, null, "s22", "r22", "w22", "l22", GrantType.LIMIT));
        RuleLimits limits = new RuleLimits();
        limits.setSpatialFilterType(SpatialFilterType.INTERSECT);
        limits.setCatalogMode(CatalogMode.HIDE);
        String areaWKT = "MultiPolygon (((-1.93327272727272859 5.5959090909090925, 2.22727272727272707 5.67609090909091041, 2.00454545454545441 4.07245454545454599, -1.92436363636363761 4.54463636363636425, -1.92436363636363761 4.54463636363636425, -1.93327272727272859 5.5959090909090925)))";
        MultiPolygon area = (MultiPolygon) reader.read(areaWKT);
        limits.setAllowedArea(area);
        ruleAdminService.setLimits(id, limits);

        long id2 = ruleAdminService.insert(new Rule(18, null, "group31", null, null, "s22", "r22", "w22", "l22", GrantType.LIMIT));
        RuleLimits limits2 = new RuleLimits();
        limits2.setSpatialFilterType(SpatialFilterType.CLIP);
        limits2.setCatalogMode(CatalogMode.HIDE);
        String areaWKT2 = "MultiPolygon (((-1.46109090909091011 5.68500000000000139, -0.68600000000000083 5.7651818181818193, -0.73945454545454625 2.00554545454545519, -1.54127272727272846 1.9610000000000003, -1.46109090909091011 5.68500000000000139)))";
        MultiPolygon area2 = (MultiPolygon) reader.read(areaWKT2);
        limits2.setAllowedArea(area2);
        ruleAdminService.setLimits(id2, limits2);

        long id3 = ruleAdminService.insert(new Rule(19, null, "group32", null, null, "s22", "r22", "w22", "l22", GrantType.LIMIT));
        RuleLimits limits3 = new RuleLimits();
        limits3.setSpatialFilterType(SpatialFilterType.CLIP);
        limits3.setCatalogMode(CatalogMode.HIDE);
        String areaWKT3 = "MultiPolygon (((-1.78181818181818308 5.95227272727272894, -0.16927272727272813 5.4711818181818197, 1.97781818181818148 3.81409090909090986, 1.93327272727272748 2.05009090909090919, -2.6638181818181832 2.64700000000000069, -1.78181818181818308 5.95227272727272894)))";
        MultiPolygon area3 = (MultiPolygon) reader.read(areaWKT3);
        limits3.setAllowedArea(area3);
        ruleAdminService.setLimits(id3, limits3);

        long id4 = ruleAdminService.insert(new Rule(20, null, "group32", null, null, "s22", "r22", "w22", "l22", GrantType.LIMIT));
        RuleLimits limits4 = new RuleLimits();
        limits4.setSpatialFilterType(SpatialFilterType.CLIP);
        limits4.setCatalogMode(CatalogMode.HIDE);
        String areaWKT4 = "MultiPolygon (((-1.30963636363636482 5.96118181818181991, 1.78181818181818175 4.84754545454545571, -0.90872727272727349 2.26390909090909132, -1.30963636363636482 5.96118181818181991)))";
        MultiPolygon area4 = (MultiPolygon) reader.read(areaWKT4);
        limits4.setAllowedArea(area4);
        ruleAdminService.setLimits(id4, limits4);

        RuleFilter filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY, true);
        filter.setWorkspace("w22");
        filter.setLayer("l22");
        filter.setUser(user.getName());
        AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
        assertEquals(GrantType.ALLOW, accessInfo.getGrant());
        assertFalse(accessInfo.getAdminRights());
        // we should have only the clip geometry
        assertNull(accessInfo.getAreaWkt());
        assertNotNull(accessInfo.getClipAreaWkt());

        // the intersects should be equal to the originally defined
        // allowed area
        Geometry expectedResult = area.intersection(area2).union(area3.intersection(area4));
        expectedResult.normalize();
        Geometry clip = reader.read(accessInfo.getClipAreaWkt());
        clip.normalize();
        assertTrue(clip.equalsExact(expectedResult, 10.0E-15));
    }

    @Test
    public void testRuleSpatialFilterTypeFourRules2() throws ParseException {
        // the user belongs to two groups and there are two rules for each group:
        // CLIP and CLIP for the first, and INTERSECTS INTERSECTS for the second.
        // The expected result are two allowedArea the first of type clip and second of type intersects.

        UserGroup g1 = createRole("group41");
        UserGroup g2 = createRole("group42");
        GSUser user = createUser("auth44", g1, g2);

        WKTReader reader = new WKTReader();

        ruleAdminService.insert(new Rule(999, null, null, null, null, "s22", "r22", "w22", "l22", GrantType.ALLOW));

        long id = ruleAdminService.insert(new Rule(21, null, "group41", null, null, "s22", "r22", "w22", "l22", GrantType.LIMIT));
        RuleLimits limits = new RuleLimits();
        limits.setSpatialFilterType(SpatialFilterType.CLIP);
        limits.setCatalogMode(CatalogMode.HIDE);
        String areaWKT = "MultiPolygon (((-1.93327272727272859 5.5959090909090925, 2.22727272727272707 5.67609090909091041, 2.00454545454545441 4.07245454545454599, -1.92436363636363761 4.54463636363636425, -1.92436363636363761 4.54463636363636425, -1.93327272727272859 5.5959090909090925)))";
        MultiPolygon area = (MultiPolygon) reader.read(areaWKT);
        limits.setAllowedArea(area);
        ruleAdminService.setLimits(id, limits);

        long id2 = ruleAdminService.insert(new Rule(22, null, "group41", null, null, "s22", "r22", "w22", "l22", GrantType.LIMIT));
        RuleLimits limits2 = new RuleLimits();
        limits2.setSpatialFilterType(SpatialFilterType.CLIP);
        limits2.setCatalogMode(CatalogMode.HIDE);
        String areaWKT2 = "MultiPolygon (((-1.46109090909091011 5.68500000000000139, -0.68600000000000083 5.7651818181818193, -0.73945454545454625 2.00554545454545519, -1.54127272727272846 1.9610000000000003, -1.46109090909091011 5.68500000000000139)))";
        MultiPolygon area2 = (MultiPolygon) reader.read(areaWKT2);
        limits2.setAllowedArea(area2);
        ruleAdminService.setLimits(id2, limits2);

        long id3 = ruleAdminService.insert(new Rule(23, null, "group42", null, null, "s22", "r22", "w22", "l22", GrantType.LIMIT));
        RuleLimits limits3 = new RuleLimits();
        limits3.setSpatialFilterType(SpatialFilterType.INTERSECT);
        limits3.setCatalogMode(CatalogMode.HIDE);
        String areaWKT3 = "MultiPolygon (((-1.78181818181818308 5.95227272727272894, -0.16927272727272813 5.4711818181818197, 1.97781818181818148 3.81409090909090986, 1.93327272727272748 2.05009090909090919, -2.6638181818181832 2.64700000000000069, -1.78181818181818308 5.95227272727272894)))";
        MultiPolygon area3 = (MultiPolygon) reader.read(areaWKT3);
        limits3.setAllowedArea(area3);
        ruleAdminService.setLimits(id3, limits3);

        long id4 = ruleAdminService.insert(new Rule(24, null, "group42", null, null, "s22", "r22", "w22", "l22", GrantType.LIMIT));
        RuleLimits limits4 = new RuleLimits();
        limits4.setSpatialFilterType(SpatialFilterType.INTERSECT);
        limits4.setCatalogMode(CatalogMode.HIDE);
        String areaWKT4 = "MultiPolygon (((-1.30963636363636482 5.96118181818181991, 1.78181818181818175 4.84754545454545571, -0.90872727272727349 2.26390909090909132, -1.30963636363636482 5.96118181818181991)))";
        MultiPolygon area4 = (MultiPolygon) reader.read(areaWKT4);
        limits4.setAllowedArea(area4);
        ruleAdminService.setLimits(id4, limits4);

        RuleFilter filter = new RuleFilter(RuleFilter.SpecialFilterType.ANY, true);
        filter.setWorkspace("w22");
        filter.setLayer("l22");
        filter.setUser(user.getName());
        AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
        assertEquals(GrantType.ALLOW, accessInfo.getGrant());
        assertFalse(accessInfo.getAdminRights());

        // we should have both
        assertNotNull(accessInfo.getAreaWkt());
        assertNotNull(accessInfo.getClipAreaWkt());

        // the intersects should be equal to the originally defined
        // allowed area
        Geometry expectedIntersects = area3.intersection(area4);
        expectedIntersects.normalize();
        Geometry intersects = reader.read(accessInfo.getAreaWkt());
        intersects.normalize();
        System.out.println(intersects.toString());
        System.out.println(expectedIntersects.toString());
        assertTrue(expectedIntersects.equalsExact(intersects, 10.0E-15));

        Geometry clip = reader.read(accessInfo.getClipAreaWkt());
        clip.normalize();
        Geometry expectedClip = area2.intersection(area);
        expectedClip.normalize();
        assertTrue(expectedClip.equalsExact(clip, 10.0E-15));

    }

}

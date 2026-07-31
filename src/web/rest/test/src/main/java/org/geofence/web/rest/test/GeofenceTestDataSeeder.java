/* (c) 2014 - 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.test;

import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.model.GSInstance;
import org.geofence.core.model.GSUser;
import org.geofence.core.model.LayerAttribute;
import org.geofence.core.model.LayerDetails;
import org.geofence.core.model.Rule;
import org.geofence.core.model.UserGroup;
import org.geofence.core.model.enums.AccessType;
import org.geofence.core.model.enums.GrantType;
import org.geofence.core.services.InstanceAdminService;
import org.geofence.core.services.RuleAdminService;
import org.geofence.core.services.UserAdminService;
import org.geofence.core.services.UserGroupAdminService;
import org.geofence.core.services.dto.ShortGroup;
import org.geofence.web.rest.utils.InstanceCleaner;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Wipes and re-seeds a fixed set of test data on startup - only present on this test webapp's classpath, not on
 * {@code geofence-web-app}'s own, so a real GeoFence deployment never runs this. Seeds the rules GeoServer's own
 * {@code GeofenceBaseTest}-derived integration tests expect to find, plus the original sample group/user/instance/rule
 * data this module's old {@code MainTest} class used to create (kept for any test relying on it, even though nothing
 * currently asserts against it directly).
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Component
public class GeofenceTestDataSeeder {

    private static final Logger LOGGER = LogManager.getLogger(GeofenceTestDataSeeder.class);

    @Autowired
    private InstanceCleaner instanceCleaner;

    @Autowired
    private RuleAdminService ruleAdminService;

    @Autowired
    private UserGroupAdminService userGroupAdminService;

    @Autowired
    private UserAdminService userAdminService;

    @Autowired
    private InstanceAdminService instanceAdminService;

    @PostConstruct
    public void seed() throws Exception {
        LOGGER.info("===== Seeding GeoFence test data =====");

        instanceCleaner.removeAll();

        seedGeofenceAccessManagerTestRules();
        seedLegacySampleData();

        LOGGER.info("===== GeoFence test data seeded =====");
    }

    /**
     * Rules matching what {@code GefenceAccessManagerTest} (in geoserver3's geofence extension) actually asserts
     * against - workspace/layer names below are {@code MockData}'s own constants (CITE_PREFIX/BASIC_POLYGONS,
     * SF_PREFIX/GENERICENTITY).
     */
    private void seedGeofenceAccessManagerTestRules() throws Exception {
        // admin -> unconditional ALLOW
        ruleAdminService.insert(new Rule(1, "admin", null, null, null, null, null, null, null, null, GrantType.ALLOW));

        // cite -> full ALLOW on its own layer (cite:BasicPolygons)
        ruleAdminService.insert(
                new Rule(2, "cite", null, null, null, null, null, null, "cite", "BasicPolygons", GrantType.ALLOW));

        // area -> ALLOW on sf:GenericEntity, restricted to a specific allowed area via LayerDetails below
        // (setDetails only accepts ALLOW rules - the "limited" semantic comes from the area restriction itself)
        Rule areaRule = new Rule(3, "area", null, null, null, null, null, null, "sf", "GenericEntity", GrantType.ALLOW);
        ruleAdminService.insert(areaRule);
        LayerDetails areaDetails = new LayerDetails();
        MultiPolygon allowedArea =
                (MultiPolygon) new WKTReader().read("MULTIPOLYGON(((48 62, 48 63, 49 63, 49 62, 48 62)))");
        areaDetails.setArea(allowedArea);
        ruleAdminService.setDetails(areaRule.getId(), areaDetails);

        // wmsuser -> full ALLOW on sf:GenericEntity (regardless of service)
        ruleAdminService.insert(
                new Rule(4, "wmsuser", null, null, null, null, null, null, "sf", "GenericEntity", GrantType.ALLOW));

        // any user, WMS service -> ALLOW
        ruleAdminService.insert(new Rule(10, null, null, null, null, "WMS", null, null, null, null, GrantType.ALLOW));

        // catch-all -> DENY (lowest priority, evaluated last)
        ruleAdminService.insert(new Rule(100, null, null, null, null, null, null, null, null, null, GrantType.DENY));
    }

    /** The original sample data this module's old {@code MainTest} class seeded - kept as-is, at lower priority. */
    private void seedLegacySampleData() {
        ShortGroup group1 = new ShortGroup();
        group1.setName("test_profile");
        long group1Id = userGroupAdminService.insert(group1);

        ShortGroup group2 = new ShortGroup();
        group2.setName("test_profile2");
        long group2Id = userGroupAdminService.insert(group2);
        UserGroup group2Entity = userGroupAdminService.get(group2Id);

        GSUser sampleAdmin = new GSUser();
        sampleAdmin.setAdmin(true);
        sampleAdmin.setName("admin");
        sampleAdmin.setPassword("password");
        sampleAdmin.getGroups().add(userGroupAdminService.get(group1Id));
        sampleAdmin.setEnabled(true);
        sampleAdmin.setFullName("Sample G.S. Admin");
        sampleAdmin.setEmailAddress("gs.admin@geofence.net");
        sampleAdmin.setExtId("sample_geoserver_user");
        userAdminService.insert(sampleAdmin);

        GSInstance instance = new GSInstance();
        instance.setName("geoserver01");
        instance.setUsername("admin");
        instance.setPassword("geoserver");
        instance.setBaseURL("http://localhost/geoserver");
        instance.setDescription("A sample instance");
        instanceAdminService.insert(instance);

        ruleAdminService.insert(new Rule(
                50,
                sampleAdmin.getName(),
                group2Entity.getName(),
                instance,
                null,
                "s0",
                "r0",
                null,
                null,
                null,
                GrantType.ALLOW));

        Rule r1 = new Rule(60, null, null, null, null, "s1", "r1", null, "w1", "l1", GrantType.ALLOW);
        ruleAdminService.insert(r1);

        LayerDetails details = new LayerDetails();
        details.getAllowedStyles().add("style1");
        details.getAllowedStyles().add("style2");
        Set<LayerAttribute> attributes = new HashSet<>();
        attributes.add(new LayerAttribute("attr1", AccessType.NONE));
        attributes.add(new LayerAttribute("attr2", AccessType.READONLY));
        attributes.add(new LayerAttribute("attr3", AccessType.READWRITE));
        details.setAttributes(attributes);
        ruleAdminService.setDetails(r1.getId(), details);
    }
}

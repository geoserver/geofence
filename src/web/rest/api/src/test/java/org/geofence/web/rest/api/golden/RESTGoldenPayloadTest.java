/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.golden;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.geofence.web.rest.api.interfaces.params.RESTAdminRuleFilter;
import org.geofence.web.rest.api.interfaces.params.RESTRuleFilter;
import org.geofence.web.rest.api.model.RESTAccessInfo;
import org.geofence.web.rest.api.model.RESTBatch;
import org.geofence.web.rest.api.model.RESTBatchOperation;
import org.geofence.web.rest.api.model.RESTInputAdminRule;
import org.geofence.web.rest.api.model.RESTInputGroup;
import org.geofence.web.rest.api.model.RESTInputInstance;
import org.geofence.web.rest.api.model.RESTInputRule;
import org.geofence.web.rest.api.model.RESTInputUser;
import org.geofence.web.rest.api.model.RESTLayerAttribute;
import org.geofence.web.rest.api.model.RESTLayerConstraints;
import org.geofence.web.rest.api.model.RESTOutputAdminRule;
import org.geofence.web.rest.api.model.RESTOutputAdminRuleList;
import org.geofence.web.rest.api.model.RESTOutputGroup;
import org.geofence.web.rest.api.model.RESTOutputInstance;
import org.geofence.web.rest.api.model.RESTOutputRule;
import org.geofence.web.rest.api.model.RESTOutputRuleList;
import org.geofence.web.rest.api.model.RESTOutputUser;
import org.geofence.web.rest.api.model.RESTRulePosition;
import org.geofence.web.rest.api.model.RESTShortInstance;
import org.geofence.web.rest.api.model.RESTShortInstanceList;
import org.geofence.web.rest.api.model.RESTShortRule;
import org.geofence.web.rest.api.model.RESTShortRuleList;
import org.geofence.web.rest.api.model.RESTShortUser;
import org.geofence.web.rest.api.model.RESTShortUserGroup;
import org.geofence.web.rest.api.model.RESTShortUserGroupList;
import org.geofence.web.rest.api.model.RESTShortUserList;
import org.geofence.web.rest.api.model.config.RESTFullConfiguration;
import org.geofence.web.rest.api.model.config.RESTFullGSInstanceList;
import org.geofence.web.rest.api.model.config.RESTFullRuleList;
import org.geofence.web.rest.api.model.config.RESTFullUserGroupList;
import org.geofence.web.rest.api.model.config.RESTFullUserList;
import org.geofence.web.rest.api.model.config.RESTInstance;
import org.geofence.web.rest.api.model.config.RESTRule;
import org.geofence.web.rest.api.model.config.RESTUser;
import org.geofence.web.rest.api.model.config.RESTUserGroup;
import org.geofence.web.rest.api.model.config.adapter.MapType;
import org.geofence.web.rest.api.model.config.adapter.RemappedType;
import org.geofence.web.rest.api.model.enums.RESTAccessType;
import org.geofence.web.rest.api.model.enums.RESTAdminGrantType;
import org.geofence.web.rest.api.model.enums.RESTCatalogMode;
import org.geofence.web.rest.api.model.enums.RESTGrantType;
import org.geofence.web.rest.api.model.enums.RESTLayerType;
import org.geofence.web.rest.api.model.enums.RESTSpatialFilterType;
import org.geofence.web.rest.api.model.util.IdName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/**
 * Golden-payload safety net for the whole REST DTO surface (see {@link GoldenPayloadSupport}) - one fully-populated
 * instance per top-level marshallable DTO in {@code web/rest/api/model}, checked against a saved XML and JSON reference
 * file. Not testing correctness of these DTOs (that's covered elsewhere) - only that today's JAX-RS/JAXB/Jackson stack
 * keeps producing the same wire format, so a future library swap can be checked against a known-good baseline instead
 * of "looks about right."
 */
public class RESTGoldenPayloadTest {

    private record Fixture(String name, Object value) {}

    @TestFactory
    Stream<DynamicTest> goldenPayloads() {
        return fixtures().stream()
                .flatMap(f -> Stream.of(
                        DynamicTest.dynamicTest(
                                f.name() + " (xml)",
                                () -> GoldenPayloadSupport.assertMatchesGoldenXml(f.name(), f.value())),
                        DynamicTest.dynamicTest(
                                f.name() + " (json)",
                                () -> GoldenPayloadSupport.assertMatchesGoldenJson(f.name(), f.value()))));
    }

    private List<Fixture> fixtures() {
        List<Fixture> list = new ArrayList<>();
        list.add(new Fixture("RESTBatch", buildBatch()));
        list.add(new Fixture("RESTBatchOperation", buildBatchOperation()));
        list.add(new Fixture("RESTInputInstance", buildInputInstance()));
        list.add(new Fixture("RESTOutputInstance", buildOutputInstance()));
        list.add(new Fixture("RESTShortInstance", buildShortInstance()));
        list.add(new Fixture("RESTShortInstanceList", buildShortInstanceList()));
        list.add(new Fixture("RESTShortUserGroup", buildShortUserGroup("g1")));
        list.add(new Fixture("RESTShortUserGroupList", buildShortUserGroupList()));
        list.add(new Fixture("RESTOutputGroup", buildOutputGroup()));
        list.add(new Fixture("RESTInputUser", buildInputUser()));
        list.add(new Fixture("RESTOutputUser", buildOutputUser()));
        list.add(new Fixture("RESTInputGroup", buildInputGroup()));
        list.add(new Fixture("RESTInputRule", buildInputRule()));
        list.add(new Fixture("RESTInputAdminRule", buildInputAdminRule()));
        list.add(new Fixture("RESTOutputAdminRule", buildOutputAdminRule()));
        list.add(new Fixture("RESTOutputAdminRuleList", buildOutputAdminRuleList()));
        list.add(new Fixture("IdName", new IdName(7L, "named-instance")));
        list.add(new Fixture("RESTUser", buildConfigUser()));
        list.add(new Fixture("RESTUserGroup", buildConfigUserGroup("grp1")));
        list.add(new Fixture("RESTInstance", buildConfigInstance()));
        list.add(new Fixture("RESTRule", buildConfigRule()));
        list.add(new Fixture("RESTFullUserList", buildFullUserList()));
        list.add(new Fixture("RESTFullUserGroupList", buildFullUserGroupList()));
        list.add(new Fixture("RESTFullGSInstanceList", buildFullGSInstanceList()));
        list.add(new Fixture("RESTFullRuleList", buildFullRuleList()));
        list.add(new Fixture("RESTFullConfiguration", buildFullConfiguration()));
        list.add(new Fixture("RemappedType", new RemappedType(1L, 2L)));
        list.add(new Fixture("MapType", buildMapType()));
        list.add(new Fixture("RESTOutputRule", buildOutputRule()));
        list.add(new Fixture("RESTOutputRuleList", buildOutputRuleList()));
        list.add(new Fixture("RESTShortUser", buildShortUser("user1")));
        list.add(new Fixture("RESTShortUserList", buildShortUserList()));
        list.add(new Fixture("RESTLayerAttribute", buildLayerAttribute("attr1")));
        list.add(new Fixture("RESTLayerConstraints", buildLayerConstraints()));
        list.add(new Fixture("RESTAccessInfo", buildAccessInfo()));
        list.add(new Fixture("RESTShortRule", buildShortRule()));
        list.add(new Fixture("RESTShortRuleList", buildShortRuleList()));
        list.add(new Fixture("RESTRuleFilter", buildRuleFilter()));
        list.add(new Fixture("RESTAdminRuleFilter", buildAdminRuleFilter()));
        return list;
    }

    // ==========================================================================

    private RESTBatch buildBatch() {
        RESTBatch batch = new RESTBatch();
        batch.add(buildBatchOperation());
        RESTBatchOperation second = new RESTBatchOperation();
        second.setService(RESTBatchOperation.ServiceName.groups);
        second.setType(RESTBatchOperation.TypeName.delete);
        second.setId(5L);
        second.setName("group-to-delete");
        second.setCascade(true);
        batch.add(second);
        return batch;
    }

    private RESTBatchOperation buildBatchOperation() {
        RESTBatchOperation op = new RESTBatchOperation();
        op.setService(RESTBatchOperation.ServiceName.rules);
        op.setType(RESTBatchOperation.TypeName.insert);
        op.setId(1L);
        op.setName("rule-op");
        op.setCascade(false);
        op.setUserName("bob");
        op.setGroupName("admins");
        op.setPayload(buildInputRule());
        return op;
    }

    private RESTInputInstance buildInputInstance() {
        RESTInputInstance i = new RESTInputInstance();
        i.setName("instance1");
        i.setDescription("a test instance");
        i.setBaseURL("http://geoserver.example.org/geoserver");
        i.setUsername("admin");
        i.setPassword("secret");
        return i;
    }

    private RESTOutputInstance buildOutputInstance() {
        RESTOutputInstance i = new RESTOutputInstance();
        i.setId(1L);
        i.setName("instance1");
        i.setDescription("a test instance");
        i.setCreationDate("2026-01-01T00:00:00");
        i.setBaseURL("http://geoserver.example.org/geoserver");
        i.setUsername("admin");
        i.setPassword("secret");
        return i;
    }

    private RESTShortInstance buildShortInstance() {
        RESTShortInstance i = new RESTShortInstance();
        i.setId(1L);
        i.setName("instance1");
        i.setUrl("http://geoserver.example.org/geoserver");
        i.setDescription("a test instance");
        return i;
    }

    private RESTShortInstanceList buildShortInstanceList() {
        RESTShortInstanceList list = new RESTShortInstanceList();
        list.add(buildShortInstance());
        RESTShortInstance i2 = new RESTShortInstance();
        i2.setId(2L);
        i2.setName("instance2");
        i2.setUrl("http://geoserver2.example.org/geoserver");
        list.add(i2);
        return list;
    }

    private RESTShortUserGroup buildShortUserGroup(String name) {
        RESTShortUserGroup g = new RESTShortUserGroup();
        g.setId((long) name.hashCode());
        g.setName(name);
        return g;
    }

    private RESTShortUserGroupList buildShortUserGroupList() {
        RESTShortUserGroupList list = new RESTShortUserGroupList();
        list.add(buildShortUserGroup("g1"));
        list.add(buildShortUserGroup("g2"));
        return list;
    }

    private RESTOutputGroup buildOutputGroup() {
        RESTOutputGroup g = new RESTOutputGroup();
        g.setId(1L);
        g.setName("group1");
        g.setExtId("ext-group1");
        g.setEnabled(true);
        return g;
    }

    private RESTInputUser buildInputUser() {
        RESTInputUser u = new RESTInputUser();
        u.setExtId("ext-user1");
        u.setName("user1");
        u.setPassword("secret");
        u.setFullName("User One");
        u.setEmailAddress("user1@example.org");
        u.setEnabled(true);
        u.setAdmin(false);
        List<IdName> groups = new ArrayList<>();
        groups.add(new IdName(1L, "group1"));
        groups.add(new IdName(2L, "group2"));
        u.setGroups(groups);
        return u;
    }

    private RESTOutputUser buildOutputUser() {
        RESTOutputUser u = new RESTOutputUser(1L, "user1");
        u.setExtId("ext-user1");
        u.setFullName("User One");
        u.setEmailAddress("user1@example.org");
        u.setEnabled(true);
        u.setAdmin(false);
        List<IdName> groups = new ArrayList<>();
        groups.add(new IdName(1L, "group1"));
        groups.add(new IdName(2L, "group2"));
        u.setGroups(groups);
        return u;
    }

    private RESTInputGroup buildInputGroup() {
        RESTInputGroup g = new RESTInputGroup();
        g.setName("group1");
        g.setExtId("ext-group1");
        g.setEnabled(true);
        return g;
    }

    private RESTInputRule buildInputRule() {
        RESTInputRule r = new RESTInputRule();
        r.setPosition(new RESTRulePosition(RESTRulePosition.RESTPositionReference.fixedPriority, 10));
        r.setGrant(RESTGrantType.ALLOW);
        r.setUsername("user1");
        r.setRolename("role1");
        r.setInstance(new IdName(1L, "instance1"));
        r.setIpaddress("10.0.0.0/8");
        r.setValidafter("2026-01-01");
        r.setValidbefore("2026-12-31");
        r.setService("WMS");
        r.setRequest("GetMap");
        r.setSubfield("sub1");
        r.setWorkspace("topp");
        r.setLayer("states");
        r.setConstraints(buildLayerConstraints());
        return r;
    }

    private RESTInputAdminRule buildInputAdminRule() {
        RESTInputAdminRule r = new RESTInputAdminRule();
        r.setPosition(new RESTRulePosition(RESTRulePosition.RESTPositionReference.offsetFromTop, 1));
        r.setGrant(RESTAdminGrantType.ADMIN);
        r.setUsername("user1");
        r.setRolename("role1");
        r.setInstance(new IdName(1L, "instance1"));
        r.setWorkspace("topp");
        return r;
    }

    private RESTOutputAdminRule buildOutputAdminRule() {
        RESTOutputAdminRule r = new RESTOutputAdminRule();
        r.setId(1L);
        r.setPriority(10L);
        r.setUsername("user1");
        r.setRolename("role1");
        r.setInstance(new IdName(1L, "instance1"));
        r.setWorkspace("topp");
        r.setGrant(RESTAdminGrantType.ADMIN);
        return r;
    }

    private RESTOutputAdminRuleList buildOutputAdminRuleList() {
        RESTOutputAdminRuleList list = new RESTOutputAdminRuleList();
        list.add(buildOutputAdminRule());
        RESTOutputAdminRule r2 = new RESTOutputAdminRule();
        r2.setId(2L);
        r2.setPriority(20L);
        r2.setGrant(RESTAdminGrantType.USER);
        list.add(r2);
        return list;
    }

    private RESTUser buildConfigUser() {
        RESTUser u = new RESTUser();
        u.setId(1L);
        u.setExtId("ext-user1");
        u.setName("user1");
        u.setFullName("User One");
        u.setPassword("secret");
        u.setEmailAddress("user1@example.org");
        u.setDateCreation(LocalDateTime.of(2026, 1, 1, 0, 0, 0));
        u.setAdmin(true);
        u.setEnabled(true);
        Set<RESTUserGroup> groups = new LinkedHashSet<>();
        groups.add(buildConfigUserGroup("grp1"));
        groups.add(buildConfigUserGroup("grp2"));
        u.setGroups(groups);
        return u;
    }

    private RESTUserGroup buildConfigUserGroup(String name) {
        RESTUserGroup g = new RESTUserGroup();
        g.setId((long) name.hashCode());
        g.setExtId("ext-" + name);
        g.setName(name);
        g.setDateCreation(LocalDateTime.of(2026, 1, 1, 0, 0, 0));
        g.setEnabled(true);
        return g;
    }

    private RESTInstance buildConfigInstance() {
        RESTInstance i = new RESTInstance();
        i.setId(1L);
        i.setName("instance1");
        i.setDescription("a test instance");
        i.setDateCreation(LocalDateTime.of(2026, 1, 1, 0, 0, 0));
        i.setBaseURL("http://geoserver.example.org/geoserver");
        i.setUsername("admin");
        i.setPassword("secret");
        return i;
    }

    private RESTRule buildConfigRule() {
        RESTRule r = new RESTRule(
                10,
                "user1",
                "role1",
                "instance1",
                "10.0.0.0/8",
                new Date(1767225600000L),
                new Date(1798761600000L),
                "WMS",
                "GetMap",
                "sub1",
                "topp",
                "states",
                RESTGrantType.ALLOW);
        r.setId(1L);
        return r;
    }

    private RESTFullUserList buildFullUserList() {
        RESTFullUserList list = new RESTFullUserList();
        list.add(buildConfigUser());
        return list;
    }

    private RESTFullUserGroupList buildFullUserGroupList() {
        RESTFullUserGroupList list = new RESTFullUserGroupList();
        list.add(buildOutputGroup());
        return list;
    }

    private RESTFullGSInstanceList buildFullGSInstanceList() {
        RESTFullGSInstanceList list = new RESTFullGSInstanceList();
        list.add(buildConfigInstance());
        return list;
    }

    private RESTFullRuleList buildFullRuleList() {
        RESTFullRuleList list = new RESTFullRuleList();
        list.add(buildConfigRule());
        return list;
    }

    private RESTFullConfiguration buildFullConfiguration() {
        RESTFullConfiguration config = new RESTFullConfiguration();
        config.setUserGroupList(buildFullUserGroupList());
        config.setUserList(buildFullUserList());
        config.setGsInstanceList(buildFullGSInstanceList());
        config.setRuleList(buildFullRuleList());
        return config;
    }

    private MapType buildMapType() {
        MapType map = new MapType();
        map.add(java.util.Map.entry(1L, 10L));
        map.add(java.util.Map.entry(2L, 20L));
        return map;
    }

    private RESTOutputRule buildOutputRule() {
        RESTOutputRule r = new RESTOutputRule();
        r.setId(1L);
        r.setPriority(10L);
        r.setUsername("user1");
        r.setRolename("role1");
        r.setInstance(new IdName(1L, "instance1"));
        r.setIpaddress("10.0.0.0/8");
        r.setValidafter("2026-01-01");
        r.setValidbefore("2026-12-31");
        r.setService("WMS");
        r.setRequest("GetMap");
        r.setSubfield("sub1");
        r.setWorkspace("topp");
        r.setLayer("states");
        r.setGrant(RESTGrantType.ALLOW);
        r.setConstraints(buildLayerConstraints());
        return r;
    }

    private RESTOutputRuleList buildOutputRuleList() {
        RESTOutputRuleList list = new RESTOutputRuleList();
        list.add(buildOutputRule());
        RESTOutputRule r2 = new RESTOutputRule();
        r2.setId(2L);
        r2.setPriority(20L);
        r2.setGrant(RESTGrantType.DENY);
        list.add(r2);
        return list;
    }

    private RESTShortUser buildShortUser(String name) {
        RESTShortUser u = new RESTShortUser();
        u.setId((long) name.hashCode());
        u.setExtId("ext-" + name);
        u.setUserName(name);
        u.setEnabled(true);
        return u;
    }

    private RESTShortUserList buildShortUserList() {
        RESTShortUserList list = new RESTShortUserList();
        list.add(buildShortUser("user1"));
        list.add(buildShortUser("user2"));
        return list;
    }

    private RESTLayerAttribute buildLayerAttribute(String name) {
        return new RESTLayerAttribute(name, "java.lang.String", RESTAccessType.READWRITE);
    }

    private RESTLayerConstraints buildLayerConstraints() {
        RESTLayerConstraints c = new RESTLayerConstraints();
        c.setType(RESTLayerType.VECTOR);
        c.setDefaultStyle("population");
        c.setCqlFilterRead("READ=1");
        c.setCqlFilterWrite("WRITE=1");
        c.setRestrictedAreaWkt("SRID=4326;POLYGON((0 0,0 1,1 1,1 0,0 0))");
        c.setSpatialFilterType(RESTSpatialFilterType.INTERSECT);
        Set<String> styles = new LinkedHashSet<>();
        styles.add("style1");
        styles.add("style2");
        c.setAllowedStyles(styles);
        // Single element only: RESTLayerConstraints.setAttributes() copies into a plain HashSet, and
        // RESTLayerAttribute's hashCode() folds in RESTAccessType's default (identity-based) enum hashCode -
        // with 2+ elements the iteration order isn't stable across JVM launches, which would make this golden
        // file flaky through no fault of the REST layer itself.
        Set<RESTLayerAttribute> attrs = new LinkedHashSet<>();
        attrs.add(buildLayerAttribute("attr1"));
        c.setAttributes(attrs);
        return c;
    }

    private RESTAccessInfo buildAccessInfo() {
        RESTAccessInfo info = new RESTAccessInfo();
        info.setGrant(RESTGrantType.LIMIT);
        info.setAdminRights(true);
        info.setAreaWkt("SRID=4326;POLYGON((0 0,0 1,1 1,1 0,0 0))");
        info.setClipAreaWkt("SRID=4326;POLYGON((0 0,0 2,2 2,2 0,0 0))");
        info.setCatalogMode(RESTCatalogMode.MIXED);
        info.setDefaultStyle("population");
        info.setCqlFilterRead("READ=1");
        info.setCqlFilterWrite("WRITE=1");
        Set<String> styles = new LinkedHashSet<>();
        styles.add("style1");
        styles.add("style2");
        info.setAllowedStyles(styles);
        Set<RESTLayerAttribute> attrs = new LinkedHashSet<>();
        attrs.add(buildLayerAttribute("attr1"));
        info.setAttributes(attrs);
        return info;
    }

    private RESTShortRule buildShortRule() {
        RESTShortRule r = new RESTShortRule();
        r.setId(1L);
        r.setPriority(10L);
        r.setUserName("user1");
        r.setRoleName("role1");
        r.setInstanceId(1L);
        r.setInstanceName("instance1");
        r.setAddressRange("10.0.0.0/8");
        r.setValidAfter("2026-01-01");
        r.setValidBefore("2026-12-31");
        r.setService("WMS");
        r.setRequest("GetMap");
        r.setSubfield("sub1");
        r.setWorkspace("topp");
        r.setLayer("states");
        r.setAccess(RESTGrantType.ALLOW);
        return r;
    }

    private RESTShortRuleList buildShortRuleList() {
        RESTShortRuleList list = new RESTShortRuleList();
        list.add(buildShortRule());
        RESTShortRule r2 = new RESTShortRule();
        r2.setId(2L);
        r2.setPriority(20L);
        r2.setAccess(RESTGrantType.DENY);
        list.add(r2);
        return list;
    }

    private RESTRuleFilter buildRuleFilter() {
        RESTRuleFilter f = new RESTRuleFilter();
        f.userName = "user1";
        f.userDefault = true;
        f.groupName = "role1";
        f.groupDefault = true;
        f.instanceName = "instance1";
        f.instanceDefault = true;
        f.ipAddress = "10.0.0.0/8";
        f.ipAddressDefault = true;
        f.date = "2026-01-01";
        f.dateDefault = true;
        f.serviceName = "WMS";
        f.serviceDefault = true;
        f.requestName = "GetMap";
        f.requestDefault = true;
        f.subfieldName = "sub1";
        f.subfieldDefault = true;
        f.workspace = "topp";
        f.workspaceDefault = true;
        f.layer = "states";
        f.layerDefault = true;
        return f;
    }

    private RESTAdminRuleFilter buildAdminRuleFilter() {
        RESTAdminRuleFilter f = new RESTAdminRuleFilter();
        f.userName = "user1";
        f.userDefault = true;
        f.groupName = "role1";
        f.groupDefault = true;
        f.instanceName = "instance1";
        f.instanceDefault = true;
        f.workspace = "topp";
        f.workspaceDefault = true;
        return f;
    }
}

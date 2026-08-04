/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.xml.bind.JAXB;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;
import org.geofence.web.rest.api.interfaces.params.RESTRuleFilter;
import org.geofence.web.rest.api.model.RESTAccessInfo;
import org.geofence.web.rest.api.model.RESTLayerAttribute;
import org.geofence.web.rest.api.model.RESTPermsResult;
import org.geofence.web.rest.api.model.RESTShortRule;
import org.geofence.web.rest.api.model.RESTShortRuleList;
import org.geofence.web.rest.api.model.enums.RESTAccessType;
import org.geofence.web.rest.api.model.enums.RESTCatalogMode;
import org.geofence.web.rest.api.model.enums.RESTGrantType;
import org.junit.jupiter.api.Test;

/**
 * JAXB round-trip checks for the runtime rule-evaluation DTOs - {@link RESTRuleFilter} is already covered by
 * {@code RESTRuleServiceImplTest}'s list-query path, so this focuses on the new output shapes.
 */
public class RESTRuleReaderModelTest {

    private <T> T roundTrip(T value, Class<T> type) {
        StringWriter w = new StringWriter();
        JAXB.marshal(value, w);
        String xml = w.toString();
        return JAXB.unmarshal(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), type);
    }

    @Test
    public void testAccessInfoRoundTrip() {
        RESTAccessInfo info = new RESTAccessInfo();
        info.setGrant(RESTGrantType.LIMIT);
        info.setAdminRights(true);
        info.setAreaWkt("SRID=4326;POLYGON((0 0,0 1,1 1,1 0,0 0))");
        info.setClipAreaWkt("SRID=4326;POLYGON((0 0,0 2,2 2,2 0,0 0))");
        info.setCatalogMode(RESTCatalogMode.MIXED);
        info.setDefaultStyle("population");
        info.setCqlFilterRead("READ=1");
        info.setCqlFilterWrite("WRITE=1");
        info.setAllowedStyles(Set.of("style1", "style2"));
        RESTLayerAttribute attr = new RESTLayerAttribute("attr1", "java.lang.String", RESTAccessType.READWRITE);
        info.setAttributes(Set.of(attr));

        RESTAccessInfo out = roundTrip(info, RESTAccessInfo.class);

        assertEquals(RESTGrantType.LIMIT, out.getGrant());
        assertTrue(out.isAdminRights());
        assertEquals(info.getAreaWkt(), out.getAreaWkt());
        assertEquals(info.getClipAreaWkt(), out.getClipAreaWkt());
        assertEquals(RESTCatalogMode.MIXED, out.getCatalogMode());
        assertEquals("population", out.getDefaultStyle());
        assertEquals("READ=1", out.getCqlFilterRead());
        assertEquals("WRITE=1", out.getCqlFilterWrite());
        assertEquals(Set.of("style1", "style2"), out.getAllowedStyles());
        assertEquals(1, out.getAttributes().size());
        RESTLayerAttribute outAttr = out.getAttributes().iterator().next();
        assertEquals("attr1", outAttr.getName());
        assertEquals("java.lang.String", outAttr.getDatatype());
        assertEquals(RESTAccessType.READWRITE, outAttr.getAccess());
    }

    @Test
    public void testShortRuleListRoundTrip() {
        RESTShortRuleList list = new RESTShortRuleList();

        RESTShortRule r1 = new RESTShortRule();
        r1.setId(1L);
        r1.setPriority(10L);
        r1.setUserName("user1");
        r1.setRoleName("role1");
        r1.setInstanceId(5L);
        r1.setInstanceName("instance1");
        r1.setAddressRange("127.0.0.1/32");
        r1.setValidAfter("2026-01-01");
        r1.setValidBefore("2026-12-31");
        r1.setService("WMS");
        r1.setRequest("GetMap");
        r1.setSubfield("sub1");
        r1.setWorkspace("topp");
        r1.setLayer("states");
        r1.setAccess(RESTGrantType.ALLOW);
        list.add(r1);

        RESTShortRule r2 = new RESTShortRule();
        r2.setId(2L);
        r2.setPriority(20L);
        r2.setAccess(RESTGrantType.DENY);
        list.add(r2);

        RESTShortRuleList out = roundTrip(list, RESTShortRuleList.class);

        assertEquals(2, out.getRuleList().size());
        RESTShortRule outR1 = out.getRuleList().get(0);
        assertEquals(1L, outR1.getId());
        assertEquals(10L, outR1.getPriority());
        assertEquals("user1", outR1.getUserName());
        assertEquals("role1", outR1.getRoleName());
        assertEquals(5L, outR1.getInstanceId());
        assertEquals("instance1", outR1.getInstanceName());
        assertEquals("127.0.0.1/32", outR1.getAddressRange());
        assertEquals("2026-01-01", outR1.getValidAfter());
        assertEquals("2026-12-31", outR1.getValidBefore());
        assertEquals("WMS", outR1.getService());
        assertEquals("GetMap", outR1.getRequest());
        assertEquals("sub1", outR1.getSubfield());
        assertEquals("topp", outR1.getWorkspace());
        assertEquals("states", outR1.getLayer());
        assertEquals(RESTGrantType.ALLOW, outR1.getAccess());

        assertEquals(RESTGrantType.DENY, out.getRuleList().get(1).getAccess());
    }

    @Test
    public void testPermsResultRoundTrip() {
        RESTPermsResult result = new RESTPermsResult();
        result.setCqlFilter("workspace = 'topp' AND layer = 'states'");
        result.setAccessibleResources(new TreeSet<>(Set.of("topp:states", "topp:roads")));

        RESTPermsResult out = roundTrip(result, RESTPermsResult.class);

        assertEquals("workspace = 'topp' AND layer = 'states'", out.getCqlFilter());
        assertEquals(Set.of("topp:states", "topp:roads"), out.getAccessibleResources());
    }
}

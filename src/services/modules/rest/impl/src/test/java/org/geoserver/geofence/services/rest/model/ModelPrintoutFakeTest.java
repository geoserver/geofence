/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXB;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.geoserver.geofence.core.model.LayerAttribute;
import org.geoserver.geofence.core.model.enums.AccessType;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.core.model.enums.LayerType;
import org.geoserver.geofence.services.rest.model.RESTRulePosition.RulePosition;
import org.geoserver.geofence.services.rest.model.util.IdName;
import org.junit.Test;

/** @author ETj (etj at geo-solutions.it) */
public class ModelPrintoutFakeTest {
    private static final Logger LOGGER = LogManager.getLogger(ModelPrintoutFakeTest.class);

    private String marshal(Object o) {

        StringWriter w = new StringWriter();
        JAXB.marshal(o, w);
        w.flush();
        return w.getBuffer().toString();
    }

    @Test
    public void testGetId() {}

    private RESTShortUser createShortUser(String base) {
        RESTShortUser ret = new RESTShortUser();
        ret.setId(base.hashCode() % 1000l);
        ret.setExtId("ext_" + base);
        ret.setUserName("user_" + base);
        ret.setEnabled(true);
        return ret;
    }

    private RESTInputUser createInputUser(String base) {
        RESTInputUser ret = new RESTInputUser();
        ret.setExtId("ext_" + base);
        ret.setName("name_" + base);
        ret.setFullName("fullname_" + base);
        ret.setPassword("pw_" + base);
        ret.setEmailAddress("email_" + base);
        ret.setEnabled(true);
        ret.setAdmin(false);

        List<IdName> groups = new ArrayList<IdName>();
        groups.add(new IdName((long) base.hashCode()));
        groups.add(new IdName("grp_" + base));

        ret.setGroups(groups);

        return ret;
    }

    private RESTInputRule createInputRule(String base) {
        RESTInputRule ret = new RESTInputRule();

        ret.setUsername("user_" + base);
        ret.setRolename("role_" + base);
        ret.setInstanceId((long) base.hashCode());
        ret.setService("WMS_" + base);
        ret.setRequest("getMap_" + base);
        ret.setWorkspace("wsp_" + base);
        ret.setLayer("layer_" + base);
        ret.setPosition(new RESTRulePosition(RulePosition.offsetFromBottom, 1));
        ret.setGrant(GrantType.ALLOW);

        RESTLayerConstraints constraints = new RESTLayerConstraints();
        constraints.setType(LayerType.VECTOR);
        constraints.setAllowedStyles(
                new HashSet(Arrays.asList("teststyle1", "teststyle2", "Style_" + base)));
        constraints.setCqlFilterRead("CQL_READ");
        constraints.setCqlFilterWrite("CQL_WRITE");
        constraints.setDefaultStyle("Style_" + base);
        constraints.setRestrictedAreaWkt("wkt_" + base);

        Set<LayerAttribute> attrs = new HashSet<LayerAttribute>();
        attrs.add(new LayerAttribute("attr1", "java.lang.String", AccessType.NONE));
        attrs.add(new LayerAttribute("attr2", "java.lang.String", AccessType.READONLY));
        attrs.add(new LayerAttribute("attr3", "java.lang.String", AccessType.READWRITE));
        constraints.setAttributes(attrs);

        ret.setConstraints(constraints);

        return ret;
    }

    private static long rulePri = 100;

    private RESTOutputRule createOutputRule(String base) {
        RESTOutputRule ret = new RESTOutputRule();

        ret.setPriority(rulePri++);
        ret.setUsername("user_" + base);
        ret.setRolename("role_" + base);
        ret.setInstance(new IdName((long) (Math.random() * 10000), "gs_" + base));
        ret.setService("WMS_" + base);
        ret.setRequest("getMap_" + base);
        ret.setWorkspace("wsp_" + base);
        ret.setLayer("layer_" + base);

        ret.setGrant(GrantType.ALLOW);

        //        RESTLayerConstraints constraints = new RESTLayerConstraints();
        //        constraints.setType(LayerType.VECTOR);
        //        constraints.setAllowedStyles(new
        // HashSet(Arrays.asList("teststyle1","teststyle2","Style_"+base)));
        //        constraints.setCqlFilterRead("CQL_READ");
        //        constraints.setCqlFilterWrite("CQL_WRITE");
        //        constraints.setDefaultStyle("Style_"+base);
        //        constraints.setRestrictedAreaWkt("wkt_"+base);
        //
        //        Set<LayerAttribute> attrs = new HashSet<LayerAttribute>();
        //        attrs.add(new LayerAttribute("attr1", "java.lang.String", AccessType.NONE));
        //        attrs.add(new LayerAttribute("attr2", "java.lang.String", AccessType.READONLY));
        //        attrs.add(new LayerAttribute("attr3", "java.lang.String", AccessType.READWRITE));
        //        constraints.setAttributes(attrs);
        //
        //        ret.setConstraints(constraints);

        return ret;
    }
}

/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.model;

import jakarta.xml.bind.JAXB;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.web.rest.api.model.RESTInputRule;
import org.geofence.web.rest.api.model.RESTInputUser;
import org.geofence.web.rest.api.model.RESTLayerAttribute;
import org.geofence.web.rest.api.model.RESTLayerConstraints;
import org.geofence.web.rest.api.model.RESTOutputRule;
import org.geofence.web.rest.api.model.RESTRulePosition;
import org.geofence.web.rest.api.model.RESTShortUser;
import org.geofence.web.rest.api.model.enums.RESTAccessType;
import org.geofence.web.rest.api.model.enums.RESTGrantType;
import org.geofence.web.rest.api.model.enums.RESTLayerType;
import org.geofence.web.rest.api.model.util.IdName;
import org.junit.jupiter.api.Test;

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

        List<IdName> groups = new ArrayList<>();
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
        ret.setPosition(new RESTRulePosition(RESTRulePosition.RESTPositionReference.offsetFromBottom, 1));
        ret.setGrant(RESTGrantType.ALLOW);

        RESTLayerConstraints constraints = new RESTLayerConstraints();
        constraints.setType(RESTLayerType.VECTOR);
        constraints.setAllowedStyles(new HashSet(Arrays.asList("teststyle1", "teststyle2", "Style_" + base)));
        constraints.setCqlFilterRead("CQL_READ");
        constraints.setCqlFilterWrite("CQL_WRITE");
        constraints.setDefaultStyle("Style_" + base);
        constraints.setRestrictedAreaWkt("wkt_" + base);

        Set<RESTLayerAttribute> attrs = new HashSet<>();
        attrs.add(new RESTLayerAttribute("attr1", "java.lang.String", RESTAccessType.NONE));
        attrs.add(new RESTLayerAttribute("attr2", "java.lang.String", RESTAccessType.READONLY));
        attrs.add(new RESTLayerAttribute("attr3", "java.lang.String", RESTAccessType.READWRITE));
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

        ret.setGrant(RESTGrantType.ALLOW);

        //        RESTLayerConstraints constraints = new RESTLayerConstraints();
        //        constraints.setType(LayerType.VECTOR);
        //        constraints.setAllowedStyles(new HashSet(Arrays.asList("teststyle1","teststyle2","Style_"+base)));
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

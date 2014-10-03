/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;

import org.geoserver.geofence.services.rest.model.RESTInputUser;
import org.geoserver.geofence.services.rest.model.RESTInputRule;
import org.geoserver.geofence.services.rest.model.RESTOutputRule;
import org.geoserver.geofence.services.rest.model.RESTShortUser;
import org.geoserver.geofence.services.rest.model.RESTShortUserList;
import org.geoserver.geofence.services.rest.model.RESTLayerConstraints;
import org.geoserver.geofence.services.rest.model.RESTRuleList;
import org.geoserver.geofence.core.model.LayerAttribute;
import org.geoserver.geofence.core.model.enums.AccessType;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.core.model.enums.LayerType;
import org.geoserver.geofence.services.rest.model.RESTInputRule.RESTRulePosition;
import org.geoserver.geofence.services.rest.model.RESTInputRule.RESTRulePosition.RulePosition;
import org.geoserver.geofence.services.rest.model.util.IdName;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.Test;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class ModelPrintoutFakeTest {
    private final static Logger LOGGER = LogManager.getLogger(ModelPrintoutFakeTest.class);

    public ModelPrintoutFakeTest() {
        LOGGER.info("RESTShortUser sample");
        RESTShortUser user = createShortUser("01");
        LOGGER.info(marshal(user));

        LOGGER.info("RESTShortUserList sample");
        RESTShortUserList userList = new RESTShortUserList();
        userList.add(createShortUser("01"));
        userList.add(createShortUser("02"));
        LOGGER.info(marshal(userList));

        LOGGER.info("RESTInputUser sample");
        RESTInputUser inputUser = createInputUser("02");
        LOGGER.info(marshal(inputUser));

        LOGGER.info("RESTInputRule sample");
        RESTInputRule inputRule = createInputRule("02");
        LOGGER.info(marshal(inputRule));

        LOGGER.info("RESTInputRuleList sample");
        RESTRuleList ruleList = new RESTRuleList();
        RESTOutputRule r1 = createOutputRule("01");
        r1.setConstraints(null);
        ruleList.add(r1);
        r1 = createOutputRule("02");
        r1.setGrant(GrantType.DENY);
        r1.setConstraints(null);
        ruleList.add(r1);

        LOGGER.info(marshal(ruleList));

    }

    private String marshal(Object o) {

        StringWriter w = new StringWriter();
        JAXB.marshal(o, w);
        w.flush();
        return w.getBuffer().toString();
    }

    @Test
    public void testGetId() {
    }

    private RESTShortUser createShortUser(String base) {
        RESTShortUser ret = new RESTShortUser();
        ret.setId(base.hashCode() % 1000l);
        ret.setExtId("ext_"+base);
        ret.setUserName("user_"+base);
        ret.setEnabled(true);
        return ret;
    }

    private RESTInputUser createInputUser(String base) {
        RESTInputUser ret = new RESTInputUser();       
        ret.setExtId("ext_"+base);
        ret.setName("name_"+base);
        ret.setFullName("fullname_"+base);
        ret.setPassword("pw_"+base);
        ret.setEmailAddress("email_"+base);
        ret.setEnabled(true);
        ret.setAdmin(false);

        List<IdName> groups = new ArrayList<IdName>();
        groups.add(new IdName((long)base.hashCode()));
        groups.add(new IdName("grp_" + base));

        ret.setGroups(groups);

        return ret;
    }

    private RESTInputRule createInputRule(String base) {
        RESTInputRule ret = new RESTInputRule();

        ret.setUserName("user_"+base);
        ret.setGroupName("grp_"+base);
        ret.setInstanceId((long)base.hashCode());
        ret.setService("WMS_"+base);
        ret.setRequest("getMap_"+base);
        ret.setWorkspace("wsp_"+base);
        ret.setLayer("layer_"+base);
        ret.setPosition(new RESTRulePosition(RulePosition.offsetFromBottom, 1));
        ret.setGrant(GrantType.ALLOW);

        RESTLayerConstraints constraints = new RESTLayerConstraints();
        constraints.setType(LayerType.VECTOR);
        constraints.setAllowedStyles(new HashSet(Arrays.asList("teststyle1","teststyle2","Style_"+base)));
        constraints.setCqlFilterRead("CQL_READ");
        constraints.setCqlFilterWrite("CQL_WRITE");
        constraints.setDefaultStyle("Style_"+base);
        constraints.setRestrictedAreaWkt("wkt_"+base);

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
        ret.setUser(new IdName((long)(Math.random()*10000), "user_"+base));
        ret.setGroup(new IdName((long)(Math.random()*10000), "grp_"+base));
        ret.setInstance(new IdName((long)(Math.random()*10000), "gs_"+base));
        ret.setService("WMS_"+base);
        ret.setRequest("getMap_"+base);
        ret.setWorkspace("wsp_"+base);
        ret.setLayer("layer_"+base);
        
        ret.setGrant(GrantType.ALLOW);

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

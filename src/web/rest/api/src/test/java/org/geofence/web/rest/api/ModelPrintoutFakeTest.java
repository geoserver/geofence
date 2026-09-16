/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api;

import jakarta.xml.bind.JAXB;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.geofence.web.rest.api.model.RESTInputGroup;
import org.geofence.web.rest.api.model.RESTInputInstance;
import org.geofence.web.rest.api.model.RESTInputRule;
import org.geofence.web.rest.api.model.RESTInputUser;
import org.geofence.web.rest.api.model.RESTLayerConstraints;
import org.geofence.web.rest.api.model.RESTOutputGroup;
import org.geofence.web.rest.api.model.RESTOutputRule;
import org.geofence.web.rest.api.model.RESTOutputRuleList;
import org.geofence.web.rest.api.model.RESTRulePosition;
import org.geofence.web.rest.api.model.RESTRulePosition.RESTPositionReference;
import org.geofence.web.rest.api.model.RESTShortInstance;
import org.geofence.web.rest.api.model.RESTShortInstanceList;
import org.geofence.web.rest.api.model.RESTShortUser;
import org.geofence.web.rest.api.model.RESTShortUserList;
import org.geofence.web.rest.api.model.config.RESTFullUserGroupList;
import org.geofence.web.rest.api.model.enums.RESTGrantType;
import org.geofence.web.rest.api.model.enums.RESTLayerType;
import org.geofence.web.rest.api.model.util.IdName;
import org.junit.jupiter.api.Test;

/** @author ETj (etj at geo-solutions.it) */
public class ModelPrintoutFakeTest {

    public ModelPrintoutFakeTest() {
        System.out.println("RESTShortUser sample");
        RESTShortUser user = createShortUser("01");
        System.out.println(marshal(user));

        System.out.println("RESTShortUserList sample");
        RESTShortUserList userList = new RESTShortUserList();
        userList.add(createShortUser("01"));
        userList.add(createShortUser("02"));
        System.out.println(marshal(userList));

        System.out.println("RESTInputUser sample");
        RESTInputUser inputUser = createInputUser("02");
        System.out.println(marshal(inputUser));

        System.out.println("RESTInputRule sample");
        RESTInputRule inputRule = createInputRule("02");
        System.out.println(marshal(inputRule));

        {
            System.out.println("RESTRuleList sample");
            RESTOutputRuleList ruleList = new RESTOutputRuleList();

            RESTOutputRule r1 = createOutputRule("01");
            r1.setConstraints(null);
            ruleList.add(r1);

            r1 = createOutputRule("02");
            r1.setGrant(RESTGrantType.DENY);
            r1.setConstraints(null);
            ruleList.add(r1);

            System.out.println(marshal(ruleList));
        }

        {
            System.out.println("RESTInputGroup sample");
            RESTInputGroup inputGroup = new RESTInputGroup();
            inputGroup.setEnabled(Boolean.TRUE);
            inputGroup.setName("sample group");
            inputGroup.setExtId("external_id_here");
            System.out.println(marshal(inputGroup));
        }
        {
            System.out.println("RESTInputGroup sample (field enable not set)");
            RESTInputGroup inputGroup = new RESTInputGroup();
            inputGroup.setName("sample group");
            System.out.println(marshal(inputGroup));
        }
        {
            System.out.println("RESTFullUserGroupList sample");
            RESTFullUserGroupList list = new RESTFullUserGroupList();
            list.add(createShortGroup("group1"));
            list.add(createShortGroup("group2"));
            System.out.println(marshal(list));
        }
        {
            System.out.println("RESTInputInstance sample");
            RESTInputInstance i = new RESTInputInstance();
            i.setName("sample instance");
            i.setDescription("sample descr");
            i.setBaseURL("http://yourgeoserver/geoserver");
            i.setUsername("admin");
            i.setPassword("clearpw");
            System.out.println(marshal(i));
        }

        {
            System.out.println("RESTShortInstanceList sample");
            RESTShortInstanceList list = new RESTShortInstanceList();
            {
                RESTShortInstance i1 = new RESTShortInstance();
                i1.setName("instance_01");
                i1.setId(100);
                i1.setUrl("http://test/geoserver");
                list.add(i1);
            }
            {
                RESTShortInstance i1 = new RESTShortInstance();
                i1.setName("instance_02");
                i1.setId(101);
                i1.setUrl("http://othertest/geoserver");
                list.add(i1);
            }
            System.out.println(marshal(list));
        }
    }

    protected RESTOutputGroup createShortGroup(String base) {
        RESTOutputGroup g1 = new RESTOutputGroup();
        g1.setName(base);
        g1.setId((long) base.hashCode());
        g1.setExtId("ext_" + base);
        g1.setEnabled(Boolean.TRUE);
        return g1;
    }

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
        ret.setPosition(new RESTRulePosition(RESTPositionReference.offsetFromBottom, 1));
        ret.setGrant(RESTGrantType.ALLOW);

        RESTLayerConstraints constraints = new RESTLayerConstraints();
        constraints.setType(RESTLayerType.VECTOR);
        constraints.setAllowedStyles(new HashSet(Arrays.asList("teststyle1", "teststyle2", "Style_" + base)));
        constraints.setCqlFilterRead("CQL_READ");
        constraints.setCqlFilterWrite("CQL_WRITE");
        constraints.setDefaultStyle("Style_" + base);
        constraints.setRestrictedAreaWkt("wkt_" + base);

        // TODO
        //        Set<LayerAttribute> attrs = new HashSet<>();
        //        attrs.add(new LayerAttribute("attr1", "java.lang.String", RESTAccessType.NONE));
        //        attrs.add(new LayerAttribute("attr2", "java.lang.String", RESTAccessType.READONLY));
        //        attrs.add(new LayerAttribute("attr3", "java.lang.String", RESTAccessType.READWRITE));
        //        constraints.setAttributes(attrs);

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

        return ret;
    }
}

/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Response;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.geoserver.geofence.services.rest.exception.ConflictRestEx;
import org.geoserver.geofence.services.rest.model.RESTInputGroup;
import org.geoserver.geofence.services.rest.model.RESTInputUser;
import org.geoserver.geofence.services.rest.model.RESTOutputUser;
import org.geoserver.geofence.services.rest.model.util.IdName;
import org.junit.Test;

/** @author ETj (etj at geo-solutions.it) */
public class RESTUserServiceImplTest extends RESTBaseTest {
    private static final Logger LOGGER = LogManager.getLogger(RESTUserServiceImplTest.class);

    @Test
    public void testInsert() {
        RESTInputGroup group = new RESTInputGroup();
        group.setName("g1");
        Response res = restUserGroupService.insert(group);
        long gid1 = (Long) res.getEntity();

        RESTInputUser user = new RESTInputUser();
        user.setName("user0");
        user.setEnabled(Boolean.TRUE);
        user.setGroups(new ArrayList<IdName>());
        user.getGroups().add(new IdName("g1"));

        Response userResp = restUserService.insert(user);
        Long id = (Long) userResp.getEntity();

        {
            RESTOutputUser out = restUserService.get("user0");
            assertNotNull(out);
            assertEquals("user0", out.getName());
        }
    }

    @Test
    public void testInsertDup() {
        RESTInputGroup group = new RESTInputGroup();
        group.setName("g1");
        Response res = restUserGroupService.insert(group);
        long gid1 = (Long) res.getEntity();

        {
            RESTInputUser user = new RESTInputUser();
            user.setName("user0");
            user.setEnabled(Boolean.TRUE);
            user.setGroups(new ArrayList<IdName>());
            user.getGroups().add(new IdName("g1"));

            Response userResp = restUserService.insert(user);
            Long id = (Long) userResp.getEntity();
        }

        LOGGER.info("Inserting dup");
        {
            RESTInputUser user = new RESTInputUser();
            user.setName("user0");
            user.setEnabled(Boolean.TRUE);
            user.setGroups(new ArrayList<IdName>());
            user.getGroups().add(new IdName("g1"));

            try {
                Response userResp = restUserService.insert(user);
                Long id = (Long) userResp.getEntity();
                fail("409 not trapped");
            } catch (ConflictRestEx e) {
                LOGGER.info("Exception properly trapped");
            }
        }
    }

    @Test
    public void testUpdateGroup() {
        {
            for (String name : Arrays.asList("g1", "g2", "g3", "g4")) {
                RESTInputGroup group1 = new RESTInputGroup();
                group1.setName(name);
                Response res = restUserGroupService.insert(group1);
                long gid1 = (Long) res.getEntity();
                LOGGER.info("Created group id:" + gid1 + " name:" + name);
            }
        }

        Long uid;
        final String USERNAME = "user0";

        { // insert user
            RESTInputUser user = new RESTInputUser();
            user.setName(USERNAME);
            user.setEnabled(Boolean.TRUE);
            user.setGroups(new ArrayList<IdName>());
            user.getGroups().add(new IdName("g1"));

            Response userResp = restUserService.insert(user);
            uid = (Long) userResp.getEntity();
        }

        { // check user
            RESTOutputUser out = restUserService.get(USERNAME);
            assertNotNull(out);
            assertEquals("user0", out.getName());
            assertTrue(out.isEnabled());
            assertEquals(1, out.getGroups().size());
            assertEquals("g1", out.getGroups().get(0).getName());
        }

        { // update 1: no group change
            RESTInputUser user = new RESTInputUser();
            user.setEnabled(false);
            restUserService.update(uid, user);
        }
        { // check user
            RESTOutputUser out = restUserService.get(USERNAME);
            assertNotNull(out);
            assertFalse(out.isEnabled());
            assertEquals(USERNAME, out.getName());
            assertEquals(1, out.getGroups().size());
            assertEquals("g1", out.getGroups().get(0).getName());
        }

        { // update 2: groups
            RESTInputUser user = new RESTInputUser();
            user.setGroups(new ArrayList<IdName>());
            user.getGroups().add(new IdName("g2"));
            restUserService.update(uid, user);
        }
        { // check user
            RESTOutputUser out = restUserService.get(USERNAME);
            assertNotNull(out);
            assertEquals(USERNAME, out.getName());
            assertEquals(1, out.getGroups().size());
            assertEquals("g2", out.getGroups().get(0).getName());
        }

        { // update 3: groups
            RESTInputUser user = new RESTInputUser();
            user.setGroups(new ArrayList<IdName>());
            user.getGroups().add(new IdName("g3"));
            user.getGroups().add(new IdName("g4"));
            restUserService.update(uid, user);
        }
        { // check user
            RESTOutputUser out = restUserService.get(USERNAME);
            assertNotNull(out);
            assertEquals(USERNAME, out.getName());
            assertEquals(2, out.getGroups().size());
            Set<String> set = new HashSet<String>();
            for (IdName idName : out.getGroups()) {
                set.add(idName.getName());
            }
            assertTrue(set.contains("g3"));
            assertTrue(set.contains("g4"));
        }
    }
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.impl;

import org.geoserver.geofence.services.rest.exception.ConflictRestEx;
import org.geoserver.geofence.services.rest.model.RESTInputGroup;
import org.geoserver.geofence.services.rest.model.RESTInputUser;
import org.geoserver.geofence.services.rest.model.RESTOutputUser;
import org.geoserver.geofence.services.rest.model.util.IdName;
import java.util.ArrayList;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RESTUserGroupServiceImplTest extends RESTBaseTest {
    private static final Logger LOGGER = LogManager.getLogger(RESTUserGroupServiceImplTest.class);

    @Test
    public void testInsert() {
        RESTInputGroup group = new RESTInputGroup();
        group.setName("g1");
        Response res = restUserGroupService.insert(group);
        long gid1 = (Long)res.getEntity();

        RESTInputUser user = new RESTInputUser();
        user.setName("user0");
        user.setEnabled(Boolean.TRUE);
        user.setGroups(new ArrayList<IdName>());
        user.getGroups().add(new IdName("g1"));

        Response userResp = restUserService.insert(user);
        Long id = (Long)userResp.getEntity();

        {
            RESTOutputUser out = restUserService.get(id);
            assertNotNull(out);
            assertEquals("user0", out.getName());            
        }

    }

    @Test
    public void testInsertDup() {

        {
            RESTInputGroup group1 = new RESTInputGroup();
            group1.setName("g1");
            restUserGroupService.insert(group1);
        }

        LOGGER.info("Inserting dup");
        try {
            RESTInputGroup group2 = new RESTInputGroup();
            group2.setName("g1");
            restUserGroupService.insert(group2);
            fail("409 not trapped");
        } catch(ConflictRestEx e) {
            LOGGER.info("Exception properly trapped");
        }
    }
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.xml.bind.JAXB;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashSet;
import org.geofence.web.rest.api.model.config.RESTUser;
import org.geofence.web.rest.api.model.config.RESTUserGroup;
import org.junit.jupiter.api.Test;

/** @author ETj (etj at geo-solutions.it) */
public class RESTUserTest {

    @Test
    public void jaxb() {
        RESTUser user = new RESTUser();
        user.setAdmin(Boolean.FALSE);
        user.setDateCreation(LocalDateTime.now());
        user.setEmailAddress("a@b.c");
        user.setEnabled(Boolean.TRUE);
        user.setExtId("extid");
        user.setFullName("fullname");
        user.setId(42l);
        user.setName("name");
        user.setPassword("pwd");
        user.setGroups(new HashSet<>());

        {
            RESTUserGroup ug = new RESTUserGroup();
            ug.setId(10l);
            ug.setExtId("this_is_a_group");
            ug.setEnabled(Boolean.TRUE);
            ug.setDateCreation(LocalDateTime.now());
            ug.setName("groupname");
            user.getGroups().add(ug);
        }
        {
            RESTUserGroup ug = new RESTUserGroup();
            ug.setId(11l);
            ug.setExtId("this_is_another_group");
            ug.setEnabled(Boolean.TRUE);
            ug.setDateCreation(LocalDateTime.now());
            ug.setName("groupname2");
            user.getGroups().add(ug);
        }

        assertEquals(2, user.getGroups().size());
        assertNotNull(user.getGroups().iterator().next());

        StringWriter w = new StringWriter();
        JAXB.marshal(user, w);
        String xml = w.toString();
        System.out.println(xml);

        StringReader r = new StringReader(xml);
        RESTUser user2 = JAXB.unmarshal(r, RESTUser.class);

        System.out.println("2nd marshalling:");
        JAXB.marshal(user2, System.out);

        assertNotNull(user2);
        assertEquals(user.getDateCreation(), user2.getDateCreation());
        assertEquals(user.getEmailAddress(), user2.getEmailAddress());
        assertEquals(user.getEnabled(), user2.getEnabled());
        assertEquals(user.getExtId(), user2.getExtId());
        assertEquals(user.getFullName(), user2.getFullName());
        assertEquals(user.getId(), user2.getId());
        assertEquals(user.getName(), user2.getName());
        assertEquals(user.getPassword(), user2.getPassword());

        assertEquals(user.getGroups().size(), user2.getGroups().size());

        for (RESTUserGroup ug1 : user.getGroups()) {
            boolean found = false;
            for (RESTUserGroup ug2 : user2.getGroups()) {
                if (ug2.getId().equals(ug1.getId())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Group " + ug1 + " not found in unmarshalled GSUser");
        }
        for (RESTUserGroup ug2 : user2.getGroups()) {
            boolean found = false;
            for (RESTUserGroup ug1 : user.getGroups()) {
                if (ug2.getId().equals(ug1.getId())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Group " + ug2 + " not found in unmarshalled GSUser");
        }
    }
}

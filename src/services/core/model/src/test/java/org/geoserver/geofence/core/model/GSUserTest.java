/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.UserGroup;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashSet;
import javax.xml.bind.JAXB;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GSUserTest {

    @Test
    public void jaxb() {
        GSUser user = new GSUser();
        user.setAdmin(Boolean.FALSE);
        user.setDateCreation(new Date());
        user.setEmailAddress("a@b.c");
        user.setEnabled(Boolean.TRUE);
        user.setExtId("extid");
        user.setFullName("fullname");
        user.setId(42l);
        user.setName("name");
        user.setPassword("pwd");
        user.setGroups(new HashSet<UserGroup>());

        {
            UserGroup ug = new UserGroup();
            ug.setId(10l);
            ug.setExtId("this_is_a_group");
            ug.setEnabled(Boolean.TRUE);
            ug.setDateCreation(new Date());
            ug.setName("groupname");
            user.getGroups().add(ug);
        }
        {
            UserGroup ug = new UserGroup();
            ug.setId(11l);
            ug.setExtId("this_is_another_group");
            ug.setEnabled(Boolean.TRUE);
            ug.setDateCreation(new Date());
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
        GSUser user2 = JAXB.unmarshal(r, GSUser.class);

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

        for (UserGroup ug1 : user.getGroups()) {
            boolean found = false;
            for (UserGroup ug2 : user2.getGroups()) {
                if (ug2.getId().equals(ug1.getId())) {
                    found = true;
                    break;
                }
            }            
            assertTrue("Group " + ug1 + " not found in unmarshalled GSUser" , found);
        }
        for (UserGroup ug2 : user2.getGroups()) {
            boolean found = false;
            for (UserGroup ug1 : user.getGroups()) {
                if (ug2.getId().equals(ug1.getId())) {
                    found = true;
                    break;
                }
            }
            assertTrue("Group " + ug2 + " not found in unmarshalled GSUser" , found);
        }

    }
}

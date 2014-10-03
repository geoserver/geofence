/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.ldap.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.geoserver.geofence.core.model.UserGroup;

import java.util.List;

import org.junit.Test;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;



/**
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 *
 */
public class UserGroupDAOLdapImplTest extends BaseDAOTest {
	@Test
	public void testFindAll() {
		List<UserGroup> groups = userGroupDAO.findAll();
		assertTrue(groups.size() > 0);
		UserGroup group = groups.get(0);
		assertTrue(group.getName().length() > 0);
	}	
	
	@Test
	public void testFind() {
		UserGroup group = userGroupDAO.find(1l);
		assertNotNull(group);
		assertNotNull(group.getName());
		assertEquals("admin", group.getName());
	}
	
	@Test
	public void testSearch() {
		Search search = new Search();
		search.addFilter(new Filter("groupname", "admin"));
		
		List<UserGroup> groups = userGroupDAO.search(search);
		assertTrue(groups.size() > 0);
		UserGroup group = groups.get(0);
		assertTrue(group.getName().length() > 0);		
	}
	
	@Test
	public void testCount() {
		assertTrue(userGroupDAO.count(new Search()) > 0);		
	}
}

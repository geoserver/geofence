/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.ldap.dao.impl;

import static org.junit.Assert.*;
import org.geoserver.geofence.core.model.GSUser;

import java.util.List;

import org.junit.Test;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;



/**
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 *
 */
public class GSUserDAOLdapImplTest extends BaseDAOTest {
	@Test
	public void testFindAll() {
		List<GSUser> users = userDAO.findAll();
		assertTrue(users.size() > 0);
		GSUser user = users.get(0);
		assertTrue(user.getName().length() > 0);
	}
	
	@Test
	public void testFind() {
		GSUser user = userDAO.find(1l);
		assertNotNull(user);
		assertEquals("admin", user.getName());
	}
	
	@Test
	public void testGetFullByName() {
		GSUser user = userDAO.getFull("admin");
		assertNotNull(user);
		assertEquals("admin", user.getName());
	}
	
	@Test
	public void testGetFullById() {
		GSUser user = userDAO.getFull(1l);
		assertNotNull(user);
		assertEquals("admin", user.getName());
	}
	
	@Test
	public void testCount() {
		assertTrue(userDAO.count(new Search()) > 0);		
	}
	
	@Test
	public void testSearch() {
		Search search = new Search();
		search.addFilter(new Filter("username", "admin"));
		List<GSUser> users = userDAO.search(search);
		assertTrue(users.size() > 0);
		GSUser user = users.get(0);
		assertTrue(user.getName().length() > 0);
	}
}

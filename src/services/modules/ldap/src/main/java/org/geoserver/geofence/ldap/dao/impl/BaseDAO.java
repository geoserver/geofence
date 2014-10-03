/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.ldap.dao.impl;

import org.geoserver.geofence.core.dao.RestrictedGenericDAO;
import org.geoserver.geofence.dao.utils.LdapUtils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.Search;

/**
 * Base DAO Implementation using LDAP services.
 * 
 * It uses a spring-ldap LdapTemplate to communicate with the LDAP server.
 * Currently only read type operations are supported (findAll, find, search).
 * 
 * A backup DAO can be defined. It will be used as a primary source for id lookups.
 * 
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 * @param <R>
 *
 */
public abstract class BaseDAO<T extends RestrictedGenericDAO<R>, R> implements RestrictedGenericDAO<R> {

	private LdapTemplate ldapTemplate;
	
	private String searchBase;
	
	private String searchFilter;
	
	private AttributesMapper attributesMapper;
	 
	T dao;
		
	/**
	 * Sets the backup DAO.
	 * 
	 * @param dao the dao to set
	 */
	public void setDao(T dao) {
		this.dao = dao;
	}

	/**
	 * Sets the base name for users in LDAP server.
	 * 
	 * @param searchBase the searchBase to set
	 */
	public void setSearchBase(String searchBase) {
		this.searchBase = searchBase;
	}
	
	/**
	 * Sets the filter used to identify objects from the base name.
	 * 
	 * @param searchFilter the searchFilter to set
	 */
	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}

	/**
	 * Sets the AttributeMapper used to build objects from LDAP
	 * objects.
	 * 
	 * @param attributesMapper the attributesMapper to set
	 */
	public void setAttributesMapper(AttributesMapper attributesMapper) {
		this.attributesMapper = attributesMapper;
	}

	/**
	 * Sets the LDAP communication object.
	 * 
	 * @param ldapTemplate the ldapTemplate to set
	 */
	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}
	
	@Override
	public List<R> findAll() {
		return search(searchFilter);
	}

	@Override
	public R find(Long id) {
		// try to load the user from db, first
		R object = searchOnDb(id);
		if(object != null) {
			return object;
		}		
		List<R> objects = search( new Filter("id", id) );		
		if(objects == null || objects.size() == 0) {
			return null;
		}
		return objects.get(0);
	}
	
	@Override
	public List<R> search(ISearch search) {
		List<R> objects = new ArrayList<R>();
		if(search.getFilters().size() == 0) {
			// no filter
			return findAll();
		}
		for(Filter filter : search.getFilters()) {
			if(filter != null) {
				List<R> filteredObjects = search(filter);
				objects.addAll(filteredObjects);						
			}
		}
		return objects;
	}
	
	@Override
	public int count(ISearch search) {
		return search(search).size();
	}
	
	@Override
	public void persist(R... entities) {
		// insert not implemented
	}


	@Override
	public R merge(R entity) {
		// update not implemented
		return entity;
	}


	@Override
	public boolean remove(R entity) {	
		// remove not implemented
		return false;
	}


	@Override
	public boolean removeById(Long id) {
		// remove not implemented
		return false;
	}
	
	/**
	 * Does a direct lookup for the distinguished name given.
	 * 
	 * @param dn distinguished name to lookup
	 * @return
	 */
	public R lookup(String dn) {
		return (R)ldapTemplate.lookup(dn, attributesMapper);
	}
	
	/**
	 * Search the given user id on the backup DAO, if defined.
	 * The id can be a classic id (>0) or an extId (<0).
	 * It's an extId if it's the id read from the LDAP server.
	 * 
	 * @param id
	 * @return
	 */
	private R searchOnDb(Long id) {
		if(dao != null) {
			if(id < 0) {
				// If negative, it's an extId (id from LDAP server)
				// we must search on that attribute
				Search search = new Search();
				Filter filter = new Filter("extId", id+"");
				List<Filter> filters = new ArrayList<Filter>();
				filters.add(filter);
				search.setFilters(filters);
				List<R> objects = dao.search(search);
				if(objects.size() > 0) {
					return objects.get(0);
				}
			} else {
				// else it's a classic id
				R object = (R) dao.find(id);
				if(object != null) {
					return object;
				}
			}	
		}
		
		return null;
	}

	/**
	 * Search using the given filter on the LDAP server.
	 * Each result object is mapped with the given mapper.
	 * Given base and filter are used.
	 * 
	 * @param base
	 * @param filter
	 * @param mapper
	 * @return
	 */
	public List search(String base, Filter filter, AttributesMapper mapper) {
		return LdapUtils.search(ldapTemplate, base, filter, mapper);
	}
	
	/**
	 * Search using the given filter on the LDAP server.
	 * Each result object is mapped with the given mapper.
	 * Given base and filter are used.
	 * 
	 * @param base
	 * @param filter
	 * @param mapper
	 * @return
	 */
	public List search(String base, String filter, AttributesMapper mapper) {
		return LdapUtils.search(ldapTemplate, base, filter, mapper);	
	}
	
	/**
	 * Search using the given filter on the LDAP server.
	 * Uses default base, filter and mapper.
	 * 
	 * @param base
	 * @param filter
	 * @param mapper
	 * @return
	 */
	public List search(Filter filter) {
		return search(searchBase, filter, attributesMapper);		
	}
	
	/**
	 * Search using the given filter on the LDAP server.
	 * Uses default base, filter and mapper.
	 * 
	 * @param base
	 * @param filter
	 * @param mapper
	 * @return
	 */
	public List search(String filter) {
		return search(searchBase, filter, attributesMapper);		
	}
	
	
}

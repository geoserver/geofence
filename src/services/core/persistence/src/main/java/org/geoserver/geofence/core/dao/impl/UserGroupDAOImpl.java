/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.core.dao.impl;

import java.util.Date;
import java.util.List;

import org.geoserver.geofence.core.dao.UserGroupDAO;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.core.dao.search.Search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geoserver.geofence.core.dao.search.LongSearch;

import org.springframework.transaction.annotation.Transactional;

/**
 * Public implementation of the UserGroupDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Transactional(value = "geofenceTransactionManager")
public class UserGroupDAOImpl extends BaseDAO<UserGroup, Long>
        // extends GenericDAOImpl<User, Long>
        implements UserGroupDAO {

    private static final Logger LOGGER = LogManager.getLogger(UserGroupDAOImpl.class);

    public UserGroupDAOImpl() {
        super(UserGroup.class);
    }

    @Override
    public void persist(UserGroup... entities) {
        Date now = new Date();
        for (UserGroup e : entities) {
            e.setDateCreation(now);
        }

        super.persist(entities);
    }

    @Override
    public List<UserGroup> findAll() {
        return super.findAll();
    }

    @Override
    public UserGroup merge(UserGroup entity) {
        return super.merge(entity);
    }

    @Override
    public boolean remove(UserGroup entity) {
        return super.remove(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public UserGroup get(String name) {
        Search<UserGroup>  search = createSearch();
        search.addFilterEqual("name", name);
        return searchUnique(search);
    }
    
    @Override    
    public List<UserGroup> search(String nameLike, Integer page, Integer entries) throws IllegalArgumentException {

        if( (page != null && entries == null) || (page ==null && entries != null)) {
            throw new IllegalArgumentException("Page and entries params should be declared together.");
        }

        Search<UserGroup> search = createSearch();

        if(page != null) {
            search.setMaxResults(entries);
            search.setPage(page);
        }

        search.addSortAsc("name");

        if (nameLike != null) {
            search.addFilterILike("name", nameLike);
        }

        return search(search);
    }
    

    @Override
    public long countByNameLike(String nameLike) {
        LongSearch<UserGroup> search = createLongSearch();

        if (nameLike != null) {
            search.addFilterILike("name", nameLike);
        }

        return count(search);
    }
    

}

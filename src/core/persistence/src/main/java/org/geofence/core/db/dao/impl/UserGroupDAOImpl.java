/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geofence.core.db.dao.impl;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.db.dao.UserGroupDAO;
import org.geofence.core.db.dao.search.LongSearch;
import org.geofence.core.db.dao.search.Search;
import org.geofence.core.model.UserGroup;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Public implementation of the UserGroupDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Transactional(value = "geofenceTransactionManager")
@Component
@Order(10)
public class UserGroupDAOImpl extends BaseDAOImpl<UserGroup, Long>
        // extends GenericDAOImpl<User, Long>
        implements UserGroupDAO {

    private static final Logger LOGGER = LogManager.getLogger(UserGroupDAOImpl.class);

    public UserGroupDAOImpl() {
        super(UserGroup.class);
    }

    @Override
    public void persist(UserGroup entity) {
        LocalDateTime now = LocalDateTime.now();
        if (entity.getDateCreation() == null) entity.setDateCreation(now);
        super.persist(entity);
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
        Search<UserGroup> search = createSearch();
        search.addFilterEqual("name", name);
        return searchUnique(search);
    }

    @Override
    public List<UserGroup> search(String nameLike, Integer page, Integer entries) throws IllegalArgumentException {

        if ((page != null && entries == null) || (page == null && entries != null)) {
            throw new IllegalArgumentException("Page and entries params should be declared together.");
        }

        Search<UserGroup> search = createSearch();

        if (page != null) {
            search.setMaxResults(entries);
            search.setPage(page);
        }

        search.addSortAsc("name");

        if (StringUtils.isNotBlank(nameLike)) {
            search.addFilterILike("name", nameLike);
        }

        return search(search);
    }

    @Override
    public long countByNameLike(String nameLike) {
        LongSearch<UserGroup> search = createLongSearch();

        if (StringUtils.isNotBlank(nameLike)) {
            search.addFilterILike("name", nameLike);
        }

        return count(search);
    }
}

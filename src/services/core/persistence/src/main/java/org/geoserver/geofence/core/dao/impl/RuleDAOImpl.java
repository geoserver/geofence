/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import java.util.List;


import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.Search;

import org.geoserver.geofence.core.dao.RuleDAO;
import static org.geoserver.geofence.core.dao.util.SearchUtil.*;
import org.geoserver.geofence.core.model.Rule;

import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.core.model.enums.InsertPosition;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.geoserver.geofence.core.dao.DuplicateKeyException;

import org.springframework.transaction.annotation.Transactional;

/**
 * Public implementation of the GSUserDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Transactional(value = "geofenceTransactionManager")
public class RuleDAOImpl extends PrioritizableDAOImpl<Rule> implements RuleDAO {

    private static final Logger LOGGER = LogManager.getLogger(RuleDAOImpl.class);

    @Override
    public void persist(Rule... entities) throws DuplicateKeyException {

        // TODO: check if there are any dups in the input list

        for (Rule rule : entities) {
            // check there are no dups for the rules received
            if ( rule.getAccess() != GrantType.LIMIT ) { // there may be as many LIMIT rules as desired
                Search search = getDupSearch(rule);
                List<Rule> dups = search(search);
                for (Rule dup : dups) {
                    if ( dup.getAccess() != GrantType.LIMIT ) {
                        if(dup.getId().equals(rule.getId())) {
                            // avoid check against self
                            continue;
                        }

                        LOGGER.warn(" ORIG: " + dup);
                        LOGGER.warn(" DUP : " + rule);
                        throw new DuplicateKeyException("Duplicate Rule " + rule);
                    }
                }
//                if (count(search) > 0)
//                {
//                    throw new DuplicateKeyException("Duplicate Rule " + rule);
//                }
            }
        }
        super.persist(entities);
    }

    @Override
    public int shift(long priorityStart, long offset) {
        return super.shift(Rule.class, priorityStart, offset);
    }

    @Override
    public long persist(Rule entity, InsertPosition position) {

        return super.persist(Rule.class, entity, position);
    }

    @Override
    public void persistInternal(Rule entity) {
        this.persist(entity);
    }


    protected Search getDupSearch(Rule rule) {
        Search search = new Search(Rule.class);
        addSearchField(search, "username", rule.getUsername());
        addSearchField(search, "rolename", rule.getRolename());
        addSearchField(search, "instance", rule.getInstance());
        addSearchField(search, "service", rule.getService());
        addSearchField(search, "request", rule.getRequest());
        addSearchField(search, "workspace", rule.getWorkspace());
        addSearchField(search, "layer", rule.getLayer());

        addAddressRangeSearch(search, rule.getAddressRange());

        return search;
    }

    @Override
    public List<Rule> findAll() {
        return super.findAll();
    }

    @Override
    public List<Rule> search(ISearch search) {
        return super.search(search);
    }

    @Override
    public Rule merge(Rule entity) {
        Search search = getDupSearch(entity);

        // check if we are dup'ing some other Rule.
        List<Rule> existent = search(search);
        switch (existent.size()) {
            case 0:
                break;

            case 1:
                // We may be updating some other fields in this Rule
                if ( !existent.get(0).getId().equals(entity.getId()) ) {
                    throw new DuplicateKeyException("Duplicating Rule " + existent.get(0) + " with " + entity);
                }
                break;

            default:
                throw new IllegalStateException("Too many rules duplicating " + entity);
        }

        return super.merge(entity);
    }

    @Override
    public boolean remove(Rule entity) {
        return super.remove(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }
}

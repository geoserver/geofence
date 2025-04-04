/* (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import java.util.List;

import static org.geoserver.geofence.core.dao.search.SearchUtil.*;

import org.geoserver.geofence.core.model.enums.InsertPosition;
import org.geoserver.geofence.core.dao.AdminRuleDAO;
import org.geoserver.geofence.core.dao.DuplicateKeyException;
import org.geoserver.geofence.core.dao.search.Search;
import org.geoserver.geofence.core.model.AdminRule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.transaction.annotation.Transactional;

/**
 * Public implementation of the AdminRuleDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Transactional(value = "geofenceTransactionManager")
public class AdminRuleDAOImpl 
        extends PrioritizableDAOImpl<AdminRule> 
        implements AdminRuleDAO {

//    public class RuleDAOImpl extends PrioritizableDAOImpl<Rule> implements RuleDAO {
    private static final Logger LOGGER = LogManager.getLogger(AdminRuleDAOImpl.class);

    public AdminRuleDAOImpl() {
        super(AdminRule.class);
    }

    @Override
    public void persist(AdminRule... entities) {

        // TODO: check if there are any dups in the input list

        for (AdminRule rule : entities) {
            // check there are no dups for the rules received
            Search<AdminRule> search = getDupSearch(rule);
            List<AdminRule> dups = search(search);
            for (AdminRule dup : dups) {
                if(dup.getId().equals(rule.getId())) {
                    // avoid check against self
                    continue;
                }

                LOGGER.warn(" ORIG: " + dup);
                LOGGER.warn(" DUP : " + rule);
                throw new DuplicateKeyException("Duplicate AdminRule " + rule);
            }
//                if (count(search) > 0)
//                {
//                    throw new DuplicateKeyException("Duplicate Rule " + rule);
//                }
        }
        super.persist(entities);
    }


    @Override
    public int shift(long priorityStart, long offset) {
        return super.shift(AdminRule.class, priorityStart, offset);
    }

    @Override
    public long persist(AdminRule entity, InsertPosition position) {

        return super.persist(AdminRule.class, entity, position);
    }

    @Override
    public void persistInternal(AdminRule entity) {
        this.persist(entity);
    }

    protected Search<AdminRule> getDupSearch(AdminRule rule) {
        Search<AdminRule> search = createSearch();
        addSearchField(search, "username", rule.getUsername());
        addSearchField(search, "rolename", rule.getRolename());
        addSearchField(search, "instance", rule.getInstance());
        addSearchField(search, "workspace", rule.getWorkspace());

        addAddressRangeSearch(search, rule.getAddressRange());

        return search;
    }

    @Override
    public List<AdminRule> findAll() {
        return super.findAll();
    }

    @Override
    public AdminRule merge(AdminRule entity) {
        Search<AdminRule> search = getDupSearch(entity);

        // check if we are dup'ing some other Rule.
        List<AdminRule> existent = search(search);
        switch (existent.size()) {
            case 0:
                break;

            case 1:
                // We may be updating some other fields in this Rule
                if ( !existent.get(0).getId().equals(entity.getId()) ) {
                    throw new DuplicateKeyException("Duplicating AdminRule " + existent.get(0) + " with " + entity);
                }
                break;

            default:
                throw new IllegalStateException("Too many AdminRules duplicating " + entity);
        }

        return super.merge(entity);
    }

    @Override
    public boolean remove(AdminRule entity) {
        return super.remove(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import com.googlecode.genericdao.search.Field;
import java.util.List;

import javax.persistence.Query;

import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.Search;

import org.geoserver.geofence.core.dao.RuleDAO;
import static org.geoserver.geofence.core.dao.util.SearchUtil.*;
import org.geoserver.geofence.core.model.Rule;

import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.core.model.enums.InsertPosition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Public implementation of the GSUserDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Transactional(value = "geofenceTransactionManager")
public class RuleDAOImpl extends BaseDAO<Rule, Long> implements RuleDAO {

    private static final Logger LOGGER = LogManager.getLogger(RuleDAOImpl.class);

    @Override
    public void persist(Rule... entities) {

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
    public long persist(Rule entity, InsertPosition position) {
        switch(position) {
            case FIXED:
                // priority is already set
                break;
            case FROM_START:
                Search search = new Search(Rule.class);
                search.setFirstResult((int)entity.getPriority());
                search.setMaxResults(1);
                search.addSortAsc("priority");
                List<Rule> list = super._search(search);
                if(list.isEmpty()) { // no rule found at given position: let's find out why
                    int count = count(new Search(Rule.class));
                    if(LOGGER.isDebugEnabled())
                        LOGGER.debug("No rule found at position " + entity.getPriority() + " -- rules count:"+count);

                    if(count == 0) { // this is the first rule inserted
                        if(LOGGER.isDebugEnabled())
                            LOGGER.debug("Inserting first rule");
                        entity.setPriority(1); // this is the only rule so far, let's put in an arbitrary value
                    } else { // some rules in, the requested postion is at bottom
                        Search s1 = new Search(Rule.class);
                        s1.addField("priority", Field.OP_MAX);
                        long maxPri = (Long)searchUnique(s1);
                        entity.setPriority(maxPri+1);
                        if(LOGGER.isDebugEnabled())
                            LOGGER.debug("Inserting rule in last position");
                    }
                } else {
                    long basepri = list.get(0).getPriority();
                    if(LOGGER.isDebugEnabled()) {
                        LOGGER.debug("shifting rules from priority " + basepri + " downward");
                    }
                    int i = shift(basepri, 1);
                    if(LOGGER.isDebugEnabled()) {
                        LOGGER.debug("shifted "+i+" rules from priority " + basepri + " downward");
                    }
                    entity.setPriority(basepri);
                }
                break;

            case FROM_END:
                // 0 based: if set to 0, this rule will go in last position
                long posFromEnd = entity.getPriority();
                int count = count(new Search(Rule.class));
                if(count == 0) {
                    if(LOGGER.isDebugEnabled())
                        LOGGER.debug("Inserting first rule");
                    entity.setPriority(1); // this is the only rule so far, let's put in an arbitrary value                                        
                } else {
                    // needed for fetching the rule just before the new one
                    // last rule is currently at position count - 1 (0-based)
                    long posFromStart = count - posFromEnd -1;

                    if(LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Inserting rule -- count:"+count+" pos:-"+posFromEnd+"+"+posFromStart);
                    }

                    if(posFromStart < 0 ) {
                        throw new IllegalArgumentException("Bad position from end ("+posFromEnd+") with count="+count);
                    }


                    Search searchEnd = new Search(Rule.class);
                    searchEnd.setFirstResult((int)posFromStart);
                    searchEnd.setMaxResults(1);
                    searchEnd.addSortAsc("priority");
                    List<Rule> list1 = super._search(searchEnd);
                    if(list1.isEmpty()) { // no rule found at given position: let's find out why
                        throw new IllegalArgumentException("Bad position from end ("+posFromEnd+") with count="+count);
                    } else {
                        long basepri = list1.get(0).getPriority()+1;
                        if(LOGGER.isDebugEnabled()) {
                            LOGGER.debug("shifting rules from priority " + basepri + " downward");
                        }
                        int i = shift(basepri, 1);
                        if(LOGGER.isDebugEnabled()) {
                            LOGGER.debug("shifted "+i+" rules from priority " + basepri + " downward");
                        }
                        entity.setPriority(basepri);
                    }
                }
                break;

            default:
                throw new IllegalArgumentException("Bad position type " + position);

        }

        this.persist(entity);
        return entity.getPriority();
    }


    private Search getDupSearch(Rule rule) {
        Search search = new Search(Rule.class);
        addSearchField(search, "gsuser", rule.getGsuser());
        addSearchField(search, "userGroup", rule.getUserGroup());
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
    public int shift(long priorityStart, long offset) {
        if ( offset <= 0 ) {
            throw new IllegalArgumentException("Positive offset required");
        }

        Search search = new Search(Rule.class);
        search.addFilterGreaterOrEqual("priority", priorityStart);
        search.addFilterLessThan("priority", priorityStart + offset);
        if ( super.count(search) == 0 ) {
            return -1;
        }

        String hql = "UPDATE Rule SET priority=priority+ :offset WHERE priority >= :priorityStart";

        Query query = em().createQuery(hql);
        query.setParameter("offset", offset);
        query.setParameter("priorityStart", priorityStart);

        return query.executeUpdate();
    }

    @Override
    public void swap(long id1, long id2) {
        Rule rule1 = super.find(id1);
        Rule rule2 = super.find(id2);

        if ( (rule1 == null) || (rule2 == null) ) {
            throw new IllegalArgumentException("Rule not found");
        }

        Long tmp = rule1.getPriority();
        rule1.setPriority(rule2.getPriority());
        rule2.setPriority(tmp);
        super.merge(rule1, rule2);
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

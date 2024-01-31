/* (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import com.googlecode.genericdao.search.Field;
import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.Search;
import java.util.List;
import javax.persistence.Query;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.geoserver.geofence.core.dao.PrioritizableDAO;
import org.geoserver.geofence.core.model.Prioritizable;
import org.geoserver.geofence.core.model.enums.InsertPosition;
import org.springframework.transaction.annotation.Transactional;

/**
 * Public implementation of the GSUserDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Transactional(value = "geofenceTransactionManager")
public abstract class PrioritizableDAOImpl<T extends Prioritizable> extends BaseDAO<T, Long>
        implements PrioritizableDAO<T> {
    private static final Logger LOGGER = LogManager.getLogger(PrioritizableDAOImpl.class);

    protected long persist(Class<T> clazz, T entity, InsertPosition position) {
        switch (position) {
            case FIXED:
                // priority is already set
                break;
            case FROM_START:
                Search search = new Search(clazz);
                search.setFirstResult((int) entity.getPriority());
                search.setMaxResults(1);
                search.addSortAsc("priority");
                List<Prioritizable> list = super._search(search);
                if (list.isEmpty()) { // no rule found at given position: let's find out why
                    int count = count(new Search(clazz));
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug(
                                "No rule found at position "
                                        + entity.getPriority()
                                        + " -- rules count:"
                                        + count);

                    if (count == 0) { // this is the first rule inserted
                        if (LOGGER.isDebugEnabled()) LOGGER.debug("Inserting first rule");
                        entity.setPriority(
                                1); // this is the only rule so far, let's put in an arbitrary value
                    } else { // some rules in, the requested postion is at bottom
                        Search s1 = new Search(clazz);
                        s1.addField("priority", Field.OP_MAX);
                        long maxPri = (Long) searchUnique(s1);
                        entity.setPriority(maxPri + 1);
                        if (LOGGER.isDebugEnabled())
                            LOGGER.debug("Inserting rule in last position");
                    }
                } else {
                    long basepri = list.get(0).getPriority();
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("shifting rules from priority " + basepri + " downward");
                    }
                    int i = shift(clazz, basepri, 1);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(
                                "shifted " + i + " rules from priority " + basepri + " downward");
                    }
                    entity.setPriority(basepri);
                }
                break;

            case FROM_END:
                // 0 based: if set to 0, this rule will go in last position
                long posFromEnd = entity.getPriority();
                int count = count(new Search(clazz));
                if (count == 0) {
                    if (LOGGER.isDebugEnabled()) LOGGER.debug("Inserting first rule");
                    entity.setPriority(
                            1); // this is the only rule so far, let's put in an arbitrary value
                } else {
                    // needed for fetching the rule just before the new one
                    // last rule is currently at position count - 1 (0-based)
                    long posFromStart = count - posFromEnd - 1;

                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(
                                "Inserting rule -- count:"
                                        + count
                                        + " pos:-"
                                        + posFromEnd
                                        + "+"
                                        + posFromStart);
                    }

                    if (posFromStart < 0) {
                        throw new IllegalArgumentException(
                                "Bad position from end (" + posFromEnd + ") with count=" + count);
                    }

                    Search searchEnd = new Search(clazz);
                    searchEnd.setFirstResult((int) posFromStart);
                    searchEnd.setMaxResults(1);
                    searchEnd.addSortAsc("priority");
                    List<Prioritizable> list1 = super._search(searchEnd);
                    if (list1.isEmpty()) { // no rule found at given position: let's find out why
                        throw new IllegalArgumentException(
                                "Bad position from end (" + posFromEnd + ") with count=" + count);
                    } else {
                        long basepri = list1.get(0).getPriority() + 1;
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("shifting rules from priority " + basepri + " downward");
                        }
                        int i = shift(clazz, basepri, 1);
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug(
                                    "shifted "
                                            + i
                                            + " rules from priority "
                                            + basepri
                                            + " downward");
                        }
                        entity.setPriority(basepri);
                    }
                }
                break;

            default:
                throw new IllegalArgumentException("Bad position type " + position);
        }

        persistInternal(entity);
        return entity.getPriority();
    }

    /**
     * This is needed to call the proper persist() metod in the child class, or automatic bean
     * conversion in DAO will not be carried out.
     */
    protected abstract void persistInternal(T rule);

    /** Each subclass needs its own filtering. */
    protected abstract Search getDupSearch(T rule);

    protected int shift(Class<T> clazz, long priorityStart, long offset) {
        if (offset <= 0) {
            throw new IllegalArgumentException("Positive offset required");
        }

        Search search = new Search(clazz);
        search.addFilterGreaterOrEqual("priority", priorityStart);
        search.addFilterLessThan("priority", priorityStart + offset);
        if (super.count(search) == 0) {
            return -1;
        }

        String hql =
                "UPDATE "
                        + clazz.getSimpleName()
                        + " SET priority=priority+ :offset WHERE priority >= :priorityStart";

        Query query = em().createQuery(hql);
        query.setParameter("offset", offset);
        query.setParameter("priorityStart", priorityStart);

        return query.executeUpdate();
    }

    @Override
    public void swap(long id1, long id2) {
        T rule1 = super.find(id1);
        T rule2 = super.find(id2);

        if ((rule1 == null) || (rule2 == null)) {
            throw new IllegalArgumentException("Rule not found");
        }

        Long tmp = rule1.getPriority();
        rule1.setPriority(rule2.getPriority());
        rule2.setPriority(tmp);
        super.merge(rule1, rule2);
    }

    @Override
    public List<T> search(ISearch search) {
        return super.search(search);
    }
}

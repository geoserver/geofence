/* (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import static org.geoserver.geofence.services.util.FilterUtils.addCriteria;
import static org.geoserver.geofence.services.util.FilterUtils.addFixedCriteria;
import static org.geoserver.geofence.services.util.FilterUtils.addFixedStringCriteria;
import static org.geoserver.geofence.services.util.FilterUtils.addPagingConstraints;
import static org.geoserver.geofence.services.util.FilterUtils.addStringCriteria;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.geoserver.geofence.core.dao.AdminRuleDAO;
import org.geoserver.geofence.core.model.AdminRule;
import org.geoserver.geofence.core.model.enums.InsertPosition;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.ShortAdminRule;
import org.geoserver.geofence.services.exception.BadRequestServiceEx;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;

/** @author ETj (etj at geo-solutions.it) */
public class AdminRuleAdminServiceImpl implements AdminRuleAdminService {

    private static final Logger LOGGER = LogManager.getLogger(AdminRuleAdminServiceImpl.class);

    private AdminRuleDAO ruleDAO;

    // =========================================================================
    // Basic operations
    // =========================================================================

    @Override
    public long insert(AdminRule rule) {
        ruleDAO.persist(rule);
        return rule.getId();
    }

    @Override
    public long insert(AdminRule rule, InsertPosition position) {
        ruleDAO.persist(rule, position);
        return rule.getId();
    }

    @Override
    public long update(AdminRule rule) throws NotFoundServiceEx {
        AdminRule orig = ruleDAO.find(rule.getId());
        if (orig == null) {
            throw new NotFoundServiceEx("AdminRule not found", rule.getId());
        }

        ruleDAO.merge(rule);
        return orig.getId();
    }

    /**
     * Shifts the priority of the rules having <TT>priority &gt;= priorityStart</TT> down by
     * <TT>offset</TT>.
     *
     * <p>The shift will not be performed if there are no Rules with priority: <br>
     * <tt> startPriority &lt;= priority &lt; startPriority + offset </TT>
     *
     * @return the number of rules updated, or -1 if no need to shift.
     */
    @Override
    public int shift(long priorityStart, long offset) {
        return ruleDAO.shift(priorityStart, offset);
    }

    @Override
    public void swap(long id1, long id2) {
        ruleDAO.swap(id1, id2);
    }

    @Override
    public AdminRule get(long id) throws NotFoundServiceEx {
        AdminRule rule = ruleDAO.find(id);

        if (rule == null) {
            throw new NotFoundServiceEx("AdminRule not found", id);
        }

        return rule;
    }

    @Override
    public boolean delete(long id) throws NotFoundServiceEx {
        AdminRule rule = ruleDAO.find(id);

        if (rule == null) {
            throw new NotFoundServiceEx("AdminRule not found", id);
        }

        return ruleDAO.remove(rule);
    }

    @Override
    public void deleteRulesByUser(String username) throws NotFoundServiceEx {
        Search searchCriteria = new Search(AdminRule.class);
        searchCriteria.addFilter(Filter.equal("username", username));

        List<AdminRule> list = ruleDAO.search(searchCriteria);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Removing " + list.size() + " AdminRule for user " + username);
        for (AdminRule rule : list) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Removing rule for user " + username + ": " + rule);
            ruleDAO.remove(rule);
        }
    }

    @Override
    public void deleteRulesByRole(String rolename) throws NotFoundServiceEx {
        Search searchCriteria = new Search(AdminRule.class);
        searchCriteria.addFilter(Filter.equal("rolename", rolename));

        List<AdminRule> list = ruleDAO.search(searchCriteria);
        for (AdminRule rule : list) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Removing rule for role " + rolename + ": " + rule);
            ruleDAO.remove(rule);
        }
    }

    @Override
    public void deleteRulesByInstance(long instanceId) throws NotFoundServiceEx {
        Search searchCriteria = new Search(AdminRule.class);
        searchCriteria.addFilter(Filter.equal("instance.id", instanceId));

        List<AdminRule> list = ruleDAO.search(searchCriteria);
        for (AdminRule rule : list) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Removing AdminRule for instance " + instanceId + ": " + rule);
            ruleDAO.remove(rule);
        }
    }

    @Override
    public List<ShortAdminRule> getAll() {
        List<AdminRule> found = ruleDAO.findAll();
        return convertToShortList(found);
    }

    @Override
    public List<ShortAdminRule> getList(RuleFilter filter, Integer page, Integer entries) {
        Search searchCriteria = buildSearch(page, entries, filter);

        List<AdminRule> found = ruleDAO.search(searchCriteria);
        return convertToShortList(found);
    }

    @Override
    public ShortAdminRule getRule(RuleFilter filter) throws BadRequestServiceEx {
        Search searchCriteria = buildFixedRuleSearch(filter);
        List<AdminRule> found = ruleDAO.search(searchCriteria);
        if (found.isEmpty()) return null;

        if (found.size() > 1) {
            LOGGER.error("Unexpected rule count for filter " + filter + " : " + found);
        }

        return new ShortAdminRule(found.get(0));
    }

    @Override
    public List<ShortAdminRule> getRulesByPriority(long priority, Integer page, Integer entries) {
        Search searchCriteria = new Search(AdminRule.class);
        searchCriteria.addFilter(Filter.greaterOrEqual("priority", priority));
        searchCriteria.addSortAsc("priority");
        addPagingConstraints(searchCriteria, page, entries);
        List<AdminRule> found = ruleDAO.search(searchCriteria);
        return convertToShortList(found);
    }

    @Override
    public ShortAdminRule getRuleByPriority(long priority) throws BadRequestServiceEx {
        Search searchCriteria = new Search(AdminRule.class);
        searchCriteria.addFilter(Filter.equal("priority", priority));
        List<AdminRule> found = ruleDAO.search(searchCriteria);
        if (found.isEmpty()) return null;

        if (found.size() > 1) {
            LOGGER.error("Unexpected rule count for priority " + priority + " : " + found);
        }

        return new ShortAdminRule(found.get(0));
    }

    @Override
    public List<AdminRule> getListFull(RuleFilter filter, Integer page, Integer entries) {
        Search searchCriteria = buildSearch(page, entries, filter);
        List<AdminRule> found = ruleDAO.search(searchCriteria);
        return found;
    }

    protected Search buildSearch(Integer page, Integer entries, RuleFilter filter)
            throws BadRequestServiceEx {
        Search searchCriteria = buildRuleSearch(filter);
        addPagingConstraints(searchCriteria, page, entries);
        searchCriteria.addSortAsc("priority");
        return searchCriteria;
    }

    @Override
    public long getCountAll() {
        return count(new RuleFilter(RuleFilter.SpecialFilterType.ANY));
    }

    @Override
    public long count(RuleFilter filter) {
        //        if(LOGGER.isDebugEnabled())
        //            LOGGER.debug("Counting rules: " + filter);

        Search searchCriteria = buildRuleSearch(filter);
        //        if(LOGGER.isDebugEnabled())
        //            LOGGER.debug("Counting rules: " + searchCriteria);
        return ruleDAO.count(searchCriteria);
    }

    // =========================================================================
    // Search stuff

    private Search buildRuleSearch(RuleFilter filter) {
        Search searchCriteria = new Search(AdminRule.class);

        if (filter != null) {
            addStringCriteria(searchCriteria, "username", filter.getUser());
            addStringCriteria(searchCriteria, "rolename", filter.getRole());
            addCriteria(searchCriteria, "instance", filter.getInstance());

            addStringCriteria(searchCriteria, "workspace", filter.getWorkspace());
        }

        return searchCriteria;
    }

    // =========================================================================

    private Search buildFixedRuleSearch(RuleFilter filter) {
        Search searchCriteria = new Search(AdminRule.class);

        if (filter != null) {
            addFixedStringCriteria(searchCriteria, "username", filter.getUser());
            addFixedStringCriteria(searchCriteria, "rolename", filter.getRole());
            addFixedCriteria(searchCriteria, "instance", filter.getInstance());

            addFixedStringCriteria(searchCriteria, "workspace", filter.getWorkspace());
        }

        return searchCriteria;
    }

    // ==========================================================================

    private List<ShortAdminRule> convertToShortList(List<AdminRule> list) {
        List<ShortAdminRule> shortList = new ArrayList(list.size());
        for (AdminRule rule : list) {
            shortList.add(new ShortAdminRule(rule));
        }

        return shortList;
    }

    // ==========================================================================

    public void setAdminRuleDAO(AdminRuleDAO ruleDAO) {
        this.ruleDAO = ruleDAO;
    }
}

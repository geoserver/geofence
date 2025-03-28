/* (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import java.util.ArrayList;
import java.util.List;

import org.geoserver.geofence.core.model.AdminRule;
import org.geoserver.geofence.core.model.enums.InsertPosition;
import org.geoserver.geofence.core.dao.AdminRuleDAO;
import org.geoserver.geofence.core.dao.search.Search;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.ShortAdminRule;

import org.geoserver.geofence.services.exception.BadRequestServiceEx;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;

import static org.geoserver.geofence.services.util.FilterUtils.addCriteria;
import static org.geoserver.geofence.services.util.FilterUtils.addPagingConstraints;
import static org.geoserver.geofence.services.util.FilterUtils.addStringCriteria;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geoserver.geofence.core.dao.search.BaseSearch;
import org.geoserver.geofence.core.dao.search.LongSearch;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class AdminRuleAdminServiceImpl implements AdminRuleAdminService {

    private final static Logger LOGGER = LogManager.getLogger(AdminRuleAdminServiceImpl.class);

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
     * Shifts the priority of the rules having <TT>priority &gt;= priorityStart</TT>
     * down by <TT>offset</TT>.
     * <P/>
     * The shift will not be performed if there are no Rules with priority: <BR/>
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
        Search search = ruleDAO.createSearch();
        search.addFilterEqual("username", username);
        List<AdminRule> list = ruleDAO.search(search);
        
        if(LOGGER.isInfoEnabled())
            LOGGER.info("Removing "+list.size()+" AdminRule for user " + username);
        for (AdminRule rule : list) {
            if(LOGGER.isInfoEnabled())
                LOGGER.info("Removing rule for user " + username+": " + rule);
            ruleDAO.remove(rule);
        }
    }

    @Override
    public void deleteRulesByRole(String rolename) throws NotFoundServiceEx {
        Search search = ruleDAO.createSearch();
        search.addFilterEqual("rolename", rolename);
        List<AdminRule> list = ruleDAO.search(search);
        
        for (AdminRule rule : list) {
            if(LOGGER.isInfoEnabled())
                LOGGER.info("Removing rule for role " + rolename+": " + rule);
            ruleDAO.remove(rule);
        }
    }

    @Override
    public void deleteRulesByInstance(long instanceId) throws NotFoundServiceEx {
        Search search = ruleDAO.createSearch();
        search.addFilterEqual("instance.id", instanceId);
        List<AdminRule> list = ruleDAO.search(search);
        
        for (AdminRule rule : list) {
            if(LOGGER.isInfoEnabled())
                LOGGER.info("Removing AdminRule for instance " + instanceId+": " + rule);
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
        Search search = buildSearch(page, entries, filter);

        List<AdminRule> found = ruleDAO.search(search);
        return convertToShortList(found);
    }

    @Override
    public List<ShortAdminRule> getRulesByPriority(long priority, Integer page, Integer entries) {
        Search search = ruleDAO.createSearch();
        search.addFilterGreaterOrEqual("priority", priority);
        search.addSortAsc("priority");
        addPagingConstraints(search, page, entries);
        List<AdminRule> found = ruleDAO.search(search);
        return convertToShortList(found);
    }

    @Override
    public ShortAdminRule getRuleByPriority(long priority) throws BadRequestServiceEx {
        Search search = ruleDAO.createSearch();
        search.addFilterEqual("priority", priority);
        List<AdminRule> found = ruleDAO.search(search);
        
        if(found.isEmpty())
            return null;

        if(found.size() > 1) {
            LOGGER.error("Unexpected rule count for priority " + priority + " : " + found);
        }

        return new ShortAdminRule(found.get(0));
    }

    @Override
    public List<AdminRule> getListFull(RuleFilter filter, Integer page, Integer entries) {
        Search search = buildSearch(page, entries, filter);
        List<AdminRule> found = ruleDAO.search(search);
        return found;
    }

    protected Search buildSearch(Integer page, Integer entries, RuleFilter filter) throws BadRequestServiceEx {
        Search search = ruleDAO.createSearch();
        buildRuleSearch(search, filter);
        addPagingConstraints(search, page, entries);
        search.addSortAsc("priority");
        return search;
    }

    @Override
    public long getCountAll() {
        return count(new RuleFilter(RuleFilter.SpecialFilterType.ANY));
    }

    @Override
    public long count(RuleFilter filter) {
        LongSearch search = ruleDAO.createLongSearch();
        buildRuleSearch(search, filter);
        return ruleDAO.count(search);
    }

    // =========================================================================
    // Search stuff

    private BaseSearch buildRuleSearch(BaseSearch search, RuleFilter filter) {

        if(filter != null) {
            addStringCriteria(search, "username", filter.getUser());
            addStringCriteria(search, "rolename", filter.getRole());
            addCriteria(search, search.addJoin("instance"), filter.getInstance());

            addStringCriteria(search, "workspace", filter.getWorkspace());
        }

        return search;
    }

    //=========================================================================

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

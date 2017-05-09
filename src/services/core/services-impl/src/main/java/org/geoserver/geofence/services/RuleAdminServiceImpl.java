/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.geoserver.geofence.core.dao.LayerDetailsDAO;
import org.geoserver.geofence.core.dao.RuleDAO;
import org.geoserver.geofence.core.dao.RuleLimitsDAO;
import org.geoserver.geofence.core.model.LayerAttribute;
import org.geoserver.geofence.core.model.LayerDetails;
import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.core.model.RuleLimits;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.core.model.enums.InsertPosition;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.ShortRule;
import org.geoserver.geofence.services.exception.BadRequestServiceEx;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.geoserver.geofence.services.util.FilterUtils.*;

/**
 * <B>Note:</B> <TT>service</TT> and <TT>request</TT> params are usually set by
 * the client, and by OGC specs they are not case sensitive, so we're going to
 * turn all of them uppercase. See also {@link RuleReaderServiceImpl}.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RuleAdminServiceImpl implements RuleAdminService {

    private final static Logger LOGGER = LogManager.getLogger(RuleAdminServiceImpl.class);

    private RuleDAO ruleDAO;
    private RuleLimitsDAO limitsDAO;
    private LayerDetailsDAO detailsDAO;

    // =========================================================================
    // Basic operations
    // =========================================================================

    @Override
    public long insert(Rule rule) {
        sanitizeFields(rule);
        ruleDAO.persist(rule);
        return rule.getId();
    }

    @Override
    public long insert(Rule rule, InsertPosition position) {
        sanitizeFields(rule);
        ruleDAO.persist(rule, position);
        return rule.getId();
    }

    @Override
    public long update(Rule rule) throws NotFoundServiceEx {
        Rule orig = ruleDAO.find(rule.getId());

        if (orig == null) {
            throw new NotFoundServiceEx("Rule not found", rule.getId());
        }
        sanitizeFields(rule);
        if (orig.getAccess().equals(GrantType.ALLOW) && rule.getAccess().equals(GrantType.LIMIT)) {
            rule.setLayerDetails(null);
            detailsDAO.remove(orig.getLayerDetails());
        }

        if (orig.getAccess().equals(GrantType.LIMIT) &&
                (rule.getAccess().equals(GrantType.ALLOW) || rule.getAccess().equals(GrantType.DENY)))
            limitsDAO.remove(orig.getRuleLimits());

        ruleDAO.merge(rule);
        return orig.getId();
    }

    /**
     * Shifts the priority of the rules having <TT>priority &gt;= priorityStart</TT>
     * down by <TT>offset</TT>.
     * <p/>
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


    /**
     * <TT>service</TT> and <TT>request</TT> params are usually set by
     * the client, and by OGC specs they are not case sensitive, so we're going to
     * turn all of them uppercase. See also {@link RuleReaderServiceImpl}.
     */
    protected void sanitizeFields(Rule rule) {
        // read class' javadoc
        if (rule.getService() != null) {
            rule.setService(rule.getService().toUpperCase());
        }
        if (rule.getRequest() != null) {
            rule.setRequest(rule.getRequest().toUpperCase());
        }
    }

    @Override
    public Rule get(long id) throws NotFoundServiceEx {
        Rule rule = ruleDAO.find(id);

        if (rule == null) {
            throw new NotFoundServiceEx("Rule not found", id);
        }

        return rule;
    }

    @Override
    public boolean delete(long id) throws NotFoundServiceEx {
        Rule rule = ruleDAO.find(id);

        if (rule == null) {
            throw new NotFoundServiceEx("Rule not found", id);
        }

        // data on ancillary tables should be deleted by cascading
        return ruleDAO.remove(rule);
    }

    @Override
    public void deleteRulesByUser(String username) throws NotFoundServiceEx {
        Search searchCriteria = new Search(Rule.class);
        searchCriteria.addFilter(Filter.equal("username", username));

        List<Rule> list = ruleDAO.search(searchCriteria);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Removing " + list.size() + " rules for user " + username);
        for (Rule rule : list) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Removing rule for user " + username + ": " + rule);
            ruleDAO.remove(rule);
        }
    }

    @Override
    public void deleteRulesByRole(String rolename) throws NotFoundServiceEx {
        Search searchCriteria = new Search(Rule.class);
        searchCriteria.addFilter(Filter.equal("rolename", rolename));

        List<Rule> list = ruleDAO.search(searchCriteria);
        for (Rule rule : list) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Removing rule for role " + rolename + ": " + rule);
            ruleDAO.remove(rule);
        }
    }

    @Override
    public void deleteRulesByInstance(long instanceId) throws NotFoundServiceEx {
        Search searchCriteria = new Search(Rule.class);
        searchCriteria.addFilter(Filter.equal("instance.id", instanceId));

        List<Rule> list = ruleDAO.search(searchCriteria);
        for (Rule rule : list) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Removing rule for instance " + instanceId + ": " + rule);
            ruleDAO.remove(rule);
        }
    }


    @Override
    public List<ShortRule> getAll() {
        List<Rule> found = ruleDAO.findAll();
        return convertToShortList(found);
    }

//    /**
//     * Creating a filter heuristically
//     * <UL>
//     * <LI>a null id will only match a null field</LI>
//     * <LI>an id=="*" will match everything, so no filter condition is needed</LI>
//     * <LI>a valid numeric id will only match that numeric value</LI>
//     * </UL>
//     */

//    /**
//     * @deprecated
//     */
//    @Override
//    @Deprecated
//    public List<ShortRule> getList(String userId, String profileId, String instanceId,
//            String service, String request,
//            String workspace, String layer,
//            Integer page, Integer entries) {
//
//        RuleFilter filter = new RuleFilter(0L, 0L, 0L, service, request, workspace, layer);
//        // adjust IDs
//        adjustFilterHeuristic(filter.getUser(), userId);
//        adjustFilterHeuristic(filter.getUserGroup(), profileId);
//        adjustFilterHeuristic(filter.getInstance(), instanceId);
//
//        return getList(filter, page, entries);
//    }

//    private void adjustFilterHeuristic(IdNameFilter filter, String id) {
//        if (id == null) {
//            filter.setType(RuleFilter.SpecialFilterType.DEFAULT);
//        } else if (id.equals("*")) {
//            filter.setType(RuleFilter.SpecialFilterType.ANY);
//        } else {
//            try {
//                filter.setId(Long.valueOf(id));
//            } catch (NumberFormatException ex) {
//                throw new BadRequestServiceEx("Bad id '" + id + "'");
//            }
//        }
//    }

    @Override
    public List<ShortRule> getList(RuleFilter filter, Integer page, Integer entries) {
        Search searchCriteria = buildSearch(page, entries, filter);

        List<Rule> found = ruleDAO.search(searchCriteria);
        return convertToShortList(found);
    }

    @Override
    public ShortRule getRule(RuleFilter filter) throws BadRequestServiceEx {
        Search searchCriteria = buildFixedRuleSearch(filter);
        List<Rule> found = ruleDAO.search(searchCriteria);
        if (found.isEmpty())
            return null;

        if (found.size() > 1) {
            LOGGER.error("Unexpected rule count for filter " + filter + " : " + found);
        }

        return new ShortRule(found.get(0));
    }

    @Override
    public List<ShortRule> getRulesByPriority(long priority, Integer page, Integer entries) {
        Search searchCriteria = new Search(Rule.class);
        searchCriteria.addFilter(Filter.greaterOrEqual("priority", priority));
        searchCriteria.addSortAsc("priority");
        addPagingConstraints(searchCriteria, page, entries);
        List<Rule> found = ruleDAO.search(searchCriteria);
        return convertToShortList(found);
    }

    @Override
    public ShortRule getRuleByPriority(long priority) throws BadRequestServiceEx {
        Search searchCriteria = new Search(Rule.class);
        searchCriteria.addFilter(Filter.equal("priority", priority));
        List<Rule> found = ruleDAO.search(searchCriteria);
        if (found.isEmpty())
            return null;

        if (found.size() > 1) {
            LOGGER.error("Unexpected rule count for priority " + priority + " : " + found);
        }

        return new ShortRule(found.get(0));
    }

    @Override
    public List<Rule> getListFull(RuleFilter filter, Integer page, Integer entries) {
        Search searchCriteria = buildSearch(page, entries, filter);
        List<Rule> found = ruleDAO.search(searchCriteria);
        return found;
    }

    protected Search buildSearch(Integer page, Integer entries, RuleFilter filter) throws BadRequestServiceEx {
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
        Search searchCriteria = new Search(Rule.class);

        if (filter != null) {
            addStringCriteria(searchCriteria, "username", filter.getUser());
            addStringCriteria(searchCriteria, "rolename", filter.getRole());
            addCriteria(searchCriteria, "instance", filter.getInstance());

            addStringCriteria(searchCriteria, "service", filter.getService()); // see class' javadoc
            addStringCriteria(searchCriteria, "request", filter.getRequest()); // see class' javadoc
            addStringCriteria(searchCriteria, "workspace", filter.getWorkspace());
            addStringCriteria(searchCriteria, "layer", filter.getLayer());
        }

        return searchCriteria;
    }

    //=========================================================================

    private Search buildFixedRuleSearch(RuleFilter filter) {
        Search searchCriteria = new Search(Rule.class);

        if (filter != null) {
            addFixedStringCriteria(searchCriteria, "username", filter.getUser());
            addFixedStringCriteria(searchCriteria, "rolename", filter.getRole());
            addFixedCriteria(searchCriteria, "instance", filter.getInstance());

            addFixedStringCriteria(searchCriteria, "service", filter.getService()); // see class' javadoc
            addFixedStringCriteria(searchCriteria, "request", filter.getRequest()); // see class' javadoc
            addFixedStringCriteria(searchCriteria, "workspace", filter.getWorkspace());
            addFixedStringCriteria(searchCriteria, "layer", filter.getLayer());
        }

        return searchCriteria;
    }


    // =========================================================================
    // Limits
    // =========================================================================

    @Override
    public void setLimits(Long ruleId, RuleLimits limits) {
        Rule rule = ruleDAO.find(ruleId);
        if (rule == null)
            throw new NotFoundServiceEx("Rule not found");

        if (rule.getAccess() != GrantType.LIMIT && limits != null)
            throw new BadRequestServiceEx("Rule is not of LIMIT type");

        // remove old limits if any
        if (rule.getRuleLimits() != null) {
            limitsDAO.remove(rule.getRuleLimits());
        }

        if (limits != null) {
            limits.setId(ruleId);
            limits.setRule(rule);
            limitsDAO.persist(limits);
        } else {
            LOGGER.info("Removing limits for " + rule);
            // TODO: remove limits (already removed above?)
        }
    }

    // =========================================================================
    // Details
    // =========================================================================

//    @Override
//    public LayerDetails getDetails(long id) throws ResourceNotFoundFault {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    @Override
    public void setDetails(Long ruleId, LayerDetails detailsNew) {
        Rule rule = ruleDAO.find(ruleId);
        if (rule == null)
            throw new NotFoundServiceEx("Rule not found");

        if (rule.getLayer() == null && detailsNew != null)
            throw new BadRequestServiceEx("Rule does not refer to a fixed layer");

        if (rule.getAccess() != GrantType.ALLOW && detailsNew != null)
            throw new BadRequestServiceEx("Rule is not of ALLOW type");

//        final Map<String, String> oldProps;
        final Set<String> oldStyles;
        final Set<LayerAttribute> oldAttrs;

        // remove old details if any
        if (rule.getLayerDetails() != null) {
//            oldProps = detailsDAO.getCustomProps(ruleId);
            oldStyles = detailsDAO.getAllowedStyles(ruleId);
            oldAttrs = rule.getLayerDetails().getAttributes();
            detailsDAO.remove(rule.getLayerDetails());
        } else {
//            oldProps = null;
            oldStyles = null;
            oldAttrs = null;
        }

        rule = ruleDAO.find(ruleId);
        if (rule.getLayerDetails() != null)
            throw new IllegalStateException("LayerDetails should be null");

        if (detailsNew != null) {

            if (detailsNew.getAttributes() == null) {
                if (oldStyles != null) {
                    LOGGER.info("Restoring " + oldAttrs.size() + " old attribs");
                    detailsNew.setAttributes(oldAttrs);
                } else {
                    // no new, no old, nothing to do
                }
            } else {
                if (LOGGER.isDebugEnabled()) {
                    if (oldAttrs != null && oldAttrs.equals(detailsNew.getAttributes())) {
                        LOGGER.debug("New attribs are the same as old ones:" + detailsNew.getAttributes());
                    } else
                        LOGGER.debug("Setting " + detailsNew.getAttributes().size() + " new attrs " + detailsNew.getAttributes());
                }
                // we're using a brand new Set, bc the old one may have been set as deleted by hibernate
                final Set<LayerAttribute> clonedAttrs = new HashSet<LayerAttribute>(detailsNew.getAttributes());
                detailsNew.setAttributes(clonedAttrs);
            }

            detailsNew.setRule(rule);
            detailsDAO.persist(detailsNew);
            // restore old properties
//            if(oldProps != null) {
//                LOGGER.info("Restoring " + oldProps.size() + " props from older LayerDetails (id:"+ruleId+")");
//                //cannot reuse the same Map returned by Hibernate, since it is detached now.
//                Map<String, String> newProps = new HashMap<String, String>();
//                newProps.putAll(oldProps);
//                detailsDAO.setCustomProps(ruleId, newProps);
//            }

            if (detailsNew.getAllowedStyles() != null) {
                LOGGER.info("Setting " + detailsNew.getAllowedStyles().size() + " new styles");
                Set<String> newStyles = new HashSet<String>();
                newStyles.addAll(detailsNew.getAllowedStyles());
                detailsDAO.setAllowedStyles(ruleId, newStyles);
            } else if (oldStyles != null) {  // no new style list, check for old list to restore
                LOGGER.info("Restoring " + oldStyles.size() + " styles from older LayerDetails (id:" + ruleId + ")");
                //cannot reuse the same Map returned by Hibernate, since it is detached now.
                Set<String> newStyles = new HashSet<String>();
                newStyles.addAll(oldStyles);
                detailsDAO.setAllowedStyles(ruleId, newStyles);
            }
        } else {
            LOGGER.info("Removing details for " + rule);
        }
    }


    @Override
    public void setAllowedStyles(Long ruleId, Set<String> styles) {
        Rule rule = ruleDAO.find(ruleId);
        if (rule == null)
            throw new NotFoundServiceEx("Rule not found");

        if (rule.getLayerDetails() == null) {
            throw new NotFoundServiceEx("Rule has no details associated");
        }

        detailsDAO.setAllowedStyles(ruleId, styles);
    }

    @Override
    public Set<String> getAllowedStyles(Long ruleId) {
        Rule rule = ruleDAO.find(ruleId);
        if (rule == null)
            throw new NotFoundServiceEx("Rule not found");

        if (rule.getLayerDetails() == null) {
            throw new NotFoundServiceEx("Rule has no details associated");
        }
        return detailsDAO.getAllowedStyles(ruleId);
    }


    // ==========================================================================

    private List<ShortRule> convertToShortList(List<Rule> list) {
        List<ShortRule> shortList = new ArrayList<ShortRule>(list.size());
        for (Rule rule : list) {
            shortList.add(new ShortRule(rule));
        }

        return shortList;
    }

    // ==========================================================================

    public void setRuleDAO(RuleDAO ruleDAO) {
        this.ruleDAO = ruleDAO;
    }

    public void setRuleLimitsDAO(RuleLimitsDAO ruleLimitsDAO) {
        this.limitsDAO = ruleLimitsDAO;
    }

    public void setLayerDetailsDAO(LayerDetailsDAO detailsDAO) {
        this.detailsDAO = detailsDAO;
    }

}

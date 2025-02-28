/* (c) 2014, 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.core.model.LayerDetails;
import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.core.model.RuleLimits;
import org.geoserver.geofence.core.model.enums.InsertPosition;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.ShortRule;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;

import java.util.List;
import java.util.Set;


/**
 * Operations on {@link Rule Rule}s.
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface RuleAdminService
{

    // ==========================================================================
    // Basic operations

    long insert(Rule rule);

    long insert(Rule rule, InsertPosition position);

    long update(Rule rule) throws NotFoundServiceEx;

    /**
     * Shifts the priority of the rules having <TT>priority &gt;= priorityStart</TT>
     * down by <TT>offset</TT>.
     *
     * @return the number of rules updated.
     */
    int shift(long priorityStart, long offset);

    /**
     * Swaps the priorities of two rules.
     */
    void swap(long id1, long id2);

    boolean delete(long id) throws NotFoundServiceEx;


    // Internal, no REST annotations
    void deleteRulesByUser(String username) throws NotFoundServiceEx;

    // Internal, no REST annotations
    void deleteRulesByRole(String rolename) throws NotFoundServiceEx;

    // Internal, no REST annotations
    void deleteRulesByInstance(long instanceId) throws NotFoundServiceEx;

    Rule get(long id) throws NotFoundServiceEx;

    List<ShortRule> getAll();

    /**
     * Return the Rules according to the filter.
     *
     * @param page used for retrieving paged data, may be null if not used. If not null, also <TT>entries</TT> should be defined.
     * @param entries used for retrieving paged data, may be null if not used. If not null, also <TT>page</TT> should be defined.
     * @return Rules in short format
     *
     * @see RuleReaderService#getMatchingRules(RuleFilter)
     */
    List<ShortRule> getList(RuleFilter filter, Integer page, Integer entries);

    /**
     * Search a Rule by priority.
     * 
     * Returns the rule having the requested priority, or null if none found.
     *
     * @return the Rule in short format or null
     */
    ShortRule getRuleByPriority(long priority);

    /**
     * Return the Rules according to the priority.Returns the rules having priority greater or equal to <code>priority</code>
     * 
     *
     * @param priority priority threshold
     * @param page used for retrieving paged data, may be null if not used. If not null, also <TT>entries</TT> should be defined.
     * @param entries used for retrieving paged data, may be null if not used. If not null, also <TT>page</TT> should be defined.
     * @return Rules in short format
     */
    List<ShortRule> getRulesByPriority(long priority, Integer page, Integer entries);
    
    /**
     * Return the Rules according to the filter.Rules will be enriched with all their joined data, so this method may be heavy to execute.
     *
     * @param page used for retrieving paged data, may be null if not used. If not null, also <TT>entries</TT> should be defined.
     * @param entries used for retrieving paged data, may be null if not used. If not null, also <TT>page</TT> should be defined.
     * @return the list of Rules in full format
     *
     * @see RuleReaderService#getMatchingRules(RuleFilter)
     */
    List<Rule> getListFull(RuleFilter filter, Integer page, Integer entries);

    /**
     * Return the Rules count according to the filter.
     * @param filter
     * @return
     */
    long count(RuleFilter filter);

    long getCountAll();

    // ==========================================================================
    // ==========================================================================

    void setLimits(Long ruleId, RuleLimits limits);

    // ==========================================================================

    /**
     * <P>
     * When setting new Details, old CustomProps will be retained.
     */
    void setDetails(Long ruleId, LayerDetails details);

    Set<String> getAllowedStyles(Long ruleId);

    void setAllowedStyles(Long ruleId, Set<String> styles);

    // ==========================================================================


}

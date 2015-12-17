/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.core.model.AdminRule;
import org.geoserver.geofence.core.model.enums.InsertPosition;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.ShortAdminRule;
import org.geoserver.geofence.services.exception.BadRequestServiceEx;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;

import java.util.List;


/**
 * Operations on {@link AdminRule AdminRule}s.
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface AdminRuleAdminService
{

    // ==========================================================================
    // Basic operations

    long insert(AdminRule rule);

    long insert(AdminRule rule, InsertPosition position);

    long update(AdminRule rule) throws NotFoundServiceEx;

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

    AdminRule get(long id) throws NotFoundServiceEx;

    List<ShortAdminRule> getAll();

//    /**
//     * Return the Rules according to the filter.
//     * <UL>
//     * <LI>If a parameter is set to "*", it will match any null or not null value;</LI>
//     * <LI>If a parameter is set to <TT>null</TT>, it will match only null values;</LI>
//     * <LI>If a parameter is set to other values, it will strictly match the related field value;</LI>
//     * </UL>
//     *
//     * @param userId The (Long) id of the GSUser, OR the "*" String, OR null
//     * @param profileId The (Long) id of the Profile, OR the "*" String, OR null
//     * @param instanceId The (Long) id of the GSInstance, OR the "*" String, OR null
//     *
//     * @param page used for retrieving paged data, may be null if not used. If not null, also <TT>entries</TT> should be defined.
//     * @param entries used for retrieving paged data, may be null if not used. If not null, also <TT>page</TT> should be defined.
//     *
//     * @see RuleReaderService#getMatchingRules(String, String, String,  String,String, String,String) RuleReaderService.getMatchingRules(...)
//     * @deprecated Use {@link getList(RuleFilter,Integer,Integer)}
//     */
//    @GET
//    @Path("/rules/user.id/{userId}/profile.id/{profileId}/instance.id/{instanceId}/{service}/{request}/{workspace}/{layer}")
//    List<ShortRule> getList(@PathParam("userId") String userId,
//        @PathParam("profileId") String profileId,
//        @PathParam("instanceId") String instanceId,
//        @PathParam("workspace") String workspace,
//        @QueryParam("page") Integer page,
//        @QueryParam("entries") Integer entries);


    /**
     * Return the Rules according to the filter.
     *
     * @param page used for retrieving paged data, may be null if not used. If not null, also <TT>entries</TT> should be defined.
     * @param entries used for retrieving paged data, may be null if not used. If not null, also <TT>page</TT> should be defined.
     *
     * @see RuleReaderService#getMatchingRules(RuleFilter)
     */
    List<ShortAdminRule> getList(RuleFilter filter, Integer page, Integer entries);

    /**
     * Search a Rule by priority.
     *
     * Returns the rule having the requested priority, or null if none found.
     */
    ShortAdminRule getRuleByPriority(long priority);

    /**
     * Return the Rules according to the priority.
     *
     * Returns the rules having priority greater or equal to <code>priority</code>
     *
     * @param page used for retrieving paged data, may be null if not used. If not null, also <TT>entries</TT> should be defined.
     * @param entries used for retrieving paged data, may be null if not used. If not null, also <TT>page</TT> should be defined.
     */
    List<ShortAdminRule> getRulesByPriority(long priority, Integer page, Integer entries);

    /**
     * Return a single Rule according to the filter.
     *
     * Search for a precise rule match.
     * No ANY filter is allowed.
     * Name/id specification with default inclusion is not allowed.
     *
     * @return the matching rule or null if not found
     *
     * @throws BadRequestServiceEx if a wildcard type is used in filter
     */
    ShortAdminRule getRule(RuleFilter filter) throws BadRequestServiceEx;
    
    /**
     * Return the Rules according to the filter.
     * Rules will be enriched with all their joined data, so this method may be heavy to execute.
     *
     * @param page used for retrieving paged data, may be null if not used. If not null, also <TT>entries</TT> should be defined.
     * @param entries used for retrieving paged data, may be null if not used. If not null, also <TT>page</TT> should be defined.
     *
     * @see RuleReaderService#getMatchingRules(RuleFilter)
     */
    List<AdminRule> getListFull(RuleFilter filter, Integer page, Integer entries);

    /**
     * Return the Rules count according to the filter.
     * @param filter
     * @return
     */
    long count(RuleFilter filter);

    long getCountAll();

    // ==========================================================================

}

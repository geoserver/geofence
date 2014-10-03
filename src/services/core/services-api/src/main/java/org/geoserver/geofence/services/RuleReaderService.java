/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.services.dto.AccessInfo;
import org.geoserver.geofence.services.dto.AuthUser;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.ShortRule;

import java.util.List;

/**
 * Operations on
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface RuleReaderService
{

    /**
     * Return info on resource accessibility.
     * <P>
     * All parameters reference instances by name.<BR>
     * <LI>If a given parameter is "<TT>*</TT>", it will match <B>any</B> value in the related {@link Rule} field.</LI>
     * <LI>If a given parameter is <TT>null</TT>, it will match only null (default) values in the related {@link Rule} field.</LI>
     * </UL>
     * In order to have a better control on the query, please use {@link #getAccessInfo(RuleFilter filter) }.
     *
     * @deprecated Use {@link #getAccessInfo(RuleFilter filter) }
     */
    AccessInfo getAccessInfo(String userName,
        String profileName,
        String instanceName,
        String sourceAddress,
        String service,
        String request,
        String workspace,
        String layer);

    /**
     * Return info on resource accessibility.
     */
    AccessInfo getAccessInfo(RuleFilter filter);

    /**
     * Return the unprocessed {@link Rule} list matching a given filter, sorted
     * by priority.
     * <P>
     * Use {@link getAccessInfo(String,String,String,String,String,String,String) getAccessInfo}
     * if you need the resulting coalesced access info.
     * <P>
     * Differently from {@link RuleAdminService#getList(String,String,String,String,String,String,String,Integer,Integer) RuleAdminService.getList(...)},
     *  when a param is set, it will match
     * all the rules with the corresponding matching field,
     * plus all the rules having that field set to <T>null</TT>.
     * <BR>Null params will only match null values.
     * <BR>The "*" string will always match.
     *
     * @deprecated Use {@link #getMatchingRules(RuleFilter filter) }
     */

    List<ShortRule> getMatchingRules(String userName,
        String profileName,
        String instanceName,
        String sourceAddress,
        String service,
        String request,
        String workspace,
        String layer);

    /**
     * Return the unprocessed {@link Rule} list matching a given filter, sorted
     * by priority.
     * <P>
     * Use {@link getAccessInfo(RuleFilter) getAccessInfo(RuleFilter)}
     * if you need the resulting coalesced access info.
     */
    List<ShortRule> getMatchingRules(RuleFilter filter);

    public AuthUser authorize(String username, String password);

    // ==========================================================================

}

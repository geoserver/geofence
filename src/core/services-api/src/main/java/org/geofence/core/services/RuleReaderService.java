/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.services;

import java.util.List;
import org.geofence.core.services.dto.AccessInfo;
import org.geofence.core.services.dto.PermsResult;
import org.geofence.core.services.dto.RuleFilter;
import org.geofence.core.services.dto.ShortRule;

/**
 * Operations on
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface RuleReaderService {

    /** Return info on resource accessibility. */
    AccessInfo getAccessInfo(RuleFilter filter);

    /**
     * Returns a closed representation (a CQL filter plus the flattened accessible-resources set) of every resource the
     * filter's user/role is granted at least some access to.
     *
     * <p>{@code filter}'s service/request/subfield/workspace/layer must all be {@code ANY} - this is a discovery query
     * over the resources a user can see, not an access check on a specific one.
     */
    PermsResult getPermissionFilter(RuleFilter filter);

    /**
     * info about admin authorization on a given workspace.
     *
     * <p>Returned AccessInfo will always be ALLOW, with the computed adminRights.
     */
    AccessInfo getAdminAuthorization(RuleFilter filter);

    /**
     * Return the unprocessed {@link Rule} list matching a given filter, sorted by priority.
     *
     * <p>Use {@link getAccessInfo(RuleFilter) getAccessInfo(RuleFilter)} if you need the resulting coalesced access
     * info.
     */
    List<ShortRule> getMatchingRules(RuleFilter filter);

    // ==========================================================================

}

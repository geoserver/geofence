/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao;

import org.geoserver.geofence.core.model.Prioritizable;
import org.geoserver.geofence.core.model.enums.InsertPosition;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public interface PrioritizableDAO<T extends Prioritizable> extends RestrictedGenericDAO<T>{

    long persist(T entity, InsertPosition position);

    /**
     * Shifts the priority of the rules having <TT>priority &gt;= priorityStart</TT>
     * down by <TT>offset</TT>.
     * <P/>
     * The shift will not be performed if there are no Rules with priority: <BR/>
     * <tt> startPriority &lt;= priority &lt; startPriority + offset </TT>
     *
     * @return the number of rules updated, or -1 if no need to shift.
     */
    int shift(long priorityStart, long offset);

    /**
     * Swaps the priorities of the two rules.
     */
    void swap(long id1, long id2);

}

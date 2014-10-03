/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model.enums;

/**
 * Used in DAOs and Services
 *
 * @author ETj (etj at geo-solutions.it)
*/
public enum InsertPosition {

    /** priority is a fixed value */
    FIXED,
    /** priority is the position from start (0 is the first one) */
    FROM_START,
    /** * priority is the position from end (0 is the last one) */
    FROM_END
}

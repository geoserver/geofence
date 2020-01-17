/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao;

import org.geoserver.geofence.core.model.AdminRule;
import org.geoserver.geofence.core.model.enums.InsertPosition;

/**
 * Public interface to define operations on AdminRule
 * 
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */

public interface AdminRuleDAO //
        extends PrioritizableDAO<AdminRule>, SearchableDAO<AdminRule> {

    long persist(AdminRule entity, InsertPosition position);
}

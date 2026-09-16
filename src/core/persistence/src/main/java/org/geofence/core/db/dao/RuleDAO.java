/* (c) 2014, 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db.dao;

import org.geofence.core.model.Rule;
import org.geofence.core.model.enums.InsertPosition;

/**
 * Public interface to define operations on Rule
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface RuleDAO //
extends PrioritizableDAO<Rule>, SearchableDAO<Rule> {

    long persist(Rule entity, InsertPosition position);
}

/* (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.services.spi;

import java.util.Set;

/** @author ETj (etj at geo-solutions.it) */
public interface UserResolver {

    Set<String> getRoles(String username);
}

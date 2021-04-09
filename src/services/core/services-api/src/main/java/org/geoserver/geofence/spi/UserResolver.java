/* (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.spi;

import java.util.Set;

/** @author ETj (etj at geo-solutions.it) */
public interface UserResolver {

    @Deprecated
    boolean existsUser(String username);

    @Deprecated
    boolean existsRole(String rolename);

    Set<String> getRoles(String username);
}

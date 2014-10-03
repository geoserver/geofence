/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao;

import java.util.Set;

/**
*
* @author ETj (etj at geo-solutions.it)
*/
public interface AllowedStylesProvider {
        
        public Set<String> getAllowedStyles(Long id);
        public void setAllowedStyles(Long id, Set<String> styles);

}

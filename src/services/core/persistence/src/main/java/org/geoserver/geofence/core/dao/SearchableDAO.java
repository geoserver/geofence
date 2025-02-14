/* (c) 2020 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.core.dao;

import java.util.List;
import org.geoserver.geofence.core.dao.search.Search;

/**
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface SearchableDAO<ENTITY> {    
    
    public Search createSearch();
    public Search createCountSearch();

    public List<ENTITY> search(Search search);
    public long count(Search search);    
}

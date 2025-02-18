/* (c) 2020 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.core.dao;

import java.util.List;
import org.geoserver.geofence.core.dao.search.LongSearch;
import org.geoserver.geofence.core.dao.search.Search;

/**
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public interface SearchableDAO<ENTITY> {
    
    public Search<ENTITY> createSearch();
    public LongSearch<ENTITY> createLongSearch();

    public List<ENTITY> search(Search<ENTITY> search);
    public long count(LongSearch<ENTITY> search);    
}

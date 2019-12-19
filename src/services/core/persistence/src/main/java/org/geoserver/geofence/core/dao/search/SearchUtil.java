/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.search;

import javax.persistence.criteria.Join;
import org.geoserver.geofence.core.model.IPAddressRange;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class SearchUtil {

    /**
     * Adds a search field for finding the precise addressrange instance.
     * Does <b>NOT</b> search for an address in a range.
     */
    public static void addAddressRangeSearch(Search search, IPAddressRange addressRange) {
        
//        search.addFetch("addressRange", IPAddressRange.class);
        Join ar = search.addJoin("addressRange");
        
        if(addressRange != null ) {
            // it's embedded
            addSearchField(search, ar, "low", addressRange.getLow());
            addSearchField(search, ar, "high", addressRange.getHigh());
            addSearchField(search, ar, "size", addressRange.getSize());
        } else {
            addSearchField(search, ar, "low", null);
            addSearchField(search, ar, "high", null);
            addSearchField(search, ar, "size", null);
        }
//        if(addressRange != null ) {
//            // it's embedded
//            addSearchField(search, "addressRange.low", addressRange.getLow());
//            addSearchField(search, "addressRange.high", addressRange.getHigh());
//            addSearchField(search, "addressRange.size", addressRange.getSize());
//        } else {
//            addSearchField(search, "addressRange.low", null);
//            addSearchField(search, "addressRange.high", null);
//            addSearchField(search, "addressRange.size", null);
//        }
    }

    public static void addSearchField(Search search, String field, Object o) {
        if ( o == null ) {
            search.addFilterNull(field);
        } else {
            search.addFilterEqual(field, o);
        }
    }
    
    public static void addSearchField(Search search, Join j, String field, Object o) {
        if ( o == null ) {
            search.addFilterNull(j, field);
        } else {
            search.addFilterEqual(j, field, o);
        }
    }
}

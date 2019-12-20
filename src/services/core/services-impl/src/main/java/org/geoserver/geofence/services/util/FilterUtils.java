/* (c) 2015 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.geoserver.geofence.core.dao.search.Search;
import org.geoserver.geofence.core.dao.search.Filter;
import org.geoserver.geofence.core.model.IPRangeProvider;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.exception.BadRequestServiceEx;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class FilterUtils {

    private final static Logger LOGGER = LogManager.getLogger(FilterUtils.class);


    /**
     * Filters out rules not matching with ip address filter.
     *
     * IP address filtering is not performed by DAO at the moment, so we'll have to filter out
     * such results by hand.
     */
    public static <T extends IPRangeProvider>
        List<T> filterByAddress(RuleFilter filter, List<T> rules)
    {
        RuleFilter.FilterType type = filter.getSourceAddress().getType();

        if(type == RuleFilter.FilterType.ANY )
            return rules;

        String ipvalue = null;
        if(type == RuleFilter.FilterType.NAMEVALUE) {
            ipvalue = filter.getSourceAddress().getText();
            if(! IPUtils.isAddressValid(ipvalue)) {
                LOGGER.error("Bad address filter " + ipvalue);
                return Collections.EMPTY_LIST;
            }
        }

        List<T> ret = new ArrayList(rules.size());

        for (T rule : rules) {
            boolean added = false;

            switch(type) {
                case DEFAULT:
                    if(rule.getAddressRange() == null) {
                        ret.add(rule);
                        added = true;
                    }
                    break;

                case NAMEVALUE:
                    if ( filter.getSourceAddress().isIncludeDefault()) {
                        if(rule.getAddressRange() == null || rule.getAddressRange().match(ipvalue) ) {
                            ret.add(rule);
                            added = true;
                        }
                    } else {
                        if(rule.getAddressRange() != null && rule.getAddressRange().match(ipvalue) ) {
                            ret.add(rule);
                            added = true;
                        }
                    }
                    break;

                case IDVALUE:
                default:
                    LOGGER.error("Bad address filter type" + type);
                    return Collections.EMPTY_LIST;
            }

            if(LOGGER.isDebugEnabled()) {
                if(added)
                    LOGGER.debug("ADDED " + rule);
                else
                    LOGGER.debug("NOT ADDED " + rule);
            }
        }

        return ret;
    }

    /**
     * Add criteria for <B>searching</B>.
     *
     * We're dealing with IDs here, so <U>we'll suppose that the related object id field is called "id"</U>.
     */
    public static void addCriteria(Search searchCriteria, String fieldName, RuleFilter.IdNameFilter filter) {
        switch (filter.getType()) {
            case ANY:
                break; // no filtering

            case DEFAULT:
                searchCriteria.addFilterNull(fieldName);
                break;

            case IDVALUE:
                if(filter.isIncludeDefault()) {
                    searchCriteria.addFilterOr(
                            searchCriteria.isNull(fieldName),
                            searchCriteria.isEqual(fieldName + ".id", filter.getId()));
                } else {
                    searchCriteria.addFilterEqual(fieldName + ".id", filter.getId());
                }
                break;

            case NAMEVALUE:
                if(filter.isIncludeDefault()) {
                    searchCriteria.addFilterOr(
                            searchCriteria.isNull(fieldName),
                            searchCriteria.isEqual(fieldName + ".name", filter.getName()));
                } else {
                    searchCriteria.addFilterEqual(fieldName + ".name", filter.getName());
                }
                break;

            default:
                throw new AssertionError();
        }
    }

    public static void addPagingConstraints(Search search, Integer page, Integer entries) {
        if( (page != null && entries == null) || (page ==null && entries != null)) {
            throw new BadRequestServiceEx("Page and entries params should be declared together.");
        }

        if(LOGGER.isInfoEnabled()) {
            LOGGER.info("Searching Rule list " + ( page==null? "(unpaged) " : (" p:"+page + "#:"+entries)));
        }

        if(entries != null) {
            search.setMaxResults(entries);
            search.setPage(page);
        }
    }

    public static void addStringCriteria(Search search, String fieldName, RuleFilter.TextFilter filter) {
        switch (filter.getType()) {
            case ANY:
                break; // no filtering

            case DEFAULT:
                search.addFilterNull(fieldName);
                break;

            case NAMEVALUE:
                if(filter.isIncludeDefault()) {
                    search.addFilterOr(
                            search.isNull(fieldName),
                            search.isEqual(fieldName, filter.getText()));
                } else {
                    search.addFilterEqual(fieldName, filter.getText());
                }
                break;

            case IDVALUE:
            default:
                throw new AssertionError();
        }
    }





    /**
     * Add criteria for <B>searching</B>.
     *
     * We're dealing with IDs here, so <U>we'll suppose that the related object id field is called "id"</U>.
     */
    public static void addFixedCriteria(Search search, String fieldName, RuleFilter.IdNameFilter filter) {
        switch (filter.getType()) {
            case ANY:
                throw new BadRequestServiceEx(fieldName + " should be a fixed search and can't be ANY");

            case DEFAULT:
                search.addFilterNull(fieldName);
                break;

            case IDVALUE:
                if(filter.isIncludeDefault()) {
                    throw new BadRequestServiceEx(fieldName + " should be a fixed search");
                } else {
                    search.addFilterEqual(fieldName + ".id", filter.getId());
                }
                break;

            case NAMEVALUE:
                if(filter.isIncludeDefault()) {
                    throw new BadRequestServiceEx(fieldName + " should be a fixed search");

                } else {
                    search.addFilterEqual(fieldName + ".name", filter.getName());
                }
                break;

            default:
                throw new AssertionError();
        }
    }




    public static void addFixedStringCriteria(Search search, String fieldName, RuleFilter.TextFilter filter) {
        switch (filter.getType()) {
            case ANY:
                throw new BadRequestServiceEx(fieldName + " should be a fixed search and can't be ANY");

            case DEFAULT:
                search.addFilterNull(fieldName);
                break;

            case NAMEVALUE:
                if(filter.isIncludeDefault()) {
                    throw new BadRequestServiceEx(fieldName + " should be a fixed search");
                } else {
                    search.addFilterEqual(fieldName, filter.getText());
                }
                break;

            case IDVALUE:
            default:
                throw new AssertionError();
        }
    }
}

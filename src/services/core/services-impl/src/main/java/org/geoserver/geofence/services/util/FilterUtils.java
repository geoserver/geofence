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

import org.geoserver.geofence.core.dao.SearchableDAO;
import org.geoserver.geofence.core.dao.search.Search;
import org.geoserver.geofence.core.model.IPRangeProvider;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.exception.BadRequestServiceEx;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class FilterUtils {

    private final static Logger LOGGER = LogManager.getLogger(FilterUtils.class);


    public static <RULETYPE extends IPRangeProvider> 
        List<RULETYPE> getFilteredRules(
                SearchableDAO<RULETYPE> dao, 
                RuleFilter filter) {
        return getFilteredRules(dao, filter, null, null);
    }        
    
    public static <RULETYPE extends IPRangeProvider> 
        List<RULETYPE> getFilteredRules(
                SearchableDAO<RULETYPE> dao, 
                RuleFilter filter, 
                Integer page, Integer entries) {
        Search search = dao.createSearch();
        search.addSortAsc("priority");
        addCriteria(search, filter);

        List<RULETYPE> found = dao.search(search);
        found = filterByAddress(filter, found);               
        found = paginateList(found, page, entries);
        return found;
    }
    
    public static long countFilteredRules(
            SearchableDAO dao, 
            RuleFilter filter) {
        
        RuleFilter.FilterType ipFilterType = filter.getSourceAddress().getType();
        if(ipFilterType == RuleFilter.FilterType.ANY) {
            // speedup: bypass ip filtering
            return dao.count(addCriteria(dao.createCountSearch(), filter));
        } else {
            Search search = addCriteria(dao.createSearch(), filter);
            List found = dao.search(search);
            found = filterByAddress(filter, found);
            return found.size();            
        }
    }        
    
    public static <T> 
        List<T> paginateList(
                List<T> list, 
                Integer page, Integer entries) {
        if( (page != null && entries == null) || (page == null && entries != null)) {
            throw new BadRequestServiceEx("Page and entries params should be declared together.");
        }        
        
        if (page == null) {
            return list;
        }        
        
        if(entries <= 0 || page <= 0) {
            throw new IllegalArgumentException("invalid page size: " + entries);
        }
    
        int fromIndex = (page - 1) * entries;
        if(list == null || list.size() <= fromIndex){
            return Collections.emptyList();
        }
    
        // toIndex exclusive
        return list.subList(fromIndex, Math.min(fromIndex + entries, list.size()));
    }
    
    /**
     * Filters out rules not matching with ip address filter.
     *
     * IP address filtering is not performed by DAO at the moment, so we'll have to filter out
     * such results by hand.
     */
    public static <T extends IPRangeProvider>
        List<T> filterByAddress(
                RuleFilter filter, 
                List<T> rules)
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

    // CRITERIA ================================================================

    private static Search addCriteria(Search search, RuleFilter filter) {
        addStringCriteria(search, "username", filter.getUser());
        addStringCriteria(search, "rolename", filter.getRole());
        addCriteria(search, search.addJoin("instance"), filter.getInstance());
        addStringCriteria(search, "service", filter.getService()); // see class' javadoc
        addStringCriteria(search, "request", filter.getRequest()); // see class' javadoc
        addStringCriteria(search, "subfield", filter.getSubfield());
        addStringCriteria(search, "workspace", filter.getWorkspace());
        addStringCriteria(search, "layer", filter.getLayer());
        return search;    
    }
        
    /**
     * Add criteria for <B>searching</B>.
     *
     * We're dealing with IDs here, so <U>we'll suppose that the related object id field is called "id"</U>.
     */
    public static void addCriteria(Search search, Search.JoinInfo join, RuleFilter.IdNameFilter filter) {
        switch (filter.getType()) {
            case ANY:
                break; // no filtering

            case DEFAULT:
                search.addFilterNull(join.getField());
                break;

            case IDVALUE:
                if(filter.isIncludeDefault()) {
                    search.addFilterOr(
                            search.isNull(join.getField()),
                            search.isEqual(join, "id", filter.getId()));
                } else {
                    search.addFilterEqual(join , "id", filter.getId());
                }
                break;

            case NAMEVALUE:
                if(filter.isIncludeDefault()) {
                    search.addFilterOr(
                            search.isNull(join.getField()),
                            search.isEqual(join, "name", filter.getName()));
                } else {
                    search.addFilterEqual(join, "name", filter.getName());
                }
                break;

            default:
                throw new AssertionError();
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
    public static void addFixedCriteria(Search search, Search.JoinInfo join, RuleFilter.IdNameFilter filter) {
        if(filter.getType() == RuleFilter.FilterType.ANY) 
            throw new BadRequestServiceEx(join.getField() + " should be a fixed search and can't be ANY");
        
        if((filter.getType() == RuleFilter.FilterType.IDVALUE || filter.getType() == RuleFilter.FilterType.NAMEVALUE) && filter.isIncludeDefault())
            throw new BadRequestServiceEx(join.getField() + " should be a fixed search");
            
    
        addCriteria(search, join, filter);       
    }

    public static void addFixedStringCriteria(Search search, String fieldName, RuleFilter.TextFilter filter) {        
        if(filter.getType() == RuleFilter.FilterType.ANY) 
            throw new BadRequestServiceEx(fieldName + " should be a fixed search and can't be ANY");
        
        if((filter.getType() == RuleFilter.FilterType.NAMEVALUE) && filter.isIncludeDefault())
            throw new BadRequestServiceEx(fieldName+ " should be a fixed search");

        addStringCriteria(search, fieldName, filter);
    }
    
    // =========================================================================
    
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
    
}

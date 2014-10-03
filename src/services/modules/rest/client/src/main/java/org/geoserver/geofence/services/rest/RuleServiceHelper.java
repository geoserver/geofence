/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest;

import org.geoserver.geofence.services.rest.RESTRuleService;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.rest.model.RESTOutputRuleList;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RuleServiceHelper {

    private RESTRuleService ruleService;

    public RuleServiceHelper(RESTRuleService ruleService) {
        this.ruleService = ruleService;
    }

    public long countAll() {
        return count(null, null, null, null, null, null, null);
    }

    public long count(RuleFilter ruleFilter) {
        return count(
                ruleFilter.getUser(), ruleFilter.getUserGroup(), ruleFilter.getInstance(),
                ruleFilter.getService(), ruleFilter.getRequest(),
                ruleFilter.getWorkspace(), ruleFilter.getLayer());
    }


    public long count(RuleFilter.IdNameFilter userFilter, RuleFilter.IdNameFilter groupFilter, RuleFilter.IdNameFilter instanceFilter,
                RuleFilter.TextFilter serviceFilter, RuleFilter.TextFilter requestFilter,
                RuleFilter.TextFilter workspaceFilter, RuleFilter.TextFilter layerFilter) {

        return ruleService.count(
//                userFilter==null?null:userFilter.getType()==RuleFilter.FilterType.IDVALUE?userFilter.getId():null,
//                userFilter==null?null:userFilter.getType()==RuleFilter.FilterType.NAMEVALUE?userFilter.getName():null,
//                userFilter==null?null: (userFilter.isIncludeDefault() || userFilter.getType() == RuleFilter.FilterType.DEFAULT),
                getFilterId(userFilter), getFilterName(userFilter), getFilterDefault(userFilter),

//                groupFilter==null?null:groupFilter.getType()==RuleFilter.FilterType.IDVALUE?groupFilter.getId():null,
//                groupFilter==null?null:groupFilter.getType()==RuleFilter.FilterType.NAMEVALUE?groupFilter.getName():null,
//                groupFilter==null?null:groupFilter.isIncludeDefault(),
                getFilterId(groupFilter), getFilterName(groupFilter), getFilterDefault(groupFilter),

//                instanceFilter==null?null:instanceFilter.getType()==RuleFilter.FilterType.IDVALUE?instanceFilter.getId():null,
//                instanceFilter==null?null:instanceFilter.getType()==RuleFilter.FilterType.NAMEVALUE?instanceFilter.getName():null,
//                instanceFilter==null?null:instanceFilter.isIncludeDefault(),
                getFilterId(instanceFilter), getFilterName(instanceFilter), getFilterDefault(instanceFilter),

//                serviceFilter==null?null:serviceFilter.getType()==RuleFilter.FilterType.NAMEVALUE?serviceFilter.getName():null,
//                serviceFilter==null?null:serviceFilter.isIncludeDefault(),
                getFilterName(serviceFilter), getFilterDefault(serviceFilter),

//                requestFilter==null?null:requestFilter.getType()==RuleFilter.FilterType.NAMEVALUE?requestFilter.getName():null,
//                requestFilter==null?null:requestFilter.isIncludeDefault(),
                getFilterName(requestFilter), getFilterDefault(requestFilter),

//                workspaceFilter==null?null:workspaceFilter.getType()==RuleFilter.FilterType.NAMEVALUE?workspaceFilter.getName():null,
//                workspaceFilter==null?null:workspaceFilter.isIncludeDefault(),
                getFilterName(workspaceFilter), getFilterDefault(workspaceFilter),

//                layerFilter==null?null:layerFilter.getType()==RuleFilter.FilterType.NAMEVALUE?layerFilter.getName():null,
//                layerFilter==null?null:layerFilter.isIncludeDefault());
                getFilterName(layerFilter), getFilterDefault(layerFilter));
    }

    private static Long getFilterId(RuleFilter.IdNameFilter filter) {
        return filter==null?
                    null:
                    filter.getType()==RuleFilter.FilterType.IDVALUE?
                        filter.getId():
                        null;
    }
    private static String getFilterName(RuleFilter.IdNameFilter filter) {
        return filter==null?
                    null:
                    filter.getType()==RuleFilter.FilterType.NAMEVALUE?
                        filter.getName():
                        null;
    }
    private static Boolean getFilterDefault(RuleFilter.IdNameFilter filter) {
        return filter==null?
                    null:
                    (filter.isIncludeDefault() || filter .getType()==RuleFilter.FilterType.DEFAULT);
    }

    private static String getFilterName(RuleFilter.TextFilter filter) {
        return filter==null?
                    null:
                    filter.getType()==RuleFilter.FilterType.NAMEVALUE?
                        filter.getText():
                        null;
    }
    private static Boolean getFilterDefault(RuleFilter.TextFilter filter) {
        return filter==null?
                    null:
                    (filter.isIncludeDefault() || filter .getType()==RuleFilter.FilterType.DEFAULT);
    }

    public RESTOutputRuleList getAll() {
        return get(null, null, false, null, null, null, null, null, null, null);
    }

    public RESTOutputRuleList get(Integer page, Integer entries, boolean full, RuleFilter ruleFilter) {
        return get(
                page, entries, full,
                ruleFilter.getUser(), ruleFilter.getUserGroup(), ruleFilter.getInstance(),
                ruleFilter.getService(), ruleFilter.getRequest(),
                ruleFilter.getWorkspace(), ruleFilter.getLayer());
    }

    public RESTOutputRuleList get(
                Integer page, Integer entries, boolean full,
                RuleFilter.IdNameFilter userFilter, RuleFilter.IdNameFilter groupFilter, RuleFilter.IdNameFilter instanceFilter,
                RuleFilter.TextFilter serviceFilter, RuleFilter.TextFilter requestFilter,
                RuleFilter.TextFilter workspaceFilter, RuleFilter.TextFilter layerFilter) {

        return ruleService.get(page,entries,full,
                getFilterId(userFilter),      getFilterName(userFilter),     getFilterDefault(userFilter),
                getFilterId(groupFilter),     getFilterName(groupFilter),    getFilterDefault(groupFilter),
                getFilterId(instanceFilter),  getFilterName(instanceFilter), getFilterDefault(instanceFilter),
                getFilterName(serviceFilter), getFilterDefault(serviceFilter),
                getFilterName(requestFilter), getFilterDefault(requestFilter),
                getFilterName(workspaceFilter), getFilterDefault(workspaceFilter),
                getFilterName(layerFilter),   getFilterDefault(layerFilter));
    }



}

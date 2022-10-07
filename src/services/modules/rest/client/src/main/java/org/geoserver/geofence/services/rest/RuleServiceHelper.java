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
        return count(null, null, null, null, null, null, null, null);
    }

    public long count(RuleFilter ruleFilter) {
        return count(
                ruleFilter.getUser(), ruleFilter.getRole(), ruleFilter.getInstance(),
                ruleFilter.getService(), ruleFilter.getRequest(), ruleFilter.getSubfield(),
                ruleFilter.getWorkspace(), ruleFilter.getLayer());
    }


    public long count(RuleFilter.TextFilter userFilter, RuleFilter.TextFilter ruleFilter, RuleFilter.IdNameFilter instanceFilter,
                RuleFilter.TextFilter serviceFilter, RuleFilter.TextFilter requestFilter, RuleFilter.TextFilter subfieldFilter,
                RuleFilter.TextFilter workspaceFilter, RuleFilter.TextFilter layerFilter) {

        return ruleService.count(
                getFilterName(userFilter), getFilterDefault(userFilter),
                getFilterName(ruleFilter), getFilterDefault(ruleFilter),
                getFilterId(instanceFilter), getFilterName(instanceFilter), getFilterDefault(instanceFilter),
                getFilterName(serviceFilter), getFilterDefault(serviceFilter),
                getFilterName(requestFilter), getFilterDefault(requestFilter),
                getFilterName(subfieldFilter), getFilterDefault(subfieldFilter),
                getFilterName(workspaceFilter), getFilterDefault(workspaceFilter),
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
        return get(null, null, false, null, null, null, null, null, null, null, null);
    }

    public RESTOutputRuleList get(Integer page, Integer entries, boolean full, RuleFilter ruleFilter) {
        return get(
                page, entries, full,
                ruleFilter.getUser(), ruleFilter.getRole(), ruleFilter.getInstance(),
                ruleFilter.getService(), ruleFilter.getRequest(), ruleFilter.getSubfield(),
                ruleFilter.getWorkspace(), ruleFilter.getLayer());
    }

    public RESTOutputRuleList get(
                Integer page, Integer entries, boolean full,
                RuleFilter.TextFilter userFilter, RuleFilter.TextFilter ruleFilter, RuleFilter.IdNameFilter instanceFilter,
                RuleFilter.TextFilter serviceFilter, RuleFilter.TextFilter requestFilter, RuleFilter.TextFilter subfieldFilter,
                RuleFilter.TextFilter workspaceFilter, RuleFilter.TextFilter layerFilter) {

        return ruleService.get(page,entries,full,
                getFilterName(userFilter),    getFilterDefault(userFilter),
                getFilterName(ruleFilter),    getFilterDefault(ruleFilter),
                getFilterId(instanceFilter),  getFilterName(instanceFilter), getFilterDefault(instanceFilter),
                getFilterName(serviceFilter), getFilterDefault(serviceFilter),
                getFilterName(requestFilter), getFilterDefault(requestFilter),
                getFilterName(subfieldFilter), getFilterDefault(subfieldFilter),
                getFilterName(workspaceFilter), getFilterDefault(workspaceFilter),
                getFilterName(layerFilter),   getFilterDefault(layerFilter));
    }



}

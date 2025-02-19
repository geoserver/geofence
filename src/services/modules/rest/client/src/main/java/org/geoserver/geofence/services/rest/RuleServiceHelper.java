/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest;

import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.RuleFilter.IdNameFilter;
import org.geoserver.geofence.services.dto.RuleFilter.TextFilter;
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
        return count(null, null, null, null, null, null, null, null, null, null);
    }

    public long count(RuleFilter ruleFilter) {
        return count(
                ruleFilter.getUser(), ruleFilter.getRole(), 
                ruleFilter.getInstance(), ruleFilter.getSourceAddress(),
                ruleFilter.getDate(),
                ruleFilter.getService(), ruleFilter.getRequest(), ruleFilter.getSubfield(),
                ruleFilter.getWorkspace(), ruleFilter.getLayer());
    }


    public long count(TextFilter user, TextFilter role, 
                IdNameFilter instance, TextFilter ipaddress,
                TextFilter date,
                TextFilter service, TextFilter request, TextFilter subfield,
                TextFilter workspace, TextFilter layer) {

        return ruleService.count(
                getFilterText(user), getFilterDefault(user),
                getFilterText(role), getFilterDefault(role),
                getFilterId(instance), getFilterName(instance), getFilterDefault(instance),
                getFilterText(ipaddress), getFilterDefault(ipaddress),
                getFilterText(date), getFilterDefault(date),
                getFilterText(service), getFilterDefault(service),
                getFilterText(request), getFilterDefault(request),
                getFilterText(subfield), getFilterDefault(subfield),
                getFilterText(workspace), getFilterDefault(workspace),
                getFilterText(layer), getFilterDefault(layer));
    }

    private static Long getFilterId(IdNameFilter filter) {
        return filter==null?
                    null:
                    filter.getType()==RuleFilter.FilterType.IDVALUE?
                        filter.getId():
                        null;
    }
    private static String getFilterName(IdNameFilter filter) {
        return filter==null?
                    null:
                    filter.getType()==RuleFilter.FilterType.NAMEVALUE?
                        filter.getName():
                        null;
    }
    private static Boolean getFilterDefault(IdNameFilter filter) {
        return filter==null?
                    null:
                    (filter.isIncludeDefault() || filter .getType()==RuleFilter.FilterType.DEFAULT);
    }

    private static String getFilterText(TextFilter filter) {
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
        return get(null, null, false, null, null, null, null, null, null, null, null, null, null);
    }

    public RESTOutputRuleList get(Integer page, Integer entries, boolean full, RuleFilter ruleFilter) {
        return get(
                page, entries, full,
                ruleFilter.getUser(), ruleFilter.getRole(),
                ruleFilter.getInstance(), ruleFilter.getSourceAddress(),
                ruleFilter.getDate(),
                ruleFilter.getService(), ruleFilter.getRequest(), ruleFilter.getSubfield(),
                ruleFilter.getWorkspace(), ruleFilter.getLayer());
    }

    public RESTOutputRuleList get(
                Integer page, Integer entries, boolean full,
                TextFilter user, TextFilter role, 
                IdNameFilter instance, TextFilter ipaddress,
                TextFilter date,
                TextFilter service, TextFilter request, TextFilter subfield,
                TextFilter workspace, TextFilter layer) {

        return ruleService.get(page,entries,full,
                getFilterText(user),    getFilterDefault(user),
                getFilterText(role),    getFilterDefault(role),
                getFilterId(instance),  getFilterName(instance), getFilterDefault(instance),
                getFilterText(service), getFilterDefault(service),
                getFilterText(ipaddress), getFilterDefault(ipaddress),
                getFilterText(date),    getFilterDefault(date),
                getFilterText(request), getFilterDefault(request),
                getFilterText(subfield), getFilterDefault(subfield),
                getFilterText(workspace), getFilterDefault(workspace),
                getFilterText(layer),   getFilterDefault(layer));
    }



}

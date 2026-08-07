/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.impl;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.services.RuleReaderService;
import org.geofence.core.services.dto.AccessInfo;
import org.geofence.core.services.dto.PermsResult;
import org.geofence.core.services.dto.RuleFilter;
import org.geofence.core.services.dto.ShortRule;
import org.geofence.web.rest.api.exception.BadRequestRestEx;
import org.geofence.web.rest.api.interfaces.RESTRuleReaderService;
import org.geofence.web.rest.api.interfaces.params.RESTRuleFilter;
import org.geofence.web.rest.api.model.RESTAccessInfo;
import org.geofence.web.rest.api.model.RESTPermsResult;
import org.geofence.web.rest.api.model.RESTShortRuleList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

/** @author ETj (etj at geo-solutions.it) */
@Service
@RestController
public class RESTRuleReaderServiceImpl implements RESTRuleReaderService {

    private static final Logger LOGGER = LogManager.getLogger(RESTRuleReaderServiceImpl.class);

    @Autowired
    private RuleReaderService ruleReaderService;

    @Override
    public RESTAccessInfo getAccessInfo(RESTRuleFilter query) throws BadRequestRestEx {
        RuleFilter filter = RESTMapper.buildFilter(query);
        AccessInfo accessInfo = ruleReaderService.getAccessInfo(filter);
        return RESTMapper.map(accessInfo);
    }

    @Override
    public RESTAccessInfo getAdminAuthorization(RESTRuleFilter query) throws BadRequestRestEx {
        RuleFilter filter = RESTMapper.buildFilter(query);
        AccessInfo accessInfo = ruleReaderService.getAdminAuthorization(filter);
        return RESTMapper.map(accessInfo);
    }

    @Override
    public RESTShortRuleList getMatchingRules(RESTRuleFilter query) throws BadRequestRestEx {
        RuleFilter filter = RESTMapper.buildFilter(query);
        List<ShortRule> rules = ruleReaderService.getMatchingRules(filter);
        RESTShortRuleList out = new RESTShortRuleList(rules.size());
        rules.forEach(rule -> out.add(RESTMapper.map(rule)));
        return out;
    }

    @Override
    public RESTPermsResult getPermissionFilter(RESTRuleFilter query) throws BadRequestRestEx {
        RuleFilter filter = RESTMapper.buildFilter(query);
        LOGGER.debug("Requesting permissions for " + filter);
        PermsResult permsResult;
        try {
            permsResult = ruleReaderService.getPermissionFilter(filter);
        } catch (IllegalArgumentException e) {
            throw new BadRequestRestEx("Bad filter: " + e.getMessage());
        }
        LOGGER.debug("Permissions for " + filter + " --> " + permsResult);
        return RESTMapper.map(permsResult);
    }
}

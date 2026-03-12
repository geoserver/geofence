/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.impl;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.model.GSInstance;
import org.geofence.core.model.GSUser;
import org.geofence.core.model.UserGroup;
import org.geofence.core.services.AdminRuleAdminService;
import org.geofence.core.services.InstanceAdminService;
import org.geofence.core.services.RuleAdminService;
import org.geofence.core.services.UserAdminService;
import org.geofence.core.services.UserGroupAdminService;
import org.geofence.core.services.exception.NotFoundServiceEx;
import org.geofence.web.rest.api.exception.BadRequestRestEx;
import org.geofence.web.rest.api.exception.NotFoundRestEx;
import org.geofence.web.rest.api.model.RESTOutputUser;
import org.geofence.web.rest.api.model.RESTShortUser;
import org.geofence.web.rest.api.model.util.IdName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
;

/** @author ETj (etj at geo-solutions.it) */
@Service
public abstract class BaseRESTServiceImpl {

    private static final Logger LOGGER = LogManager.getLogger(BaseRESTServiceImpl.class);

    @Autowired
    protected UserAdminService userAdminService;

    @Autowired
    protected UserGroupAdminService userGroupAdminService;

    @Autowired
    protected InstanceAdminService instanceAdminService;

    @Autowired
    protected RuleAdminService ruleAdminService;

    @Autowired
    protected AdminRuleAdminService adminRuleAdminService;

    @Autowired
    protected RESTMapper mapper;

    protected UserGroup getUserGroup(IdName groupFilter) throws BadRequestRestEx, NotFoundRestEx {

        try {
            if (groupFilter.getId() != null) {
                throw new BadRequestRestEx("Groups can only be referenced by name");
            } else if (groupFilter.getName() != null) {
                return userGroupAdminService.get(groupFilter.getName());
            } else {
                throw new BadRequestRestEx("Bad UserGroup filter " + groupFilter);
            }
        } catch (NotFoundServiceEx e) {
            LOGGER.warn("UserGroup not found " + groupFilter);
            throw new NotFoundRestEx("UserGroup not found " + groupFilter);
        }
    }

    protected GSUser getUser(IdName userFilter) throws BadRequestRestEx, NotFoundRestEx {

        try {
            if (userFilter.getId() != null) {
                throw new BadRequestRestEx("Users can only be referenced by name");
            } else if (userFilter.getName() != null) {
                return userAdminService.getFull(userFilter.getName());
            } else {
                throw new BadRequestRestEx("Bad GSUser filter " + userFilter);
            }
        } catch (NotFoundServiceEx e) {
            LOGGER.warn("GSUser not found " + userFilter);
            throw new NotFoundRestEx("GSUser not found " + userFilter);
        }
    }

    protected GSInstance getInstance(IdName filter) throws BadRequestRestEx, NotFoundRestEx {

        try {
            if (filter.getId() != null) {
                return instanceAdminService.get(filter.getId());
            } else if (filter.getName() != null) {
                return instanceAdminService.get(filter.getName());
            } else {
                throw new BadRequestRestEx("Bad GSInstance filter " + filter);
            }
        } catch (NotFoundServiceEx e) {
            LOGGER.warn("GSInstance not found " + filter);
            throw new NotFoundRestEx("GSInstance not found " + filter);
        }
    }

    // ==========================================================================
    protected static RESTShortUser toShortUser(GSUser user) {
        RESTShortUser shu = new RESTShortUser();
        shu.setId(user.getId());
        shu.setExtId(user.getExtId());
        shu.setUserName(user.getName());
        shu.setEnabled(user.getEnabled());

        return shu;
    }

    protected static RESTOutputUser toOutputUser(GSUser user) {
        RESTOutputUser ret = new RESTOutputUser();
        ret.setId(user.getId());
        ret.setExtId(user.getExtId());
        ret.setName(user.getName());
        ret.setEnabled(user.getEnabled());
        ret.setAdmin(user.isAdmin());
        ret.setFullName(user.getFullName());
        ret.setEmailAddress(user.getEmailAddress());

        List<IdName> groups = new ArrayList<IdName>();
        for (UserGroup group : user.getGroups()) {
            IdName nameId = new IdName(group.getId(), group.getName());
            groups.add(nameId);
        }
        ret.setGroups(groups);

        return ret;
    }

    // ==========================================================================
    public void setUserAdminService(UserAdminService service) {
        this.userAdminService = service;
    }

    public void setUserGroupAdminService(UserGroupAdminService service) {
        this.userGroupAdminService = service;
    }

    public void setRuleAdminService(RuleAdminService service) {
        this.ruleAdminService = service;
    }

    public void setInstanceAdminService(InstanceAdminService instanceAdminService) {
        this.instanceAdminService = instanceAdminService;
    }

    public void setAdminRuleAdminService(AdminRuleAdminService adminRuleAdminService) {
        this.adminRuleAdminService = adminRuleAdminService;
    }
}

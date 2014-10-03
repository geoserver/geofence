/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.impl;

import org.geoserver.geofence.core.model.GSInstance;
import java.util.List;


import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.services.InstanceAdminService;
import org.geoserver.geofence.services.RuleAdminService;
import org.geoserver.geofence.services.UserGroupAdminService;
import org.geoserver.geofence.services.UserAdminService;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;
import org.geoserver.geofence.services.rest.exception.BadRequestRestEx;
import org.geoserver.geofence.services.rest.exception.NotFoundRestEx;
import org.geoserver.geofence.services.rest.model.RESTOutputUser;
import org.geoserver.geofence.services.rest.model.RESTShortUser;
import org.geoserver.geofence.services.rest.model.util.IdName;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public abstract class BaseRESTServiceImpl {

    private static final Logger LOGGER = LogManager.getLogger(BaseRESTServiceImpl.class);
    
    protected UserAdminService userAdminService;
    protected UserGroupAdminService userGroupAdminService;
    protected InstanceAdminService instanceAdminService;
    protected RuleAdminService ruleAdminService;


    protected UserGroup getUserGroup(IdName filter) throws BadRequestRestEx, NotFoundRestEx {

        try {
            if ( filter.getId() != null ) {
                return userGroupAdminService.get(filter.getId());
            } else if ( filter.getName() != null ) {
                return userGroupAdminService.get(filter.getName());
            } else {
                throw new BadRequestRestEx("Bad UserGroup filter " + filter);
            }
        } catch (NotFoundServiceEx e) {
            LOGGER.warn("UserGroup not found " + filter);
            throw new NotFoundRestEx("UserGroup not found " + filter);
        }
    }

    protected GSUser getUser(IdName filter) throws BadRequestRestEx, NotFoundRestEx {

        try {
            if ( filter.getId() != null ) {
                return userAdminService.get(filter.getId());
            } else if ( filter.getName() != null ) {
                return userAdminService.get(filter.getName());
            } else {
                throw new BadRequestRestEx("Bad GSUser filter " + filter);
            }
        } catch (NotFoundServiceEx e) {
            LOGGER.warn("GSUser not found " + filter);
            throw new NotFoundRestEx("GSUser not found " + filter);
        }
    }

    protected GSInstance getInstance(IdName filter) throws BadRequestRestEx, NotFoundRestEx {

        try {
            if ( filter.getId() != null ) {
                return instanceAdminService.get(filter.getId());
            } else if ( filter.getName() != null ) {
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

}

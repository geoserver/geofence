/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.utils;

import java.util.List;

import org.geoserver.geofence.core.model.GSInstance;
import org.geoserver.geofence.services.GFUserAdminService;
import org.geoserver.geofence.services.InstanceAdminService;
import org.geoserver.geofence.services.UserGroupAdminService;
import org.geoserver.geofence.services.RuleAdminService;
import org.geoserver.geofence.services.UserAdminService;
import org.geoserver.geofence.services.dto.ShortGroup;
import org.geoserver.geofence.services.dto.ShortRule;
import org.geoserver.geofence.services.dto.ShortUser;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/** 
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class InstanceCleaner {

    private static final Logger LOGGER = LogManager.getLogger(InstanceCleaner.class);

    private RuleAdminService ruleAdminService;
    private UserGroupAdminService userGroupAdminService;
    private UserAdminService userAdminService;
    private GFUserAdminService gfUserAdminService;
    private InstanceAdminService instanceAdminService;

    public void removeAll() throws NotFoundServiceEx {
        LOGGER.warn("***** removeAll()");
        removeAllRules();
        removeAllUsers();
//        removeAllGFUsers();
        removeAllProfiles();
        removeAllInstances();
    }

    public void removeAllRules() throws NotFoundServiceEx {
        List<ShortRule> list = ruleAdminService.getAll();
        for (ShortRule item : list) {
            LOGGER.warn("Removing " + item);

            boolean ret = ruleAdminService.delete(item.getId());
            if ( !ret ) {
                LOGGER.error("Could not remove " + item);
            }
        }

        long count = ruleAdminService.getCountAll();
        if ( count > 0 ) {
            LOGGER.error("Items not removed");
        }
    }

    public void removeAllUsers() throws NotFoundServiceEx {
        List<ShortUser> list = userAdminService.getList(null, null, null);
        for (ShortUser item : list) {
            LOGGER.warn("Removing " + item);

            boolean ret = userAdminService.delete(item.getId());
            if ( !ret ) {
                LOGGER.error("Could not remove " + item);
            }
        }

        long count = userAdminService.getCount(null);
        if ( count > 0 ) {
            LOGGER.error("Items not removed");
        }
    }

    public void removeAllGFUsers() throws NotFoundServiceEx {
        List<ShortUser> list = gfUserAdminService.getList(null, null, null);
        for (ShortUser item : list) {
            LOGGER.warn("Removing " + item);

            boolean ret = gfUserAdminService.delete(item.getId());
            if ( !ret ) {
                LOGGER.error("Could not remove " + item);
            }
        }

        long count = gfUserAdminService.getCount(null);
        if ( count > 0 ) {
            LOGGER.error("Items not removed");
        }
    }

    public void removeAllProfiles() throws NotFoundServiceEx {
        List<ShortGroup> list = userGroupAdminService.getList(null, null, null);
        for (ShortGroup item : list) {
            LOGGER.warn("Removing " + item);

            boolean ret = userGroupAdminService.delete(item.getId());
            if ( !ret ) {
                LOGGER.error("Could not remove " + item);
            }
        }

        long count = userGroupAdminService.getCount(null);
        if ( count > 0 ) {
            LOGGER.error("Items not removed");
        }
    }

    public void removeAllInstances() throws NotFoundServiceEx {
        List<GSInstance> list = instanceAdminService.getAll();
        for (GSInstance item : list) {
            LOGGER.warn("Removing " + item);

            boolean ret = instanceAdminService.delete(item.getId());
            if ( !ret ) {
                LOGGER.error("Could not remove " + item);
            }
        }

        long count = instanceAdminService.getCount(null);
        if ( count > 0 ) {
            LOGGER.error("Items not removed");
        }
    }

    // ==========================================================================
    public void setGfUserAdminService(GFUserAdminService service) {
        this.gfUserAdminService = service;
    }

    public void setInstanceAdminService(InstanceAdminService service) {
        this.instanceAdminService = service;
    }

    public void setUserGroupAdminService(UserGroupAdminService service) {
        this.userGroupAdminService = service;
    }

    public void setRuleAdminService(RuleAdminService service) {
        this.ruleAdminService = service;
    }

    public void setUserAdminService(UserAdminService service) {
        this.userAdminService = service;
    }
}

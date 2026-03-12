/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.utils;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.model.GSInstance;
import org.geofence.core.services.InstanceAdminService;
import org.geofence.core.services.RuleAdminService;
import org.geofence.core.services.UserAdminService;
import org.geofence.core.services.UserGroupAdminService;
import org.geofence.core.services.dto.ShortGroup;
import org.geofence.core.services.dto.ShortRule;
import org.geofence.core.services.dto.ShortUser;
import org.geofence.core.services.exception.NotFoundServiceEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author ETj (etj at geo-solutions.it) */
@Component
public class InstanceCleaner {

    private static final Logger LOGGER = LogManager.getLogger(InstanceCleaner.class);

    @Autowired
    private RuleAdminService ruleAdminService;

    @Autowired
    private UserGroupAdminService userGroupAdminService;

    @Autowired
    private UserAdminService userAdminService;

    @Autowired
    private InstanceAdminService instanceAdminService;

    public void removeAll() throws NotFoundServiceEx {
        LOGGER.warn("***** removeAll()");
        removeAllRules();
        removeAllUsers();
        removeAllProfiles();
        removeAllInstances();
    }

    public void removeAllRules() throws NotFoundServiceEx {
        List<ShortRule> list = ruleAdminService.getAll();
        for (ShortRule item : list) {
            LOGGER.warn("Removing " + item);

            boolean ret = ruleAdminService.delete(item.getId());
            if (!ret) {
                LOGGER.error("Could not remove " + item);
            }
        }

        long count = ruleAdminService.getCountAll();
        if (count > 0) {
            LOGGER.error("Items not removed");
        }
    }

    public void removeAllUsers() throws NotFoundServiceEx {
        List<ShortUser> list = userAdminService.getList(null, null, null);
        for (ShortUser item : list) {
            LOGGER.warn("Removing " + item);

            boolean ret = userAdminService.delete(item.getId());
            if (!ret) {
                LOGGER.error("Could not remove " + item);
            }
        }

        long count = userAdminService.getCount(null);
        if (count > 0) {
            LOGGER.error("Items not removed");
        }
    }

    public void removeAllProfiles() throws NotFoundServiceEx {
        List<ShortGroup> list = userGroupAdminService.getList(null, null, null);
        for (ShortGroup item : list) {
            LOGGER.warn("Removing " + item);

            boolean ret = userGroupAdminService.delete(item.getId());
            if (!ret) {
                LOGGER.error("Could not remove " + item);
            }
        }

        long count = userGroupAdminService.getCount(null);
        if (count > 0) {
            LOGGER.error("Items not removed");
        }
    }

    public void removeAllInstances() throws NotFoundServiceEx {
        List<GSInstance> list = instanceAdminService.getAll();
        for (GSInstance item : list) {
            LOGGER.warn("Removing " + item);

            boolean ret = instanceAdminService.delete(item.getId());
            if (!ret) {
                LOGGER.error("Could not remove " + item);
            }
        }

        long count = instanceAdminService.getCount(null);
        if (count > 0) {
            LOGGER.error("Items not removed");
        }
    }
}

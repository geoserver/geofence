/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.web;

import org.geoserver.geofence.core.model.GFUser;
import org.geoserver.geofence.login.util.MD5Util;
import org.geoserver.geofence.services.GFUserAdminServiceImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;


public class StartupService implements InitializingBean
{
    private static final Logger LOGGER = LogManager.getLogger(StartupService.class);

    @Autowired
    GFUserAdminServiceImpl gfUserAdminService;

    @Override
    public void afterPropertiesSet() throws Exception {
        long cnt = gfUserAdminService.getCount(null);
        if(cnt == 0) {
            LOGGER.warn("No GF users found. Creating the default admin.");
            
            GFUser user = new GFUser();
            user.setFullName("Default admin");
            user.setName("admin");
            user.setPassword(MD5Util.getHash("geofence"));
            user.setEnabled(Boolean.TRUE);
            gfUserAdminService.insert(user);
        }
    }

    public void setGfUserAdminService(GFUserAdminServiceImpl gfUserAdminService) {
        this.gfUserAdminService = gfUserAdminService;
    }
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.service.impl;

import org.geoserver.geofence.core.model.GFUser;
import org.geoserver.geofence.gui.client.configuration.GeofenceGlobalConfiguration;
import org.geoserver.geofence.gui.server.service.IStartupService;
import org.geoserver.geofence.login.util.MD5Util;
import org.geoserver.geofence.services.GFUserAdminServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import org.springframework.beans.factory.annotation.Autowired;


// TODO: Auto-generated Javadoc
/**
 * The Class StartupService.
 */
public class StartupService implements IStartupService, InitializingBean
{
    private static final Logger LOGGER = LogManager.getLogger(StartupService.class);

    /** The geofence global configuration. */
    @Autowired
    private GeofenceGlobalConfiguration geofenceGlobalConfiguration;

    @Autowired
    GFUserAdminServiceImpl gfUserAdminService;

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.server.service.IStartupService#initServerConfiguration()
     */
    public GeofenceGlobalConfiguration initServerConfiguration()
    {
        // TODO Auto-generated method stub
        return geofenceGlobalConfiguration;
    }

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

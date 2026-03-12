/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

public class StartupService implements InitializingBean {
    private static final Logger LOGGER = LogManager.getLogger(StartupService.class);

    @Override
    public void afterPropertiesSet() throws Exception {}
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.gwt;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.geoserver.geofence.gui.client.configuration.GeofenceGlobalConfiguration;
import org.geoserver.geofence.gui.client.service.ConfigurationRemote;
import org.geoserver.geofence.gui.server.service.IStartupService;
import org.geoserver.geofence.gui.spring.ApplicationContextUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


// TODO: Auto-generated Javadoc
/**
 * The Class ConfigurationRemoteImpl.
 */
public class ConfigurationRemoteImpl extends RemoteServiceServlet implements ConfigurationRemote
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6320939080552026131L;

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** The startup service. */
    private IStartupService startupService;

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());

        ApplicationContextUtil.getInstance().setSpringContext(context);

        this.startupService = (IStartupService) ApplicationContextUtil.getInstance().getBean(
                "startupService");

        logger.info("SPRING CONTEXT INITIALIZED" + this.startupService);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.service.ConfigurationRemote#
     * initServerConfiguration()
     */
    public GeofenceGlobalConfiguration initServerConfiguration()
    {
        // TODO Auto-generated method stub
        return startupService.initServerConfiguration();
    }

}

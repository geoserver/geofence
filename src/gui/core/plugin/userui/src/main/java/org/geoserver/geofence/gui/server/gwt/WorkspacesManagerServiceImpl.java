/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.gwt;

import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.GSInstance;
import org.geoserver.geofence.gui.client.model.Rule;
import org.geoserver.geofence.gui.client.model.data.Layer;
import org.geoserver.geofence.gui.client.model.data.LayerStyle;
import org.geoserver.geofence.gui.client.model.data.Workspace;
import org.geoserver.geofence.gui.client.service.WorkspacesManagerRemoteService;
import org.geoserver.geofence.gui.server.service.IWorkspacesManagerService;
import org.geoserver.geofence.gui.spring.ApplicationContextUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: Auto-generated Javadoc
/**
 * The Class WorkspacesManagerServiceImpl.
 */
public class WorkspacesManagerServiceImpl extends RemoteServiceServlet implements WorkspacesManagerRemoteService
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 9061178642285826473L;

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** The workspace manager service. */
    private IWorkspacesManagerService workspaceManagerService;

    /**
     * Instantiates a new workspaces manager service impl.
     */
    public WorkspacesManagerServiceImpl()
    {
        this.workspaceManagerService = (IWorkspacesManagerService) ApplicationContextUtil.getInstance().getBean("workspacesManagerServiceGWT");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.client.service.WorkspacesManagerRemoteService#getWorkspaces(com
     * .extjs .gxt.ui.client.data.PagingLoadConfig)
     */
    public PagingLoadResult<Workspace> getWorkspaces(int offset, int limit, String URL,
        GSInstance gsInstance) throws ApplicationException
    {
        return workspaceManagerService.getWorkspaces(offset, limit, URL, gsInstance);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.client.service.WorkspacesManagerRemoteService#getLayers(com.extjs
     * .gxt.ui.client.data.PagingLoadConfig, java.lang.String, java.lang.String)
     */
    public PagingLoadResult<Layer> getLayers(int offset, int limit, String baseURL,
        GSInstance gsInstance, String workspace, String service) throws ApplicationException
    {
        return workspaceManagerService.getLayers(offset, limit, baseURL, gsInstance, workspace, service);
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.geoserver.geofence.gui.client.service.WorkspacesManagerServiceRemote#getStyles(it.
     * geosolutions.geofence.gui.client.model.GSInstance)
     */
    public List<LayerStyle> getStyles(Rule rule) throws ApplicationException
    {
        return workspaceManagerService.getStyles(rule);
    }
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.service.impl;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.extjs.gxt.ui.client.data.PagingLoadResult;

import org.geoserver.geofence.core.model.LayerDetails;
import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.configuration.WorkspaceConfigOpts;
import org.geoserver.geofence.gui.client.model.GSInstance;
import org.geoserver.geofence.gui.client.model.Rule;
import org.geoserver.geofence.gui.client.model.data.Layer;
import org.geoserver.geofence.gui.client.model.data.LayerStyle;
import org.geoserver.geofence.gui.client.model.data.Workspace;
import org.geoserver.geofence.gui.client.model.data.rpc.RpcPageLoadResult;
import org.geoserver.geofence.gui.server.service.IWorkspacesManagerService;
import org.geoserver.geofence.gui.service.GeofenceRemoteService;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTAbstractList;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.decoder.RESTStyleList;
import it.geosolutions.geoserver.rest.decoder.RESTWorkspaceList;
import it.geosolutions.geoserver.rest.decoder.RESTWorkspaceList.RESTShortWorkspace;
import it.geosolutions.geoserver.rest.decoder.utils.NameLinkElem;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


// TODO: Auto-generated Javadoc
/**
 * The Class WorkspacesManagerServiceImpl.
 */
@Component("workspacesManagerServiceGWT")
public class WorkspacesManagerServiceImpl implements IWorkspacesManagerService
{

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** The geofence remote service. */
    @Autowired
    private GeofenceRemoteService geofenceRemoteService;

    /** The geofence workspace options. */
    @Autowired
    private WorkspaceConfigOpts workspaceConfigOpts;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.server.service.IWorkspacesManagerService#getWorkspaces(com.extjs
     * .gxt.ui.client.data.PagingLoadConfig, java.lang.String)
     */
    public PagingLoadResult<Workspace> getWorkspaces(int offset, int limit, String remoteURL,
        GSInstance gsInstance) throws ApplicationException
    {

        List<Workspace> workspacesListDTO = new ArrayList<Workspace>();
        workspacesListDTO.add(new Workspace("*"));

        if ((remoteURL != null) && !remoteURL.equals("*") && !remoteURL.contains("?"))
        {
            try
            {
                GeoServerRESTReader gsreader = new GeoServerRESTReader(remoteURL, gsInstance.getUsername(), gsInstance.getPassword());

                RESTWorkspaceList workspaces = gsreader.getWorkspaces();
                if ((workspaces != null) && !workspaces.isEmpty())
                {
                    Iterator<RESTShortWorkspace> wkIT = workspaces.iterator();
                    while (wkIT.hasNext())
                    {
                        RESTShortWorkspace workspace = wkIT.next();

                        workspacesListDTO.add(new Workspace(workspace.getName()));
                    }
                }
            }
            catch (MalformedURLException e)
            {
                logger.error(e.getLocalizedMessage(), e);
                throw new ApplicationException(e.getLocalizedMessage(), e);
            }
        }

        return new RpcPageLoadResult<Workspace>(workspacesListDTO, 0, workspacesListDTO.size());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.server.service.IWorkspacesManagerService#getLayers(com.extjs.
     * gxt.ui.client.data.PagingLoadConfig, java.lang.String, java.lang.String)
     */
    public PagingLoadResult<Layer> getLayers(int offset, int limit, String baseURL,
        GSInstance gsInstance, String workspace, String service) throws ApplicationException
    {

        List<Layer> layersListDTO = new ArrayList<Layer>();
        layersListDTO.add(new Layer("*"));

        if ((baseURL != null) &&
                !baseURL.equals("*") &&
                !baseURL.contains("?") &&
                (workspace != null) &&
                (workspace.length() > 0))
        {
            try
            {
                GeoServerRESTReader gsreader = new GeoServerRESTReader(baseURL, gsInstance.getUsername(), gsInstance.getPassword());
                
                if (workspace.equals("*") && workspaceConfigOpts.isShowDefaultGroups() && service.equals("WMS"))
                {
                    RESTAbstractList<NameLinkElem> layerGroups = gsreader.getLayerGroups();

                    if ((layerGroups != null)) {
                        for (NameLinkElem lg : layerGroups) {
//                            RESTLayerGroup group = gsreader.getLayerGroup(lg.getName());
//                            if (group != null)
//                            {
//                                layersListDTO.add(new Layer(group.getName()));
//                            }
                            layersListDTO.add(new Layer(lg.getName()));                            
                        }
                    }
                }
                else
                {
                    SortedSet<String> sortedLayerNames = new TreeSet<String>();
                    RESTAbstractList<NameLinkElem> layers = gsreader.getLayers();

                    if (workspace.equals("*")) { // load all layers
                    	if (layers != null)
                    		for (NameLinkElem layerLink : layers) {
                    			sortedLayerNames.add(layerLink.getName());
                    		}
                    } else {
                        if ((layers != null) && !layers.isEmpty()) {

                            for (NameLinkElem layerNL : layers) {
                                // next block is really too slow
                                RESTLayer layer = gsreader.getLayer(layerNL.getName());
                                if(layer.getResourceUrl().contains("workspaces/" + workspace + "/"))  {
                                    sortedLayerNames.add(layerNL.getName());
                                    //layersListDTO.add(new Layer(layerNL.getName()));
                                }
                            }
                        }
                    }
                    // return the sorted layers list
                    for (String layerName : sortedLayerNames) {
                        layersListDTO.add(new Layer(layerName));
                    }
                }
            } catch (MalformedURLException e) {
                logger.error(e.getLocalizedMessage(), e);
                throw new ApplicationException(e.getLocalizedMessage(), e);
            }
        }

        return new RpcPageLoadResult<Layer>(layersListDTO, 0, layersListDTO.size());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.server.service.IWorkspacesManagerService#getStyles(org.geoserver
     * .geofence.gui.client.model.GSInstance)
     */
    public List<LayerStyle> getStyles(Rule rule) throws ApplicationException
    {

        List<LayerStyle> layerStyles = new ArrayList<LayerStyle>();

        Set<String> allowedStyles = null;
        int size = 0;

        try
        {
            LayerDetails layerDetails = geofenceRemoteService.getRuleAdminService().get(rule.getId()).getLayerDetails();

            if (layerDetails != null)
            {
                allowedStyles = layerDetails.getAllowedStyles();

                if (allowedStyles != null)
                {
                    size = allowedStyles.size();
                }
            }

            GeoServerRESTReader gsreader = new GeoServerRESTReader(rule.getInstance().getBaseURL(),
                    rule.getInstance().getUsername(), rule.getInstance().getPassword());

            RESTStyleList styles = gsreader.getStyles();
            List<String> names = styles.getNames();
            Iterator<String> iterator = names.iterator();

            while (iterator.hasNext())
            {
                String name = iterator.next();

                LayerStyle layerStyle = new LayerStyle();

                if (size > 0)
                {
                    Iterator<String> styleIterator = allowedStyles.iterator();
                    while (styleIterator.hasNext())
                    {
                        String allowed = styleIterator.next();

                        if (allowed.equalsIgnoreCase(name))
                        {
                            layerStyle.setEnabled(true);
                        }
                    }
                }
                else
                {
                    layerStyle.setEnabled(false);
                }

                layerStyle.setStyle(name);

                layerStyles.add(layerStyle);
            }

        }
        catch (MalformedURLException e)
        {
            logger.error(e.getLocalizedMessage(), e);
            throw new ApplicationException(e.getLocalizedMessage(), e);
        }
        catch (NotFoundServiceEx e)
        {
            logger.error(e.getLocalizedMessage(), e);
            throw new ApplicationException(e.getLocalizedMessage(), e);
        }

        return layerStyles;
    }

}

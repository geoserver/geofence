/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.service;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.GSInstance;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * The Interface InstancesManagerRemoteService.
 */
@RemoteServiceRelativePath("InstancesManagerRemoteService")
public interface InstancesManagerRemoteService extends RemoteService
{

    /**
     * Gets the instances.
     *
     * @param config
     *            the config
     * @return the instances
     * @throws ApplicationException
     *             the application exception
     */
    public PagingLoadResult<GSInstance> getInstances(int offset, int limit, boolean full) throws ApplicationException;


    /**
     * Get the instance
     *
     * @param config
     * @param name
     * @return
     */
    public GSInstance getInstance(int offset, int limit, long id);

    /**
     * Save instance.
     *
     * @param instance
     *            the instance
     * @param asyncCallback
     *            the async callback
     */
    public void saveInstance(GSInstance instance);

    /**
     * Delete instance.
     *
     * @param instance
     *            the instance
     * @param asyncCallback
     *            the async callback
     */
    public void deleteInstance(GSInstance instance);
    
    /**
     * Test connection with instance.
     * 
     * @param url
     * @param callback
     */
    public void testConnection( org.geoserver.geofence.gui.client.model.GSInstance instance) throws ApplicationException;

}

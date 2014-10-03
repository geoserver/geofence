/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.service;

import com.extjs.gxt.ui.client.data.PagingLoadResult;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.GSInstance;


/**
 * The Interface IInstancesManagerService.
 */
public interface IInstancesManagerService
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
     * @param l
     * @return
     */
    public GSInstance getInstance(int offset, int limit, long l) throws ApplicationException;

    /**
     * Delete instance.
     *
     * @param instance
     *            the instance
     */
    public void deleteInstance(GSInstance instance);

    /**
     * Save instance.
     *
     * @param instance
     *            the instance
     */
    public void saveInstance(GSInstance instance);
    
    /**
     * Test connection to instance.
     * 
     * @param url
     */
    public void testConnection(org.geoserver.geofence.gui.client.model.GSInstance instance) throws ApplicationException;

}

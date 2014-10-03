/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.gwt;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.GSInstance;
import org.geoserver.geofence.gui.client.service.InstancesManagerRemoteService;
import org.geoserver.geofence.gui.server.service.IInstancesManagerService;
import org.geoserver.geofence.gui.spring.ApplicationContextUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class InstancesManagerServiceImpl.
 */
public class InstancesManagerRemoteServiceImpl extends RemoteServiceServlet implements InstancesManagerRemoteService
{


    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4502086167905144601L;

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** The instances manager service. */
    private IInstancesManagerService instancesManagerService;

    /**
     * Instantiates a new instances manager service impl.
     */
    public InstancesManagerRemoteServiceImpl()
    {
        this.instancesManagerService = (IInstancesManagerService) ApplicationContextUtil.getInstance().getBean("instancesManagerServiceGWT");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.client.service.InstancesManagerRemoteService#getInstances(com.extjs
     * .gxt.ui.client.data.PagingLoadConfig)
     */
    public PagingLoadResult<GSInstance> getInstances(int offset, int limit, boolean full) throws ApplicationException
    {
        return instancesManagerService.getInstances(offset, limit, full);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.client.service.InstancesManagerRemoteService#getInstances(com.extjs
     * .gxt.ui.client.data.PagingLoadConfig)
     */
    public GSInstance getInstance(int offset, int limit, long id) throws ApplicationException
    {
        return instancesManagerService.getInstance(offset, limit, id);
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.service.InstancesManagerRemoteService#deleteInstance(org.geoserver.geofence.gui.client.model.Instance)
     */
    public void deleteInstance(GSInstance instance) throws ApplicationException
    {
        instancesManagerService.deleteInstance(instance);
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.service.InstancesManagerRemoteService#saveInstance(org.geoserver.geofence.gui.client.model.Instance)
     */
    public void saveInstance(GSInstance instance) throws ApplicationException
    {
        instancesManagerService.saveInstance(instance);
    }

	public void testConnection(org.geoserver.geofence.gui.client.model.GSInstance instance)  throws ApplicationException {
		instancesManagerService.testConnection(instance);
		
	}

}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.gwt;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.GSUser;
import org.geoserver.geofence.gui.client.model.data.UserLimitsInfo;
import org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteService;
import org.geoserver.geofence.gui.server.service.IGsUsersManagerService;
import org.geoserver.geofence.gui.spring.ApplicationContextUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class GsUsersManagerServiceImpl.
 */
public class GsUsersManagerServiceImpl extends RemoteServiceServlet implements GsUsersManagerRemoteService
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6961825619542958052L;

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** The gs profile manager service. */
    private IGsUsersManagerService gsUserManagerService;

    /**
     * Instantiates a new gs users manager service impl.
     */
    public GsUsersManagerServiceImpl()
    {
        this.gsUserManagerService = (IGsUsersManagerService) ApplicationContextUtil.getInstance().getBean(
                "gsUsersManagerServiceGWT");
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteService#getGsUsers(com.extjs.gxt.ui.client.data.PagingLoadConfig)
     */
    public PagingLoadResult<GSUser> getGsUsers(int offset, int limit, boolean full) throws ApplicationException
    {
        return gsUserManagerService.getGsUsers(offset, limit, full);
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteService#saveGsUser(org.geoserver.geofence.gui.client.model.GSUser)
     */
    public void saveGsUser(GSUser user) throws ApplicationException
    {
        gsUserManagerService.saveUser(user);
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteService#deleteGsUser(org.geoserver.geofence.gui.client.model.GSUser)
     */
    public void deleteGsUser(GSUser user) throws ApplicationException
    {
        gsUserManagerService.deleteUser(user);
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteService#getUserLimitsInfo(org.geoserver.geofence.gui.client.model.GSUser)
     */
    public UserLimitsInfo getUserLimitsInfo(GSUser user) throws ApplicationException
    {
        return gsUserManagerService.getUserLimitsInfo(user);
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteService#saveUserLimitsInfo(org.geoserver.geofence.gui.client.model.GSUser)
     */
    public UserLimitsInfo saveUserLimitsInfo(UserLimitsInfo userLimitInfo) throws ApplicationException
    {
        return gsUserManagerService.saveUserLimitsInfo(userLimitInfo);
    }
}

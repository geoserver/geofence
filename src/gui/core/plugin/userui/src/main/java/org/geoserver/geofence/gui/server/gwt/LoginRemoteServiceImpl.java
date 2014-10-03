/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.gwt;

import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.geoserver.geofence.gui.client.model.User;
import org.geoserver.geofence.gui.client.service.LoginRemoteService;
import org.geoserver.geofence.gui.server.GeofenceKeySessionValues;
import org.geoserver.geofence.gui.server.service.ILoginService;
import org.geoserver.geofence.gui.spring.ApplicationContextUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: Auto-generated Javadoc
/**
 * The Class LoginRemoteImpl.
 */
public class LoginRemoteServiceImpl extends RemoteServiceServlet implements LoginRemoteService
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6763250533126295210L;

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** The login service. */
    private ILoginService loginService;

    /**
     * Instantiates a new login remote impl.
     */
    public LoginRemoteServiceImpl()
    {
        this.loginService = (ILoginService) ApplicationContextUtil.getInstance().getBean(
                "loginService");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.service.LoginRemote#authenticate(java .lang.String,
     * java.lang.String)
     */
    public User authenticate(String userName, String password)
    {
        HttpSession session = getThreadLocalRequest().getSession();

        return loginService.authenticate(userName, password, session);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.service.LoginRemote#logout()
     */
    public void logout()
    {
        HttpSession session = getThreadLocalRequest().getSession();
        if (session != null)
        {
            session.removeAttribute(GeofenceKeySessionValues.USER_LOGGED_TOKEN.getValue());
            session.setAttribute(GeofenceKeySessionValues.USER_LOGGED_TOKEN.getValue(), "");
        }

    }

    public Boolean isAuthenticated()
    {
        boolean authenticated = false;

        HttpSession session = getThreadLocalRequest().getSession();
        if ((session.getAttribute(GeofenceKeySessionValues.USER_LOGGED_TOKEN.getValue()) != null) &&
                !((String) session.getAttribute(GeofenceKeySessionValues.USER_LOGGED_TOKEN.getValue())).isEmpty())
        {
            authenticated = true;
        }

        return authenticated;
    }

}

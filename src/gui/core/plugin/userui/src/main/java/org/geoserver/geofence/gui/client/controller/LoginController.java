/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.controller;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.model.User;
import org.geoserver.geofence.gui.client.service.LoginRemoteServiceAsync;
import org.geoserver.geofence.gui.client.widget.GeofenceChooserWidget;
import org.geoserver.geofence.gui.client.widget.GeofenceSearchWidget;
import org.geoserver.geofence.gui.client.widget.GeofenceUpdateWidget;
import org.geoserver.geofence.gui.client.widget.LoginStatus.EnumLoginStatus;
import org.geoserver.geofence.gui.client.widget.LoginWidget;
import org.geoserver.geofence.gui.client.widget.SearchPagUserWidget;
import org.geoserver.geofence.gui.client.widget.SearchStatus.EnumSearchStatus;
import org.geoserver.geofence.gui.client.widget.UpdateUserWidget;
import org.geoserver.geofence.gui.client.widget.UserManagementWidget;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.user.client.rpc.AsyncCallback;


// TODO: Auto-generated Javadoc
/**
 * The Class LoginController.
 */
public class LoginController extends Controller
{

    /** The login remote. */
    private LoginRemoteServiceAsync loginRemote = LoginRemoteServiceAsync.Util.getInstance();

    /** The login widget. */
    private LoginWidget loginWidget;

    /** The choose profile widget. */
    private GeofenceChooserWidget<User> chooseUserWidget;

    /** The profile management widget. */
    private UserManagementWidget userManagementWidget;

    /** The search widget. */
    private GeofenceSearchWidget<User> searchWidget;

    /** The update profile. */
    private GeofenceUpdateWidget<User> updateUser;

    /**
     * Instantiates a new login controller.
     */
    public LoginController()
    {
        registerEventTypes(GeofenceEvents.INIT_USER_UI_MODULE, GeofenceEvents.INIT_GEOFENCE_WIDGET,
            GeofenceEvents.LOGIN, GeofenceEvents.ATTACH_USER_WIDGET,
            GeofenceEvents.SHOW_CHOOSER_USER_WIDGET, GeofenceEvents.SHOW_ADD_USER_WIDGET,
            GeofenceEvents.SAVE_USER, GeofenceEvents.SHOW_SEARCH_USER_WIDGET,
            GeofenceEvents.BIND_SELECTED_USER, GeofenceEvents.DELETE_USER,
            GeofenceEvents.SHOW_UPDATE_USER_WIDGET, GeofenceEvents.UPDATE_USER,
            GeofenceEvents.NOTIFY_UNSHARE_ERROR, GeofenceEvents.NOTIFY_UNSHARE_SUCCESS,
            GeofenceEvents.UNBIND_USER_WIDGET, GeofenceEvents.LOGOUT,
            GeofenceEvents.CHECK_RELATED_USERS_COUNT);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.mvc.Controller#initialize()
     */
    @Override
    protected void initialize()
    {
        this.loginWidget = new LoginWidget();

        this.loginWidget.addListener(Events.Hide, new Listener<WindowEvent>()
            {

                public void handleEvent(WindowEvent be)
                {
                    Dispatcher.forwardEvent(GeofenceEvents.INIT_GEOFENCE_MAIN_UI);
                    loginWidget.reset();
                }
            });

        initWidget();
    }

    /**
     * Inits the widget.
     */
    private void initWidget()
    {
        this.searchWidget = new SearchPagUserWidget(this.loginRemote);
        this.updateUser = new UpdateUserWidget();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.mvc.Controller#handleEvent(com.extjs.gxt.ui.client.mvc.AppEvent)
     */
    @Override
    public void handleEvent(AppEvent event)
    {
        if (event.getType() == GeofenceEvents.INIT_GEOFENCE_WIDGET)
        {
            onShowLoginWidget();
        }

        if (event.getType() == GeofenceEvents.LOGIN)
        {
            onLogin(event);
        }

        // if (event.getType() == GeofenceEvents.ATTACH_USER_WIDGET)
        // onAttachUserWidget(event);

        if (event.getType() == GeofenceEvents.SHOW_CHOOSER_USER_WIDGET)
        {
            onShowChooseUserWidget();
        }

        if (event.getType() == GeofenceEvents.SHOW_SEARCH_USER_WIDGET)
        {
            onShowSearchUSerWidget();
        }

        if (event.getType() == GeofenceEvents.SHOW_UPDATE_USER_WIDGET)
        {
            onShowUpdateUserWidget(event);
        }

        if (event.getType() == GeofenceEvents.NOTIFY_UNSHARE_ERROR)
        {
            onNotifyUnshareError();
        }

        if (event.getType() == GeofenceEvents.NOTIFY_UNSHARE_SUCCESS)
        {
            onNotifyUnshareSuccess();
        }

        if (event.getType() == GeofenceEvents.LOGOUT)
        {
            onLogout();
        }

    }

    /**
     * On logout.
     */
    private void onLogout()
    {
        this.loginRemote.logout(new AsyncCallback<Void>()
            {

                public void onFailure(Throwable caught)
                {
                    Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE,
                        new String[] { "Logout Service", "There was an error in logout" });
                }

				public void onSuccess(Void result) {
					Dispatcher.forwardEvent(GeofenceEvents.SESSION_EXPIRED);
				}

            });

    }

    /**
     * On notify unshare success.
     */
    private void onNotifyUnshareSuccess()
    {
        this.chooseUserWidget.setSearchStatus(
            EnumSearchStatus.STATUS_SEARCH,
            EnumSearchStatus.STATUS_MESSAGE_AOI_UNSHARE);
        this.chooseUserWidget.getChooser().hide();
    }

    /**
     * On notify unshare error.
     */
    private void onNotifyUnshareError()
    {
        this.chooseUserWidget.setSearchStatus(
            EnumSearchStatus.STATUS_SEARCH_ERROR,
            EnumSearchStatus.STATUS_MESSAGE_AOI_UNSHARE_ERROR);
    }

    /**
     * On show update profile widget.
     *
     * @param event
     *            the event
     */
    private void onShowUpdateUserWidget(AppEvent event)
    {
        this.updateUser.bind((User) event.getData());
        this.updateUser.show();
    }

    /**
     * On show search u ser widget.
     */
    private void onShowSearchUSerWidget()
    {
        this.searchWidget.show();
    }

    /**
     * On show choose profile widget.
     */
    private void onShowChooseUserWidget()
    {
        this.chooseUserWidget.getChooser().show();
    }

    /**
     * On login.
     *
     * @param event
     *            the event
     */
    private void onLogin(AppEvent event)
    {
        String[] values = (String[]) event.getData();
        this.loginRemote.authenticate(values[0], values[1], new AsyncCallback<User>()
            {

                public void onSuccess(User user)
                {
                    if (user != null)
                    {
                        loginWidget.setStatusLoginFinder(EnumLoginStatus.STATUS_LOGIN,
                            EnumLoginStatus.STATUS_MESSAGE_LOGIN);
                        loginWidget.hide();
                    }
                    else
                    {
                        loginWidget.setStatusLoginFinder(EnumLoginStatus.STATUS_NO_LOGIN,
                            EnumLoginStatus.STATUS_MESSAGE_NOT_LOGIN);
                    }
                }

                public void onFailure(Throwable caught)
                {
                    boolean handled = false;
                    if (caught instanceof ApplicationException)
                    {

                        /*ApplicationException appException = (ApplicationException) caught;
                        if (appException.getDetailedMessage().contains(
                                    "org.geoserver.geofence.security.exception.AuthException"))
                        {*/
                        // Authentication failed
                        handled = true;
                        loginWidget.resetPassword();
                        loginWidget.setStatusLoginFinder(EnumLoginStatus.STATUS_LOGIN_ERROR,
                            EnumLoginStatus.STATUS_MESSAGE_NOT_LOGIN);
                        /*}*/
                    }

                    if (!handled)
                    {
                        loginWidget.setStatusLoginFinder(EnumLoginStatus.STATUS_LOGIN_ERROR,
                            EnumLoginStatus.STATUS_MESSAGE_LOGIN_ERROR);
                    }
                }
            });
    }

    /**
     * On show login widget.
     */
    private void onShowLoginWidget()
    {
        this.loginRemote.isAuthenticated(new AsyncCallback<Boolean>()
            {

                public void onFailure(Throwable caught)
                {
                    loginWidget.show();
                }

                public void onSuccess(Boolean result)
                {
                    if (!result)
                    {
                        loginWidget.show();
                    }
                    else
                    {
                        loginWidget.hide();
                        Dispatcher.forwardEvent(GeofenceEvents.INIT_GEOFENCE_MAIN_UI);
                    }
                }

            });
    }

}

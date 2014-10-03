/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.controller;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.i18n.I18nProvider;
import org.geoserver.geofence.gui.client.model.GSUser;
import org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.view.UsersView;
import org.geoserver.geofence.gui.client.widget.UserGridWidget;
import org.geoserver.geofence.gui.client.widget.tab.GsUsersTabItem;
import org.geoserver.geofence.gui.client.widget.tab.TabWidget;


// TODO: Auto-generated Javadoc
/**
 * The Class UsersController.
 */
public class UsersController extends Controller
{

    /** The Constant USERS_TAB_ITEM_ID. */
    private static final String USERS_TAB_ITEM_ID = "UsersTabItem";

    /** The gs manager service remote. */
    private GsUsersManagerRemoteServiceAsync gsManagerServiceRemote =
        GsUsersManagerRemoteServiceAsync.Util.getInstance();

    /** The profiles manager service remote. */
    private ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote =
        ProfilesManagerRemoteServiceAsync.Util.getInstance();

    /** The tab widget. */
    private TabWidget tabWidget;

    /** The users view. */
    private UsersView usersView;

    /**
     * Instantiates a new users controller.
     */
    public UsersController()
    {
        registerEventTypes(
            GeofenceEvents.INIT_MAPS_UI_MODULE,




            GeofenceEvents.CREATE_NEW_USER,
            GeofenceEvents.SAVE_USER,
            GeofenceEvents.UPDATE_USER,
            GeofenceEvents.DELETE_USER,




            GeofenceEvents.ATTACH_BOTTOM_TAB_WIDGETS);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.mvc.Controller#initialize()
     */
    @Override
    protected void initialize()
    {
        this.usersView = new UsersView(this);
        initWidget();
    }

    /**
     * Inits the widget.
     */
    private void initWidget()
    {
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.mvc.Controller#handleEvent(com.extjs.gxt.ui.client
     * .mvc.AppEvent)
     */
    @Override
    public void handleEvent(AppEvent event)
    {
        if (event.getType() == GeofenceEvents.ATTACH_BOTTOM_TAB_WIDGETS)
        {
            onAttachTabWidgets(event);
        }

        if ((event.getType() == GeofenceEvents.UPDATE_USER) ||
                (event.getType() == GeofenceEvents.SAVE_USER))
        {
            onSaveUser(event);
        }

        if (event.getType() == GeofenceEvents.DELETE_USER)
        {
            onDeleteUser(event);
        }

        forwardToView(usersView, event);
    }

    /**
     * On attach tab widgets.
     *
     * @param event
     *            the event
     */
    private void onAttachTabWidgets(AppEvent event)
    {
        if (tabWidget == null)
        {
            tabWidget = (TabWidget) event.getData();

            TabItem usersTabItem = new GsUsersTabItem(USERS_TAB_ITEM_ID, gsManagerServiceRemote,
                    profilesManagerServiceRemote);
            tabWidget.add(usersTabItem);
        }
    }

    /**
     * On update profile.
     *
     * @param event
     *            the event
     */
    private void onSaveUser(AppEvent event)
    {
        if (tabWidget != null)
        {

            GsUsersTabItem usersTabItem = (GsUsersTabItem) tabWidget.getItemByItemId(USERS_TAB_ITEM_ID);
            final UserGridWidget usersInfoWidget = usersTabItem.getUserManagementWidget().getUsersInfo();
            final Grid<GSUser> grid = usersInfoWidget.getGrid();

            if ((grid != null) && (grid.getStore() != null) && (event.getData() != null) && (event.getData() instanceof GSUser))
            {

                GSUser user = event.getData();

                gsManagerServiceRemote.saveGsUser(user, new AsyncCallback<Void>()
                    {

                        public void onFailure(Throwable caught)
                        {

                            Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE,
                                new String[]
                                {
                            		"User Service",
                                    "Error occurred while saving the new user!"
                                });
                        }

                        public void onSuccess(Void result)
                        {
                            grid.getStore().getLoader().load();
                            grid.repaint();

                            Dispatcher.forwardEvent(
                                GeofenceEvents.BIND_MEMBER_DISTRIBUTION_NODES, result);
                            Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                new String[] {  /* TODO: I18nProvider.getMessages().ruleServiceName()*/"User Service",
                                    /* TODO: I18nProvider.getMessages().ruleFetchSuccessMessage() */ "User saved successfully!" });
                        }
                    });

            }
        }
    }

    /**
     * On delete profile.
     *
     * @param event
     *            the event
     */
    private void onDeleteUser(AppEvent event)
    {
        if (tabWidget != null)
        {

            GsUsersTabItem usersTabItem = (GsUsersTabItem) tabWidget.getItemByItemId(USERS_TAB_ITEM_ID);
            final UserGridWidget usersInfoWidget = usersTabItem.getUserManagementWidget().getUsersInfo();
            final Grid<GSUser> grid = usersInfoWidget.getGrid();

            if ((grid != null) && (grid.getStore() != null) && (event.getData() != null) && (event.getData() instanceof GSUser))
            {

                GSUser user = event.getData();

                gsManagerServiceRemote.deleteGsUser(user, new AsyncCallback<Void>()
                    {

                        public void onFailure(Throwable caught)
                        {

                            Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE,
                                new String[]
                                {
                            		"User Service",
                                    "Error occurred while deleting the selected user."
                                });
                        }

                        public void onSuccess(Void result)
                        {

                            // grid.getStore().sort(BeanKeyValue.USER_NAME.getValue(),SortDir.ASC);//<<-- ric mod 20100215
                            grid.getStore().getLoader().load();
                            grid.repaint();

                            Dispatcher.forwardEvent(
                                GeofenceEvents.BIND_MEMBER_DISTRIBUTION_NODES, result);
                            Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                new String[] {  /* TODO: I18nProvider.getMessages().ruleServiceName()*/"User Service",
                                    /* TODO: I18nProvider.getMessages().ruleFetchSuccessMessage() */ "User removed successfully!" });
                        }
                    });

            }
        }
    }

    /**
     * Forward to tab widget.
     *
     * @param event
     *            the event
     */
    @SuppressWarnings("unused")
    private void forwardToTabWidget(AppEvent event)
    {
        this.tabWidget.fireEvent(event.getType(), event);
    }

}

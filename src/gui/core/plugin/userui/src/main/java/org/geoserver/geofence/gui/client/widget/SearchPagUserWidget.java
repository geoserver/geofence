/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import org.geoserver.geofence.gui.client.widget.GeofenceSearchWidget;
import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.model.BeanKeyValue;
import org.geoserver.geofence.gui.client.model.User;
import org.geoserver.geofence.gui.client.service.LoginRemoteServiceAsync;
import org.geoserver.geofence.gui.client.widget.SearchStatus.EnumSearchStatus;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;


// TODO: Auto-generated Javadoc
/**
 * The Class SearchPagUserWidget.
 */
public class SearchPagUserWidget extends GeofenceSearchWidget<User>
{

    /** The service. */
    private LoginRemoteServiceAsync service;

    /**
     * Instantiates a new search pag user widget.
     *
     * @param service
     *            the service
     */
    public SearchPagUserWidget(LoginRemoteServiceAsync service)
    {
        super();
        this.service = service;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.widget.GEOFENCESearchWidget#setWindowProperties()
     */
    @Override
    public void setWindowProperties()
    {
        setHeading("Search for Registered Member");
        super.setSize(420, 490);

        super.addWindowListener(new WindowListener()
            {

                @Override
                public void windowShow(WindowEvent we)
                {
                    searchText = "";
                    loader.load(0, 25);
                }

            });
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.widget.GEOFENCESearchWidget#createStore()
     */
    @Override
    public void createStore()
    {
        toolBar = new PagingToolBar(org.geoserver.geofence.gui.client.Constants.DEFAULT_PAGESIZE);

        this.proxy = new RpcProxy<PagingLoadResult<User>>()
            {

                @Override
                protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<User>> callback)
                {

                    // TODO REFACTOR GG
                    // service.loadUsers((PagingLoadConfig) loadConfig, searchText,
                    // callback);
                }
            };

        loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
        loader.setRemoteSort(false);

        store = new ListStore<User>(loader);

        this.toolBar.bind(loader);
        // toolBar.disable();

        setUpLoadListener();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.widget.GEOFENCESearchWidget#setGridProperties()
     */
    @Override
    public void setGridProperties()
    {
        grid.setAutoExpandColumn(BeanKeyValue.USER_NAME.getValue());

        grid.setWidth(350);
        grid.setHeight("100%");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.widget.GEOFENCESearchWidget#prepareColumnModel()
     */
    @Override
    public ColumnModel prepareColumnModel()
    {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig userNameColumn = new ColumnConfig();
        userNameColumn.setId(BeanKeyValue.USER_NAME.getValue());
        userNameColumn.setHeader("User Name");
        userNameColumn.setWidth(80);
        configs.add(userNameColumn);

        ColumnConfig emailAddress = new ColumnConfig();
        emailAddress.setId(BeanKeyValue.EMAIL.getValue());
        emailAddress.setHeader("Email");
        emailAddress.setWidth(120);
        configs.add(emailAddress);

        return new ColumnModel(configs);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.widget.GEOFENCESearchWidget#select()
     */
    @Override
    public void select()
    {
        searchStatus.setBusy("Get User Details....");
        Dispatcher.forwardEvent(GeofenceEvents.BIND_SELECTED_USER, grid.getSelectionModel().getSelectedItem());
    }

    /**
     * Sets the up load listener.
     */
    private void setUpLoadListener()
    {
        loader.addLoadListener(new LoadListener()
            {

                @Override
                public void loaderBeforeLoad(LoadEvent le)
                {
                    searchStatus.setBusy("Connection to the Server");
                    if (select.isEnabled())
                    {
                        select.disable();
                    }
                }

                @Override
                public void loaderLoad(LoadEvent le)
                {
                    setSearchStatus(EnumSearchStatus.STATUS_SEARCH,
                        EnumSearchStatus.STATUS_MESSAGE_SEARCH);
                    // toolBar.enable();
                }

                @Override
                public void loaderLoadException(LoadEvent le)
                {
                    clearGridElements();
                    try
                    {
                        throw le.exception;
                    }
                    catch (ApplicationException e)
                    {
                        setSearchStatus(EnumSearchStatus.STATUS_NO_SEARCH,
                            EnumSearchStatus.STATUS_MESSAGE_NOT_SEARCH);
                    }
                    catch (Throwable e)
                    {
                        // TODO Auto-generated catch block
                        setSearchStatus(EnumSearchStatus.STATUS_SEARCH_ERROR,
                            EnumSearchStatus.STATUS_MESSAGE_SEARCH_ERROR);
                    }
                }

            });

    }
}

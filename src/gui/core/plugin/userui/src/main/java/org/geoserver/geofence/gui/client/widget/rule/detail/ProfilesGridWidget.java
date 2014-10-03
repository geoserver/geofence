/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.rule.detail;

import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.i18n.I18nProvider;
import org.geoserver.geofence.gui.client.model.BeanKeyValue;
import org.geoserver.geofence.gui.client.model.GSUser;
import org.geoserver.geofence.gui.client.model.UserGroup;
import org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.widget.GeofenceGridWidget;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;


// TODO: Auto-generated Javadoc
/**
 * The Class ProfilesGridWidget.
 */
public class ProfilesGridWidget extends GeofenceGridWidget<UserGroup>
{
	/** The users service */
	private final GsUsersManagerRemoteServiceAsync usersService;

    /** The profiles service. */
    private final ProfilesManagerRemoteServiceAsync profilesService;

    /** The user details widget. */
    private UserDetailsWidget userDetailsWidget;
    
    /** The proxy. */
    private RpcProxy<PagingLoadResult<UserGroup>> proxy;

    /** The loader. */
    private BaseListLoader<ListLoadResult<ModelData>> loader;

	private GSUser user;

    /**
     * Instantiates a new rule details grid widget.
     * @param model 
     *
     * @param model
     *            the model
     * @param usersService 
     * @param workspacesService
     *            the workspaces service
     * @param ruleDetailsWidget
     *            the rule details widget
     */
    public ProfilesGridWidget(GSUser model,
    		GsUsersManagerRemoteServiceAsync usersService, ProfilesManagerRemoteServiceAsync profilesService, UserDetailsWidget userDetailsWidget)
    {
        super();
        this.user = model;
        this.usersService = usersService;
        this.profilesService = profilesService;
        this.userDetailsWidget = userDetailsWidget;
    }

    /*
     * (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.widget.GEOFENCEGridWidget#setGridProperties ()
     */
    @Override
    public void setGridProperties()
    {
        grid.setLoadMask(true);
        grid.setAutoWidth(true);
        //grid.setHeight(300);
        grid.setAutoHeight(true);
        grid.setAutoWidth(true);
    }

    /**
     * Clear grid elements.
     */
    public void clearGridElements()
    {
        this.store.removeAll();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.widget.GEOFENCEGridWidget#createStore()
     */
    @Override
    public void createStore()
    {

        // /////////////////////////////
        // Loader for rulesService
        // /////////////////////////////

        this.proxy = new RpcProxy<PagingLoadResult<UserGroup>>()
            {
                @Override
                protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<UserGroup>> callback)
                {
                    profilesService.getProfiles(-1,-1,false, callback);
                }
            };

        loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy);
        loader.setRemoteSort(false);
        store = new ListStore<UserGroup>(loader);
        store.sort(BeanKeyValue.STYLES_COMBO.getValue(), SortDir.ASC);

        setUpLoadListener();
    }

    /**
     * Sets the up load listener.
     */
    private void setUpLoadListener()
    {
        loader.addLoadListener(new LoadListener()
            {

                @Override
                public void loaderLoad(LoadEvent le)
                {

                    // TODO: change messages here!!

                    BasePagingLoadResult<?> result = le.getData();
                    if (!result.getData().isEmpty())
                    {
                        int size = result.getData().size();
                        String message = "";
                        if (size == 1)
                        {
                            message = I18nProvider.getMessages().recordLabel();
                        }
                        else
                        {
                            message = I18nProvider.getMessages().recordPluralLabel();
                        }
                        Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                            new String[]
                            {
                                I18nProvider.getMessages().remoteServiceName(),
                                I18nProvider.getMessages().foundLabel() + " " + result.getData().size() +
                                " " + message
                            });
                    }
                    else
                    {
                        Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                            new String[]
                            {
                                I18nProvider.getMessages().remoteServiceName(),
                                I18nProvider.getMessages().recordNotFoundMessage()
                            });
                    }
                }

            });
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.widget.GeofenceGridWidget#prepareColumnModel()
     */
    @Override
    public ColumnModel prepareColumnModel()
    {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig attributeProfileColumn = new ColumnConfig();
        attributeProfileColumn.setId(BeanKeyValue.STYLES_COMBO.getValue());
        attributeProfileColumn.setHeader("Group");
        attributeProfileColumn.setWidth(180);
        attributeProfileColumn.setRenderer(this.createProfileTextBox());
        attributeProfileColumn.setMenuDisabled(true);
        attributeProfileColumn.setSortable(false);

        configs.add(attributeProfileColumn);

        ColumnConfig attributeEnableColumn = new ColumnConfig();
        attributeEnableColumn.setId(BeanKeyValue.STYLE_ENABLED.getValue());
        attributeEnableColumn.setHeader("Enable");
        attributeEnableColumn.setWidth(80);
        attributeEnableColumn.setRenderer(this.createEnableCheckBox());
        attributeEnableColumn.setMenuDisabled(true);
        attributeEnableColumn.setSortable(false);
        configs.add(attributeEnableColumn);

        return new ColumnModel(configs);
    }

    /**
     * Creates the style text box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<UserGroup> createProfileTextBox()
    {

        GridCellRenderer<UserGroup> textRendered = new GridCellRenderer<UserGroup>()
            {

                private boolean init;

                public Object render(final UserGroup model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<UserGroup> store, Grid<UserGroup> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<UserGroup>>()
                            {

                                public void handleEvent(GridEvent<UserGroup> be)
                                {
                                    for (int i = 0; i < be.getGrid().getStore().getCount(); i++)
                                    {
                                        if ((be.getGrid().getView().getWidget(i, be.getColIndex()) != null) &&
                                                (be.getGrid().getView().getWidget(i, be.getColIndex()) instanceof BoxComponent))
                                        {
                                            ((BoxComponent) be.getGrid().getView().getWidget(i,
                                                    be.getColIndex())).setWidth(be.getWidth() - 10);
                                        }
                                    }
                                }
                            });
                    }

                    LabelField profileName = new LabelField();
                    profileName.setWidth(150);
                    profileName.setReadOnly(true);
                    profileName.setValue(model.getName());

                    return profileName;
                }
            };

        return textRendered;
    }

    /**
     * Creates the enable check box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<UserGroup> createEnableCheckBox()
    {

        GridCellRenderer<UserGroup> textRendered = new GridCellRenderer<UserGroup>()
            {

                private boolean init;

                public Object render(final UserGroup model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<UserGroup> store, Grid<UserGroup> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<UserGroup>>()
                            {

                                public void handleEvent(GridEvent<UserGroup> be)
                                {
                                    for (int i = 0; i < be.getGrid().getStore().getCount(); i++)
                                    {
                                        if ((be.getGrid().getView().getWidget(i, be.getColIndex()) != null) &&
                                                (be.getGrid().getView().getWidget(i, be.getColIndex()) instanceof BoxComponent))
                                        {
                                            ((BoxComponent) be.getGrid().getView().getWidget(i,
                                                    be.getColIndex())).setWidth(be.getWidth() - 10);
                                        }
                                    }
                                }
                            });
                    }

                    CheckBox available = new CheckBox();
                    model.setEnabled(false);
                    for( UserGroup group : user.getUserGroups()) {
                    	if (group.getName().equals(model.getName())) {
                    		available.setValue(true);
                    		model.setEnabled(true);
                    		break;
                    	}
                    }

                    available.addListener(Events.Change, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                Boolean enable = (Boolean) be.getField().getValue();

                                if (enable.booleanValue())
                                {
                                    Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                        new String[] { "User Groups", "Group " + model.getName() + ": enabled" });

                                    model.setEnabled(enable);

                                    /*logger.error("TODO: profile refactoring!!!");*/
                                    userDetailsWidget.enableSaveButton();

                                }
                                else
                                {
                                    Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                        new String[] { "User Groups", "Group " + model.getName() + ": disabled" });

                                    model.setEnabled(enable);

                                    /*logger.error("TODO: profile refactoring!!!");*/
                                    userDetailsWidget.enableSaveButton();
                                }
                            }
                        });

                    return available;
                }
            };

        return textRendered;
    }

    /**
     * Gets the loader.
     *
     * @return the loader
     */
    public BaseListLoader<ListLoadResult<ModelData>> getLoader()
    {
        return loader;
    }

}

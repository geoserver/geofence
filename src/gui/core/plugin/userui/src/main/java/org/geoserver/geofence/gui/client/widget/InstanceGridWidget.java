/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import org.geoserver.geofence.gui.client.widget.GeofenceGridWidget;
import org.geoserver.geofence.gui.client.widget.SearchFilterField;
import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.geoserver.geofence.gui.client.Constants;
import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.Resources;
import org.geoserver.geofence.gui.client.i18n.I18nProvider;
import org.geoserver.geofence.gui.client.model.BeanKeyValue;
import org.geoserver.geofence.gui.client.model.GSInstance;
import org.geoserver.geofence.gui.client.service.InstancesManagerRemoteServiceAsync;


// TODO: Auto-generated Javadoc
/**
 * The Class InstanceGridWidget.
 */
public class InstanceGridWidget extends GeofenceGridWidget<GSInstance>
{

    /** The service. */
    private InstancesManagerRemoteServiceAsync service;

    /** The proxy. */
    private RpcProxy<PagingLoadResult<GSInstance>> proxy;

    /** The loader. */
    private PagingLoader<PagingLoadResult<ModelData>> loader;

    /** The tool bar. */
    private PagingToolBar toolBar;

    /**
     * Instantiates a new instance grid widget.
     *
     * @param service
     *            the service
     */
    public InstanceGridWidget(InstancesManagerRemoteServiceAsync service)
    {
        super();
        this.service = service;
    }

    /**
     * Instantiates a new instance grid widget.
     *
     * @param models
     *            the models
     */
    public InstanceGridWidget(List<GSInstance> models)
    {
        super(models);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.widget.GEOFENCEGridWidget#setGridProperties ()
     */
    @Override
    public void setGridProperties()
    {
        grid.setHeight(Constants.SOUTH_PANEL_DIMENSION - 25);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.widget.GEOFENCEGridWidget# prepareColumnModel()
     */
    @Override
    public ColumnModel prepareColumnModel()
    {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig instanceNameColumn = new ColumnConfig();
        instanceNameColumn.setId(BeanKeyValue.NAME.getValue());
        instanceNameColumn.setHeader("Instance Name");
        instanceNameColumn.setWidth(180);
        instanceNameColumn.setRenderer(this.createInstanceNameTextBox());
        configs.add(instanceNameColumn);

        ColumnConfig dateCreationColumn = new ColumnConfig();
        dateCreationColumn.setId(BeanKeyValue.DATE_CREATION.getValue());
        dateCreationColumn.setHeader("Date Creation");
        dateCreationColumn.setWidth(180);
        configs.add(dateCreationColumn);

        ColumnConfig instanceDescription = new ColumnConfig();
        instanceDescription.setId(BeanKeyValue.DESCRIPTION.getValue());
        instanceDescription.setHeader("Description");
        instanceDescription.setWidth(180);
        instanceDescription.setRenderer(this.createInstanceDescriptionTextBox());
        instanceDescription.setMenuDisabled(true);
        instanceDescription.setSortable(false);
        configs.add(instanceDescription);

        ColumnConfig instanceBaseUrl = new ColumnConfig();
        instanceBaseUrl.setId(BeanKeyValue.BASE_URL.getValue());
        instanceBaseUrl.setHeader("Base Url");
        instanceBaseUrl.setWidth(180);
        instanceBaseUrl.setRenderer(this.createInstanceBaseurlTextBox());
        instanceBaseUrl.setMenuDisabled(true);
        instanceBaseUrl.setSortable(false);
        configs.add(instanceBaseUrl);

        ColumnConfig instanceUsername = new ColumnConfig();
        instanceUsername.setId(BeanKeyValue.USER_NAME.getValue());
        instanceUsername.setHeader("Username");
        instanceUsername.setWidth(180);
        instanceUsername.setRenderer(this.createInstanceUsernameTextBox());
        instanceUsername.setMenuDisabled(true);
        instanceUsername.setSortable(false);
        configs.add(instanceUsername);

        ColumnConfig instancePassword = new ColumnConfig();
        instancePassword.setId(BeanKeyValue.PASSWORD.getValue());
        instancePassword.setHeader("Password");
        instancePassword.setWidth(180);
        instancePassword.setRenderer(this.createInstancePasswordTextBox());
        instancePassword.setMenuDisabled(true);
        instancePassword.setSortable(false);
        configs.add(instancePassword);

        ColumnConfig removeActionColumn = new ColumnConfig();
        removeActionColumn.setId("removeInstance");
        removeActionColumn.setWidth(80);
        removeActionColumn.setRenderer(this.createInstanceDeleteButton());
        removeActionColumn.setMenuDisabled(true);
        removeActionColumn.setSortable(false);
        configs.add(removeActionColumn);
        
        ColumnConfig testConnectionActionColumn = new ColumnConfig();
        testConnectionActionColumn.setId("testConnection");
        testConnectionActionColumn.setWidth(80);
        testConnectionActionColumn.setRenderer(this.createInstanceTestConnectionButton());
        testConnectionActionColumn.setMenuDisabled(true);
        testConnectionActionColumn.setSortable(false);
        configs.add(testConnectionActionColumn);

        return new ColumnModel(configs);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.widget.GEOFENCEGridWidget#createStore()
     */
    @Override
    public void createStore()
    {
        this.toolBar = new PagingToolBar(
                org.geoserver.geofence.gui.client.Constants.DEFAULT_PAGESIZE);

        // Loader fro service
        this.proxy = new RpcProxy<PagingLoadResult<GSInstance>>()
            {

                @Override
                protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<GSInstance>> callback)
                {
                    service.getInstances(((PagingLoadConfig) loadConfig).getOffset(), ((PagingLoadConfig) loadConfig).getLimit(), false, callback);
                }

            };
        loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
        loader.setRemoteSort(false);
        store = new ListStore<GSInstance>(loader);
        // store.sort(BeanKeyValue.NAME.getValue(), SortDir.ASC);

        // Search tool
        SearchFilterField<GSInstance> filter = new SearchFilterField<GSInstance>()
            {

                @Override
                protected boolean doSelect(Store<GSInstance> store, GSInstance parent,
                    GSInstance record, String property, String filter)
                {

                    String name = parent.get(BeanKeyValue.NAME.getValue());
                    name = name.toLowerCase();
                    if (name.indexOf(filter.toLowerCase()) != -1)
                    {
                        return true;
                    }

                    return false;
                }

            };
        filter.setWidth(130);
        filter.setIcon(Resources.ICONS.search());
        // Bind the filter field to your grid store (grid.getStore())
        filter.bind(store);

        // Add Instance button
        // TODO: generalize this!
        Button addInstanceButton = new Button("Add Instance");
        addInstanceButton.setIcon(Resources.ICONS.add());
        // TODO: temporally disabled!
        addInstanceButton.setEnabled(false);

        addInstanceButton.addListener(Events.OnClick, new Listener<ButtonEvent>()
            {

                public void handleEvent(ButtonEvent be)
                {
                    Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                        new String[] { "GeoServer Instance", "Add Instance" });

                    Dispatcher.forwardEvent(GeofenceEvents.CREATE_NEW_INSTANCE);
                }
            });

        this.toolBar.bind(loader);
        this.toolBar.add(new SeparatorToolItem());
        this.toolBar.add(addInstanceButton);
        this.toolBar.add(new SeparatorToolItem());
        this.toolBar.add(filter);
        this.toolBar.add(new SeparatorToolItem());
        setUpLoadListener();
    }

    /**
     * Gets the loader.
     *
     * @return the loader
     */
    public PagingLoader<PagingLoadResult<ModelData>> getLoader()
    {
        return loader;
    }

    /**
     * Gets the tool bar.
     *
     * @return the tool bar
     */
    public PagingToolBar getToolBar()
    {
        return toolBar;
    }

    /**
     * Clear grid elements.
     */
    public void clearGridElements()
    {
        this.store.removeAll();
        this.toolBar.clear();
        this.toolBar.disable();
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
                    if (!toolBar.isEnabled())
                    {
                        toolBar.enable();
                    }
                }

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

    /**
     * Creates the instance name text box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<GSInstance> createInstanceNameTextBox()
    {
        GridCellRenderer<GSInstance> buttonRendered = new GridCellRenderer<GSInstance>()
            {

                private boolean init;

                public Object render(final GSInstance model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<GSInstance> store, Grid<GSInstance> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<GSInstance>>()
                            {

                                public void handleEvent(GridEvent<GSInstance> be)
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

                    TextField<String> instanceNameTextBox = new TextField<String>();
                    instanceNameTextBox.setWidth(150);
                    // TODO: add correct tooltip text here!
                    // instanceNameTextBox("Test");

                    instanceNameTextBox.setValue(model.getName());

                    instanceNameTextBox.addListener(Events.Change, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                    new String[]
                                    {
                                        "GeoServer Instance",
                                        "Instance name changed to -> " + be.getField().getValue()
                                    });

                                model.setName((String) be.getField().getValue());
                                Dispatcher.forwardEvent(GeofenceEvents.UPDATE_INSTANCE, model);
                            }
                        });

                    return instanceNameTextBox;
                }
            };

        return buttonRendered;
    }

    /**
     * Creates the instance description text box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<GSInstance> createInstanceDescriptionTextBox()
    {
        GridCellRenderer<GSInstance> buttonRendered = new GridCellRenderer<GSInstance>()
            {

                private boolean init;

                public Object render(final GSInstance model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<GSInstance> store, Grid<GSInstance> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<GSInstance>>()
                            {

                                public void handleEvent(GridEvent<GSInstance> be)
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

                    TextField<String> instanceDescriptionTextBox = new TextField<String>();
                    instanceDescriptionTextBox.setWidth(150);
                    // TODO: add correct tooltip text here!
                    // instanceNameTextBox("Test");

                    instanceDescriptionTextBox.setValue(model.getDescription());

                    instanceDescriptionTextBox.addListener(Events.Change, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                    new String[]
                                    {
                                        "GeoServer Instance",
                                        "Instance description changed to -> " + be.getField().getValue()
                                    });

                                model.setDescription((String) be.getField().getValue());
                                Dispatcher.forwardEvent(GeofenceEvents.UPDATE_INSTANCE, model);
                            }
                        });

                    return instanceDescriptionTextBox;
                }
            };

        return buttonRendered;
    }

    /**
     * Creates the instance baseurl text box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<GSInstance> createInstanceBaseurlTextBox()
    {
        GridCellRenderer<GSInstance> buttonRendered = new GridCellRenderer<GSInstance>()
            {

                private boolean init;

                public Object render(final GSInstance model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<GSInstance> store, Grid<GSInstance> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<GSInstance>>()
                            {

                                public void handleEvent(GridEvent<GSInstance> be)
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

                    TextField<String> instanceBaseUrlTextBox = new TextField<String>();
                    instanceBaseUrlTextBox.setWidth(150);
                    // TODO: add correct tooltip text here!
                    // instanceNameTextBox("Test");

                    instanceBaseUrlTextBox.setValue(model.getBaseURL());

                    instanceBaseUrlTextBox.addListener(Events.Change, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                    new String[]
                                    {
                                        "GeoServer Instance",
                                        "Instance baseurl changed to -> " + be.getField().getValue()
                                    });

                                model.setBaseURL((String) be.getField().getValue());
                                Dispatcher.forwardEvent(GeofenceEvents.UPDATE_INSTANCE, model);
                            }
                        });

                    return instanceBaseUrlTextBox;
                }
            };

        return buttonRendered;
    }

    /**
     * Creates the instance username text box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<GSInstance> createInstanceUsernameTextBox()
    {
        GridCellRenderer<GSInstance> buttonRendered = new GridCellRenderer<GSInstance>()
            {

                private boolean init;

                public Object render(final GSInstance model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<GSInstance> store, Grid<GSInstance> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<GSInstance>>()
                            {

                                public void handleEvent(GridEvent<GSInstance> be)
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

                    TextField<String> instanceUsernameTextBox = new TextField<String>();
                    instanceUsernameTextBox.setWidth(150);
                    // TODO: add correct tooltip text here!
                    // instanceNameTextBox("Test");

                    instanceUsernameTextBox.setValue(model.getUsername());

                    instanceUsernameTextBox.addListener(Events.Change, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                    new String[]
                                    {
                                        "GeoServer Instance",
                                        "Instance username changed to -> " + be.getField().getValue()
                                    });

                                model.setUsername((String) be.getField().getValue());
                                Dispatcher.forwardEvent(GeofenceEvents.UPDATE_INSTANCE, model);
                            }
                        });

                    return instanceUsernameTextBox;
                }
            };

        return buttonRendered;
    }

    /**
     * Creates the instance password text box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<GSInstance> createInstancePasswordTextBox()
    {
        GridCellRenderer<GSInstance> buttonRendered = new GridCellRenderer<GSInstance>()
            {

                private boolean init;

                public Object render(final GSInstance model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<GSInstance> store, Grid<GSInstance> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<GSInstance>>()
                            {

                                public void handleEvent(GridEvent<GSInstance> be)
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

                    TextField<String> instancePasswordTextBox = new TextField<String>();
                    instancePasswordTextBox.setWidth(150);
                    instancePasswordTextBox.setPassword(true);
                    // TODO: add correct tooltip text here!
                    // instanceNameTextBox("Test");

                    instancePasswordTextBox.setValue(model.getPassword());

                    instancePasswordTextBox.addListener(Events.Change, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                    new String[]
                                    {
                                        "GeoServer Instance",
                                        "Instance password changed to -> " + be.getField().getValue()
                                    });

                                model.setPassword((String) be.getField().getValue());
                                Dispatcher.forwardEvent(GeofenceEvents.UPDATE_INSTANCE, model);
                            }
                        });

                    return instancePasswordTextBox;
                }
            };

        return buttonRendered;
    }

    /**
     * Creates the instance delete button.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<GSInstance> createInstanceDeleteButton()
    {
        GridCellRenderer<GSInstance> buttonRendered = new GridCellRenderer<GSInstance>()
            {

                private boolean init;

                public Object render(final GSInstance model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<GSInstance> store, Grid<GSInstance> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<GSInstance>>()
                            {

                                public void handleEvent(GridEvent<GSInstance> be)
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

                    // TODO: generalize this!
                    Button removeInstanceButton = new Button("Remove");
                    removeInstanceButton.setIcon(Resources.ICONS.delete());

                    removeInstanceButton.addListener(Events.OnClick, new Listener<ButtonEvent>()
                        {

                            public void handleEvent(ButtonEvent be)
                            {
                                final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>()
                                    {
                                        public void handleEvent(MessageBoxEvent ce)
                                        {
                                            Button btn = ce.getButtonClicked();

                                            if (btn.getText().equalsIgnoreCase("Yes"))
                                            {
                                                Dispatcher.forwardEvent(GeofenceEvents.DELETE_INSTANCE, model);
                                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                                    new String[]
                                                    {
                                                        "GeoServer Instance", "Remove Instance: " + model.getName()
                                                    });
                                            }
                                        }
                                    };

                                MessageBox.confirm("Confirm",
                                    "The Instance will be deleted. Are you sure you want to do that?", l);
                            }
                        });

                    return removeInstanceButton;
                }

            };

        return buttonRendered;
    }
    
    /**
     * Creates the instance delete button.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<GSInstance> createInstanceTestConnectionButton()
    {
        GridCellRenderer<GSInstance> buttonRendered = new GridCellRenderer<GSInstance>()
            {

                private boolean init;

                public Object render(final GSInstance model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<GSInstance> store, Grid<GSInstance> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<GSInstance>>()
                            {

                                public void handleEvent(GridEvent<GSInstance> be)
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

                    // TODO: generalize this!
                    Button testConnectionButton = new Button("Test");
                    testConnectionButton.setIcon(Resources.ICONS.test());

                    testConnectionButton.addListener(Events.OnClick, new Listener<ButtonEvent>()
                        {

                            public void handleEvent(ButtonEvent be)
                            {
                            	Dispatcher.forwardEvent(GeofenceEvents.TEST_CONNECTION, model);
                                
                            }
                        });

                    return testConnectionButton;
                }

            };

        return buttonRendered;
    }

}

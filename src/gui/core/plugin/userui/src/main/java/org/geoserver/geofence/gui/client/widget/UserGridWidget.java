/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import org.geoserver.geofence.gui.client.widget.GeofenceGridWidget;
import org.geoserver.geofence.gui.client.widget.SearchFilterField;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
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
import org.geoserver.geofence.gui.client.model.GSUser;
//import org.geoserver.geofence.gui.client.model.Profile;
import org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;


// TODO: Auto-generated Javadoc
/**
 * The Class UserGridWidget.
 */
public class UserGridWidget extends GeofenceGridWidget<GSUser>
{

    /** The gs users service. */
    private GsUsersManagerRemoteServiceAsync gsUsersService;

    /** The profiles service. */
    private ProfilesManagerRemoteServiceAsync profilesService;

    /** The proxy. */
    private RpcProxy<PagingLoadResult<GSUser>> proxy;

    /** The loader. */
    private PagingLoader<PagingLoadResult<ModelData>> loader;

    /** The tool bar. */
    private PagingToolBar toolBar;

    /**
     * Instantiates a new user grid widget.
     *
     * @param service
     *            the service
     * @param profilesService
     *            the profiles service
     */
    public UserGridWidget(GsUsersManagerRemoteServiceAsync service,
        ProfilesManagerRemoteServiceAsync profilesService)
    {
        super();
        this.gsUsersService = service;
        this.profilesService = profilesService;
    }

    /**
     * Instantiates a new user grid widget.
     *
     * @param models
     *            the models
     */
    public UserGridWidget(List<GSUser> models)
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

        ColumnConfig userNameColumn = new ColumnConfig();
        userNameColumn.setId(BeanKeyValue.NAME.getValue());
        userNameColumn.setHeader("User Name");

        TextField<String> userNameField = new TextField<String>();
        userNameField.setAllowBlank(false);
        userNameColumn.setEditor(new CellEditor(userNameField));
        userNameColumn.setWidth(100);
        configs.add(userNameColumn);

        ColumnConfig dateCreationColumn = new ColumnConfig();
        dateCreationColumn.setId(BeanKeyValue.DATE_CREATION.getValue());
        dateCreationColumn.setHeader("Date Creation");
        dateCreationColumn.setWidth(180);
        configs.add(dateCreationColumn);

        ColumnConfig userEnabledColumn = new ColumnConfig();
        userEnabledColumn.setId(BeanKeyValue.USER_ENABLED.getValue());
        userEnabledColumn.setHeader("Enabled");
        userEnabledColumn.setWidth(80);
        userEnabledColumn.setRenderer(this.createEnableCheckBox());
        userEnabledColumn.setMenuDisabled(true);
        userEnabledColumn.setSortable(false);
        configs.add(userEnabledColumn);

        ColumnConfig userAdminColumn = new ColumnConfig();
        userAdminColumn.setId(BeanKeyValue.USER_ADMIN.getValue());
        userAdminColumn.setHeader("Admin");
        userAdminColumn.setWidth(80);
        userAdminColumn.setRenderer(this.createAdminCheckBox());
        userAdminColumn.setMenuDisabled(true);
        userAdminColumn.setSortable(false);
        configs.add(userAdminColumn);

        ColumnConfig emailColumn = new ColumnConfig();
        emailColumn.setId(BeanKeyValue.EMAIL.getValue());
        emailColumn.setHeader("E-mail");
        emailColumn.setWidth(180);
        emailColumn.setRenderer(this.createEMailTextBox());
        emailColumn.setMenuDisabled(true);
        emailColumn.setSortable(false);
        configs.add(emailColumn);

        ColumnConfig passwordColumn = new ColumnConfig();
        passwordColumn.setId(BeanKeyValue.PASSWORD.getValue());
        passwordColumn.setHeader("Password");
        passwordColumn.setWidth(180);
        passwordColumn.setRenderer(this.createPasswordTextBox());
        passwordColumn.setMenuDisabled(true);
        passwordColumn.setSortable(false);
        configs.add(passwordColumn);

        /*ColumnConfig userProfileColumn = new ColumnConfig();
        userProfileColumn.setId(BeanKeyValue.PROFILE.getValue());
        userProfileColumn.setHeader("Group");
        userProfileColumn.setWidth(160);
        userProfileColumn.setRenderer(this.createProfilesComboBox());
        userProfileColumn.setMenuDisabled(true);
        userProfileColumn.setSortable(false);
        configs.add(userProfileColumn);*/

        ColumnConfig detailsUserColumn = new ColumnConfig();
        detailsUserColumn.setId("detailsUser");
        detailsUserColumn.setWidth(80);
        detailsUserColumn.setRenderer(this.createUserDetailsButton());
        detailsUserColumn.setMenuDisabled(true);
        detailsUserColumn.setSortable(false);
        configs.add(detailsUserColumn);

        ColumnConfig removeActionColumn = new ColumnConfig();
        removeActionColumn.setId("removeUser");
        removeActionColumn.setWidth(80);
        removeActionColumn.setRenderer(this.createUserDeleteButton());
        removeActionColumn.setMenuDisabled(true);
        removeActionColumn.setSortable(false);
        configs.add(removeActionColumn);

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

        // Loader fro gsUsersService
        this.proxy = new RpcProxy<PagingLoadResult<GSUser>>()
            {

                @Override
                protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<GSUser>> callback)
                {
                    gsUsersService.getGsUsers(((PagingLoadConfig) loadConfig).getOffset(), ((PagingLoadConfig) loadConfig).getLimit(), false, callback);
                }

            };
        loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
        loader.setRemoteSort(false);
        store = new ListStore<GSUser>(loader);

        // Search tool
        SearchFilterField<GSUser> filter = new SearchFilterField<GSUser>()
            {

                @Override
                protected boolean doSelect(Store<GSUser> store, GSUser parent, GSUser record,
                    String property, String filter)
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

        // Add User button
        Button addUserButton = new Button("Add User");
        addUserButton.setIcon(Resources.ICONS.add());
        addUserButton.setEnabled(false);

        addUserButton.addListener(Events.OnClick, new Listener<ButtonEvent>()
            {

                public void handleEvent(ButtonEvent be)
                {
                    Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                        new String[] { "GeoServer Users", "Add User" });

                    Dispatcher.forwardEvent(GeofenceEvents.CREATE_NEW_USER);
                }
            });

        this.toolBar.bind(loader);
        this.toolBar.add(new SeparatorToolItem());
        this.toolBar.add(addUserButton);
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
                                "User Service",
                                I18nProvider.getMessages().foundLabel() + " " + result.getData().size() +
                                " " + message
                            });
                    }
                    else
                    {
                        Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                            new String[]
                            {
                        		"User Service",
                                I18nProvider.getMessages().recordNotFoundMessage()
                            });
                    }
                }

            });
    }

    /**
     * Creates the enable check box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<GSUser> createEnableCheckBox()
    {

        GridCellRenderer<GSUser> buttonRendered = new GridCellRenderer<GSUser>()
            {

                private boolean init;

                public Object render(final GSUser model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<GSUser> store, Grid<GSUser> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<GSUser>>()
                            {

                                public void handleEvent(GridEvent<GSUser> be)
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

                    CheckBox userEnabledButton = new CheckBox();
                    userEnabledButton.setValue(model.isEnabled());

                    userEnabledButton.addListener(Events.OnClick, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                    new String[] { "GeoServer Users", "Enable check!" });

                                model.setEnabled((Boolean) be.getField().getValue());
                                Dispatcher.forwardEvent(GeofenceEvents.UPDATE_USER, model);
                            }
                        });

                    return userEnabledButton;
                }
            };

        return buttonRendered;
    }

    /**
     * Creates the admin check box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<GSUser> createAdminCheckBox()
    {

        GridCellRenderer<GSUser> buttonRendered = new GridCellRenderer<GSUser>()
            {

                private boolean init;

                public Object render(final GSUser model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<GSUser> store, Grid<GSUser> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<GSUser>>()
                            {

                                public void handleEvent(GridEvent<GSUser> be)
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

                    CheckBox userAdminButton = new CheckBox();
                    userAdminButton.setValue(model.isAdmin());

                    userAdminButton.addListener(Events.OnClick, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                    new String[] { "GeoServer Users", "Admin check!" });

                                model.setAdmin((Boolean) be.getField().getValue());
                                Dispatcher.forwardEvent(GeofenceEvents.UPDATE_USER, model);
                            }
                        });

                    return userAdminButton;
                }
            };

        return buttonRendered;
    }

    /**
     * Creates the user password text box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<GSUser> createPasswordTextBox()
    {
        GridCellRenderer<GSUser> buttonRendered = new GridCellRenderer<GSUser>()
            {

                private boolean init;

                public Object render(final GSUser model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<GSUser> store, Grid<GSUser> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<GSUser>>()
                            {

                                public void handleEvent(GridEvent<GSUser> be)
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

                    TextField<String> userPasswordTextBox = new TextField<String>();
                    userPasswordTextBox.setWidth(150);
                    userPasswordTextBox.setPassword(true);
                    userPasswordTextBox.setValue(model.getPassword());

                    userPasswordTextBox.addListener(Events.Change, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                    new String[]
                                    {
                                        "GeoServer User",
                                        "User password changed to -> " + be.getField().getValue()
                                    });

                                model.setPassword((String) be.getField().getValue());
                                Dispatcher.forwardEvent(GeofenceEvents.UPDATE_USER, model);
                            }
                        });

                    return userPasswordTextBox;
                }
            };

        return buttonRendered;
    }

    /**
     * Creates the user password text box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<GSUser> createEMailTextBox()
    {
        GridCellRenderer<GSUser> buttonRendered = new GridCellRenderer<GSUser>()
            {

                private boolean init;

                public Object render(final GSUser model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<GSUser> store, Grid<GSUser> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<GSUser>>()
                            {

                                public void handleEvent(GridEvent<GSUser> be)
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

                    TextField<String> emailTextBox = new TextField<String>();
                    emailTextBox.setWidth(150);
                    emailTextBox.setValue(model.getEmailAddress());

                    emailTextBox.addListener(Events.Change, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                    new String[]
                                    {
                                        "GeoServer User",
                                        "User e-mail changed to -> " + be.getField().getValue()
                                    });

                                model.setEmailAddress((String) be.getField().getValue());
                                Dispatcher.forwardEvent(GeofenceEvents.UPDATE_USER, model);
                            }
                        });

                    return emailTextBox;
                }
            };

        return buttonRendered;
    }

//    /**
//     * Creates the profiles combo box.
//     *
//     * @return the grid cell renderer
//     */
//    private GridCellRenderer<GSUser> createProfilesComboBox()
//    {
//
//        GridCellRenderer<GSUser> buttonRendered = new GridCellRenderer<GSUser>()
//            {
//
//                private boolean init;
//
//                public Object render(final GSUser model, String property, ColumnData config,
//                    int rowIndex, int colIndex, ListStore<GSUser> store, Grid<GSUser> grid)
//                {
//
//                    if (!init)
//                    {
//                        init = true;
//                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<GSUser>>()
//                            {
//
//                                public void handleEvent(GridEvent<GSUser> be)
//                                {
//                                    for (int i = 0; i < be.getGrid().getStore().getCount(); i++)
//                                    {
//                                        if ((be.getGrid().getView().getWidget(i, be.getColIndex()) != null) &&
//                                                (be.getGrid().getView().getWidget(i, be.getColIndex()) instanceof BoxComponent))
//                                        {
//                                            ((BoxComponent) be.getGrid().getView().getWidget(i,
//                                                    be.getColIndex())).setWidth(be.getWidth() - 10);
//                                        }
//                                    }
//                                }
//                            });
//                    }
//
//                    // TODO: generalize this!
//                    ComboBox<Profile> profilesComboBox = new ComboBox<Profile>();
//                    profilesComboBox.setId("userProfilesCombo");
//                    profilesComboBox.setName("userProfilesCombo");
//                    profilesComboBox.setEmptyText("(No profile available)");
//                    profilesComboBox.setDisplayField(BeanKeyValue.NAME.getValue());
//                    profilesComboBox.setEditable(false);
//                    profilesComboBox.setStore(getAvailableProfiles());
//                    profilesComboBox.setTypeAhead(true);
//                    profilesComboBox.setTriggerAction(TriggerAction.ALL);
//                    profilesComboBox.setWidth(150);
//
//                    if (model.getProfile() != null)
//                    {
//                        profilesComboBox.setValue(model.getProfile());
//                        profilesComboBox.setSelection(Arrays.asList(model.getProfile()));
//                    }
//
//                    profilesComboBox.addListener(Events.Select, new Listener<FieldEvent>()
//                        {
//
//                            public void handleEvent(FieldEvent be)
//                            {
//                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
//                                    new String[] { "GeoServer Users", "Groups" });
//
//                                model.setProfile((Profile) be.getField().getValue());
//                                Dispatcher.forwardEvent(GeofenceEvents.UPDATE_USER, model);
//                            }
//                        });
//
//                    return profilesComboBox;
//                }
//
//                /**
//                 * TODO: Call Profile Service here!!
//                 *
//                 * @return
//                 */
//                private ListStore<Profile> getAvailableProfiles()
//                {
//                    ListStore<Profile> availableProfiles = new ListStore<Profile>();
//                    RpcProxy<PagingLoadResult<Profile>> profileProxy = new RpcProxy<PagingLoadResult<Profile>>()
//                        {
//
//                            @Override
//                            protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<Profile>> callback)
//                            {
//                                profilesService.getProfiles(((PagingLoadConfig) loadConfig).getOffset(), ((PagingLoadConfig) loadConfig).getLimit(), false, callback);
//                            }
//
//                        };
//
//                    BasePagingLoader<PagingLoadResult<ModelData>> profilesLoader =
//                        new BasePagingLoader<PagingLoadResult<ModelData>>(
//                            profileProxy);
//                    profilesLoader.setRemoteSort(false);
//                    availableProfiles = new ListStore<Profile>(profilesLoader);
//
//                    return availableProfiles;
//                }
//            };
//
//        return buttonRendered;
//    }

    /**
     * Creates the user delete button.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<GSUser> createUserDeleteButton()
    {
        GridCellRenderer<GSUser> buttonRendered = new GridCellRenderer<GSUser>()
            {

                private boolean init;

                public Object render(final GSUser model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<GSUser> store, Grid<GSUser> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<GSUser>>()
                            {

                                public void handleEvent(GridEvent<GSUser> be)
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

                    Button removeUserButton = new Button("Remove");
                    removeUserButton.setIcon(Resources.ICONS.delete());

                    removeUserButton.addListener(Events.OnClick, new Listener<ButtonEvent>()
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
                                                Dispatcher.forwardEvent(GeofenceEvents.DELETE_USER, model);
                                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                                    new String[]
                                                    {
                                                        "GeoServer Users", "Remove User: " + model.getName()
                                                    });
                                            }
                                        }
                                    };

                                MessageBox.confirm("Confirm",
                                    "The User will be deleted. Are you sure you want to do that?", l);
                            }
                        });

                    return removeUserButton;
                }

            };

        return buttonRendered;
    }

    /**
     * Creates the user details button.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<GSUser> createUserDetailsButton()
    {
        GridCellRenderer<GSUser> buttonRendered = new GridCellRenderer<GSUser>()
            {

                private boolean init;

                public Object render(final GSUser model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<GSUser> store, Grid<GSUser> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<GSUser>>()
                            {

                                public void handleEvent(GridEvent<GSUser> be)
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

                    Button groupsUserButton = new Button("Groups");
                    groupsUserButton.setIcon(Resources.ICONS.table());

                    groupsUserButton.addListener(Events.OnClick, new Listener<ButtonEvent>()
                        {

                            public void handleEvent(ButtonEvent be)
                            {
                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                    new String[] { "GeoServer User", "User Groups: " + model.getName() });

                                Dispatcher.forwardEvent(GeofenceEvents.EDIT_USER_DETAILS, model);
                            }
                        });

                    return groupsUserButton;
                }

            };

        return buttonRendered;
    }

}

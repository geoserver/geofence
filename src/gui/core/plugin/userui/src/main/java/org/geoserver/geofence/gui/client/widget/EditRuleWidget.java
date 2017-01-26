/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.form.GeofenceFormWidget;
import org.geoserver.geofence.gui.client.i18n.I18nProvider;
import org.geoserver.geofence.gui.client.model.BeanKeyValue;
import org.geoserver.geofence.gui.client.model.GSInstanceModel;
import org.geoserver.geofence.gui.client.model.GSUserModel;
import org.geoserver.geofence.gui.client.model.RuleModel;
import org.geoserver.geofence.gui.client.model.UserGroupModel;
import org.geoserver.geofence.gui.client.model.data.Grant;
import org.geoserver.geofence.gui.client.model.data.Layer;
import org.geoserver.geofence.gui.client.model.data.RequestModel;
import org.geoserver.geofence.gui.client.model.data.ServiceModel;
import org.geoserver.geofence.gui.client.model.data.WorkspaceModel;
import org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.InstancesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.RulesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.WorkspacesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.view.RulesView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import org.geoserver.geofence.gui.client.model.RolenameModel;
import org.geoserver.geofence.gui.client.model.UsernameModel;


// TODO: Auto-generated Javadoc
/**
 * The Class EditRuleWidget.
 *
 *  Note:
 * There's a bug preventing the proper header alignment:
 * http://www.sencha.com/forum/showthread.php?234952-Sencha-Ext-GWT-grid-columns-not-aligned-with-grid-header
 */
public class EditRuleWidget extends GeofenceFormWidget
{

	private static final int COLUMN_HEADER_OFFSET = 20;

    /** The column priority width. */
    private static final int COLUMN_PRIORITY_WIDTH = 60;

    /** The Constant COLUMN_USER_WIDTH. */
    private static final int COLUMN_USER_WIDTH = 130; // 130;

    /** The Constant COLUMN_PROFILE_WIDTH. */
    private static final int COLUMN_GROUPS_WIDTH = 110; // 160;

    /** The Constant COLUMN_INSTANCE_WIDTH. */
    private static final int COLUMN_INSTANCE_WIDTH = 150; // 160;

    private static final int COLUMN_IPRANGE_WIDTH = 150; // 160;

    /** The Constant COLUMN_SERVICE_WIDTH. */
    private static final int COLUMN_SERVICE_WIDTH = 100; // 100;

    /** The Constant COLUMN_REQUEST_WIDTH. */
    private static final int COLUMN_REQUEST_WIDTH = 180; // 190;

    /** The Constant COLUMN_WORKSPACE_WIDTH. */
    private static final int COLUMN_WORKSPACE_WIDTH = 150; // 130;

    /** The Constant COLUMN_LAYER_WIDTH. */
    private static final int COLUMN_LAYER_WIDTH = 150; // 130;

    /** The Constant COLUMN_GRANT_WIDTH. */
    private static final int COLUMN_GRANT_WIDTH = 100; // 100;

    /** The submit event. */
    private EventType submitEvent;

    /** The close on submit. */
    private boolean closeOnSubmit;

    /** The rules service. */
    private RulesManagerRemoteServiceAsync rulesService;

    /** The gs users service. */
    private GsUsersManagerRemoteServiceAsync gsUsersService;

    /** The profiles service. */
    private ProfilesManagerRemoteServiceAsync profilesService;

    /** The instances service. */
    private InstancesManagerRemoteServiceAsync instancesService;

    /** The workspaces service. */
    private WorkspacesManagerRemoteServiceAsync workspacesService;

    /** The loader. */
    private PagingLoader<PagingLoadResult<ModelData>> loader;

    /** The model. */
    public RuleModel model = new RuleModel();

    /** The store. */
    public ListStore<RuleModel> store = new ListStore<RuleModel>();

    /** The grid. */
    public Grid<RuleModel> grid;

    /** The status. */
    public String status = "UPDATE";

    /** The unique. */
    boolean unique = false;

    /** The parent grid. */
    public Grid<RuleModel> parentGrid;

    /** The priority edited. */
    boolean priorityEdited = false;

    /** The rules view. */
    RulesView rulesView;

    boolean onExecute = false;

	private static ListStore<WorkspaceModel> workspaces = new ListStore<WorkspaceModel>();

	private static ListStore<Layer> layers = new ListStore<Layer>();

    /* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.Window#show()
	 */
	@Override
	public void show() {
		super.show();
		
		initializeWorkspaces(model.getInstance());
	}

	/**
     * Instantiates a new edits the rule widget.
     *
     * @param submitEvent
     *            the submit event
     * @param closeOnSubmit
     *            the close on submit
     * @param rulesService
     *            the rules service
     * @param gsUsersService
     *            the gs users service
     * @param profilesService
     *            the profiles service
     * @param instancesService
     *            the instances service
     * @param workspacesService
     *            the workspaces service
     */
    public EditRuleWidget(EventType submitEvent, boolean closeOnSubmit,
        RulesManagerRemoteServiceAsync rulesService,
        GsUsersManagerRemoteServiceAsync gsUsersService,
        ProfilesManagerRemoteServiceAsync profilesService,
        InstancesManagerRemoteServiceAsync instancesService,
        WorkspacesManagerRemoteServiceAsync workspacesService)
    {
        super();
        this.submitEvent = submitEvent;
        this.closeOnSubmit = closeOnSubmit;
        this.rulesService = rulesService;
        this.gsUsersService = gsUsersService;
        this.profilesService = profilesService;
        this.instancesService = instancesService;
        this.workspacesService = workspacesService;
    }

    /**
     * Instantiates a new edits the rule widget.
     *
     * @param models
     *            the models
     */
    public EditRuleWidget(List<RuleModel> models)
    {
        createStore();
        this.store.add(models);
        initGrid();
    }

    /**
     * Instantiates a new edits the rule widget.
     */
    public EditRuleWidget()
    {
        createStore();
        initGrid();
    }

    /**
     * Gets the submit event.
     *
     * @return the submit event
     */
    protected EventType getSubmitEvent()
    {
        return this.submitEvent;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.form.IForm#execute()
     */
    public void execute() {
        this.saveStatus.setBusy("Operation in progress");

        if (status.equals("UPDATE")) {
            Dispatcher.forwardEvent(GeofenceEvents.RULE_SAVE, model);
            onExecute = true;
        } else {
            Dispatcher.forwardEvent(GeofenceEvents.RULE_ADD, model);
            onExecute = true;
        }

        if (this.closeOnSubmit) {
            cancel();
        }

        this.injectEvent();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.form.GeofenceFormWidget#addComponentToForm ()
     */
    @Override
    public void addComponentToForm()
    {

        if (grid != null)
        {
            this.formPanel.add(grid);
        }
        addOtherComponents();
    }

    /**
     * Prepare column model.
     *
     * @return the column model
     */
    public ColumnModel prepareColumnModel()
    {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig priCol = new ColumnConfig();
        priCol.setId(BeanKeyValue.PRIORITY.getValue());
        priCol.setWidth(COLUMN_PRIORITY_WIDTH);
        priCol.setRenderer(this.createPriorityRenderer());
        priCol.setMenuDisabled(true);
        priCol.setSortable(false);
        configs.add(priCol);

        ColumnConfig userCol = new ColumnConfig();
        userCol.setId(BeanKeyValue.USERNAME.getValue());
        userCol.setHeader("User");
        userCol.setWidth(COLUMN_USER_WIDTH);
        userCol.setRenderer(this.createUsersRenderer());
        userCol.setMenuDisabled(true);
        userCol.setSortable(false);
        configs.add(userCol);

        ColumnConfig groupCol = new ColumnConfig();
        groupCol.setId(BeanKeyValue.ROLENAME.getValue());
        groupCol.setHeader("Role");
        groupCol.setWidth(COLUMN_GROUPS_WIDTH);
        groupCol.setRenderer(this.createGroupsRenderer());
        groupCol.setMenuDisabled(true);
        groupCol.setSortable(false);
        configs.add(groupCol);

        ColumnConfig instanceCol = new ColumnConfig();
        instanceCol.setId(BeanKeyValue.INSTANCE.getValue());
        instanceCol.setHeader("Instance");
        instanceCol.setWidth(COLUMN_INSTANCE_WIDTH);
        instanceCol.setRenderer(this.createInstancesRenderer());
        instanceCol.setMenuDisabled(true);
        instanceCol.setSortable(false);
        configs.add(instanceCol);

        ColumnConfig ipRangeCol = new ColumnConfig();
        ipRangeCol.setId(BeanKeyValue.SOURCE_IP_RANGE.getValue());
        ipRangeCol.setHeader("Src IP range");
        ipRangeCol.setWidth(COLUMN_IPRANGE_WIDTH);
        ipRangeCol.setRenderer(new IPRangeRenderer());
        ipRangeCol.setMenuDisabled(true);
        ipRangeCol.setSortable(false);
        configs.add(ipRangeCol);

        ColumnConfig serviceCol = new ColumnConfig();
        serviceCol.setId(BeanKeyValue.SERVICE.getValue());
        serviceCol.setHeader("Service");
        serviceCol.setWidth(COLUMN_SERVICE_WIDTH);
        serviceCol.setRenderer(this.createServicesRenderer());
        serviceCol.setMenuDisabled(true);
        serviceCol.setSortable(false);
        configs.add(serviceCol);

        ColumnConfig reqCol = new ColumnConfig();
        reqCol.setId(BeanKeyValue.REQUEST.getValue());
        reqCol.setHeader("Request");
        reqCol.setWidth(COLUMN_REQUEST_WIDTH);
        reqCol.setRenderer(this.createRequestRenderer());
        reqCol.setMenuDisabled(true);
        reqCol.setSortable(false);
        configs.add(reqCol);

        ColumnConfig wsCol = new ColumnConfig();
        wsCol.setId(BeanKeyValue.WORKSPACE.getValue());
        wsCol.setHeader("Workspace");
        wsCol.setWidth(COLUMN_WORKSPACE_WIDTH);
        wsCol.setRenderer(this.createWorkspacesRenderer());
        wsCol.setMenuDisabled(true);
        wsCol.setSortable(false);
        configs.add(wsCol);

        ColumnConfig layerCol = new ColumnConfig();
        layerCol.setId(BeanKeyValue.LAYER.getValue());
        layerCol.setHeader("Layer");
        layerCol.setWidth(COLUMN_LAYER_WIDTH);
        layerCol.setRenderer(this.createLayersRenderer());
        layerCol.setMenuDisabled(true);
        layerCol.setSortable(false);
        configs.add(layerCol);

        ColumnConfig grantCol = new ColumnConfig();
        grantCol.setId(BeanKeyValue.GRANT.getValue());
        grantCol.setHeader("Grant");
        grantCol.setWidth(COLUMN_GRANT_WIDTH);
        grantCol.setRenderer(this.createGrantsRenderer());
        grantCol.setMenuDisabled(true);
        grantCol.setSortable(false);
        configs.add(grantCol);

        return new ColumnModel(configs);
    }


    class ResizeListener implements Listener<GridEvent<RuleModel>> {
        private final int offset;

        public ResizeListener(int offset) {
            this.offset = offset;
        }

        public void handleEvent(GridEvent<RuleModel> be) {

            Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                new String[]{"DEBUG","Resizing " + be.getGrid().getColumnModel().getColumnId(be.getColIndex())});

            for (int i = 0; i < be.getGrid().getStore().getCount(); i++) { // there should be only 1 row

                // let's force a resize on all the columns
                for (int col = 0; col < be.getGrid().getColumnModel().getColumnCount(); col++) {

//                    final Widget widget = be.getGrid().getView().getWidget(i, be.getColIndex());

                    final Widget widget = be.getGrid().getView().getWidget(i, col);

//                    Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
//                        new String[]{"DEBUG","Resizing " + widget.getClass().getName() + "--" + widget.getTitle()});

                    int colWidth = be.getGrid().getColumnModel().getColumn(col).getWidth();

                    if ((widget != null) && (widget instanceof BoxComponent)) {
//                        ((BoxComponent) widget).setWidth(be.getWidth() - offset);
                        ((BoxComponent) widget).setWidth(colWidth - offset);
                    }
                }
            }
        }
    }
    
    /**
     * Creates the priority custom field.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<RuleModel> createPriorityRenderer()
    {
        GridCellRenderer<RuleModel> comboRendered = new GridCellRenderer<RuleModel>()
            {

//                private boolean init;

                public Object render(final RuleModel model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<RuleModel> store, Grid<RuleModel> grid)
                {

//                    if (!init)
//                    {
//                        init = true;
//                        grid.addListener(Events.ColumnResize, new ResizeListener(10));
//                    }

                    // TODO: generalize this!
                    ArrayList<RuleModel> list = new ArrayList<RuleModel>();

                    for (int i = 0; i < store.getCount(); i++)
                    {
                        RuleModel rule = ((RuleModel) store.getAt(i));
                        list.add(rule);
                    }

                    final TextField<String> field = new TextField<String>(); // (ListField)
                    field.setId("editRulePriority");
                    field.setName("editRulePriority");
                    field.setEmptyText("*");
                    field.setFieldLabel(BeanKeyValue.PRIORITY.getValue()); // DisplayField
                    field.setValue(BeanKeyValue.PRIORITY.getValue());
                    field.setReadOnly(false);

                    field.setWidth(COLUMN_PRIORITY_WIDTH - COLUMN_HEADER_OFFSET);
//                    field.setAutoWidth(true);
                    field.show();

                    if (model.getPriority() != -1)
                    {
                        long name2 = model.getPriority();
                        field.setValue(Long.valueOf(name2).toString());
                    }
                    else
                    {
                        field.setValue("-1");
                    }

                    KeyListener keyListener = new KeyListener()
                        {

                            @Override
                            public void componentKeyUp(ComponentEvent event)
                            {
                                if (event.getKeyCode() == '\r')
                                {
                                    event.cancelBubble();

                                    priorityEdited = true;
                                    try
                                    {
                                        model.setPriority(Long.parseLong((String) field.getRawValue()));
                                    }
                                    catch (Exception e)
                                    {
                                        Dispatcher.forwardEvent(GeofenceEvents.SEND_ALERT_MESSAGE,
                                            new String[]
                                            {
                                                I18nProvider.getMessages().remoteServiceName(),
                                                e.getMessage()
                                            });
                                    }
                                    Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO,
                                        model);
                                }
                            }
                        };

                    field.addKeyListener(keyListener);

                    field.addListener(Events.Blur, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                            	priorityEdited = true;
                                try
                                {
                                    model.setPriority(Long.parseLong((String) field.getRawValue()));
                                }
                                catch (Exception e)
                                {
                                    Dispatcher.forwardEvent(GeofenceEvents.SEND_ALERT_MESSAGE,
                                        new String[]
                                        {
                                            I18nProvider.getMessages().remoteServiceName(),
                                            e.getMessage()
                                        });
                                }
                                Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO,
                                    model);
                            }

                        });
                    
                    
                    
                    return field;
                }

            };

        return comboRendered;
    }

    /**
     * Creates the users combo box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<RuleModel> createUsersRenderer()
    {
        GridCellRenderer<RuleModel> comboRendered = new GridCellRenderer<RuleModel>()
            {

//                private boolean init;

                public Object render(final RuleModel model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<RuleModel> store, Grid<RuleModel> grid)
                {

//                    if (!init)
//                    {
//                        init = true;
//                        grid.addListener(Events.ColumnResize, new ResizeListener(10));
//                    }

                    // TODO: generalize this!
                    ComboBox<UsernameModel> combo = new ComboBox<UsernameModel>();
                    combo.setId("editRuleUser");
                    combo.setName("editRuleUser");
                    combo.setEmptyText("(No user available)");
                    combo.setDisplayField(BeanKeyValue.USERNAME.getValue());
                    combo.setEditable(false);
                    combo.setStore(getAvailableUsers());
                    combo.setTypeAhead(true);
                    combo.setTriggerAction(TriggerAction.ALL);
//                    usersComboBox.setWidth(100);
                    combo.setWidth(COLUMN_USER_WIDTH - COLUMN_HEADER_OFFSET);
//                    combo.setAutoWidth(true);

                    if (model.getUsername()!= null)
                    {
                        combo.setValue(new UsernameModel(model.getUsername()));
                        combo.setSelection(Arrays.asList(new UsernameModel(model.getUsername())));
                    }
                    else
                    {
                        UsernameModel def = new UsernameModel("*");
                        combo.setValue(def);
                        combo.setSelection(Arrays.asList(def));
                    }


                    combo.addListener(Events.Select, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
//                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
//                                    new String[]
//                                    {
//                                        "GeoServer Rules",
//                                        "Rule " + model.getPriority() + ": Rule changed"
//                                    });

                                final UsernameModel r = (UsernameModel) be.getField().getValue();
                                model.setUsername(r.getUsername());
                                Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
                            }
                        });

                    return combo;
                }

                /**
                 * TODO: Call User Service here!!
                 *
                 * @return
                 */
                private ListStore<UsernameModel> getAvailableUsers()
                {
                    RpcProxy<PagingLoadResult<UsernameModel>> userProxy = new RpcProxy<PagingLoadResult<UsernameModel>>()
                        {

                            @Override
                            protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<UsernameModel>> callback)
                            {
                                gsUsersService.getGsUsernames(((PagingLoadConfig) loadConfig).getOffset(), ((PagingLoadConfig) loadConfig).getLimit(), true, callback);
                            }

                        };

                    BasePagingLoader<PagingLoadResult<ModelData>> usersLoader = new BasePagingLoader<PagingLoadResult<ModelData>>( userProxy);
                    usersLoader.setRemoteSort(false);
                    usersLoader.setSortField(BeanKeyValue.USERNAME.getValue());
                    usersLoader.setSortDir(SortDir.ASC);

                    ListStore<UsernameModel> availableUsers = new ListStore<UsernameModel>(usersLoader);

                    return availableUsers;
                }
            };

        return comboRendered;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.form.GeofenceFormWidget#cancel()
     */
    @SuppressWarnings("deprecation")
    @Override
    public void cancel()
    {
        resetComponents();
        super.close();
        hide();
    }

    /**
     * Reset components.
     */
    public void resetComponents()
    {
        if ((grid != null) && (grid.getStore() != null))
        {
            this.grid.getStore().removeAll();
        }

        this.saveStatus.clearStatus("");

        if (!this.onExecute)
        {
            Dispatcher.forwardEvent(GeofenceEvents.LOAD_RULES);
        }

        this.onExecute = false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.widget.AddGenericAOIWidget# addOtherComponents()
     */
    /**
     * Adds the other components.
     */
    public void addOtherComponents()
    {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.form.GeofenceFormWidget#initSize()
     */
    @Override
    public void initSize()
    {
        setHeading( /* TODO: I18nProvider.getMessages().addAoiDialogTitle() */"Edit rule");
        setSize(800, 175);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.form.GeofenceFormWidget#initSizeFormPanel ()
     */
    @Override
    public void initSizeFormPanel()
    {
        formPanel.setHeaderVisible(false);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.form.GeofenceFormWidget#injectEvent()
     */
    @Override
    public void injectEvent()
    {
        Dispatcher.forwardEvent(getSubmitEvent(), this.model);
    }

    /**
     * Sets the rule service.
     *
     * @param rulesManagerServiceRemote
     *            the new rule service
     */
    public void setRuleService(RulesManagerRemoteServiceAsync rulesManagerServiceRemote)
    {
        this.rulesService = rulesManagerServiceRemote;
    }

    /**
     * Sets the user service.
     *
     * @param usersManagerServiceRemote
     *            the new user service
     */
    public void setUserService(GsUsersManagerRemoteServiceAsync usersManagerServiceRemote)
    {
        this.gsUsersService = usersManagerServiceRemote;
    }

    /**
     * Creates the profiles combo box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<RuleModel> createGroupsRenderer()
    {
        GridCellRenderer<RuleModel> comboRendered = new GridCellRenderer<RuleModel>()
            {

//                private boolean init;

                public Object render(final RuleModel model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<RuleModel> store, Grid<RuleModel> grid)
                {

//                    if (!init)
//                    {
//                        init = true;
//                        grid.addListener(Events.ColumnResize, new ResizeListener(10));
//                    }

                    // TODO: generalize this!
                    ComboBox<RolenameModel> combo = new ComboBox<RolenameModel>();
                    combo.setId("editRuleGroup");
                    combo.setName("editRuleGroup");
                    combo.setEmptyText("(No group available)");
                    combo.setDisplayField(BeanKeyValue.ROLENAME.getValue());
                    combo.setEditable(false);
                    combo.setStore(getAvailableGroups());
                    combo.setTypeAhead(true);
                    combo.setTriggerAction(TriggerAction.ALL);
                    combo.setWidth(COLUMN_GROUPS_WIDTH -COLUMN_HEADER_OFFSET);
//                    combo.setAutoWidth(true);


                    if (model.getRolename()!= null)
                    {
                        combo.setValue(new RolenameModel(model.getRolename()));
                        combo.setSelection(Arrays.asList(new RolenameModel(model.getRolename())));
                    }

                    combo.addListener(Events.Select, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                final RolenameModel r = (RolenameModel) be.getField().getValue();
                                model.setRolename(r.getRolename());
                                Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
                            }
                        });

                    return combo;
                }

                /**
                 * TODO: Call Profile Service here!!
                 *
                 * @return
                 */
                private ListStore<RolenameModel> getAvailableGroups()
                {
                    ListStore<RolenameModel> groups = new ListStore<RolenameModel>();
                    RpcProxy<PagingLoadResult<RolenameModel>> profileProxy = new RpcProxy<PagingLoadResult<RolenameModel>>()
                        {

                            @Override
                            protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<RolenameModel>> callback)
                            {
                                profilesService.getRolenames(((PagingLoadConfig) loadConfig).getOffset(), ((PagingLoadConfig) loadConfig).getLimit(), true, callback);
                            }

                        };

                    BasePagingLoader<PagingLoadResult<ModelData>> groupLoader =
                        new BasePagingLoader<PagingLoadResult<ModelData>>(
                            profileProxy);
                    groupLoader.setRemoteSort(false);
                    groupLoader.setSortField(BeanKeyValue.ROLENAME.getValue());
                    groupLoader.setSortDir(SortDir.ASC);

                    groups = new ListStore<RolenameModel>(groupLoader);

                    return groups;
                }
            };

        return comboRendered;
    }

    /**
     * Creates the instances combo box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<RuleModel> createInstancesRenderer()
    {
        GridCellRenderer<RuleModel> comboRendered = new GridCellRenderer<RuleModel>()
            {

//                private boolean init;

                public Object render(final RuleModel model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<RuleModel> store, Grid<RuleModel> grid)
                {

//                    if (!init)
//                    {
//                        init = true;
//                        grid.addListener(Events.ColumnResize, new ResizeListener(10));
//                    }

                    // TODO: generalize this!
                    ComboBox<GSInstanceModel> combo = new ComboBox<GSInstanceModel>();
                    combo.setId("editRuleInstance");
                    combo.setName("editRuleInstance");
                    combo.setEmptyText("(No instance available)");
                    combo.setDisplayField(BeanKeyValue.NAME.getValue());
                    combo.setEditable(false);
                    combo.setStore(getAvailableInstances());
                    combo.setTypeAhead(true);
                    combo.setTriggerAction(TriggerAction.ALL);
                    combo.setWidth(COLUMN_INSTANCE_WIDTH - COLUMN_HEADER_OFFSET);
//                    combo.setAutoWidth(true);

                    if (model.getInstance() != null)
                    {
                        combo.setValue(model.getInstance());
                        combo.setSelection(Arrays.asList(model.getInstance()));
                    }
                    else
                    {
                        GSInstanceModel all = new GSInstanceModel();
                        all.setId(-1);
                        all.setName("*");
                        all.setBaseURL("*");
                        combo.setValue(all);
                        combo.setSelection(Arrays.asList(all));
                    }

                    combo.addListener(Events.Select, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                final GSInstanceModel instance = (GSInstanceModel) be.getField().getValue();

                                model.setInstance(instance);
                                model.setService("*");
                                model.setRequest("*");
                                model.setWorkspace("*");
                                model.setLayer("*");
                                
                                getAvailableWorkspaces(instance);
                                Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
                            }

                            /**
                             * TODO: Call User Service here!!
                             *
                             * @param rule
                             *
                             * @return
                             */
                            private ListStore<WorkspaceModel> getAvailableWorkspaces(final GSInstanceModel gsInstance)
                            {
                                /*RpcProxy<PagingLoadResult<Workspace>> workspacesProxy = new RpcProxy<PagingLoadResult<Workspace>>()
                                    {

                                        @Override
                                        protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<Workspace>> callback)
                                        {
                                            workspacesService.getWorkspaces(((PagingLoadConfig) loadConfig).getOffset(), ((PagingLoadConfig) loadConfig).getLimit(),
                                                (gsInstance != null) ? gsInstance.getBaseURL() : null, gsInstance,
                                                callback);
                                            
                                        }

                                    };

                                BasePagingLoader<PagingLoadResult<ModelData>> workspacesLoader =
                                    new BasePagingLoader<PagingLoadResult<ModelData>>(
                                        workspacesProxy);
                                workspacesLoader.setRemoteSort(false);

                                ListStore<Workspace> availableWorkspaces = new ListStore<Workspace>(
                                        workspacesLoader);

                                return availableWorkspaces;*/
                            	
                            	initializeWorkspaces(gsInstance);
                            	
                            	return null;
                            }
                        });

                    return combo;
                }

                /**
                 * TODO: Call Instance Service here!!
                 *
                 * @return
                 */
                private ListStore<GSInstanceModel> getAvailableInstances()
                {
                    RpcProxy<PagingLoadResult<GSInstanceModel>> gsInstancesProxy =
                        new RpcProxy<PagingLoadResult<GSInstanceModel>>()
                        {

                            @Override
                            protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<GSInstanceModel>> callback)
                            {
                                instancesService.getInstances(((PagingLoadConfig) loadConfig).getOffset(), ((PagingLoadConfig) loadConfig).getLimit(), true, callback);
                            }

                        };

                    BasePagingLoader<PagingLoadResult<ModelData>> gsInstancesLoader =
                        new BasePagingLoader<PagingLoadResult<ModelData>>(
                            gsInstancesProxy);
                    gsInstancesLoader.setRemoteSort(false);

                    ListStore<GSInstanceModel> availableInstances = new ListStore<GSInstanceModel>(
                            gsInstancesLoader);

                    return availableInstances;
                }
            };

        return comboRendered;
    }

    class IPRangeRenderer implements GridCellRenderer<RuleModel> {


        public Object render(final RuleModel model, String property, ColumnData config,
            int rowIndex, int colIndex, ListStore<RuleModel> store, Grid<RuleModel> grid)
        {

            TextField<String> field = new TextField<String>();

            field.setId("editRuleIpRange");
            field.setName("editRuleIpRange");
            field.setEmptyText("*");
            field.setRegex(CIDR_FORMAT);
            field.setAutoValidate(true);
            field.setWidth(COLUMN_IPRANGE_WIDTH - COLUMN_HEADER_OFFSET);
//            ipRangeField.setValidator(null);

            if (model.getSourceIPRange() != null) {
                field.setValue(model.getSourceIPRange());
            } else {
                field.setValue("*");
            }

//            field.addListener(Events.Change,
//                    new Listener<FieldEvent>()
//                {
//
//                    public void handleEvent(FieldEvent be)
//                    {
//                        if( ((TextField)be.getSource()).isValid()) {
//
//                            String value = (String)be.getField().getValue();
//
//                            if(value.equals(model.getSourceIPRange())) {
//                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
//                                new String[]
//                                {
//                                    "DEBUG","No change in IP range: " + value
//                                });
//                                return;
//                            }
//
//                            model.setSourceIPRange(value);
//
//                            Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
//                            new String[]
//                            {
//                                "IP range","Updating current IP range: " + value
//                            });
//
//                            Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
//                        }
//                    }
//                });

                field.addListener(Events.Blur, new Listener<FieldEvent>()
                    {
                        public void handleEvent(FieldEvent be)
                        {
                            try
                            {
                                String value = (String)be.getField().getRawValue().trim();
                                if ( checkIpRange(value)) {
                                    model.setSourceIPRange(value);
                                } else {
                                    Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                        new String[]
                                        {
                                            "Input error", "IP range has bad format. e.g: 100.111.112.0/24"
                                        });
                                }

                            }
                            catch (Exception e)
                            {
                                Dispatcher.forwardEvent(GeofenceEvents.SEND_ALERT_MESSAGE,
                                    new String[]
                                    {
                                        I18nProvider.getMessages().remoteServiceName(),
                                        e.getMessage()
                                    });
                            }
                            
                            Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
                        }

                    });


            return field;
        }
    }

    private static final String IP_ADDRESS = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})";
    private static final String CIDR_FORMAT = IP_ADDRESS + "/(\\d{1,3})";

    public boolean checkIpRange(String s) {
        if(s == null || s.trim().equals(""))
            return true;

        RegExp regExp = RegExp.compile(CIDR_FORMAT);
        MatchResult matcher = regExp.exec(s);
        boolean matchFound = (matcher != null); // equivalent to regExp.test(inputStr);

        if (! matchFound) {
            Dispatcher.forwardEvent(GeofenceEvents.SEND_ALERT_MESSAGE,
                new String[]
                {
                    "Input error", "Bad Range format"
                });
            return false;
        }

        // Get address bytes
        for (int i=1; i<5; i++) {
            String groupStr = matcher.getGroup(i);
            int b = Integer.parseInt(groupStr);
            if(b>255) {
                Dispatcher.forwardEvent(GeofenceEvents.SEND_ALERT_MESSAGE,
                    new String[]
                    {
                        "Input error", "Range bytes should be 0..255"
                    });
                return false;
            }

        }

        int size = Integer.parseInt(matcher.getGroup(5));
        if(size > 32) {
            Dispatcher.forwardEvent(GeofenceEvents.SEND_ALERT_MESSAGE,
                new String[]
                {
                    "Input error", "Bad CIDR block size"
                });
            return false;
        }

        return true;
    }

    /**
     * Creates the services combo box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<RuleModel> createServicesRenderer()
    {
        GridCellRenderer<RuleModel> comboRendered = new GridCellRenderer<RuleModel>()
            {

//                private boolean init;

                public Object render(final RuleModel model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<RuleModel> store, Grid<RuleModel> grid)
                {

//                    if (!init)
//                    {
//                        init = true;
//                        grid.addListener(Events.ColumnResize, new ResizeListener(10));
//                    }

                    // TODO: generalize this!
                    final ComboBox<ServiceModel> combo = new ComboBox<ServiceModel>();
                    combo.setId("editRuleService");
                    combo.setName("editRuleService");
                    combo.setEmptyText("(No service available)");
                    combo.setDisplayField(BeanKeyValue.SERVICE.getValue());
                    combo.setStore(getAvailableServices(model.getInstance()));
                    combo.setEditable(true);
                    combo.setTypeAhead(true);
                    combo.setTriggerAction(TriggerAction.ALL);
//                    servicesComboBox.setWidth(90);
                    combo.setWidth(COLUMN_SERVICE_WIDTH - COLUMN_HEADER_OFFSET);
//                    combo.setAutoWidth(true);

                    KeyListener keyListener = new KeyListener()
                        {

                            @Override
                            public void componentKeyUp(ComponentEvent event)
                            {
                                if (event.getKeyCode() == '\r')
                                {
                                    event.cancelBubble();

                                    model.setService(combo.getRawValue());
                                    //model.setRequest("*");
                                    Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO,
                                        model);
                                }
                            }
                        };

                    combo.addKeyListener(keyListener);

                    combo.addListener(Events.Blur, new Listener<FieldEvent>()
                            {

                                public void handleEvent(FieldEvent be)
                                {
                                	model.setService(combo.getRawValue());
                                    //model.setRequest("*");
                                    Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO,
                                        model);
                                }

                            });
                    
                    if (model.getService() != null)
                    {
                        combo.setValue(new ServiceModel(model.getService()));
                        combo.setSelection(Arrays.asList(new ServiceModel(model.getService())));
                    }

                    combo.addListener(Events.Select, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                final ServiceModel service = (ServiceModel) be.getField().getValue();

                                model.setService(service.getService());
                                model.setRequest("*");
                                Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
                            }

                        });

                    return combo;
                }

                /**
                 * TODO: Call Services Service here!!
                 *
                 * @param gsInstance
                 *
                 * @return
                 */
                private ListStore<ServiceModel> getAvailableServices(GSInstanceModel gsInstance)
                {
                    ListStore<ServiceModel> availableServices = new ListStore<ServiceModel>();
                    List<ServiceModel> services = new ArrayList<ServiceModel>();

                    ServiceModel all = new ServiceModel("*");

                    services.add(all);

                    if ((gsInstance != null) && (gsInstance.getBaseURL() != null) &&
                            !gsInstance.getBaseURL().equals("*"))
                    {
                        ServiceModel wms = new ServiceModel("WMS");
                        ServiceModel wcs = new ServiceModel("WCS");
                        ServiceModel wfs = new ServiceModel("WFS");

                        services.add(wms);
                        services.add(wcs);
                        services.add(wfs);
                    }

                    availableServices.add(services);

                    return availableServices;
                }
            };

        return comboRendered;
    }

    /**
     * Creates the services request combo box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<RuleModel> createRequestRenderer()
    {
        GridCellRenderer<RuleModel> comboRendered = new GridCellRenderer<RuleModel>()
            {

//                private boolean init;

                public Object render(final RuleModel model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<RuleModel> store, Grid<RuleModel> grid)
                {

//                    if (!init)
//                    {
//                        init = true;
//                        grid.addListener(Events.ColumnResize, new ResizeListener(10));
//                    }

                    // TODO: generalize this!
                    final ComboBox<RequestModel> combo = new ComboBox<RequestModel>();
                    combo.setId("editRuleRequest");
                    combo.setName("editRuleRequest");
                    combo.setEmptyText("(No OGC request available)");
                    combo.setDisplayField(BeanKeyValue.REQUEST.getValue());
                    combo.setStore(getSampleRequests(model.getInstance(),
                            model.getService()));
                    combo.setEditable(true);
                    combo.setTypeAhead(true);
                    combo.setTriggerAction(TriggerAction.ALL);
//                    serviceRequestsComboBox.setWidth(150);
                    combo.setWidth(COLUMN_REQUEST_WIDTH - COLUMN_HEADER_OFFSET);
//                    combo.setAutoWidth(true);

                    KeyListener keyListener = new KeyListener()
                        {

                            @Override
                            public void componentKeyUp(ComponentEvent event)
                            {
                                if (event.getKeyCode() == '\r')
                                {
                                    event.cancelBubble();

                                    model.setRequest(combo.getRawValue());
                                    Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO,
                                        model);
                                }
                            }
                        };

                    combo.addKeyListener(keyListener);

                    combo.addListener(Events.Blur, new Listener<FieldEvent>()
                            {

                                public void handleEvent(FieldEvent be)
                                {
                                    model.setRequest(combo.getRawValue());
                                    Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
                                }
                            });

                    if (model.getService() != null)
                    {
                        combo.setValue(new RequestModel(model.getRequest()));
                        combo.setSelection(Arrays.asList(new RequestModel(model.getRequest())));
                    }

                    combo.addListener(Events.Select, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                final RequestModel request = (RequestModel) be.getField().getValue();

                                model.setRequest(request.getRequest());
                                Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
                            }
                        });

                    return combo;
                }

                /**
                 * TODO: Call Services Service here!!
                 *
                 * @param gsInstance
                 * @param rulesService
                 *
                 * @return
                 */
                private ListStore<RequestModel> getSampleRequests(GSInstanceModel gsInstance, String service)
                {
                    ListStore<RequestModel> availableServicesRequests = new ListStore<RequestModel>();
                    List<RequestModel> requests = new ArrayList<RequestModel>();

                    RequestModel all = new RequestModel("*");
                    RequestModel getCapabilities = new RequestModel("GetCapabilities");

                    requests.add(all);
                    requests.add(getCapabilities);

                    if ((service != null) && service.equalsIgnoreCase("WMS"))
                    {
                        RequestModel getMap = new RequestModel("GetMap");
                        RequestModel getFeatureInfo = new RequestModel("GetFeatureInfo");
                        RequestModel describeLayer = new RequestModel("DescribeLayer");

                        requests.add(getMap);
                        requests.add(getFeatureInfo);
                        requests.add(describeLayer);
                    }

                    if ((service != null) && service.equalsIgnoreCase("WCS"))
                    {
                        RequestModel getCoverage = new RequestModel("GetCoverage");
                        RequestModel describeCoverage = new RequestModel("DescribeCoverage");

                        requests.add(getCoverage);
                        requests.add(describeCoverage);
                    }

                    if ((service != null) && service.equalsIgnoreCase("WFS"))
                    {
                        RequestModel getFeatureType = new RequestModel("GetFeatureType");
                        RequestModel describeFeatureType = new RequestModel("DescribeFeatureType");
                        RequestModel getFeature = new RequestModel("GetFeature");
                        RequestModel getGmlObject = new RequestModel("GetGmlObject");
                        RequestModel lockFeature = new RequestModel("LockFeature");
                        RequestModel getFeatureWithLock = new RequestModel("GetFeatureWithLock");
                        RequestModel transaction = new RequestModel("Transaction");

                        requests.add(getFeatureType);
                        requests.add(describeFeatureType);
                        requests.add(getFeature);
                        requests.add(getGmlObject);
                        requests.add(lockFeature);
                        requests.add(getFeatureWithLock);
                        requests.add(transaction);
                    }

                    availableServicesRequests.add(requests);

                    return availableServicesRequests;
                }
            };

        return comboRendered;
    }

    /**
     * Creates the service workspaces combo box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<RuleModel> createWorkspacesRenderer()
    {
        GridCellRenderer<RuleModel> comboRendered = new GridCellRenderer<RuleModel>()
            {

//                private boolean init;
				
                public Object render(final RuleModel model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<RuleModel> store, Grid<RuleModel> grid)
                {

//                    if (!init)
//                    {
//                        init = true;
//                        grid.addListener(Events.ColumnResize, new ResizeListener(10));
//                    }

                    // TODO: generalize this!
                    ComboBox<WorkspaceModel> combo = new ComboBox<WorkspaceModel>();
                    combo.setId("editRuleWorkspace");
                    combo.setName("editRuleWorkspace");

                    combo.setDisplayField(BeanKeyValue.WORKSPACE.getValue());
                    combo.setStore(workspaces);
                    combo.setEditable(true);
                    combo.setTypeAhead(true);
                    combo.setTriggerAction(TriggerAction.ALL);
//                    workspacesComboBox.setWidth(120);
                    combo.setWidth(COLUMN_WORKSPACE_WIDTH - COLUMN_HEADER_OFFSET);
//                    combo.setAutoWidth(true);

                    if (model.getWorkspace() != null)
                    {
                        combo.setValue(new WorkspaceModel(model.getWorkspace()));
                        combo.setSelection(Arrays.asList(new WorkspaceModel(model.getWorkspace())));
                    }
                    combo.setEmptyText("(No workspace available)");
                    combo.addListener(Events.Select, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                final WorkspaceModel workspace = (WorkspaceModel) be.getField().getValue();

                                model.setWorkspace(workspace.getWorkspace());
                                model.setLayer("*");
                                getAvailableLayers(model.getInstance(), model.getWorkspace(), model.getService());
                                Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
                            }

                            /**
                             * TODO: Call User Service here!!
                             *
                             * @param workspace
                             * @param rule
                             *
                             * @return
                             */
                            private ListStore<Layer> getAvailableLayers(final GSInstanceModel gsInstance,
                                final String workspace, final String service)
                            {
//                                RpcProxy<PagingLoadResult<Layer>> workspacesProxy = new RpcProxy<PagingLoadResult<Layer>>()
//                                    {
//
//                                        @Override
//                                        protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<Layer>> callback)
//                                        {
//                                            workspacesService.getLayers(((PagingLoadConfig) loadConfig).getOffset(), ((PagingLoadConfig) loadConfig).getLimit(),
//                                                (gsInstance != null) ? gsInstance.getBaseURL() : null, gsInstance,
//                                                workspace, service, callback);
//                                        }
//
//                                    };
//
//                                BasePagingLoader<PagingLoadResult<ModelData>> workspacesLoader =
//                                    new BasePagingLoader<PagingLoadResult<ModelData>>(
//                                        workspacesProxy);
//                                workspacesLoader.setRemoteSort(false);
//
//                                ListStore<Layer> availableWorkspaceLayers = new ListStore<Layer>(workspacesLoader);
//
//                                return availableWorkspaceLayers;
                            	
                            	initializeLayers(gsInstance, workspace, service);
                            	
                            	return null;
                            }

                        });

                    return combo;
                }
            };

        return comboRendered;
    }

    /**
     * Creates the workspaces layers combo box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<RuleModel> createLayersRenderer()
    {
        GridCellRenderer<RuleModel> comboRendered = new GridCellRenderer<RuleModel>()
            {

//                private boolean init;

                public Object render(final RuleModel model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<RuleModel> store, Grid<RuleModel> grid)
                {

//                    if (!init)
//                    {
//                        init = true;
//                        grid.addListener(Events.ColumnResize, new ResizeListener(10));
//                    }

                    // TODO: generalize this!
                    ComboBox<Layer> combo = new ComboBox<Layer>();
                    combo.setId("editRuleLayer");
                    combo.setName("editRuleLayer");

                    combo.setDisplayField(BeanKeyValue.LAYER.getValue());
                    combo.setStore(layers);
                    combo.setEditable(true);
                    combo.setTypeAhead(true);
                    combo.setTriggerAction(TriggerAction.ALL);
//                    workspaceLayersComboBox.setWidth(120);
                    combo.setWidth(COLUMN_LAYER_WIDTH - COLUMN_HEADER_OFFSET);
//                    combo.setAutoWidth(true);


                    if (model.getLayer() != null)
                    {
                        combo.setValue(new Layer(model.getLayer()));
                        combo.setSelection(Arrays.asList(new Layer(model.getLayer())));
                    }
                    combo.setEmptyText("(No layer available)");
                    combo.addListener(Events.Select, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                final Layer layer = (Layer) be.getField().getValue();

                                model.setLayer(layer.getLayer());
                                Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
                            }
                        });

                    return combo;
                }
            };

        return comboRendered;
    }

    /**
     * Creates the grants combo box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<RuleModel> createGrantsRenderer()
    {
        GridCellRenderer<RuleModel> comboRendered = new GridCellRenderer<RuleModel>()
            {

//                private boolean init;

                public Object render(final RuleModel model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<RuleModel> store, Grid<RuleModel> grid)
                {

//                    if (!init)
//                    {
//                        init = true;
//    					grid.addListener(Events.ColumnResize, new ResizeListener(10));
//                    }

                    // TODO: generalize this!
                    final ComboBox<Grant> combo = new ComboBox<Grant>();
                    combo.setId("editRuleGrant");
                    combo.setName("editRuleGrant");

                    combo.setDisplayField(BeanKeyValue.GRANT.getValue());
                    combo.setEditable(false);
                    combo.setStore(getAvailableGrants());
                    combo.setTypeAhead(true);
                    combo.setTriggerAction(TriggerAction.ALL);
//                    grantsComboBox.setWidth(70);
                    combo.setWidth(COLUMN_GRANT_WIDTH - COLUMN_HEADER_OFFSET);
//                    combo.setAutoWidth(true);

                    if (model.getGrant() != null)
                    {
                        combo.setValue(new Grant(model.getGrant()));
                        combo.setSelection(Arrays.asList(new Grant(model.getGrant())));
                    }
                    combo.setEmptyText("(No grant types available)");
                    combo.addListener(Events.Select, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                final Grant grant = (Grant) be.getField().getValue();

                                final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>()
                                    {
                                        public void handleEvent(MessageBoxEvent ce)
                                        {
                                            Button btn = ce.getButtonClicked();

                                            if (btn.getText().equalsIgnoreCase("Yes"))
                                            {
                                                model.setGrant(grant.getGrant());
                                                Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO,
                                                    model);

                                                Info.display("MessageBox", "Rule Grant changed", btn.getText());
                                            }
                                            else
                                            {
                                                combo.setValue(new Grant(model.getGrant()));
                                            }
                                        }
                                    };

                                if (!grant.getGrant().equalsIgnoreCase(model.getGrant()) && (model.getId() != -1))
                                {
                                    MessageBox.confirm("Confirm",
                                        "Grant is changed. Saving the rule the details or rule limits will be deleted. Are you sure you want to do that?",
                                        l);
                                }
                                else
                                {
                                    model.setGrant(grant.getGrant());
                                    Dispatcher.forwardEvent(GeofenceEvents.RULE_UPDATE_EDIT_GRID_COMBO, model);
                                }

                            }
                        });

                    return combo;
                }

                /**
                 * TODO: Call User Service here!!
                 *
                 * @param workspace
                 * @param rule
                 *
                 * @return
                 */
                private ListStore<Grant> getAvailableGrants()
                {
                    ListStore<Grant> availableRuleGrants = new ListStore<Grant>();
                    List<Grant> grants = new ArrayList<Grant>();

                    Grant allow = new Grant("ALLOW");
                    Grant deny = new Grant("DENY");
                    Grant limit = new Grant("LIMIT");

                    grants.add(allow);
                    grants.add(deny);
                    grants.add(limit);

                    availableRuleGrants.add(grants);

                    return availableRuleGrants;
                }
            };

        return comboRendered;
    }

    /**
     * Creates the store.
     */
    public void createStore()
    {
        store = new ListStore<RuleModel>();
        if (store != null)
        {
            store.setSortField(BeanKeyValue.PRIORITY.getValue());
            store.setSortDir(SortDir.ASC);
        }

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
     * Clear grid elements.
     */
    public void clearGridElements()
    {
        this.store.removeAll();
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
                    /*
                     * if (!toolBar.isEnabled()) toolBar.enable();
                     */
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
                        Dispatcher.forwardEvent(GeofenceEvents.SEND_ALERT_MESSAGE,
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
     * Sets the grid properties.
     */
    public void setGridProperties()
    {

        grid.setLoadMask(true);
        grid.setAutoWidth(true);
        if (grid.getStore() != null)
        {
            grid.getStore().setSortField(BeanKeyValue.PRIORITY.getValue());
            grid.getStore().setSortDir(SortDir.ASC);
        }

        grid.addListener(Events.ColumnResize, new ResizeListener(COLUMN_HEADER_OFFSET));
    }

    /**
     * Inits the grid.
     */
    public void initGrid()
    {
        ColumnModel cm = prepareColumnModel();

        grid = new Grid<RuleModel>(store, cm);
        grid.setBorders(true);

        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.setHeight("70px");
        grid.setLazyRowRender(0);
        setGridProperties();
    }

    /**
     * Inits the grid.
     *
     * @param store
     *            the store
     */
    public void initGrid(ListStore<RuleModel> store)
    {
        ColumnModel cm = prepareColumnModel();

        grid = new Grid<RuleModel>(store, cm);
        grid.setBorders(true);

        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.setHeight("70px");
        grid.setLazyRowRender(0);
        setGridProperties();
    }

    /**
     * Sets the profile.
     *
     * @param model
     *            the new profile
     */
    public void setModel(RuleModel model)
    {
        this.model = model;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.widget.Component#getModel()
     */
    @SuppressWarnings({ "unchecked" })
    public RuleModel getModel()
    {
        return model;
    }

    /**
     * Sets the instance service.
     *
     * @param instancesManagerServiceRemote
     *            the new instance service
     */
    public void setInstanceService(InstancesManagerRemoteServiceAsync instancesManagerServiceRemote)
    {
        this.instancesService = instancesManagerServiceRemote;
    }

    /**
     * Sets the workspace service.
     *
     * @param workspacesManagerServiceRemote
     *            the new workspace service
     */
    public void setWorkspaceService(
        WorkspacesManagerRemoteServiceAsync workspacesManagerServiceRemote)
    {
        this.workspacesService = workspacesManagerServiceRemote;
    }

    /**
     * Sets the profile service.
     *
     * @param profilesManagerServiceRemote
     *            the new profile service
     */
    public void setProfileService(ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote)
    {
        this.profilesService = profilesManagerServiceRemote;
    }

    /**
     * Sets the gs user service.
     *
     * @param usersManagerServiceRemote
     *            the new gs user service
     */
    public void setGsUserService(GsUsersManagerRemoteServiceAsync usersManagerServiceRemote)
    {
        this.gsUsersService = usersManagerServiceRemote;
    }

    /**
     * Gets the RuleGridWidget.
     *
     * @return the RuleGridWidget
     */
    public Grid<RuleModel> getParentGrid()
    {
        return parentGrid;
    }

    /**
     * Sets the RuleGridWidget.
     *
     * @param parentGrid
     *            the new RuleGridWidget
     */
    public void setParentGrid(Grid<RuleModel> parentGrid)
    {
        this.parentGrid = parentGrid;
    }

    /**
     * Gets the rules view.
     *
     * @return the rules view
     */
    public RulesView getRulesView()
    {
        return rulesView;
    }

    /**
     * Sets the rules view.
     *
     * @param rulesView
     *            the new rules view
     */
    public void setRulesView(RulesView rulesView)
    {
        this.rulesView = rulesView;
    }
    
    /**
	 * @param gsInstance
	 */
	private void initializeWorkspaces(
			final GSInstanceModel gsInstance) {
		workspacesService.getWorkspaces(0, 0, (gsInstance != null) ? gsInstance.getBaseURL() : null, gsInstance,
                new AsyncCallback<PagingLoadResult<WorkspaceModel>>() {

					public void onFailure(Throwable caught) {
						workspaces.removeAll();
					}

					public void onSuccess(PagingLoadResult<WorkspaceModel> result) {
						workspaces.removeAll();
						workspaces.add(result.getData());
						
						initializeLayers(gsInstance, model.getWorkspace(), model.getService());
					}
				});
	}
	
    /**
	 * @param gsInstance
	 * @param workspace
	 * @param service
	 */
	private void initializeLayers(
			final GSInstanceModel gsInstance,
			final String workspace, final String service) {
		workspacesService.getLayers(0, 0, (gsInstance != null) ? gsInstance.getBaseURL() : null, gsInstance, workspace, service, 
    			new AsyncCallback<PagingLoadResult<Layer>>() {

    		public void onFailure(Throwable caught) {
    			layers.removeAll();
    		}

    		public void onSuccess(PagingLoadResult<Layer> result) {
    			layers.removeAll();
    			layers.add(result.getData());
    		}
    	});
	}
}

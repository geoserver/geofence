/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import org.geoserver.geofence.gui.client.widget.GeofenceGridWidget;
import org.geoserver.geofence.gui.client.widget.SearchFilterField;
import org.geoserver.geofence.gui.client.Constants;
import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.Resources;
import org.geoserver.geofence.gui.client.i18n.I18nProvider;
import org.geoserver.geofence.gui.client.model.BeanKeyValue;
import org.geoserver.geofence.gui.client.model.GSInstance;
import org.geoserver.geofence.gui.client.model.GSUser;
import org.geoserver.geofence.gui.client.model.UserGroup;
import org.geoserver.geofence.gui.client.model.Rule;
import org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.InstancesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.RulesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.WorkspacesManagerRemoteServiceAsync;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SortDir;
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
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

// TODO: Auto-generated Javadoc
/**
 * The Class RuleGridWidget.
 *
  *  Note:
 * There's a bug preventing the proper header alignment:
 * http://www.sencha.com/forum/showthread.php?234952-Sencha-Ext-GWT-grid-columns-not-aligned-with-grid-header

 */
public class RuleGridWidget extends GeofenceGridWidget<Rule> {

	private static final int COLUMN_HEADER_OFFSET = 10;


	/** The Constant COLUMN_PRIORITY_WIDTH. */
	private static final int COLUMN_PRIORITY_WIDTH = 30;

	/** The Constant COLUMN_USER_WIDTH. */
	private static final int COLUMN_USER_WIDTH = 100; // 130;

	/** The Constant COLUMN_PROFILE_WIDTH. */
	private static final int COLUMN_GROUP_WIDTH = 80; // 160;

	/** The Constant COLUMN_INSTANCE_WIDTH. */
	private static final int COLUMN_INSTANCE_WIDTH = 150; // 60;

    private static final int COLUMN_IPRANGE_WIDTH = 150;

	/** The Constant COLUMN_SERVICE_WIDTH. */
	private static final int COLUMN_SERVICE_WIDTH = 60; // 100;

	/** The Constant COLUMN_REQUEST_WIDTH. */
	private static final int COLUMN_REQUEST_WIDTH = 150; // 190;

	/** The Constant COLUMN_WORKSPACE_WIDTH. */
	private static final int COLUMN_WORKSPACE_WIDTH = 100; // 130;

	/** The Constant COLUMN_LAYER_WIDTH. */
	private static final int COLUMN_LAYER_WIDTH = 110; // 130;

	/** The Constant COLUMN_GRANT_WIDTH. */
	private static final int COLUMN_GRANT_WIDTH = 80; // 100;

	/** The Constant COLUMN_EDIT_RULE_WIDTH. */
	private static final int COLUMN_EDIT_RULE_WIDTH = 80;

	/** The Constant COLUMN_RULE_DETAILS_WIDTH. */
	private static final int COLUMN_RULE_DETAILS_WIDTH = 80;

	/** The Constant COLUMN_REMOVE_RULE_WIDTH. */
	private static final int COLUMN_REMOVE_RULE_WIDTH = 80;

	/** The Constant COLUMN_ADD_RULE_WIDTH. */
	private static final int COLUMN_ADD_RULE_WIDTH = 30;

	/** The Constant COLUMN_UP_RULE_WIDTH. */
	private static final int COLUMN_UP_RULE_WIDTH = 30;

	/** The Constant COLUMN_DOWN_RULE_WIDTH. */
	private static final int COLUMN_DOWN_RULE_WIDTH = 30;

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

	/** The proxy. */
	private RpcProxy<PagingLoadResult<Rule>> proxy;

	/** The loader. */
	private PagingLoader<PagingLoadResult<ModelData>> loader;

	/** The tool bar. */
	private PagingToolBar toolBar;

	/** The edit rule widget. */
	private EditRuleWidget editRuleWidget;

	/** The parent edit rule widget. */
	public RuleGridWidget parentEditRuleWidget;

	/** The button rendered. */
	private GridCellRenderer<Rule> buttonRendered;

	/**
	 * Instantiates a new rule grid widget.
	 * 
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
	public RuleGridWidget(RulesManagerRemoteServiceAsync rulesService,
			GsUsersManagerRemoteServiceAsync gsUsersService,
			ProfilesManagerRemoteServiceAsync profilesService,
			InstancesManagerRemoteServiceAsync instancesService,
			WorkspacesManagerRemoteServiceAsync workspacesService) {
		super();
		this.rulesService = rulesService;
		this.gsUsersService = gsUsersService;
		this.profilesService = profilesService;
		this.instancesService = instancesService;
		this.workspacesService = workspacesService;
		this.parentEditRuleWidget = this;
	}

	/**
	 * Instantiates a new rule grid widget.
	 * 
	 * @param models
	 *            the models
	 */
	public RuleGridWidget(List<Rule> models) {
		super(models);
		if (this.parentEditRuleWidget == null) {
			this.parentEditRuleWidget = this;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geoserver.geofence.gui.client.widget.GEOFENCEGridWidget#
	 * setGridProperties ()
	 */
	@Override
	public void setGridProperties() {
		grid.setHeight(Constants.SOUTH_PANEL_DIMENSION - 25);

		if (grid.getStore() != null) {
			grid.getStore().setSortField(BeanKeyValue.PRIORITY.getValue());
			grid.getStore().setSortDir(SortDir.ASC);
		}

		grid.addListener(Events.RowDoubleClick,
				new Listener<GridEvent<Rule>>() {
					public void handleEvent(GridEvent<Rule> be) {
						Rule ruleModel = be.getModel();
						Dispatcher.forwardEvent(
								GeofenceEvents.EDIT_RULE_UPDATE,
								new GridStatus(grid, ruleModel));
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geoserver.geofence.gui.client.widget.GEOFENCEGridWidget#
	 * prepareColumnModel()
	 */
	@Override
	public ColumnModel prepareColumnModel() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig rulePriorityColumn = new ColumnConfig();
		rulePriorityColumn.setId(BeanKeyValue.PRIORITY.getValue());
		rulePriorityColumn.setWidth(COLUMN_PRIORITY_WIDTH);
		rulePriorityColumn.setMenuDisabled(false);
		rulePriorityColumn.setSortable(true);
		configs.add(rulePriorityColumn);

		ColumnConfig ruleUserColumn = new ColumnConfig();
		ruleUserColumn.setId(BeanKeyValue.USER.getValue());
		ruleUserColumn.setHeader("User");
		ruleUserColumn.setWidth(COLUMN_USER_WIDTH);
		ruleUserColumn.setRenderer(new UserRenderer());
		ruleUserColumn.setMenuDisabled(true);
		ruleUserColumn.setSortable(false);
		configs.add(ruleUserColumn);

		ColumnConfig ruleProfileColumn = new ColumnConfig();
		ruleProfileColumn.setId(BeanKeyValue.PROFILE.getValue());
		ruleProfileColumn.setHeader("Group");
		ruleProfileColumn.setWidth(COLUMN_GROUP_WIDTH);
		ruleProfileColumn.setRenderer(new GroupRenderer());
		ruleProfileColumn.setMenuDisabled(true);
		ruleProfileColumn.setSortable(false);
		configs.add(ruleProfileColumn);

		ColumnConfig ruleInstanceColumn = new ColumnConfig();
		ruleInstanceColumn.setId(BeanKeyValue.INSTANCE.getValue());
		ruleInstanceColumn.setHeader("Instance");
		ruleInstanceColumn.setWidth(COLUMN_INSTANCE_WIDTH);
		ruleInstanceColumn.setRenderer(new InstanceRenderer());
		ruleInstanceColumn.setMenuDisabled(true);
		ruleInstanceColumn.setSortable(false);
		configs.add(ruleInstanceColumn);


        ColumnConfig ipcfg = new ColumnConfig();
		ipcfg.setId(BeanKeyValue.SOURCE_IP_RANGE.getValue());
		ipcfg.setHeader("Src IP addr");
		ipcfg.setWidth(COLUMN_IPRANGE_WIDTH);
		ipcfg.setRenderer(new IPRangeRenderer());
		ipcfg.setMenuDisabled(true);
		ipcfg.setSortable(false);
		configs.add(ipcfg);

		ColumnConfig ruleServiceColumn = new ColumnConfig();
		ruleServiceColumn.setId(BeanKeyValue.SERVICE.getValue());
		ruleServiceColumn.setHeader("Service");
		ruleServiceColumn.setWidth(COLUMN_SERVICE_WIDTH);
		ruleServiceColumn.setRenderer(new ServiceRenderer());
		ruleServiceColumn.setMenuDisabled(true);
		ruleServiceColumn.setSortable(false);
		configs.add(ruleServiceColumn);

		ColumnConfig ruleRequestColumn = new ColumnConfig();
		ruleRequestColumn.setId(BeanKeyValue.REQUEST.getValue());
		ruleRequestColumn.setHeader("Request");
		ruleRequestColumn.setWidth(COLUMN_REQUEST_WIDTH);
		ruleRequestColumn.setRenderer(new RequestRenderer());
		ruleRequestColumn.setMenuDisabled(true);
		ruleRequestColumn.setSortable(false);
		configs.add(ruleRequestColumn);

		ColumnConfig ruleWorkspacesColumn = new ColumnConfig();
		ruleWorkspacesColumn.setId(BeanKeyValue.WORKSPACE.getValue());
		ruleWorkspacesColumn.setHeader("Workspace");
		ruleWorkspacesColumn.setWidth(COLUMN_WORKSPACE_WIDTH);
		ruleWorkspacesColumn.setRenderer(new WorkspaceRenderer());
		ruleWorkspacesColumn.setMenuDisabled(true);
		ruleWorkspacesColumn.setSortable(false);
		configs.add(ruleWorkspacesColumn);

		ColumnConfig ruleLayersColumn = new ColumnConfig();
		ruleLayersColumn.setId(BeanKeyValue.LAYER.getValue());
		ruleLayersColumn.setHeader("Layer");
		ruleLayersColumn.setWidth(COLUMN_LAYER_WIDTH);
		ruleLayersColumn.setRenderer(new LayerRenderer());
		ruleLayersColumn.setMenuDisabled(true);
		ruleLayersColumn.setSortable(false);
		configs.add(ruleLayersColumn);


		ColumnConfig ruleGrantsColumn = new ColumnConfig();
		ruleGrantsColumn.setId(BeanKeyValue.GRANT.getValue());
		ruleGrantsColumn.setHeader("Grant");
		ruleGrantsColumn.setWidth(COLUMN_GRANT_WIDTH);
		ruleGrantsColumn.setRenderer(this.createGrantsCustomField());
		ruleGrantsColumn.setMenuDisabled(true);
		ruleGrantsColumn.setSortable(false);
		configs.add(ruleGrantsColumn);

		ColumnConfig editRuleColumn = new ColumnConfig();
		editRuleColumn.setId("editRuleDetails");
		editRuleColumn.setWidth(COLUMN_EDIT_RULE_WIDTH);
		editRuleColumn.setRenderer(this.editRuleButton());
		editRuleColumn.setMenuDisabled(true);
		editRuleColumn.setSortable(false);
		configs.add(editRuleColumn);

		ColumnConfig detailsActionColumn = new ColumnConfig();
		detailsActionColumn.setId("ruleDetails");
		detailsActionColumn.setWidth(COLUMN_RULE_DETAILS_WIDTH);
		detailsActionColumn.setRenderer(this.createRuleDetailsButton());
		detailsActionColumn.setMenuDisabled(true);
		detailsActionColumn.setSortable(false);
		configs.add(detailsActionColumn);

		ColumnConfig removeActionColumn = new ColumnConfig();
		removeActionColumn.setId("removeRule");
		removeActionColumn.setWidth(COLUMN_REMOVE_RULE_WIDTH);
		removeActionColumn.setRenderer(this.createRuleDeleteButton());
		removeActionColumn.setMenuDisabled(true);
		removeActionColumn.setSortable(false);
		configs.add(removeActionColumn);

		ColumnConfig addActionColumn = new ColumnConfig();
		addActionColumn.setId("addRule");
		addActionColumn.setWidth(COLUMN_ADD_RULE_WIDTH);
		addActionColumn.setRenderer(this.createRuleAddButton());
		addActionColumn.setMenuDisabled(true);
		addActionColumn.setSortable(false);
		configs.add(addActionColumn);

		ColumnConfig priorityUpActionColumn = new ColumnConfig();
		priorityUpActionColumn.setId("rulePriorityUp");
		priorityUpActionColumn.setWidth(COLUMN_UP_RULE_WIDTH);
		priorityUpActionColumn.setRenderer(this.createRulePriorityUpButton());
		priorityUpActionColumn.setMenuDisabled(true);
		priorityUpActionColumn.setSortable(false);
		configs.add(priorityUpActionColumn);

		ColumnConfig priorityDownActionColumn = new ColumnConfig();
		priorityDownActionColumn.setId("rulePriorityDwn");
		priorityDownActionColumn.setWidth(COLUMN_DOWN_RULE_WIDTH);
		priorityDownActionColumn.setRenderer(this
				.createRulePriorityDownButton());
		priorityDownActionColumn.setMenuDisabled(true);
		priorityDownActionColumn.setSortable(false);
		configs.add(priorityDownActionColumn);

		return new ColumnModel(configs);
	}


    class ResizeListener implements Listener<GridEvent<Rule>> {
        private final int offset;

        public ResizeListener(int offset) {
            this.offset = offset;
        }

        public void handleEvent(GridEvent<Rule> be) {
            for (int i = 0; i < be.getGrid().getStore().getCount(); i++) {
                final Widget wid = be.getGrid().getView().getWidget(i, be.getColIndex());
                if ((wid != null) && (wid instanceof BoxComponent)) {
                    ((BoxComponent) wid).setWidth(be.getWidth() - offset);
                }
            }
        }
    }



    abstract class FieldRenderer implements GridCellRenderer<Rule> {

        private boolean init;

        private final String fieldId;
        private final int initialWidth;

        public FieldRenderer(String fieldId, int initialWidth) {
            this.fieldId = fieldId;
            this.initialWidth = initialWidth;
        }

        public Object render(final Rule model, String property,
                ColumnData config, int rowIndex, int colIndex,
                ListStore<Rule> store, Grid<Rule> grid) {

            if (!init) {
                init = true;
                grid.addListener(Events.ColumnResize, new ResizeListener(20));
            }

            LabelField field = new LabelField();
            field.setId(fieldId);
            field.setName(fieldId);
            field.setEmptyText("*");
            field.setFieldLabel(BeanKeyValue.NAME.getValue());
            field.setValue(BeanKeyValue.NAME.getValue());
            field.setReadOnly(true);

//            field.setWidth(initialWidth - 10);
            field.setAutoWidth(true);
            field.show();

            setFieldValue(model, field);
            
            return field;
        }

        abstract void setFieldValue(Rule model, LabelField field);
        
    }

    class UserRenderer extends FieldRenderer {
        public UserRenderer() {
            super("viewRuleUser", COLUMN_USER_WIDTH);
        }

        void setFieldValue(Rule model, LabelField field) {
            if ((model.getUser() != null)
                    && (model.getUser().getName() != null)) {
                String name2 = model.getUser().getName();
                field.setValue(name2);
            } else {
                field.setValue("*");
            }
        }
    }

    class GroupRenderer extends FieldRenderer {
        public GroupRenderer() {
            super("viewRuleGroup", COLUMN_GROUP_WIDTH);
        }

        void setFieldValue(Rule model, LabelField field) {
            if ((model.getProfile() != null)
                    && (model.getProfile().getName() != null)) {
                String name2 = model.getProfile().getName();
                field.setValue(name2);
            } else {
                field.setValue("*");
            }
        }
    }

    class InstanceRenderer extends FieldRenderer {
        public InstanceRenderer() {
            super("viewRuleInstance", COLUMN_INSTANCE_WIDTH);
        }

        void setFieldValue(Rule model, LabelField field) {
            if ((model.getInstance() != null)
                    && (model.getInstance().getName() != null)) {
                field.setValue(model.getInstance().getName());
            } else {
                field.setValue("*");
            }
        }
    }

    class IPRangeRenderer extends FieldRenderer {
        public IPRangeRenderer() {
            super("viewRuleIPRange", COLUMN_IPRANGE_WIDTH);
        }

        void setFieldValue(Rule model, LabelField field) {
            if (model.getSourceIPRange() != null) {
                field.setValue(model.getSourceIPRange());
            } else {
                field.setValue("*");
            }
        }
    }

    class ServiceRenderer extends FieldRenderer {
        public ServiceRenderer() {
            super("viewRuleService", COLUMN_SERVICE_WIDTH);
        }

        void setFieldValue(Rule model, LabelField field) {
            if (model.getService() != null) {
                field.setValue(model.getService());
            } else {
                field.setValue("*");
            }
        }
    }

    class RequestRenderer extends FieldRenderer {
        public RequestRenderer() {
            super("viewRuleRequest", COLUMN_REQUEST_WIDTH);
        }

        void setFieldValue(Rule model, LabelField field) {
            if (model.getRequest() != null) {
                field.setValue(model.getRequest());
            } else {
                field.setValue("*");
            }
        }
    }

    class WorkspaceRenderer extends FieldRenderer {
        public WorkspaceRenderer() {
            super("viewRuleWorkspace", COLUMN_WORKSPACE_WIDTH);
        }

        void setFieldValue(Rule model, LabelField field) {
            if (model.getWorkspace() != null) {
                field.setValue(model.getWorkspace());
            } else {
                field.setValue("*");
            }
        }
    }

    class LayerRenderer extends FieldRenderer {
        public LayerRenderer() {
            super("viewRuleLayer", COLUMN_LAYER_WIDTH);
        }

        void setFieldValue(Rule model, LabelField field) {
            if (model.getLayer() != null) {
                field.setValue(model.getLayer());
            } else {
                field.setValue("*");
            }
        }
    }



	/**
	 * Creates the grants custom field.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> createGrantsCustomField() {
		GridCellRenderer<Rule> comboRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				LabelField field = new LabelField();
				field.setId("grantsCombo");
				field.setName("grantsCombo");

				field.setFieldLabel(BeanKeyValue.GRANT.getValue());
				field.setReadOnly(false);
				field.setWidth(COLUMN_GRANT_WIDTH - 10);

				if (model.getGrant() != null) {
					field.setValue(model.getGrant());
				} else {
					field.setValue("ALLOW");
				}

				field.setEmptyText("(No grant available)");

				return field;
			}

		};

		return comboRendered;
	}

	/**
	 * Creates the rule delete button.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> createRuleDeleteButton() {
		GridCellRenderer<Rule> buttonRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				Button removeRuleButton = new Button("Remove");
				removeRuleButton.setIcon(Resources.ICONS.delete());
				removeRuleButton.setEnabled(true);

				removeRuleButton.addListener(Events.OnClick,
						new Listener<ButtonEvent>() {

							public void handleEvent(ButtonEvent be) {

								final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
									public void handleEvent(MessageBoxEvent ce) {
										Button btn = ce.getButtonClicked();

										if (btn.getText().equalsIgnoreCase(
												"Yes")) {
											Dispatcher.forwardEvent(
													GeofenceEvents.RULE_DEL,
													model);
											Dispatcher
													.forwardEvent(
															GeofenceEvents.SEND_INFO_MESSAGE,
															new String[] {
																	"GeoServer Rules",
																	"Remove Rule #"
																			+ model.getPriority() });
										}
									}
								};

								MessageBox
										.confirm(
												"Confirm",
												"The Rule will be deleted. Are you sure you want to do that?",
												l);
							}
						});

				return removeRuleButton;
			}

		};

		return buttonRendered;
	}

	/**
	 * Edits the rule button.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> editRuleButton() {
		buttonRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, final Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				Button ruleDetailsButton = new Button("Edit rule");
				ruleDetailsButton.setIcon(Resources.ICONS.table());
				// TODO: add correct tooltip text here!
				ruleDetailsButton.setToolTip("Edit this rule");
				ruleDetailsButton.setEnabled(true);

				ruleDetailsButton.addListener(Events.OnClick,
						new Listener<ButtonEvent>() {

							public void handleEvent(ButtonEvent be) {
								Dispatcher.forwardEvent(
										GeofenceEvents.EDIT_RULE_UPDATE,
										new GridStatus(grid, model));
							}
						});

				return ruleDetailsButton;
			}

		};

		return buttonRendered;
	}

	/**
	 * Creates the rule details button.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> createRuleDetailsButton() {
		GridCellRenderer<Rule> buttonRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				Button ruleDetailsButton = new Button("Details");
				ruleDetailsButton.setIcon(Resources.ICONS.table());
				ruleDetailsButton.setToolTip("Show Rule Details");

				if (!model.getLayer().equalsIgnoreCase("*")
						&& model.getGrant().equalsIgnoreCase("ALLOW")) {
					ruleDetailsButton.setEnabled(true);
				} else if (model.getGrant().equalsIgnoreCase("LIMIT")) {
					ruleDetailsButton.setEnabled(true);
				} else {
					ruleDetailsButton.setEnabled(false);
				}

				ruleDetailsButton.addListener(Events.OnClick,
						new Listener<ButtonEvent>() {

							public void handleEvent(ButtonEvent be) {
								Dispatcher
										.forwardEvent(
												GeofenceEvents.EDIT_RULE_DETAILS,
												model);
							}
						});

				return ruleDetailsButton;
			}

		};

		return buttonRendered;
	}

	/**
	 * Creates the rule add button.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> createRuleAddButton() {
		GridCellRenderer<Rule> buttonRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, final Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				Button ruleAddButton = new Button();
				ruleAddButton.setBorders(false);
				ruleAddButton.setIcon(Resources.ICONS.add());
				// TODO: add correct tooltip text here!
				ruleAddButton.setEnabled(true);

				ruleAddButton.addListener(Events.OnClick,
						new Listener<ButtonEvent>() {

							public void handleEvent(ButtonEvent be) {
								Rule new_rule = Constants.getInstance()
										.createNewRule(model);
								Dispatcher.forwardEvent(
										GeofenceEvents.EDIT_RULE,
										new GridStatus(grid, new_rule));
							}
						});

				return ruleAddButton;
			}

		};

		return buttonRendered;
	}

	/**
	 * Creates the rule priority up button.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> createRulePriorityUpButton() {
		GridCellRenderer<Rule> buttonRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				Button priorityUpButton = new Button();
				priorityUpButton.setBorders(false);
				priorityUpButton.setIcon(Resources.ICONS.arrowUp());
				// TODO: add correct tooltip text here!
				priorityUpButton.setEnabled(true);

				priorityUpButton.addListener(Events.OnClick,
						new Listener<ButtonEvent>() {

							public void handleEvent(ButtonEvent be) {
								Dispatcher
										.forwardEvent(
												GeofenceEvents.SEND_INFO_MESSAGE,
												new String[] {
														"GeoServer Rules",
														"Selected Rule #"
																+ model.getPriority() });

								Dispatcher.forwardEvent(
										GeofenceEvents.RULE_PRIORITY_UP, model);
							}
						});

				return priorityUpButton;
			}

		};

		return buttonRendered;
	}

	/**
	 * Creates the rule priority down button.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<Rule> createRulePriorityDownButton() {
		GridCellRenderer<Rule> buttonRendered = new GridCellRenderer<Rule>() {

			private boolean init;

			public Object render(final Rule model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Rule> store, Grid<Rule> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<Rule>>() {

								public void handleEvent(GridEvent<Rule> be) {
									for (int i = 0; i < be.getGrid().getStore()
											.getCount(); i++) {
										if ((be.getGrid().getView()
												.getWidget(i, be.getColIndex()) != null)
												&& (be.getGrid()
														.getView()
														.getWidget(
																i,
																be.getColIndex()) instanceof BoxComponent)) {
											((BoxComponent) be
													.getGrid()
													.getView()
													.getWidget(i,
															be.getColIndex()))
													.setWidth(be.getWidth() - 10);
										}
									}
								}
							});
				}

				// TODO: generalize this!
				Button priorityDownButton = new Button();
				priorityDownButton.setBorders(false);
				priorityDownButton.setIcon(Resources.ICONS.arrowDown());
				// TODO: add correct tooltip text here!
				priorityDownButton.setEnabled(true);

				priorityDownButton.addListener(Events.OnClick,
						new Listener<ButtonEvent>() {

							public void handleEvent(ButtonEvent be) {
								Dispatcher
										.forwardEvent(
												GeofenceEvents.SEND_INFO_MESSAGE,
												new String[] {
														"GeoServer Rules",
														"Selected Rule #"
																+ model.getPriority() });

								Dispatcher.forwardEvent(
										GeofenceEvents.RULE_PRIORITY_DOWN,
										model);
							}
						});

				return priorityDownButton;
			}

		};

		return buttonRendered;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.geoserver.geofence.gui.client.widget.GEOFENCEGridWidget#createStore
	 * ()
	 */
	@Override
	public void createStore() {
		this.toolBar = new PagingToolBar(
				org.geoserver.geofence.gui.client.Constants.DEFAULT_PAGESIZE);

		// Loader for rulesService
		this.proxy = new RpcProxy<PagingLoadResult<Rule>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<Rule>> callback) {
				rulesService.getRules(
						((PagingLoadConfig) loadConfig).getOffset(),
						((PagingLoadConfig) loadConfig).getLimit(), false,
						callback);
			}

		};
		loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
		loader.setRemoteSort(false);
		loader.setSortField(BeanKeyValue.PRIORITY.getValue());
		loader.setSortDir(SortDir.ASC);
		store = new ListStore<Rule>(loader);

		if (store != null) {
			store.setSortField(BeanKeyValue.PRIORITY.getValue());
			store.setSortDir(SortDir.ASC);
		}

		// Search tool
		SearchFilterField<Rule> filter = new SearchFilterField<Rule>() {

			@Override
			protected boolean doSelect(Store<Rule> store, Rule parent,
					Rule record, String property, String filter) {

				String field = ((GSUser) parent.get(BeanKeyValue.USER
						.getValue())).getName();
				field = field.toLowerCase();
				if (field.indexOf(filter.toLowerCase()) != -1) {
					return true;
				}

				field = ((UserGroup) parent
						.get(BeanKeyValue.PROFILE.getValue())).getName();
				field = field.toLowerCase();
				if (field.indexOf(filter.toLowerCase()) != -1) {
					return true;
				}

				field = ((GSInstance) parent.get(BeanKeyValue.INSTANCE
						.getValue())).getName();
				field = field.toLowerCase();
				if (field.indexOf(filter.toLowerCase()) != -1) {
					return true;
				}

				field = parent.get(BeanKeyValue.SERVICE.getValue());
				field = field.toLowerCase();
				if (field.indexOf(filter.toLowerCase()) != -1) {
					return true;
				}

				field = parent.get(BeanKeyValue.REQUEST.getValue());
				field = field.toLowerCase();
				if (field.indexOf(filter.toLowerCase()) != -1) {
					return true;
				}

				field = parent.get(BeanKeyValue.WORKSPACE.getValue());
				field = field.toLowerCase();
				if (field.indexOf(filter.toLowerCase()) != -1) {
					return true;
				}

				field = parent.get(BeanKeyValue.LAYER.getValue());
				field = field.toLowerCase();
				if (field.indexOf(filter.toLowerCase()) != -1) {
					return true;
				}

				field = parent.get(BeanKeyValue.GRANT.getValue());
				field = field.toLowerCase();
				if (field.indexOf(filter.toLowerCase()) != -1) {
					return true;
				}

				return false;
			}

		};

		filter.setWidth(130);
		filter.setIcon(Resources.ICONS.search());

		// Bind the filter field to your grid store (grid.getStore())
		filter.bind(store);

		// Apply Changes button
		// TODO: generalize this!
		Button addRuleButton = new Button("Add Rule");
		addRuleButton.setIcon(Resources.ICONS.add());

		addRuleButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {

			public void handleEvent(ButtonEvent be) {
				Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
						new String[] { "GeoServer Rules", "Add Rule" });

				Rule new_rule = new Rule();
				new_rule.setId(-1);
				new_rule.setPriority(-1);
				new_rule = Constants.getInstance().createNewRule(new_rule);
				Dispatcher.forwardEvent(GeofenceEvents.EDIT_RULE,
						new GridStatus(grid, new_rule));

			}
		});

		this.toolBar.bind(loader);
		this.toolBar.add(new SeparatorToolItem());
		this.toolBar.add(addRuleButton);
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
	public PagingLoader<PagingLoadResult<ModelData>> getLoader() {
		return loader;
	}

	/**
	 * Gets the tool bar.
	 * 
	 * @return the tool bar
	 */
	public PagingToolBar getToolBar() {
		return toolBar;
	}

	/**
	 * Clear grid elements.
	 */
	public void clearGridElements() {
		this.store.removeAll();
		this.toolBar.clear();
		this.toolBar.disable();
	}

	/**
	 * Sets the up load listener.
	 */
	private void setUpLoadListener() {
		loader.addLoadListener(new LoadListener() {

			@Override
			public void loaderBeforeLoad(LoadEvent le) {
				if (!toolBar.isEnabled()) {
					toolBar.enable();
				}
			}

			@Override
			public void loaderLoad(LoadEvent le) {

				// TODO: change messages here!!

				BasePagingLoadResult<?> result = le.getData();
				if (!result.getData().isEmpty()) {
					int size = result.getData().size();
					String message = "";
					if (size == 1) {
						message = I18nProvider.getMessages().recordLabel();
					} else {
						message = I18nProvider.getMessages()
								.recordPluralLabel();
					}
					Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
							new String[] {
									I18nProvider.getMessages()
											.remoteServiceName(),
									I18nProvider.getMessages().foundLabel()
											+ " " + result.getData().size()
											+ " " + message });
				} else {
					Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
							new String[] {
									I18nProvider.getMessages()
											.remoteServiceName(),
									I18nProvider.getMessages()
											.recordNotFoundMessage() });
				}
			}

		});
	}

}

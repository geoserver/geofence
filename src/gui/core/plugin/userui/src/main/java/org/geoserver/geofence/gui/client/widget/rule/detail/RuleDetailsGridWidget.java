/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.rule.detail;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
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

import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.i18n.I18nProvider;
import org.geoserver.geofence.gui.client.model.BeanKeyValue;
import org.geoserver.geofence.gui.client.model.Rule;
import org.geoserver.geofence.gui.client.model.data.LayerStyle;
import org.geoserver.geofence.gui.client.service.WorkspacesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.widget.GeofenceGridWidget;

// TODO: Auto-generated Javadoc
/**
 * The Class RuleDetailsGridWidget.
 */
public class RuleDetailsGridWidget extends GeofenceGridWidget<LayerStyle> {

	/** The rule. */
	private Rule theRule;

	/** The workspaces service. */
	private WorkspacesManagerRemoteServiceAsync workspacesService;

	/** The rule details widget. */
	private RuleDetailsWidget ruleDetailsWidget;

	/** The proxy. */
	private RpcProxy<List<LayerStyle>> proxy;

	/** The loader. */
	private BaseListLoader<ListLoadResult<ModelData>> loader;

	/**
	 * Instantiates a new rule details grid widget.
	 * 
	 * @param model
	 *            the model
	 * @param workspacesService
	 *            the workspaces service
	 * @param ruleDetailsWidget
	 *            the rule details widget
	 */
	public RuleDetailsGridWidget(Rule model,
			WorkspacesManagerRemoteServiceAsync workspacesService,
			RuleDetailsWidget ruleDetailsWidget) {
		super();
		this.theRule = model;
		this.workspacesService = workspacesService;
		this.ruleDetailsWidget = ruleDetailsWidget;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geoserver.geofence.gui.client.widget.GEOFENCEGridWidget#
	 * setGridProperties ()
	 */
	@Override
	public void setGridProperties() {
		grid.setLoadMask(true);
		grid.setAutoWidth(true);
		grid.setHeight(300);
		grid.setWidth(650);
	}

	/**
	 * Clear grid elements.
	 */
	public void clearGridElements() {
		this.store.removeAll();
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

		// /////////////////////////////
		// Loader for rulesService
		// /////////////////////////////

		this.proxy = new RpcProxy<List<LayerStyle>>() {
			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<LayerStyle>> callback) {
				workspacesService.getStyles(theRule, callback);
			}
		};

		loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy);
		loader.setRemoteSort(false);
		store = new ListStore<LayerStyle>(loader);
		store.sort(BeanKeyValue.STYLES_COMBO.getValue(), SortDir.ASC);

		setUpLoadListener();
	}

	/**
	 * Sets the up load listener.
	 */
	private void setUpLoadListener() {
		loader.addLoadListener(new LoadListener() {

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geoserver.geofence.gui.client.widget.GeofenceGridWidget#
	 * prepareColumnModel()
	 */
	@Override
	public ColumnModel prepareColumnModel() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig attributeStyleColumn = new ColumnConfig();
		attributeStyleColumn.setId(BeanKeyValue.STYLES_COMBO.getValue());
		attributeStyleColumn.setHeader("Name");
		attributeStyleColumn.setWidth(180);
		attributeStyleColumn.setRenderer(this.createStyleTextBox());
		attributeStyleColumn.setMenuDisabled(true);
		attributeStyleColumn.setSortable(false);

		configs.add(attributeStyleColumn);

		ColumnConfig attributeEnableColumn = new ColumnConfig();
		attributeEnableColumn.setId(BeanKeyValue.STYLE_ENABLED.getValue());
		attributeEnableColumn.setHeader("Enable");
		attributeEnableColumn.setWidth(180);
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
	private GridCellRenderer<LayerStyle> createStyleTextBox() {

		GridCellRenderer<LayerStyle> textRendered = new GridCellRenderer<LayerStyle>() {

			private boolean init;

			public Object render(final LayerStyle model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<LayerStyle> store, Grid<LayerStyle> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<LayerStyle>>() {

								public void handleEvent(GridEvent<LayerStyle> be) {
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

				LabelField styleName = new LabelField();
				styleName.setWidth(150);
				styleName.setReadOnly(true);
				styleName.setValue(model.getStyle());

				return styleName;
			}
		};

		return textRendered;
	}

	/**
	 * Creates the enable check box.
	 * 
	 * @return the grid cell renderer
	 */
	private GridCellRenderer<LayerStyle> createEnableCheckBox() {

		GridCellRenderer<LayerStyle> textRendered = new GridCellRenderer<LayerStyle>() {

			private boolean init;

			public Object render(final LayerStyle model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<LayerStyle> store, Grid<LayerStyle> grid) {

				if (!init) {
					init = true;
					grid.addListener(Events.ColumnResize,
							new Listener<GridEvent<LayerStyle>>() {

								public void handleEvent(GridEvent<LayerStyle> be) {
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

				CheckBox available = new CheckBox();
				available.setValue(model.isEnabled());

				available.addListener(Events.Change,
						new Listener<FieldEvent>() {

							public void handleEvent(FieldEvent be) {
								Boolean enable = (Boolean) be.getField()
										.getValue();

								if (enable.booleanValue()) {
									Dispatcher.forwardEvent(
											GeofenceEvents.SEND_INFO_MESSAGE,
											new String[] {
													"Layer Style",
													"Style " + model.getStyle()
															+ ": enabled" });

									model.setEnabled(enable);

									ruleDetailsWidget.enableSaveButton();

								} else {
									Dispatcher.forwardEvent(
											GeofenceEvents.SEND_INFO_MESSAGE,
											new String[] {
													"Layer Style",
													"Style " + model.getStyle()
															+ ": disabled" });

									model.setEnabled(enable);

									ruleDetailsWidget.enableSaveButton();
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
	public BaseListLoader<ListLoadResult<ModelData>> getLoader() {
		return loader;
	}

}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.rule.detail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.ComponentManager;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.Resources;
import org.geoserver.geofence.gui.client.i18n.I18nProvider;
import org.geoserver.geofence.gui.client.model.BeanKeyValue;
import org.geoserver.geofence.gui.client.model.Rule;
import org.geoserver.geofence.gui.client.model.data.AccessType;
import org.geoserver.geofence.gui.client.model.data.LayerAttribUI;
import org.geoserver.geofence.gui.client.service.RulesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.widget.GeofenceGridWidget;


/**
 * The Class LayerAttributesGridWidget.
 */
public class LayerAttributesGridWidget extends GeofenceGridWidget<LayerAttribUI>
{

    /** The rule. */
    private Rule theRule;

    /** The rules service. */
    private RulesManagerRemoteServiceAsync rulesService;

    /** The proxy. */
    private RpcProxy<List<LayerAttribUI>> proxy;

    /** The loader. */
    private BaseListLoader<ListLoadResult<ModelData>> loader;

    /** The tool bar. */
    private ToolBar toolBar;

    /** The save rule attributes button. */
    private Button saveRuleAttributesButton;

    /** The cancel rule attributes button. */
    private Button cancelButton;

    /**
     * Instantiates a new layer attributes grid widget.
     *
     * @param model
     *            the model
     * @param rulesService
     *            the rules service
     */
    public LayerAttributesGridWidget(Rule model, RulesManagerRemoteServiceAsync rulesService)
    {
        super();
        this.theRule = model;
        this.rulesService = rulesService;
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
    }

    /**
     * Clear grid elements.
     */
    public void clearGridElements()
    {
        this.store.removeAll();
        this.saveRuleAttributesButton.disable();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.widget.GEOFENCEGridWidget#createStore()
     */
    @Override
    public void createStore()
    {
        this.toolBar = new ToolBar();

        // /////////////////////////////
        // Loader for rulesService
        // /////////////////////////////

        this.proxy = new RpcProxy<List<LayerAttribUI>>()
            {
                @Override
                protected void load(Object loadConfig, AsyncCallback<List<LayerAttribUI>> callback)
                {
                    rulesService.getLayerAttributes(theRule, callback);
                }
            };

        loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy);
        loader.setRemoteSort(false);
        store = new ListStore<LayerAttribUI>(loader);
        store.sort(BeanKeyValue.ATTR_NAME.getValue(), SortDir.ASC);

        this.saveRuleAttributesButton = new Button("Save");
        saveRuleAttributesButton.setIcon(Resources.ICONS.save());
        saveRuleAttributesButton.disable();

        saveRuleAttributesButton.addListener(Events.OnClick, new Listener<ButtonEvent>()
            {

                public void handleEvent(ButtonEvent be)
                {
                    int storeCount = store.getCount();

                    if (storeCount > 0)
                    {
                        Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                            new String[] { "GeoServer Rules: Layer Attributes", "Apply Changes" });

                        Dispatcher.forwardEvent(GeofenceEvents.ATTRIBUTE_UPDATE_GRID_COMBO, theRule.getId());
                    }
                    else
                    {
                        Dispatcher.forwardEvent(GeofenceEvents.SEND_ALERT_MESSAGE,
                            new String[]
                            {
                                "GeoServer Rules: Layer Attributes",
                                "No attribute to save!"
                            });
                    }

                }
            });

        cancelButton = new Button("Cancel");
        cancelButton.addListener(Events.OnClick, new Listener<ButtonEvent>()
            {
                public void handleEvent(ButtonEvent be)
                {
                    // /////////////////////////////////////////////////////////
                    // Getting the Rule details edit dialogs and hiding this
                    // /////////////////////////////////////////////////////////
                    ComponentManager.get().get(I18nProvider.getMessages().ruleDialogId()).hide();
                }
            });

        this.toolBar.add(new FillToolItem());
        this.toolBar.add(saveRuleAttributesButton);
        this.toolBar.add(cancelButton);

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

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.widget.GeofenceGridWidget#prepareColumnModel()
     */
    @Override
    public ColumnModel prepareColumnModel()
    {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig attributeNameColumn = new ColumnConfig();
        attributeNameColumn.setId(BeanKeyValue.ATTR_NAME.getValue());
        attributeNameColumn.setHeader("Name");
        attributeNameColumn.setWidth(180);
        attributeNameColumn.setRenderer(this.createNameTextBox());
        attributeNameColumn.setMenuDisabled(true);
        attributeNameColumn.setSortable(false);

        configs.add(attributeNameColumn);

        ColumnConfig attributeTypeColumn = new ColumnConfig();
        attributeTypeColumn.setId(BeanKeyValue.ATTR_TYPE.getValue());
        attributeTypeColumn.setHeader("Data Type");
        attributeTypeColumn.setWidth(180);
        attributeTypeColumn.setRenderer(this.createTypeTextBox());
        attributeTypeColumn.setMenuDisabled(true);
        attributeTypeColumn.setSortable(false);
        configs.add(attributeTypeColumn);

        ColumnConfig attributeAccessColumn = new ColumnConfig();
        attributeAccessColumn.setId(BeanKeyValue.ATTR_ACCESS.getValue());
        attributeAccessColumn.setHeader("Access Type");
        attributeAccessColumn.setWidth(180);
        attributeAccessColumn.setRenderer(this.createAccessTypeComboBox());
        attributeAccessColumn.setMenuDisabled(true);
        attributeAccessColumn.setSortable(false);
        configs.add(attributeAccessColumn);

        return new ColumnModel(configs);
    }

    /**
     * Creates the name text box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<LayerAttribUI> createNameTextBox()
    {

        GridCellRenderer<LayerAttribUI> textRendered = new GridCellRenderer<LayerAttribUI>()
            {

                private boolean init;

                public Object render(final LayerAttribUI model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<LayerAttribUI> store, Grid<LayerAttribUI> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<LayerAttribUI>>()
                            {

                                public void handleEvent(GridEvent<LayerAttribUI> be)
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

                    LabelField attrName = new LabelField();
                    attrName.setWidth(150);
                    attrName.setReadOnly(true);
                    attrName.setValue(model.getName());

                    return attrName;
                }
            };

        return textRendered;
    }

    /**
     * Creates the type text box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<LayerAttribUI> createTypeTextBox()
    {

        GridCellRenderer<LayerAttribUI> textRendered = new GridCellRenderer<LayerAttribUI>()
            {

                private boolean init;

                public Object render(final LayerAttribUI model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<LayerAttribUI> store, Grid<LayerAttribUI> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<LayerAttribUI>>()
                            {

                                public void handleEvent(GridEvent<LayerAttribUI> be)
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

                    LabelField attrType = new LabelField();
                    attrType.setWidth(150);
                    attrType.setReadOnly(true);
                    attrType.setValue(model.getDataType());

                    return attrType;
                }
            };

        return textRendered;
    }

    /**
     * Creates the access type combo box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<LayerAttribUI> createAccessTypeComboBox()
    {

        GridCellRenderer<LayerAttribUI> comboRendered = new GridCellRenderer<LayerAttribUI>()
            {

                private boolean init;

                public Object render(final LayerAttribUI model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<LayerAttribUI> store, Grid<LayerAttribUI> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<LayerAttribUI>>()
                            {

                                public void handleEvent(GridEvent<LayerAttribUI> be)
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

                    ComboBox<AccessType> typeComboBox = new ComboBox<AccessType>();
                    typeComboBox.setId("accessCombo");
                    typeComboBox.setName("accessCombo");
                    typeComboBox.setDisplayField(BeanKeyValue.ATTR_ACCESS.getValue());
                    typeComboBox.setEditable(false);
                    typeComboBox.setStore(getAvailableAccessType());
                    typeComboBox.setTypeAhead(true);
                    typeComboBox.setTriggerAction(TriggerAction.ALL);
                    typeComboBox.setWidth(100);

                    if (model.getAccessType() != null)
                    {
                        typeComboBox.setValue(new AccessType(model.getAccessType()));
                        typeComboBox.setSelection(Arrays.asList(new AccessType(model.getAccessType())));
                    }

                    typeComboBox.setEmptyText("(No access types available)");
                    typeComboBox.addListener(Events.Select, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                final AccessType accessType = (AccessType) be.getField().getValue();
                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                    new String[]
                                    {
                                        "Attribute Access Type",
                                        "Attribute " + model.getName() + ": Attribute access type changed"
                                    });

                                model.setAccessType(accessType.getType());
                                saveRuleAttributesButton.enable();
                            }
                        });

                    return typeComboBox;
                }

                /**
                 * TODO: Call User Service here!!
                 *
                 * @param workspace
                 * @param rule
                 *
                 * @return ListStore<AccessType>
                 */
                private ListStore<AccessType> getAvailableAccessType()
                {
                    ListStore<AccessType> availableAccessTypes = new ListStore<AccessType>();
                    List<AccessType> accessType = new ArrayList<AccessType>();

                    AccessType none = new AccessType("NONE");
                    AccessType readonly = new AccessType("READONLY");
                    AccessType readwrite = new AccessType("READWRITE");

                    accessType.add(none);
                    accessType.add(readonly);
                    accessType.add(readwrite);

                    availableAccessTypes.add(accessType);

                    return availableAccessTypes;
                }
            };

        return comboRendered;
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

    /**
     * Gets the tool bar.
     *
     * @return the tool bar
     */
    public ToolBar getToolBar()
    {
        return toolBar;
    }
}

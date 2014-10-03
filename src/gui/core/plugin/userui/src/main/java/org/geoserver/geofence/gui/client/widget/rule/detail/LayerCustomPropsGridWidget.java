/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.rule.detail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.ComponentManager;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.Resources;
import org.geoserver.geofence.gui.client.i18n.I18nProvider;
import org.geoserver.geofence.gui.client.model.BeanKeyValue;
import org.geoserver.geofence.gui.client.model.Rule;
import org.geoserver.geofence.gui.client.model.data.LayerCustomProps;
import org.geoserver.geofence.gui.client.service.RulesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.widget.GeofenceGridWidget;


// TODO: Auto-generated Javadoc
/**
 * The Class LayerCustomPropsGridWidget.
 */
public class LayerCustomPropsGridWidget extends GeofenceGridWidget<LayerCustomProps>
{

    /** The rules service. */
    private RulesManagerRemoteServiceAsync rulesService;

    /** The proxy. */
    private RpcProxy<PagingLoadResult<LayerCustomProps>> proxy;

    /** The loader. */
    private PagingLoader<PagingLoadResult<ModelData>> loader;

    /** The tool bar. */
    private ToolBar toolBar;

    /** The rule. */
    private Rule theRule;


    /**
     * Instantiates a new layer custom props grid widget.
     *
     * @param model
     *            the model
     * @param rulesService
     *            the rules service
     */
    public LayerCustomPropsGridWidget(Rule model, RulesManagerRemoteServiceAsync rulesService)
    {
        super();
        this.theRule = model;
        this.rulesService = rulesService;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.widget.GEOFENCEGridWidget#setGridProperties ()
     */
    @Override
    public void setGridProperties()
    {
        grid.setLoadMask(true);
        grid.setAutoWidth(true);
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.widget.GeofenceGridWidget#prepareColumnModel()
     */
    @Override
    public ColumnModel prepareColumnModel()
    {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig layerPropKeyColumn = new ColumnConfig();
        layerPropKeyColumn.setId(BeanKeyValue.PROP_KEY.getValue());
        layerPropKeyColumn.setHeader("Key");
        layerPropKeyColumn.setWidth(210);
        layerPropKeyColumn.setRenderer(this.createPropKeyTextBox());
        layerPropKeyColumn.setMenuDisabled(true);
        layerPropKeyColumn.setSortable(false);
        configs.add(layerPropKeyColumn);

        ColumnConfig layerPropValueColumn = new ColumnConfig();
        layerPropValueColumn.setId(BeanKeyValue.PROP_VALUE.getValue());
        layerPropValueColumn.setHeader("Value");
        layerPropValueColumn.setWidth(260);
        layerPropValueColumn.setRenderer(this.createPropValueTextBox());
        layerPropValueColumn.setMenuDisabled(true);
        layerPropValueColumn.setSortable(false);
        configs.add(layerPropValueColumn);

        ColumnConfig removeActionColumn = new ColumnConfig();
        removeActionColumn.setId("removeLayerCustomProp");
        removeActionColumn.setWidth(30);
        removeActionColumn.setRenderer(this.createDeleteButton());
        removeActionColumn.setMenuDisabled(true);
        removeActionColumn.setSortable(false);
        configs.add(removeActionColumn);

        return new ColumnModel(configs);
    }

    /**
     * Creates the prop key text box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<LayerCustomProps> createPropKeyTextBox()
    {

        GridCellRenderer<LayerCustomProps> textRendered = new GridCellRenderer<LayerCustomProps>()
            {

                private boolean init;

                public Object render(final LayerCustomProps model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<LayerCustomProps> store, Grid<LayerCustomProps> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<LayerCustomProps>>()
                            {

                                public void handleEvent(GridEvent<LayerCustomProps> be)
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
                    final TextField<String> propKey = new TextField<String>();
                    propKey.setWidth(200);
                    propKey.setAllowBlank(false);
                    propKey.setValue(model.getPropKey());

                    propKey.addListener(Events.Change, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                    new String[]
                                    {
                                        "GeoServer Rules: Layer Custom Properties",
                                        "Key " + model.getPropKey() + ": Key changed -> " +
                                        be.getField().getValue()
                                    });

//                        Map<String, LayerCustomProps> updateDTO = new HashMap<String, LayerCustomProps>();
//                        LayerCustomProps newModel = new LayerCustomProps();
//                        newModel.setPropKey((String) be.getField().getValue());
//                        newModel.setPropValue(model.getPropValue());
//                        updateDTO.put(model.getPropKey(), newModel);
//                        Dispatcher.forwardEvent(GeofenceEvents.RULE_CUSTOM_PROP_UPDATE_KEY, updateDTO);

                                model.setPropKey((String) be.getField().getValue());
                            }

                        });

                    propKey.addListener(Events.OnKeyPress, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {

                                int keycode = be.getKeyCode();

                                if (keycode == 36)
                                {
                                    // HOME KEY PRESSED
                                    propKey.setCursorPos(0);
                                }
                                else if (keycode == 35)
                                {
                                    // END KEY PRESSED
                                    propKey.setCursorPos(propKey.getValue().length());
                                }
                            }

                        });

                    return propKey;
                }

            };

        return textRendered;
    }

    /**
     * Creates the prop value text box.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<LayerCustomProps> createPropValueTextBox()
    {

        GridCellRenderer<LayerCustomProps> textRendered = new GridCellRenderer<LayerCustomProps>()
            {

                private boolean init;

                public Object render(final LayerCustomProps model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<LayerCustomProps> store, Grid<LayerCustomProps> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<LayerCustomProps>>()
                            {

                                public void handleEvent(GridEvent<LayerCustomProps> be)
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

                    final TextField<String> propValue = new TextField<String>();
                    propValue.setWidth(250);
                    propValue.setAllowBlank(true);
                    propValue.setValue(model.getPropValue());

                    propValue.addListener(Events.Change, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {
                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                    new String[]
                                    {
                                        "GeoServer Rules: Layer Custom Properties",
                                        "Key " + model.getPropKey() + ": Value changed -> " +
                                        be.getField().getValue()
                                    });

//                        Map<String, LayerCustomProps> updateDTO = new HashMap<String, LayerCustomProps>();
//                        LayerCustomProps newModel = new LayerCustomProps();
//                        newModel.setPropKey(model.getPropKey());
//                        newModel.setPropValue((String) be.getField().getValue());
//                        updateDTO.put(model.getPropKey(), newModel);
//                        Dispatcher.forwardEvent(GeofenceEvents.RULE_CUSTOM_PROP_UPDATE_VALUE, updateDTO);

                                model.setPropValue((String) be.getField().getValue());
                            }

                        });

                    propValue.addListener(Events.OnKeyPress, new Listener<FieldEvent>()
                        {

                            public void handleEvent(FieldEvent be)
                            {

                                int keycode = be.getKeyCode();

                                if (keycode == 36)
                                {
                                    // HOME KEY PRESSED
                                    propValue.setCursorPos(0);
                                }
                                else if (keycode == 35)
                                {
                                    // END KEY PRESSED
                                    propValue.setCursorPos(propValue.getValue().length());
                                }
                            }

                        });

                    return propValue;
                }

            };

        return textRendered;
    }

    /**
     * Creates the delete button.
     *
     * @return the grid cell renderer
     */
    private GridCellRenderer<LayerCustomProps> createDeleteButton()
    {
        GridCellRenderer<LayerCustomProps> buttonRendered = new GridCellRenderer<LayerCustomProps>()
            {

                private boolean init;

                public Object render(final LayerCustomProps model, String property, ColumnData config,
                    int rowIndex, int colIndex, ListStore<LayerCustomProps> store, Grid<LayerCustomProps> grid)
                {

                    if (!init)
                    {
                        init = true;
                        grid.addListener(Events.ColumnResize, new Listener<GridEvent<LayerCustomProps>>()
                            {

                                public void handleEvent(GridEvent<LayerCustomProps> be)
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
                    Button removeButton = new Button();
                    removeButton.setBorders(false);
                    removeButton.setIcon(Resources.ICONS.delete());
                    // TODO: add correct tooltip text here!
                    removeButton.setEnabled(true);

                    removeButton.addListener(Events.OnClick, new Listener<ButtonEvent>()
                        {

                            public void handleEvent(ButtonEvent be)
                            {
                                Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                                    new String[] { "GeoServer Rules: Layer Custom Properties", "Remove Property" });

                                Map<Long, LayerCustomProps> updateDTO = new HashMap<Long, LayerCustomProps>();
                                updateDTO.put(theRule.getId(), model);
                                Dispatcher.forwardEvent(GeofenceEvents.RULE_CUSTOM_PROP_DEL, updateDTO);
                            }
                        });

                    return removeButton;
                }

            };

        return buttonRendered;
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

        // Loader fro rulesService
        this.proxy = new RpcProxy<PagingLoadResult<LayerCustomProps>>()
            {

                @Override
                protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<LayerCustomProps>> callback)
                {
//                    rulesService.getLayerCustomProps(((PagingLoadConfig) loadConfig).getOffset(), ((PagingLoadConfig) loadConfig).getLimit(), theRule, callback);
                }

            };
        loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
        loader.setRemoteSort(true);
        store = new ListStore<LayerCustomProps>(loader);

        // Apply Changes button
        Button addRuleCustomPropertyButton = new Button("Add Property");
        addRuleCustomPropertyButton.setIcon(Resources.ICONS.add());

        addRuleCustomPropertyButton.addListener(Events.OnClick, new Listener<ButtonEvent>()
            {

                public void handleEvent(ButtonEvent be)
                {
                    Map<Long, LayerCustomProps> updateDTO = new HashMap<Long, LayerCustomProps>();
                    updateDTO.put(theRule.getId(), null);
                    Dispatcher.forwardEvent(GeofenceEvents.RULE_CUSTOM_PROP_ADD, updateDTO);
                }
            });

        Button saveRuleCustomPropertiesButton = new Button("Save");
        saveRuleCustomPropertiesButton.setIcon(Resources.ICONS.save());

        saveRuleCustomPropertiesButton.addListener(Events.OnClick, new Listener<ButtonEvent>()
            {

                public void handleEvent(ButtonEvent be)
                {
                    Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                        new String[] { "GeoServer Rules: Layer Custom Properties", "Apply Changes" });

                    Dispatcher.forwardEvent(GeofenceEvents.RULE_CUSTOM_PROP_APPLY_CHANGES, theRule.getId());
                }
            });

        Button cancelButton = new Button("Cancel");
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
        this.toolBar.add(addRuleCustomPropertyButton);
        this.toolBar.add(new SeparatorToolItem());
        this.toolBar.add(saveRuleCustomPropertiesButton);
        this.toolBar.add(cancelButton);

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
    public ToolBar getToolBar()
    {
        return toolBar;
    }

    /**
     * Clear grid elements.
     */
    public void clearGridElements()
    {
        this.store.removeAll();
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

}

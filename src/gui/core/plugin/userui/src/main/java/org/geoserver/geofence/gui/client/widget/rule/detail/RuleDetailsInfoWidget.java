/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.rule.detail;

import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.i18n.I18nProvider;
import org.geoserver.geofence.gui.client.model.BeanKeyValue;
import org.geoserver.geofence.gui.client.model.Rule;
import org.geoserver.geofence.gui.client.model.data.LayerDetailsInfo;
import org.geoserver.geofence.gui.client.model.data.LayerStyle;
import org.geoserver.geofence.gui.client.service.WorkspacesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.widget.GeofenceFormBindingWidget;
import it.geosolutions.geogwt.gui.client.Resources;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.geoserver.geofence.gui.client.model.data.ClientCatalogMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * The Class RuleDetailsInfoWidget.
 *
 * @author Tobia di Pisa
 *
 */
public class RuleDetailsInfoWidget extends GeofenceFormBindingWidget<LayerDetailsInfo>
{

    /** The rule. */
    private Rule theRule;

    /** The workspaces service. */
    private WorkspacesManagerRemoteServiceAsync workspacesService;

    /** The rule details widget. */
    private RuleDetailsWidget ruleDetailsWidget;

    /** The combo styles. */
    private ComboBox<LayerStyle> comboStyles;

    /** The cql filter read. */
    private TextArea cqlFilterRead;

    /** The cql filter write. */
    private TextArea cqlFilterWrite;

    private ComboBox<ClientCatalogMode> catalogModeBox;

    /** The allowed area. */
    private TextArea allowedArea;

	private Button draw;

    private Map<String, ClientCatalogMode> nameMode = new HashMap<String, ClientCatalogMode>();


    /**
     * Instantiates a new rule details info widget.
     *
     * @param model
     *            the model
     * @param workspacesService
     *            the workspaces service
     * @param ruleDetailsWidget
     *            the rule details widget
     */
    public RuleDetailsInfoWidget(Rule model, WorkspacesManagerRemoteServiceAsync workspacesService,
        RuleDetailsWidget ruleDetailsWidget)
    {
        this.theRule = model;
        this.workspacesService = workspacesService;
        this.ruleDetailsWidget = ruleDetailsWidget;
        formPanel = createFormPanel();

        initModeMap();
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.widget.GeofenceFormBindingWidget#createFormPanel()
     */
    @Override
    public FormPanel createFormPanel()
    {
        FormPanel fp = new FormPanel();
        fp.setFrame(true);
        fp.setHeaderVisible(false);
        fp.setHeight(400);
        fp.setWidth(650);

        FieldSet fieldSet = new FieldSet();
        fieldSet.setHeading("Layer Details");
        fieldSet.setCheckboxToggle(false);
        fieldSet.setCollapsible(false);

        FormLayout layout = new FormLayout();
        fieldSet.setLayout(layout);

        comboStyles = new ComboBox<LayerStyle>();
        comboStyles.setFieldLabel("Default Style");
        comboStyles.setEmptyText("Select a Style");
        comboStyles.setId(BeanKeyValue.STYLES_COMBO.getValue());
        comboStyles.setName(BeanKeyValue.STYLES_COMBO.getValue());
        comboStyles.setDisplayField(BeanKeyValue.STYLES_COMBO.getValue());
        comboStyles.setEditable(false);
        comboStyles.setAllowBlank(true);
        comboStyles.setForceSelection(false);
        comboStyles.setStore(getAvailableStyles(theRule));
        comboStyles.setTypeAhead(true);
        comboStyles.setTriggerAction(TriggerAction.ALL);
        comboStyles.addListener(Events.Select, new Listener<FieldEvent>()
            {

                public void handleEvent(FieldEvent be)
                {
                    ruleDetailsWidget.enableSaveButton();
                }

            });

        fieldSet.add(comboStyles);

        cqlFilterRead = new TextArea();
        cqlFilterRead.setFieldLabel("CQL Read");
        cqlFilterRead.setWidth(200);
        cqlFilterRead.setPreventScrollbars(true);
        cqlFilterRead.addListener(Events.Change, new Listener<FieldEvent>()
            {

                public void handleEvent(FieldEvent be)
                {
                    ruleDetailsWidget.enableSaveButton();
                }

            });

        fieldSet.add(cqlFilterRead);

        cqlFilterWrite = new TextArea();
        cqlFilterWrite.setFieldLabel("CQL Write");
        cqlFilterWrite.setWidth(200);
        cqlFilterWrite.setEnabled(true);
        cqlFilterWrite.setPreventScrollbars(true);
        cqlFilterWrite.addListener(Events.Change, new Listener<FieldEvent>()
            {

                public void handleEvent(FieldEvent be)
                {
                    ruleDetailsWidget.enableSaveButton();
                }

            });

        fieldSet.add(cqlFilterWrite);


        catalogModeBox = new ComboBox<ClientCatalogMode>();
        catalogModeBox.setFieldLabel("Catalog Mode");
        catalogModeBox.setId(BeanKeyValue.CATALOG_MODE.getValue());
        catalogModeBox.setName(BeanKeyValue.CATALOG_MODE.getValue());
        catalogModeBox.setDisplayField(BeanKeyValue.CATALOG_MODE.getValue());
        catalogModeBox.setWidth(70);
        catalogModeBox.setEditable(false);
        catalogModeBox.setStore(getAvailableCatalogModes());
        catalogModeBox.setTriggerAction(ComboBox.TriggerAction.ALL);

        catalogModeBox.addListener(Events.Select,
                new Listener<FieldEvent>()
                    {
                        public void handleEvent(FieldEvent be) {
                            ruleDetailsWidget.enableSaveButton();
                        }
                    }
                );

        fieldSet.add(catalogModeBox);


        allowedArea = new TextArea();
        allowedArea.setFieldLabel("Allowed Area");
        allowedArea.setWidth(200);
        allowedArea.setPreventScrollbars(true);
        allowedArea.addListener(Events.Change, new Listener<FieldEvent>()
            {

                public void handleEvent(FieldEvent be)
                {
                    ruleDetailsWidget.enableSaveButton();
                }

            });

        fieldSet.add(allowedArea);

        draw = new Button(I18nProvider.getMessages().drawAoiButton(),
                new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        Dispatcher.forwardEvent(GeofenceEvents.ACTIVATE_DRAW_FEATURES,
                                RuleDetailsInfoWidget.this);
                    	Dispatcher.forwardEvent(GeofenceEvents.RULE_EDITOR_DIALOG_HIDE);
                    }
                });

        draw.setIcon(Resources.ICONS.drawFeature());

        
        fp.add(fieldSet);
        ruleDetailsWidget.getToolBar().add(draw);
        
        return fp;
    }

    /**
     * Gets the model data.
     *
     * @return the model data
     */
    public LayerDetailsInfo getModelData()
    {
        LayerDetailsInfo layerDetailsForm = new LayerDetailsInfo();

        String area = allowedArea.getValue();

        String wkt, srid;
        if (area != null)
        {
            if (area.indexOf("SRID=") != -1)
            {
                String[] allowedAreaArray = area.split(";");

                srid = allowedAreaArray[0].split("=")[1];
                wkt = allowedAreaArray[1];
            }
            else
            {
                srid = "4326";
                wkt = area;
            }
        }
        else
        {
            srid = null;
            wkt = null;
        }

        layerDetailsForm.setAllowedArea(wkt);
        layerDetailsForm.setSrid(srid);
        layerDetailsForm.setCqlFilterRead(cqlFilterRead.getValue());
        layerDetailsForm.setCqlFilterWrite(cqlFilterWrite.getValue());

        LayerStyle layerStyle = comboStyles.getValue();
        if (layerStyle != null)
        {
            layerDetailsForm.setDefaultStyle(layerStyle.getStyle());
        }
        else
        {
            layerDetailsForm.setDefaultStyle(null);
        }

        layerDetailsForm.setCatalogMode(catalogModeBox.getValue());

        layerDetailsForm.setRuleId(theRule.getId());

        return layerDetailsForm;
    }

	/**
	 * @return the ruleDetailsWidget
	 */
	public RuleDetailsWidget getRuleDetailsWidget() {
		return ruleDetailsWidget;
	}

	/**
     * Bind model data.
     *
     * @param layerDetailsInfo
     *            the layer details info
     */
    public void bindModelData(LayerDetailsInfo layerDetailsInfo)
    {
        this.bindModel(layerDetailsInfo);

        String defaultStyle = layerDetailsInfo.getDefaultStyle();
        if (defaultStyle != null)
        {
            comboStyles.setValue(new LayerStyle(defaultStyle));
        }

        String cqlRead = layerDetailsInfo.getCqlFilterRead();
        if (cqlRead != null)
        {
            cqlFilterRead.setValue(cqlRead);
        }


        String cqlWrite = layerDetailsInfo.getCqlFilterWrite();
        if (cqlWrite != null)
        {
            cqlFilterWrite.setValue(cqlWrite);
        }

        String area = layerDetailsInfo.getAllowedArea();
        String srid = layerDetailsInfo.getSrid();
        if ((area != null) && (srid != null))
        {
            allowedArea.setValue("SRID=" + srid + ";" + area);
        }

        if(layerDetailsInfo.getCatalogMode() != null) {

            // get local instance
            ClientCatalogMode lcm = nameMode.get(layerDetailsInfo.getCatalogMode().getCatalogMode());
            catalogModeBox.setValue(lcm);

        } else {
            catalogModeBox.setValue(ClientCatalogMode.DEFAULT);

            Dispatcher.forwardEvent(
                GeofenceEvents.SEND_INFO_MESSAGE, new String[] {
                        "Info", "CatalogMode is null"});
        }

    }

    /**
     * Gets the available styles.
     *
     * @param rule
     *            the rule
     * @return the available styles
     */
    private ListStore<LayerStyle> getAvailableStyles(final Rule rule)
    {
        RpcProxy<List<LayerStyle>> workspacesProxy = new RpcProxy<List<LayerStyle>>()
            {

                @Override
                protected void load(Object loadConfig, AsyncCallback<List<LayerStyle>> callback)
                {
                    workspacesService.getStyles(rule, callback);
                }

            };

        BaseListLoader<ListLoadResult<ModelData>> workspacesLoader = new BaseListLoader<ListLoadResult<ModelData>>(
                workspacesProxy);
        workspacesLoader.setRemoteSort(false);

        ListStore<LayerStyle> geoserverStyles = new ListStore<LayerStyle>(workspacesLoader);

        return geoserverStyles;
    }

    private ListStore<ClientCatalogMode> getAvailableCatalogModes()
    {
        ListStore<ClientCatalogMode> ret = new ListStore<ClientCatalogMode>();
        List<ClientCatalogMode> list = new ArrayList<ClientCatalogMode>();

        list.add(ClientCatalogMode.DEFAULT);
        list.add(ClientCatalogMode.HIDE);
        list.add(ClientCatalogMode.MIXED);
        list.add(ClientCatalogMode.CHALLENGE);

        ret.add(list);

        return ret;
    }


    /**
	 * @param allowedArea the allowedArea to set
	 */
	public void setAllowedArea(TextArea allowedArea) {
		this.allowedArea = allowedArea;
	}

	/**
	 * @return the allowedArea
	 */
	public TextArea getAllowedArea() {
		return allowedArea;
	}

	/**
    * Disable cql filter buttons.
    */
    public void disableCQLFilterButtons()
    {
        this.cqlFilterRead.disable();
        this.cqlFilterWrite.disable();
    }

    /**
    * Enable cql filter buttons.
    */
    public void enableCQLFilterButtons()
    {
        this.cqlFilterRead.enable();
        this.cqlFilterWrite.enable();
    }

    private void initModeMap() {
        nameMode.put(ClientCatalogMode.NAME_DEFAULT, ClientCatalogMode.DEFAULT);
        nameMode.put(ClientCatalogMode.NAME_HIDE, ClientCatalogMode.HIDE);
        nameMode.put(ClientCatalogMode.NAME_MIXED, ClientCatalogMode.MIXED);
        nameMode.put(ClientCatalogMode.NAME_CHALLENGE, ClientCatalogMode.CHALLENGE);
    }

}

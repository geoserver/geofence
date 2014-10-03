/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.rule.detail;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ComponentManager;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.Resources;
import org.geoserver.geofence.gui.client.i18n.I18nProvider;
import org.geoserver.geofence.gui.client.model.Rule;
import org.geoserver.geofence.gui.client.model.data.LayerDetailsInfo;
import org.geoserver.geofence.gui.client.service.WorkspacesManagerRemoteServiceAsync;


// TODO: Auto-generated Javadoc
/**
 * The Class RuleDetailsWidget.
 */
public class RuleDetailsWidget extends ContentPanel
{

    /** The rule. */
    private Rule theRule;

    /** The rule details info. */
    private RuleDetailsInfoWidget ruleDetailsInfo;

    /** The rule details grid. */
    private RuleDetailsGridWidget ruleDetailsGrid;

    /** The tool bar. */
    private ToolBar toolBar;

    /** The save layer details button. */
    private Button saveLayerDetailsButton;

    private Button cancelButton;

    /**
    * Instantiates a new rule details widget.
    *
    * @param model
    *            the model
    * @param workspacesService
    *            the workspaces service
    */
    public RuleDetailsWidget(Rule model, WorkspacesManagerRemoteServiceAsync workspacesService)
    {
        this.theRule = model;
        this.toolBar = new ToolBar();

        setHeaderVisible(false);
        setFrame(true);
        setHeight(330);
        setWidth(690);
        setLayout(new FitLayout());

        ruleDetailsInfo = new RuleDetailsInfoWidget(this.theRule, workspacesService, this);
        add(ruleDetailsInfo.getFormPanel());

        ruleDetailsGrid = new RuleDetailsGridWidget(this.theRule, workspacesService, this);
        add(ruleDetailsGrid.getGrid());

        super.setMonitorWindowResize(true);

        setScrollMode(Scroll.AUTOY);

        this.saveLayerDetailsButton = new Button("Save");
        saveLayerDetailsButton.setIcon(Resources.ICONS.save());
        saveLayerDetailsButton.disable();

        saveLayerDetailsButton.addListener(Events.OnClick, new Listener<ButtonEvent>()
            {

                public void handleEvent(ButtonEvent be)
                {

                    disableSaveButton();

                    LayerDetailsInfo detailsInfoModel = ruleDetailsInfo.getModelData();
                    Dispatcher.forwardEvent(GeofenceEvents.SAVE_LAYER_DETAILS, detailsInfoModel);

                    Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                        new String[] { "GeoServer Rules: Layer Details", "Apply Changes" });

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
        this.toolBar.add(saveLayerDetailsButton);
        this.toolBar.add(cancelButton);
        setBottomComponent(this.toolBar);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.widget.Component#onWindowResize(int, int)
     */
    @Override
    protected void onWindowResize(int width, int height)
    {
//        super.setWidth(width - 5);
        super.layout();
    }

    /**
     * Sets the rule details info.
     *
     * @param layerCustomPropsInfo
     *            the new rule details info
     */
    public void setRuleDetailsInfo(RuleDetailsInfoWidget layerCustomPropsInfo)
    {
        this.ruleDetailsInfo = layerCustomPropsInfo;
    }

    /**
     * Gets the rule details info.
     *
     * @return the rule details info
     */
    public RuleDetailsInfoWidget getRuleDetailsInfo()
    {
        return ruleDetailsInfo;
    }

    /**
     * Gets the rule details grid.
     *
     * @return the rule details grid
     */
    public RuleDetailsGridWidget getRuleDetailsGrid()
    {
        return ruleDetailsGrid;
    }

    /**
    * Sets the rule details grid.
    *
    * @param ruleDetailsGrid
    *            the new rule details grid
    */
    public void setRuleDetailsGrid(RuleDetailsGridWidget ruleDetailsGrid)
    {
        this.ruleDetailsGrid = ruleDetailsGrid;
    }

	/**
	 * @return the toolBar
	 */
	public ToolBar getToolBar() {
		return toolBar;
	}

	/**
     * Disable save button.
     */
    public void disableSaveButton()
    {
        if (this.saveLayerDetailsButton.isEnabled())
        {
            this.saveLayerDetailsButton.disable();
        }
    }

    /**
     * Enable save button.
     */
    public void enableSaveButton()
    {
        if (!this.saveLayerDetailsButton.isEnabled())
        {
            this.saveLayerDetailsButton.enable();
        }
    }

}

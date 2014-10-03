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
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.Resources;
import org.geoserver.geofence.gui.client.i18n.I18nProvider;
import org.geoserver.geofence.gui.client.model.Rule;
import org.geoserver.geofence.gui.client.model.data.LayerLimitsInfo;
import org.geoserver.geofence.gui.client.service.RulesManagerRemoteServiceAsync;


/**
 * The Class RuleLimitsWidget.
 *
 */
public class RuleLimitsWidget extends ContentPanel
{

    /** The rule. */
    private Rule theRule;

    /** The rule details info. */
    private RuleLimitsInfoWidget ruleLimitsInfo;

    /** The tool bar. */
    private ToolBar toolBar;

    /** The save layer details button. */
    private Button saveLayerDetailsButton;

    private Button cancelButton;

    /**
    * Instantiates a new rule limits widget.
    *
    * @param model
    *            the model
    * @param rulesService
    *            the rule service
    */
    public RuleLimitsWidget(Rule model, RulesManagerRemoteServiceAsync rulesService)
    {
        this.theRule = model;
        this.toolBar = new ToolBar();

        setHeaderVisible(false);
        setFrame(true);
        setHeight(330);
        setWidth(690);
        setLayout(new FitLayout());

        ruleLimitsInfo = new RuleLimitsInfoWidget(this.theRule, rulesService, this);
        add(ruleLimitsInfo.getFormPanel());

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

                    LayerLimitsInfo limitsInfoModel = ruleLimitsInfo.getModelData();
                    Dispatcher.forwardEvent(GeofenceEvents.SAVE_LAYER_LIMITS, limitsInfoModel);

                    Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
                        new String[] { "GeoServer Rules: Layer Limits", "Apply Changes" });

                }
            });

        cancelButton = new Button("Cancel");
        cancelButton.addListener(Events.OnClick, new Listener<ButtonEvent>()
            {
                public void handleEvent(ButtonEvent be)
                {
                    // /////////////////////////////////////////////////////////
                    // Getting the Rule limits edit dialogs and hiding this
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
        super.layout();
    }

    /**
     * Sets the rule limits info.
     *
     * @param ruleLimitsInfoWidget
     *            the new rule limits info
     */
    public void setRuleDetailsInfo(RuleLimitsInfoWidget ruleLimitsInfoWidget)
    {
        this.ruleLimitsInfo = ruleLimitsInfoWidget;
    }

    /**
     * Gets the rule limits info.
     *
     * @return the rule limits info
     */
    public RuleLimitsInfoWidget getRuleLimitsInfo()
    {
        return ruleLimitsInfo;
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

    /**
	 * @return the toolBar
	 */
	public ToolBar getToolBar() {
		return this.toolBar;
	}

}

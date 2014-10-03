/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.rule.detail;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.TabItem;

import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.Resources;
import org.geoserver.geofence.gui.client.model.Rule;
import org.geoserver.geofence.gui.client.service.WorkspacesManagerRemoteServiceAsync;

// TODO: Auto-generated Javadoc
/**
 * The Class RuleDetailsTabItem.
 */
public class RuleDetailsTabItem extends TabItem {

	/** The rule details widget. */
	private RuleDetailsWidget ruleDetailsWidget;

	/** The rule. */
	private Rule theRule;

	/**
	 * Instantiates a new rule details tab item.
	 * 
	 * @param tabItemId
	 *            the tab item id
	 */
	private RuleDetailsTabItem(String tabItemId) {
		// TODO: add I18n message
		// super(I18nProvider.getMessages().profiles());
		super("Layer Details");
		setId(tabItemId);
		setIcon(Resources.ICONS.addAOI());
	}

	/**
	 * Instantiates a new rule details tab item.
	 * 
	 * @param tabItemId
	 *            the tab item id
	 * @param model
	 *            the model
	 * @param workspacesService
	 *            the workspaces service
	 * @param loadModel
	 */
	public RuleDetailsTabItem(String tabItemId, Rule model,
			WorkspacesManagerRemoteServiceAsync workspacesService,
			final boolean loadModel) {
		this(tabItemId);
		this.theRule = model;

		setRuleDetailsWidget(new RuleDetailsWidget(this.theRule,
				workspacesService));
		add(getRuleDetailsWidget());

		setScrollMode(Scroll.NONE);

			this.addListener(Events.Select, new Listener<BaseEvent>() {

				public void handleEvent(BaseEvent be) {
					if (loadModel && ruleDetailsWidget.getRuleDetailsInfo().getModel() == null) {
						Dispatcher.forwardEvent(
								GeofenceEvents.LOAD_LAYER_DETAILS, theRule);
					}

					if (ruleDetailsWidget.getRuleDetailsGrid().getStore()
							.getCount() < 1) {
						ruleDetailsWidget.getRuleDetailsGrid().getLoader()
								.load();
					}
				}

			});
		// getLayerCustomPropsWidget().getLayerCustomPropsInfo().getLoader().load(0,
		// org.geoserver.geofence.gui.client.Constants.DEFAULT_PAGESIZE);

	}

	/**
	 * Sets the rule details widget.
	 * 
	 * @param ruleDetailsWidget
	 *            the new rule details widget
	 */
	public void setRuleDetailsWidget(RuleDetailsWidget ruleDetailsWidget) {
		this.ruleDetailsWidget = ruleDetailsWidget;
	}

	/**
	 * Gets the rule details widget.
	 * 
	 * @return the rule details widget
	 */
	public RuleDetailsWidget getRuleDetailsWidget() {
		return ruleDetailsWidget;
	}

}

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
import org.geoserver.geofence.gui.client.service.RulesManagerRemoteServiceAsync;

/**
 * The Class RuleLimitsTabItem.
 */
public class RuleLimitsTabItem extends TabItem {

	/** The rule details widget. */
	private RuleLimitsWidget ruleLimitsWidget;

	/** The rule. */
	private Rule theRule;

	/**
	 * Instantiates a new rule limits tab item.
	 * 
	 * @param tabItemId
	 *            the tab item id
	 */
	private RuleLimitsTabItem(String tabItemId) {
		// TODO: add I18n message
		// super(I18nProvider.getMessages().profiles());
		super("Layer Limits");
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
	 * @param loadModel
	 * @param workspacesService
	 *            the workspaces service
	 */
	public RuleLimitsTabItem(String tabItemId, Rule model,
			RulesManagerRemoteServiceAsync rulesService, boolean loadModel) {
		this(tabItemId);
		this.theRule = model;

		setRuleLimitsWidget(new RuleLimitsWidget(this.theRule, rulesService));
		add(getRuleLimitsWidget());

		setScrollMode(Scroll.NONE);

		if (loadModel) {
			this.addListener(Events.Select, new Listener<BaseEvent>() {

				public void handleEvent(BaseEvent be) {
					if (ruleLimitsWidget.getRuleLimitsInfo().getModel() == null) {
						Dispatcher.forwardEvent(
								GeofenceEvents.LOAD_LAYER_LIMITS, theRule);
					}
				}

			});
		}

	}

	/**
	 * Sets the rule details widget.
	 * 
	 * @param ruleLimitsWidget
	 *            the new rule details widget
	 */
	public void setRuleLimitsWidget(RuleLimitsWidget ruleDetailsWidget) {
		this.ruleLimitsWidget = ruleDetailsWidget;
	}

	/**
	 * Gets the rule limits widget.
	 * 
	 * @return the rule limits widget
	 */
	public RuleLimitsWidget getRuleLimitsWidget() {
		return ruleLimitsWidget;
	}

}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.rule.detail;

import org.geoserver.geofence.gui.client.Resources;
import org.geoserver.geofence.gui.client.model.Rule;
import org.geoserver.geofence.gui.client.service.RulesManagerRemoteServiceAsync;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.TabItem;


// TODO: Auto-generated Javadoc
/**
 * The Class LayerAttributesTabItem.
 */
public class LayerAttributesTabItem extends TabItem
{

    /** The layer attributes widget. */
    private LayerAttributesWidget layerAttributesWidget;

    /** The model. */
    private Rule model;

    /**
     * Instantiates a new layer attributes tab item.
     *
     * @param tabItemId
     *            the tab item id
     */
    private LayerAttributesTabItem(String tabItemId)
    {
        // TODO: add I18n message
        // super(I18nProvider.getMessages().profiles());
        super("Layer Attributes");
        setId(tabItemId);
        setIcon(Resources.ICONS.table());
    }

    /**
     * Instantiates a new layer attributes tab item.
     *
     * @param tabItemId
     *            the tab item id
     * @param model
     *            the model
     * @param rulesService
     *            the rules service
     */
    public LayerAttributesTabItem(String tabItemId, Rule model, RulesManagerRemoteServiceAsync rulesService)
    {
        this(tabItemId);
        this.model = model;

        setLayerAttributesWidget(new LayerAttributesWidget(model, rulesService));
        add(getLayerAttributesWidget());

        setScrollMode(Scroll.NONE);

        this.addListener(Events.Select, new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				getLayerAttributesWidget().getLayerAttributesInfo().getLoader().load();
			}

		});
    }

    /**
     * Sets the layer attributes widget.
     *
     * @param layerAttributesWidget
     *            the new layer attributes widget
     */
    public void setLayerAttributesWidget(LayerAttributesWidget layerAttributesWidget)
    {
        this.layerAttributesWidget = layerAttributesWidget;
    }

    /**
     * Gets the layer attributes widget.
     *
     * @return the layer attributes widget
     */
    public LayerAttributesWidget getLayerAttributesWidget()
    {
        return layerAttributesWidget;
    }

}

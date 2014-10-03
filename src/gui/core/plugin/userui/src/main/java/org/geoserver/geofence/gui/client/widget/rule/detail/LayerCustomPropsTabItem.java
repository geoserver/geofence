/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.rule.detail;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.TabItem;

import org.geoserver.geofence.gui.client.Resources;
import org.geoserver.geofence.gui.client.model.Rule;
import org.geoserver.geofence.gui.client.service.RulesManagerRemoteServiceAsync;


// TODO: Auto-generated Javadoc
/**
 * The Class LayerCustomPropsTabItem.
 */
public class LayerCustomPropsTabItem extends TabItem
{

    /** The layer custom props widget. */
    private LayerCustomPropsWidget layerCustomPropsWidget;

    /** The model. */
    private Rule model;

    /**
     * Instantiates a new layer custom props tab item.
     *
     * @param tabItemId
     *            the tab item id
     */
    private LayerCustomPropsTabItem(String tabItemId)
    {
        // TODO: add I18n message
        // super(I18nProvider.getMessages().profiles());
        super("Layer Custom Properties");
        setId(tabItemId);
        setIcon(Resources.ICONS.table());
    }

    /**
     * Instantiates a new layer custom props tab item.
     *
     * @param tabItemId
     *            the tab item id
     * @param model
     *            the model
     * @param rulesService
     *            the rules service
     */
    public LayerCustomPropsTabItem(String tabItemId, Rule model, RulesManagerRemoteServiceAsync rulesService)
    {
        this(tabItemId);
        this.model = model;

        setLayerCustomPropsWidget(new LayerCustomPropsWidget(model, rulesService));
        add(getLayerCustomPropsWidget());

        setScrollMode(Scroll.NONE);

        getLayerCustomPropsWidget().getLayerCustomPropsInfo().getLoader().load(0, org.geoserver.geofence.gui.client.Constants.DEFAULT_PAGESIZE);

    }

    /**
     * Sets the layer custom props widget.
     *
     * @param layerCustomPropsWidget
     *            the new layer custom props widget
     */
    public void setLayerCustomPropsWidget(LayerCustomPropsWidget layerCustomPropsWidget)
    {
        this.layerCustomPropsWidget = layerCustomPropsWidget;
    }

    /**
     * Gets the layer custom props widget.
     *
     * @return the layer custom props widget
     */
    public LayerCustomPropsWidget getLayerCustomPropsWidget()
    {
        return layerCustomPropsWidget;
    }

}

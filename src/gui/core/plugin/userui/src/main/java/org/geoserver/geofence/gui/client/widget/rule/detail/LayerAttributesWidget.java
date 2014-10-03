/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.rule.detail;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

import org.geoserver.geofence.gui.client.model.Rule;
import org.geoserver.geofence.gui.client.service.RulesManagerRemoteServiceAsync;


// TODO: Auto-generated Javadoc
/**
 * The Class LayerAttributesWidget.
 */
public class LayerAttributesWidget extends ContentPanel
{

    /** The layer attributes info. */
    private LayerAttributesGridWidget layerAttributesInfo;

    /** The rule. */
    private Rule theRule;

    /**
     * Instantiates a new layer attributes widget.
     *
     * @param model
     *            the model
     * @param rulesService
     *            the rules service
     */
    public LayerAttributesWidget(Rule model, RulesManagerRemoteServiceAsync rulesService)
    {
        this.theRule = model;
        setHeaderVisible(false);
        setFrame(true);
        setHeight(330);
        setLayout(new FitLayout());

        setLayerAttributesInfo(new LayerAttributesGridWidget(this.theRule, rulesService));

        add(getLayerAttributesInfo().getGrid());

        super.setMonitorWindowResize(true);

        setScrollMode(Scroll.NONE);

        setBottomComponent(this.getLayerAttributesInfo().getToolBar());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.widget.Component#onWindowResize(int, int)
     */
    @Override
    protected void onWindowResize(int width, int height)
    {
        super.setWidth(width - 5);
        super.layout();
    }

    /**
     * Sets the layer attributes info.
     *
     * @param layerAttributesInfo
     *            the new layer attributes info
     */
    public void setLayerAttributesInfo(LayerAttributesGridWidget layerAttributesInfo)
    {
        this.layerAttributesInfo = layerAttributesInfo;
    }

    /**
     * Gets the layer attributes info.
     *
     * @return the layer attributes info
     */
    public LayerAttributesGridWidget getLayerAttributesInfo()
    {
        return layerAttributesInfo;
    }

}

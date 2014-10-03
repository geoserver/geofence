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
 * The Class LayerCustomPropsWidget.
 */
public class LayerCustomPropsWidget extends ContentPanel
{

    /** The layer custom props info. */
    private LayerCustomPropsGridWidget layerCustomPropsInfo;

    /** The model. */
    private Rule model;

    /**
     * Instantiates a new layer custom props widget.
     *
     * @param model
     *            the model
     * @param rulesService
     *            the rules service
     */
    public LayerCustomPropsWidget(Rule model, RulesManagerRemoteServiceAsync rulesService)
    {
        this.model = model;

        setHeaderVisible(false);
        setFrame(true);
        setHeight(330);
        setLayout(new FitLayout());

        setLayerCustomPropsInfo(new LayerCustomPropsGridWidget(model, rulesService));

        add(getLayerCustomPropsInfo().getGrid());

        super.setMonitorWindowResize(true);

        setScrollMode(Scroll.NONE);

        setBottomComponent(this.getLayerCustomPropsInfo().getToolBar());
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
     * Sets the layer custom props info.
     *
     * @param layerCustomPropsInfo
     *            the new layer custom props info
     */
    public void setLayerCustomPropsInfo(LayerCustomPropsGridWidget layerCustomPropsInfo)
    {
        this.layerCustomPropsInfo = layerCustomPropsInfo;
    }

    /**
     * Gets the layer custom props info.
     *
     * @return the layer custom props info
     */
    public LayerCustomPropsGridWidget getLayerCustomPropsInfo()
    {
        return layerCustomPropsInfo;
    }

}

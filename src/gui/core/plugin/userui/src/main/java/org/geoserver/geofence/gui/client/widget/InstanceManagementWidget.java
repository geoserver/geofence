/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

import org.geoserver.geofence.gui.client.Constants;
import org.geoserver.geofence.gui.client.service.InstancesManagerRemoteServiceAsync;


// TODO: Auto-generated Javadoc
/**
 * The Class InstanceManagementWidget.
 */
public class InstanceManagementWidget extends ContentPanel
{

    /** The instances info. */
    private InstanceGridWidget instancesInfo;

    /**
     * Instantiates a new instance management widget.
     *
     * @param instancesManagerServiceRemote
     *            the instances manager service remote
     */
    public InstanceManagementWidget(InstancesManagerRemoteServiceAsync instancesManagerServiceRemote)
    {
        setMonitorWindowResize(true);
        setHeaderVisible(false);
        setFrame(true);
        setLayout(new FitLayout());
        setScrollMode(Scroll.NONE);
        setAutoWidth(true);
        setHeight(Constants.SOUTH_PANEL_DIMENSION - 25);

        setInstancesInfo(new InstanceGridWidget(instancesManagerServiceRemote));
        add(getInstancesInfo().getGrid());
        setBottomComponent(this.getInstancesInfo().getToolBar());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.widget.Component#onWindowResize(int, int)
     */
    @Override
    protected void onWindowResize(int width, int height)
    {
        // TODO Auto-generated method stub
        super.setWidth(width - 5);
        super.layout();
    }

    /**
     * Sets the instances info.
     *
     * @param instancesInfo
     *            the new instances info
     */
    public void setInstancesInfo(InstanceGridWidget instancesInfo)
    {
        this.instancesInfo = instancesInfo;
    }

    /**
     * Gets the instances info.
     *
     * @return the instances info
     */
    public InstanceGridWidget getInstancesInfo()
    {
        return instancesInfo;
    }

}

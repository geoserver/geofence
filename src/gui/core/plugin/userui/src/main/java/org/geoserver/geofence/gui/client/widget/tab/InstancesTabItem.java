/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.tab;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.TabItem;

import org.geoserver.geofence.gui.client.Constants;
import org.geoserver.geofence.gui.client.Resources;
import org.geoserver.geofence.gui.client.service.InstancesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.widget.InstanceManagementWidget;


// TODO: Auto-generated Javadoc
/**
 * The Class InstancesTabItem.
 */
public class InstancesTabItem extends TabItem
{

    /** The instance management widget. */
    private InstanceManagementWidget instanceManagementWidget;

    /**
     * Instantiates a new instances tab item.
     */
    public InstancesTabItem(String tabItemId)
    {
        // TODO: add I18n message
        // super(I18nProvider.getMessages().instances());
        super("Instances");
        setId(tabItemId);
        setIcon(Resources.ICONS.pageEdit());
    }

    /**
     * Instantiates a new instances tab item.
     * @param instancesTabItemId
     *
     * @param instancesManagerServiceRemote
     *            the instances manager service remote
     */
    public InstancesTabItem(String tabItemId, InstancesManagerRemoteServiceAsync instancesManagerServiceRemote)
    {
        this(tabItemId);
        setScrollMode(Scroll.NONE);
        setAutoWidth(true);
        setHeight(Constants.SOUTH_PANEL_DIMENSION - 25);

        setInstanceManagementWidget(new InstanceManagementWidget(instancesManagerServiceRemote));
        add(getInstanceManagementWidget());

        getInstanceManagementWidget().getInstancesInfo().getLoader().load(0, org.geoserver.geofence.gui.client.Constants.DEFAULT_PAGESIZE);

    }

    /**
     * Sets the feature management widget.
     *
     * @param instanceManagementWidget
     *            the new feature management widget
     */
    public void setInstanceManagementWidget(InstanceManagementWidget instanceManagementWidget)
    {
        this.instanceManagementWidget = instanceManagementWidget;
    }

    /**
     * Gets the feature management widget.
     *
     * @return the feature management widget
     */
    public InstanceManagementWidget getInstanceManagementWidget()
    {
        return instanceManagementWidget;
    }

}

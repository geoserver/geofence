/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.tab;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.widget.TabItem;

import org.geoserver.geofence.gui.client.Constants;
import org.geoserver.geofence.gui.client.Resources;
import org.geoserver.geofence.gui.client.model.BeanKeyValue;
import org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.InstancesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.RulesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.WorkspacesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.widget.RuleManagementWidget;


// TODO: Auto-generated Javadoc
/**
 * The Class RulesTabItem.
 */
public class RulesTabItem extends TabItem
{

    /** The rules management widget. */
    private RuleManagementWidget rulesManagementWidget;

    /**
     * Instantiates a new rules tab item.
     */
    public RulesTabItem(String tabItemId)
    {
        // TODO: add I18n message
        // super(I18nProvider.getMessages().profiles());
        super("Rules");
        setId(tabItemId);
        setIcon(Resources.ICONS.table());
    }

    /**
     * Instantiates a new rules tab item.
     *
     * @param tabItemId
     *
     * @param rulesManagerServiceRemote
     *            the rules manager service remote
     */
    public RulesTabItem(String tabItemId, RulesManagerRemoteServiceAsync rulesService,
        GsUsersManagerRemoteServiceAsync gsUsersService,
        ProfilesManagerRemoteServiceAsync profilesService,
        InstancesManagerRemoteServiceAsync instancesService,
        WorkspacesManagerRemoteServiceAsync workspacesService)
    {
        this(tabItemId);
        setScrollMode(Scroll.NONE);
        setAutoWidth(true);
        setHeight(Constants.SOUTH_PANEL_DIMENSION - 25);

        setRuleManagementWidget(new RuleManagementWidget(rulesService, gsUsersService,
                profilesService, instancesService, workspacesService));
        add(getRuleManagementWidget());

        getRuleManagementWidget().getRulesInfo().getStore().setSortField(
            BeanKeyValue.PRIORITY.getValue());
        getRuleManagementWidget().getRulesInfo().getStore().setSortDir(SortDir.ASC);
        getRuleManagementWidget().getRulesInfo().getLoader().load(0,
            org.geoserver.geofence.gui.client.Constants.DEFAULT_PAGESIZE);

    }

    /**
     * Sets the rule management widget.
     *
     * @param rulesManagementWidget
     *            the new rule management widget
     */
    public void setRuleManagementWidget(RuleManagementWidget rulesManagementWidget)
    {
        this.rulesManagementWidget = rulesManagementWidget;
    }

    /**
     * Gets the rule management widget.
     *
     * @return the rule management widget
     */
    public RuleManagementWidget getRuleManagementWidget()
    {
        return rulesManagementWidget;
    }

}

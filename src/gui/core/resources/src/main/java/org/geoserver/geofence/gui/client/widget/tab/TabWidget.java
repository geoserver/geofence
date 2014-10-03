/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.tab;

//import org.geoserver.geofence.gui.client.AdministrationMode;
import org.geoserver.geofence.gui.client.GeofenceEvents;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;

// TODO: Auto-generated Javadoc
/**
 * The Class TabWidget.
 */
public class TabWidget extends TabPanel implements Listener {

    /**
     * Instantiates a new tab widget.
     */
    public TabWidget() {
        super();
        initTab();
        initTabItem();

        addListener(GeofenceEvents.BIND_SELECTED_MEMBER, this);
    }

    /**
     * Inits the tab.
     */
    private void initTab() {
        setAutoWidth(true);
        //System.out.println(super.getHeight());
        if(this.height!=null)System.out.println("height: "+height);
        if(this.getParent()!=null)System.out.println("getParent().getOffsetHeight(): "+this.getParent().getOffsetHeight());
        if(GXT.isIE || GXT.isIE7 || GXT.isIE8){
                System.out.println("this.getOffsetHeight=="+this.getOffsetHeight());
                System.out.println("this.getElement().getClientHeight()=="+this.getElement().getClientHeight());
        }
        setAutoHeight(true);
        setHeight("95%");
    }

    /**
     * Inits the tab item.
     */
    private void initTabItem() {
        super.doLayout();

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
     */
    public void handleEvent(BaseEvent e) {
//        if (e.getType() == GeofenceEvents.ADMIN_MODE_CHANGE) {
//            onAdminModeChange((AppEvent) e);
//        }
        if (e.getType() == GeofenceEvents.BIND_SELECTED_MEMBER) {
            onBindMember((AppEvent) e);
        }
//        if (e.getType() == GeofenceEvents.GEOCONSTRAINT_DELETED) {
//            onGeoConstraintDeleted((AppEvent) e);
//        }
//        if (e.getType() == GeofenceEvents.RELOAD_GEOCONSTRAINTS) {
//            onReloadGeoConstraints((AppEvent) e);
//        }
    }

    /**
     * Forward to all tabs.
     * 
     * @param event
     *            the event
     */
    private void forwardToAllTabs(AppEvent event) {
        for (TabItem tabItem : this.getItems()) {
            tabItem.fireEvent(event.getType(), event);
        }
    }

    /**
     * On bind member.
     * 
     * @param event
     *            the event
     */
    private void onBindMember(AppEvent event) {
        forwardToAllTabs(event);
    }

    /**
     * On reload geo constraints.
     * 
     * @param event
     *            the event
     */
    private void onReloadGeoConstraints(AppEvent event) {
        forwardToAllTabs(event);
    }

}

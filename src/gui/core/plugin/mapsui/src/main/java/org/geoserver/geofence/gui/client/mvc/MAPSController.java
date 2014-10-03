/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.mvc;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;

import org.geoserver.geofence.gui.client.GeofenceEvents;


// TODO: Auto-generated Javadoc
/**
 * The Class MAPSController.
 */
public class MAPSController extends Controller
{

//    /** The feature remote. */
//    private FeatureServiceRemoteAsync featureRemote = FeatureServiceRemote.Util.getInstance();

//    /** The tab widget. */
//    private TabWidget tabWidget;

//    /** The aoi search widget. */
//    private GeofenceSearchWidget<AOI> aoiSearchWidget;
//
//    /** The geo constraint search widget. */
//    private GeofenceSearchWidget<GeoConstraint> geoConstraintSearchWidget;

    /**
    * Instantiates a new mAPS controller.
    */
    public MAPSController()
    {
        registerEventTypes(
            GeofenceEvents.INIT_MAPS_UI_MODULE,
            GeofenceEvents.SHOW_UPLOAD_WIDGET,
            GeofenceEvents.ATTACH_AOI_WIDGET,
            GeofenceEvents.ATTACH_BOTTOM_TAB_WIDGETS,
            GeofenceEvents.AOI_MANAGEMENT_BIND,
            GeofenceEvents.AOI_MANAGEMENT_UNBIND,
            GeofenceEvents.SHOW_ADD_AOI,
            GeofenceEvents.SHOW_ADD_GEOCONSTRAINT,
            GeofenceEvents.INJECT_WKT,
            GeofenceEvents.SAVE_AOI,
            GeofenceEvents.SEARCH_AOI,
            GeofenceEvents.BIND_SELECTED_AOI,
            GeofenceEvents.DELETE_AOI,
            GeofenceEvents.UPDATE_AOI,
            GeofenceEvents.INJECT_WKT_FROM_SHP,
            GeofenceEvents.SAVE_AOI_FROM_SHP,
            GeofenceEvents.CHECK_AOI_OWNER,
            GeofenceEvents.SEARCH_USER_GEORSS,
            GeofenceEvents.RESET_AOI_GRID,
            GeofenceEvents.RESET_RSS_GRID,
            GeofenceEvents.CHECK_AOI_STATUS,
            GeofenceEvents.ADMIN_MODE_CHANGE,
            GeofenceEvents.ATTACH_GEOCONSTRAINT_AOI_WIDGET,
            GeofenceEvents.BIND_SELECTED_GEOCONSTRAINT,
            GeofenceEvents.DELETE_CONTENT,
            GeofenceEvents.BIND_SELECTED_MEMBER,
            GeofenceEvents.SAVE_GEOCONSTRAINT,
            GeofenceEvents.ENABLE_DRAW_BUTTON,
            GeofenceEvents.SEARCH_GEOCONSTRAINT,
            GeofenceEvents.DELETE_GEOCONSTRAINT,
            GeofenceEvents.SEARCH_MEMBER_WATCHES,
            GeofenceEvents.LOAD_WATCH_AOI,
            GeofenceEvents.RELOAD_GEOCONSTRAINTS,
            GeofenceEvents.GEOCONSTRAINT_DELETED,
            GeofenceEvents.RESET_WATCH_GRID,
            GeofenceEvents.REFRESH_WATCH_GRID,
            GeofenceEvents.MEMBER_GEOCONSTRAINT_UNBOUND);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.mvc.Controller#initialize()
     */
    @Override
    protected void initialize()
    {
        initWidget();
    }

    /**
     * Inits the widget.
     */
    private void initWidget()
    {
//        this.aoiSearchWidget = new SearchPagAOIWidget(this.aoiRemote);
//        this.geoConstraintSearchWidget = new SearchPagingGeoConstraintWidget(this.membersRemote);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.mvc.Controller#handleEvent(com.extjs.gxt.ui.client
     * .mvc.AppEvent)
     */
    @Override
    public void handleEvent(AppEvent event)
    {
//        if (event.getType() == GeofenceEvents.ATTACH_BOTTOM_TAB_WIDGETS)
//            onAttachTabWidgets(event);

//        if (event.getType() == GeofenceEvents.SEARCH_USER_GEORSS)
//            onSearchUserFeature(event);

//        if (event.getType() == GeofenceEvents.RESET_RSS_GRID)
//            onResetRSSGrid();


//        forwardToView(aoiView, event);
    }

    /**
     * Forward to tab widget.
     *
     * @param event
     *            the event
     */
    private void forwardToTabWidget(AppEvent event)
    {
//        this.tabWidget.fireEvent(event.getType(), event);
    }

}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.mvc;

import org.geoserver.geofence.gui.client.Constants;
import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.GeofenceUtils;
import org.geoserver.geofence.gui.client.action.application.LogoutAction;
//import org.geoserver.geofence.gui.client.action.toolbar.UpdateUsersAction;
import org.geoserver.geofence.gui.client.configuration.ConfigurationMainUI;
import org.geoserver.geofence.gui.client.configuration.GeofenceGlobalConfiguration;
import org.geoserver.geofence.gui.client.i18n.I18nProvider;
import org.geoserver.geofence.gui.client.widget.tab.TabWidget;
import it.geosolutions.geogwt.gui.client.GeoGWTEvents;
import it.geosolutions.geogwt.gui.client.GeoGWTUtils;
import it.geosolutions.geogwt.gui.client.ToolbarItemManager;
import it.geosolutions.geogwt.gui.client.configuration.ActionClientTool;
import it.geosolutions.geogwt.gui.client.configuration.GenericClientTool;
import it.geosolutions.geogwt.gui.client.configuration.GeoGWTConfiguration;
import it.geosolutions.geogwt.gui.client.configuration.ToolbarAction;
import it.geosolutions.geogwt.gui.client.widget.map.ButtonBar;
import it.geosolutions.geogwt.gui.client.widget.map.action.ToolActionCreator;
import it.geosolutions.geogwt.gui.client.widget.map.action.ToolbarActionRegistry;

import java.util.ArrayList;
import java.util.List;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapUnits;
import org.gwtopenmaps.openlayers.client.control.LayerSwitcher;
import org.gwtopenmaps.openlayers.client.control.MousePosition;
import org.gwtopenmaps.openlayers.client.control.MousePositionOptions;
import org.gwtopenmaps.openlayers.client.control.MousePositionOutput;
import org.gwtopenmaps.openlayers.client.control.Navigation;
import org.gwtopenmaps.openlayers.client.control.PanZoom;
import org.gwtopenmaps.openlayers.client.control.ScaleLine;
import org.gwtopenmaps.openlayers.client.layer.TransitionEffect;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;
import org.gwtopenmaps.openlayers.client.util.JObjectArray;
import org.gwtopenmaps.openlayers.client.util.JSObject;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.RootPanel;


// TODO: Auto-generated Javadoc
/**
 * The Class AppView.
 */
public class AppView extends View
{

    /** The viewport. */
    private Viewport viewport;

    /** The east. */
    protected ContentPanel east;

    /** The south. */
    protected ContentPanel south;

    /** The center. */
    protected ContentPanel center;

    /** The north. */
    protected ContentPanel north;

    /** The tab widget. */
    private TabWidget tabWidget;

    /**
     * Instantiates a new app view.
     *
     * @param controller
     *            the controller
     */
    public AppView(Controller controller)
    {
        super(controller);
    }

    /**
     * Inits the ui.
     */
    private void initUI()
    {
        this.viewport = new Viewport();
        this.viewport.setLayout(new BorderLayout());

        createNorth();
        // createEast();
        createCenter();
        createSouth();

        // registry serves as a global context
        Registry.register(ConfigurationMainUI.VIEWPORT.getValue(), viewport);
        Registry.register(ConfigurationMainUI.EAST.getValue(), east);
        Registry.register(ConfigurationMainUI.SOUTH.getValue(), south);
        Registry.register(ConfigurationMainUI.CENTER.getValue(), center);

        RootPanel.get().add(viewport);
    }

    /**
     * Creates the north.
     */
    private void createNorth()
    {
        north = new ContentPanel();
        north.setHeaderVisible(false);
        north.addListener(Events.Resize, new Listener<BaseEvent>()
            {

                public void handleEvent(BaseEvent be)
                {
                    Dispatcher.forwardEvent(GeoGWTEvents.UPDATE_MAP_SIZE);
                    // Dispatcher.forwardEvent(GeofenceEvents.UPDATE_SOUTH_SIZE);
                }
            });

        BorderLayoutData data = new BorderLayoutData(LayoutRegion.NORTH,
                Constants.NORTH_PANEL_DIMENSION);
        data.setMargins(new Margins(0, 5, 0, 5));
        data.setSplit(true);

        viewport.add(north, data);
    }

    /**
     * Creates the east.
     */
    private void createEast()
    {
        BorderLayoutData data = new BorderLayoutData(LayoutRegion.EAST,
                Constants.EASTH_PANEL_DIMENSION);
        data.setMargins(new Margins(5, 0, 5, 5));
        data.setCollapsible(true);
        data.setSplit(true);
        east = new ContentPanel();
        east.setBodyBorder(false);
        east.setLayout(new AccordionLayout());
        east.setHeading(I18nProvider.getMessages().accordionLabel());
        east.setScrollMode(Scroll.AUTO);

        east.addListener(Events.Resize, new Listener<BaseEvent>()
            {

                public void handleEvent(BaseEvent be)
                {
                    Dispatcher.forwardEvent(GeoGWTEvents.UPDATE_MAP_SIZE);
                    // Dispatcher.forwardEvent(GeofenceEvents.UPDATE_SOUTH_SIZE);
                }
            });
        east.addListener(Events.Move, new Listener<BaseEvent>()
            {

                public void handleEvent(BaseEvent be)
                {
                    Dispatcher.forwardEvent(GeoGWTEvents.UPDATE_MAP_SIZE);
                    // Dispatcher.forwardEvent(GeofenceEvents.UPDATE_SOUTH_SIZE);
                }
            });
        east.setStyleAttribute("height", "auto");
        east.setStyleAttribute("width", "auto");
        configureAccordionPanel();
        east.setMonitorWindowResize(true);
        east.setLayoutOnChange(true);
        viewport.add(east, data);
    }

    /**
     * Creates the south.
     */
    private void createSouth()
    {
        BorderLayoutData data = new BorderLayoutData(LayoutRegion.SOUTH,
                Constants.SOUTH_PANEL_DIMENSION + 25);
        // data.setMargins(new Margins(5, 5, 5, 5));
        // data.setHideCollapseTool(false);
        data.setMargins(new Margins(5, 0, 0, 0));
        data.setCollapsible(true);
        data.setSplit(false);

        /*
         * south = new ContentPanel(); south.setBodyBorder(false); south.setAnimCollapse(true);
         * south.setCollapsible(true); south.setLayout(new FitLayout());
         * south.setLayoutOnChange(true); south.setScrollMode(Scroll.AUTOY);
         * south.setHeaderVisible(true);
         * south.setHeading(I18nProvider.getMessages().accordionLabel());
         * south.setMonitorWindowResize(true); south.setLayoutOnChange(true); south.layout();
         */
        south = new ContentPanel();
        south.setBodyBorder(false);
        south.setLayout(new FitLayout());
        south.setScrollMode(Scroll.AUTOY);
        south.setHeaderVisible(true);
        // south.setHeading(I18nProvider.getMessages().accordionLabel());
        south.setMonitorWindowResize(true);
        south.setLayoutOnChange(true);
        south.layout();

        south.addListener(Events.Resize, new Listener<BaseEvent>()
            {

                public void handleEvent(BaseEvent be)
                {
                    Dispatcher.forwardEvent(GeoGWTEvents.UPDATE_MAP_SIZE);
                    // Dispatcher.forwardEvent(GeofenceEvents.UPDATE_SOUTH_SIZE);
                }
            });
        south.addListener(Events.Move, new Listener<BaseEvent>()
            {

                public void handleEvent(BaseEvent be)
                {
                    Dispatcher.forwardEvent(GeoGWTEvents.UPDATE_MAP_SIZE);
                    // Dispatcher.forwardEvent(GeofenceEvents.UPDATE_SOUTH_SIZE);
                }
            });
        this.tabWidget = new TabWidget();

        south.add(this.tabWidget);

        Dispatcher.forwardEvent(GeofenceEvents.ATTACH_BOTTOM_TAB_WIDGETS, this.tabWidget);

        center.add(south, data);
    }

    /**
     * Creates the center.
     */
    private void createCenter()
    {
        center = new ContentPanel();
        center.setLayout(new BorderLayout());
        center.setHeaderVisible(false);

        ContentPanel map = new ContentPanel();
        map.setLayout(new FitLayout());
        map.setHeaderVisible(false);

        // we dont need this since we have listener on south panel as well

        map.addListener(Events.Resize, new Listener<BaseEvent>()
            {

                public void handleEvent(BaseEvent be)
                {
                    Dispatcher.forwardEvent(GeoGWTEvents.UPDATE_MAP_SIZE);
                    // Dispatcher.forwardEvent(AcoDBEvents.UPDATE_SOUTH_SIZE);
                }
            });
        map.addListener(Events.Move, new Listener<BaseEvent>()
            {

                public void handleEvent(BaseEvent be)
                {
                    Dispatcher.forwardEvent(GeoGWTEvents.UPDATE_MAP_SIZE);
                    // Dispatcher.forwardEvent(AcoDBEvents.UPDATE_SOUTH_SIZE);
                }
            });

        map.setMonitorWindowResize(true);
        map.setLayoutOnChange(true);

        BorderLayoutData data = new BorderLayoutData(LayoutRegion.CENTER);
        data.setMargins(new Margins(0));

        // add map to center region of center panel
        center.add(map, data);

        data = new BorderLayoutData(LayoutRegion.CENTER);
        // data.setCollapsible(true);
        // data.setFloatable(true);
        // data.setSplit(true);
        data.setMargins(new Margins(5, 5, 5, 5));

        viewport.add(center, data);

        /* map options */
        MapOptions mapOptions = new MapOptions();
        mapOptions.setUnits(MapUnits.DEGREES);
        mapOptions.setProjection("EPSG:4326");

        MousePositionOutput mpOut = new MousePositionOutput()
            {

                public String format(LonLat lonLat, Map map)
                {
                    String out = "";
                    out += "<font size='2' color='black' style='font-weight:900'>";
                    out += (float) lonLat.lon();
                    out += " ";
                    out += (float) lonLat.lat();
                    out += "</font>";

                    return out;
                }

            };

        MousePositionOptions mpOptions = new MousePositionOptions();
        mpOptions.setFormatOutput(mpOut);

        JSObject[] controls = new JSObject[]
            {
                new Navigation().getJSObject(),
                new PanZoom().getJSObject(), new LayerSwitcher().getJSObject(),
                new ScaleLine().getJSObject(), new MousePosition(mpOptions).getJSObject()
            };
        JObjectArray mapControls = new JObjectArray(controls);
        mapOptions.setControls(mapControls);

        Dispatcher.forwardEvent(GeoGWTEvents.INIT_MAPS_UI_MODULE, mapOptions);

        addBaseLayer();

        Dispatcher.forwardEvent(GeoGWTEvents.ATTACH_MAP_WIDGET, map);
        
        GeofenceGlobalConfiguration geoFenceConfiguration = (GeofenceGlobalConfiguration) GeofenceUtils.
                getInstance().getGlobalConfiguration();

        // Adjusting the Zoom level
        // Dispatcher.forwardEvent(GeoGWTEvents.ZOOM_TO_MAX_EXTENT);
        Dispatcher.forwardEvent(GeoGWTEvents.SET_MAP_CENTER, new Double[] { Double.valueOf(geoFenceConfiguration.getMapCenterLon()),
                Double.valueOf(geoFenceConfiguration.getMapCenterLat()) });
        Dispatcher.forwardEvent(GeoGWTEvents.ZOOM, Integer.parseInt(geoFenceConfiguration.getMapZoom()));

        ToolbarItemManager toolbarItemManager = new ToolbarItemManager();

        // defining toolbar tools
        GenericClientTool poweredBy = new GenericClientTool();
        poweredBy.setId(ButtonBar.POWERED_BY);
        poweredBy.setOrder(-400);

        GenericClientTool toolbarSeparator1 = new GenericClientTool();
        toolbarSeparator1.setId(ButtonBar.TOOLBAR_SEPARATOR);
        toolbarSeparator1.setOrder(-300);

        ActionClientTool pan = new ActionClientTool();
        pan.setId("pan");
        pan.setEnabled(true);
        pan.setType("toggle");
        pan.setOrder(0);

        ActionClientTool zoomAll = new ActionClientTool();
        zoomAll.setId("zoomAll");
        zoomAll.setEnabled(true);
        zoomAll.setType("button");
        zoomAll.setOrder(10);

        ActionClientTool zoomBox = new ActionClientTool();
        zoomBox.setId("zoomBox");
        zoomBox.setEnabled(true);
        zoomBox.setType("toggle");
        zoomBox.setOrder(20);

        ActionClientTool zoomIn = new ActionClientTool();
        zoomIn.setId("zoomIn");
        zoomIn.setEnabled(true);
        zoomIn.setType("button");
        zoomIn.setOrder(30);

        ActionClientTool zoomOut = new ActionClientTool();
        zoomOut.setId("zoomOut");
        zoomOut.setEnabled(true);
        zoomOut.setType("button");
        zoomOut.setOrder(40);

        ActionClientTool drawFeature = new ActionClientTool();
        drawFeature.setId("drawFeature");
        drawFeature.setEnabled(true);
        drawFeature.setType("toggle");
        drawFeature.setOrder(50);
        
        GenericClientTool toolbarSeparator2 = new GenericClientTool();
        toolbarSeparator2.setId(ButtonBar.TOOLBAR_SEPARATOR);
        toolbarSeparator2.setOrder(300);

//        ToolbarActionRegistry.getRegistry().put("synchUsers", new ToolActionCreator()
//            {
//
//                public ToolbarAction createActionTool()
//                {
//                    UpdateUsersAction action = new UpdateUsersAction();
//                    action.initialize();
//
//                    return action;
//                }
//            });
//
//        ActionClientTool synchUsers = new ActionClientTool();
//        synchUsers.setId("synchUsers");
//        synchUsers.setEnabled(true);
//        synchUsers.setType("button");
//        synchUsers.setOrder(310);

        GenericClientTool fillItem = new GenericClientTool();
        fillItem.setId(ButtonBar.FILL_ITEM);
        fillItem.setOrder(320);

        ToolbarActionRegistry.getRegistry().put("logout", new ToolActionCreator()
            {

                public ToolbarAction createActionTool()
                {
                    LogoutAction action = new LogoutAction();
                    action.initialize();

                    return action;
                }
            });

        ActionClientTool logout = new ActionClientTool();
        logout.setId("logout");
        logout.setEnabled(true);
        logout.setType("button");
        logout.setOrder(500);

        List<GenericClientTool> clientTools = new ArrayList<GenericClientTool>();
        // clientTools.add(poweredBy);
        // clientTools.add(toolbarSeparator1);
        clientTools.add(pan);
        clientTools.add(zoomAll);
        clientTools.add(zoomBox);
        clientTools.add(zoomIn);
        clientTools.add(zoomOut);
        clientTools.add(drawFeature);
        clientTools.add(toolbarSeparator2);
//        clientTools.add(synchUsers);
        clientTools.add(fillItem);
        clientTools.add(logout);

        toolbarItemManager.setClientTools(clientTools);

        if (GeoGWTUtils.getInstance().getGlobalConfiguration() == null)
        {
            GeoGWTUtils.getInstance().setGlobalConfiguration(new GeoGWTConfiguration());
        }

        GeoGWTUtils.getInstance().getGlobalConfiguration().setToolbarItemManager(toolbarItemManager);

        Dispatcher.forwardEvent(GeoGWTEvents.ATTACH_TOOLBAR, this.north);
        // Dispatcher.forwardEvent(GeofenceEvents.ATTACH_MAP_WIDGET, this.center);
    }

    /**
     *
     */
    private void addBaseLayer()
    {

    	GeofenceGlobalConfiguration geoFenceConfiguration = (GeofenceGlobalConfiguration) GeofenceUtils.getInstance().getGlobalConfiguration();
    	
        /* base layer */
        WMSParams wmsParams = new WMSParams();
        wmsParams.setLayers(geoFenceConfiguration.getBaseLayerName());
        wmsParams.setFormat(geoFenceConfiguration.getBaseLayerFormat());
        wmsParams.setStyles(geoFenceConfiguration.getBaseLayerStyle());

        WMSOptions wmsLayerParams = new WMSOptions();
        wmsLayerParams.setTransitionEffect(TransitionEffect.RESIZE);

        WMS layer = new WMS(geoFenceConfiguration.getBaseLayerTitle(),
        		geoFenceConfiguration.getBaseLayerURL(), wmsParams, wmsLayerParams);
        Dispatcher.forwardEvent(GeoGWTEvents.ADD_LAYER, layer);
    }

    /**
     * Configure accordion panel.
     */
    private void configureAccordionPanel()
    {
        AppController controller = (AppController) this.getController();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.mvc.View#handleEvent(com.extjs.gxt.ui.client.mvc.AppEvent)
     */
    @Override
    protected void handleEvent(AppEvent event)
    {
        if (event.getType() == GeofenceEvents.INIT_GEOFENCE_MAIN_UI)
        {
            initUI();
        }
    }

    /**
     * Reload.
     */
    public native void reload()
    /*-{
        $wnd.window.location.reload();
    }-*/;

}

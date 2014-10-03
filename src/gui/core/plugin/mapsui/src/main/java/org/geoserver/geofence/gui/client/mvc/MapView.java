/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.mvc;

import org.geoserver.geofence.gui.client.GeofenceEvents;
import it.geosolutions.geogwt.gui.client.GeoGWTEvents;
import it.geosolutions.geogwt.gui.client.widget.map.MapLayoutWidget;

import org.gwtopenmaps.openlayers.client.LonLat;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;

// TODO: Auto-generated Javadoc
/**
 * The Class MapView.
 */
public class MapView extends View {

	/** The map layout. */
	private MapLayoutWidget mapLayout;

	/** The button bar. */
	// private ButtonBar buttonBar;

	/**
	 * Instantiates a new map view.
	 * 
	 * @param controller
	 *            the controller
	 */
	public MapView(Controller controller) {
		super(controller);

		this.mapLayout = new MapLayoutWidget();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extjs.gxt.ui.client.mvc.View#handleEvent(com.extjs.gxt.ui.client.
	 * mvc.AppEvent)
	 */
	@Override
	protected void handleEvent(AppEvent event) {
		// if (event.getType() == GeofenceEvents.ATTACH_MAP_WIDGET) {
		// this.mapLayout.onAddToCenterPanel((ContentPanel) event.getData());
		// }

		if (event.getType() == GeofenceEvents.UPDATE_MAP_SIZE) {
			this.mapLayout.updateMapSize();
		}

		// if (event.getType() == GeofenceEvents.ATTACH_TOOLBAR) {
		// onAttachToolbar(event);
		// }

		if (event.getType() == GeofenceEvents.ACTIVATE_DRAW_FEATURES) {
			onActivateDrawFeature();
		}

		if (event.getType() == GeofenceEvents.DEACTIVATE_DRAW_FEATURES) {
			onDeactivateDrawFeature();
		}

		if (event.getType() == GeofenceEvents.ERASE_AOI_FEATURES) {
			onEraseAOIFeatures();
		}

		// if (event.getType() == GeofenceEvents.ENABLE_DRAW_BUTTON) {
		// onEnableDrawButton();
		// }
		//
		// if (event.getType() == GeofenceEvents.DISABLE_DRAW_BUTTON) {
		// onDisableDrawButton();
		// }

		// if (event.getType() == GeofenceEvents.DRAW_AOI_ON_MAP) {
		// onDrawAoiOnMap(event);
		// }

		// if (event.getType() == GeofenceEvents.ZOOM_TO_CENTER) {
		// onZoomToCenter();
		// }

		// if (event.getType() == GeofenceEvents.LOGIN_SUCCESS) {
		// this.buttonBar.fireEvent(event.getType(), event);
		// }
		
		if(event.getType() == GeoGWTEvents.INJECT_WKT) {
			Dispatcher.forwardEvent(GeoGWTEvents.ERASE_FEATURES);
		}
	}

	/**
	 * On zoom to center.
	 */
	private void onZoomToCenter() {
		LonLat center = this.mapLayout.getMap().getCenter();
		this.mapLayout.getMap().setCenter(center, 3);
	}

	// /**
	// * On draw aoi on map.
	// *
	// * @param event
	// * the event
	// */
	// private void onDrawAoiOnMap(AppEvent event) {
	// this.mapLayout.drawAoiOnMap((String) event.getData());
	// Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE, new String[] {
	// "AOI Service",
	// "Zoom to selected AOI." });
	// }

	// /**
	// * On disable draw button.
	// */
	// private void onDisableDrawButton() {
	// this.buttonBar.changeButtonState("drawFeature", false);
	// this.mapLayout.deactivateDrawFeature();
	// }
	//
	// /**
	// * On enable draw button.
	// */
	// private void onEnableDrawButton() {
	// this.buttonBar.changeButtonState("drawFeature", true);
	// this.mapLayout.activateDrawFeature();
	// }

	/**
	 * On erase aoi features.
	 */
	private void onEraseAOIFeatures() {
		this.mapLayout.eraseFeatures();
	}

	/**
	 * On activate draw feature.
	 */
	private void onActivateDrawFeature() {
		// this.mapLayout.activateDrawFeature();
		Dispatcher.forwardEvent(GeoGWTEvents.ACTIVATE_DRAW_FEATURES);
	}

	/**
	 * On deactivate draw feature.
	 */
	private void onDeactivateDrawFeature() {
		// this.mapLayout.deactivateDrawFeature();
		Dispatcher.forwardEvent(GeoGWTEvents.DEACTIVATE_DRAW_FEATURES);
	}

	// /**
	// * On attach toolbar.
	// *
	// * @param event
	// * the event
	// */
	// private void onAttachToolbar(AppEvent event) {
	// mapLayout.setTools(GeofenceUtils.getInstance().getGlobalConfiguration()
	// .getToolbarItemManager().getClientTools());
	//
	// this.buttonBar = new ButtonBar(mapLayout);
	//
	// ContentPanel north = (ContentPanel) event.getData();
	// north.add(buttonBar.getToolBar());
	//
	// north.layout();
	// }
}

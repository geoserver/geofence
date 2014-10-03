/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client;

import org.geoserver.geofence.gui.client.GeofenceUtils;
import org.geoserver.geofence.gui.client.GeofenceEvents;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.geoserver.geofence.gui.client.configuration.GeofenceGlobalConfiguration;
import org.geoserver.geofence.gui.client.mvc.AppController;
import org.geoserver.geofence.gui.client.service.ConfigurationRemote;

// TODO: Auto-generated Javadoc
/**
 * The Class Application.
 */
public class Application implements EntryPoint {

	/** The dispatcher. */
	private Dispatcher dispatcher;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	public void onModuleLoad() {
		GXT.hideLoadingPanel("loading");

		dispatcher = Dispatcher.get();

		dispatcher.addController(new AppController());

		ConfigurationRemote.Util.getInstance().initServerConfiguration(
				new AsyncCallback<GeofenceGlobalConfiguration>() {

					public void onSuccess(GeofenceGlobalConfiguration result) {
						GeofenceUtils.getInstance().setGlobalConfiguration(
								result);
						Dispatcher
								.forwardEvent(GeofenceEvents.INIT_GEOFENCE_WIDGET);

					}

					public void onFailure(Throwable caught) {
						Info.display("Configuration Service Error",
								caught.getMessage());

					}
				});

		// Dispatcher.forwardEvent(GeofenceEvents.INIT_GEOFENCE_WIDGET);

	}
}

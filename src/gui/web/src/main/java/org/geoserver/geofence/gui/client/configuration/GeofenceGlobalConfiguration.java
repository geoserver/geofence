/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.configuration;

import org.geoserver.geofence.gui.client.configuration.IGeofenceConfiguration;
import org.geoserver.geofence.gui.client.configuration.IUserBeanManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// TODO: Auto-generated Javadoc
/**
 * The Class GeofenceGlobalConfiguration.
 */
@Component("geofenceGlobalConfiguration")
public class GeofenceGlobalConfiguration implements IGeofenceConfiguration {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3377836318526396981L;

	/** The profile bean manager. */
	@Autowired
	private IUserBeanManager userBeanManager;

	private String baseLayerURL;
	private String baseLayerName;
	private String baseLayerTitle;
	private String baseLayerFormat;
	private String baseLayerStyle;
	private String mapCenterLon;
	private String mapCenterLat;
	private String mapZoom;
	/**
	 * Gets the profile bean manager.
	 * 
	 * @return the profile bean manager
	 */
	public IUserBeanManager getUserBeanManager() {
		// TODO Auto-generated method stub
		return userBeanManager;
	}

	/**
	 * @param baseLayerURL the baseLayerURL to set
	 */
	public void setBaseLayerURL(String baseLayerURL) {
		this.baseLayerURL = baseLayerURL;
	}

	/**
	 * @return the baseLayerURL
	 */
	public String getBaseLayerURL() {
		return baseLayerURL;
	}

	/**
	 * @param baseLayerName the baseLayerName to set
	 */
	public void setBaseLayerName(String baseLayerName) {
		this.baseLayerName = baseLayerName;
	}

	/**
	 * @return the baseLayerName
	 */
	public String getBaseLayerName() {
		return baseLayerName;
	}

	/**
	 * @param baseLayerTitle the baseLayerTitle to set
	 */
	public void setBaseLayerTitle(String baseLayerTitle) {
		this.baseLayerTitle = baseLayerTitle;
	}

	/**
	 * @return the baseLayerTitle
	 */
	public String getBaseLayerTitle() {
		return baseLayerTitle;
	}

	/**
	 * @param baseLayerFormat the baseLayerFormat to set
	 */
	public void setBaseLayerFormat(String baseLayerFormat) {
		this.baseLayerFormat = baseLayerFormat;
	}

	/**
	 * @return the baseLayerFormat
	 */
	public String getBaseLayerFormat() {
		return baseLayerFormat;
	}

	/**
	 * @param baseLayerStyle the baseLayerStyle to set
	 */
	public void setBaseLayerStyle(String baseLayerStyle) {
		this.baseLayerStyle = baseLayerStyle;
	}

	/**
	 * @return the baseLayerStyle
	 */
	public String getBaseLayerStyle() {
		return baseLayerStyle;
	}

	/**
	 * @return the mapCenterLon
	 */
	public String getMapCenterLon() {
		return mapCenterLon;
	}

	/**
	 * @param mapCenterLon the mapCenterLon to set
	 */
	public void setMapCenterLon(String mapCenterLon) {
		this.mapCenterLon = mapCenterLon;
	}

	/**
	 * @return the mapCenterLat
	 */
	public String getMapCenterLat() {
		return mapCenterLat;
	}

	/**
	 * @param mapCenterLat the mapCenterLat to set
	 */
	public void setMapCenterLat(String mapCenterLat) {
		this.mapCenterLat = mapCenterLat;
	}

	/**
	 * @return the mapZoom
	 */
	public String getMapZoom() {
		return mapZoom;
	}

	/**
	 * @param mapZoom the mapZoom to set
	 */
	public void setMapZoom(String mapZoom) {
		this.mapZoom = mapZoom;
	}

	
}

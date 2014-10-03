/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.map;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapUnits;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.OpenLayers;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.geometry.Geometry;
import org.gwtopenmaps.openlayers.client.geometry.MultiPolygon;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.OSMOptions;
import org.gwtopenmaps.openlayers.client.layer.TransitionEffect;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.layer.VectorOptions;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;


// TODO: Auto-generated Javadoc
/**
 * The Class MapPreviewWidget.
 */
public class MapPreviewWidget
{

    /** The map widget. */
    private MapWidget mapWidget;

    /** The default map options. */
    private MapOptions defaultMapOptions;

    /** The map. */
    private Map map;

    /** The layer. */
    private Layer layer;

    /** The osm. */
    private Layer osm;

    /** The vector. */
    private Vector vector;

    /**
     * Instantiates a new map preview widget.
     */
    public MapPreviewWidget()
    {
        this.createMapOption(true);
    }

    /**
     * Creates the map option.
     *
     * @param isGoogle
     *            the is google
     */
    private void createMapOption(boolean isGoogle)
    {
        // TODO Auto-generated method stub

        OpenLayers.setProxyHost("gwtOpenLayersProxy?targetURL=");

        this.defaultMapOptions = new MapOptions();
        this.defaultMapOptions.setNumZoomLevels(18);
        if (isGoogle)
        {
            this.defaultMapOptions.setProjection("EPSG:900913");
            this.defaultMapOptions.setDisplayProjection(new Projection("EPSG:4326"));
            this.defaultMapOptions.setUnits(MapUnits.METERS);
            this.defaultMapOptions.setMaxExtent(new Bounds(-20037508, -20037508, 20037508,
                    20037508.34));
            this.defaultMapOptions.setMaxResolution(new Double(156543.0339).floatValue());
        }
        else
        {
            this.defaultMapOptions.setProjection("EPSG:4326");
        }

        initMapWidget(this.defaultMapOptions, isGoogle);
    }

    /**
     * Inits the map widget.
     *
     * @param defaultMapOptions
     *            the default map options
     * @param isGoogle
     *            the is google
     */
    private void initMapWidget(MapOptions defaultMapOptions, boolean isGoogle)
    {
        mapWidget = new MapWidget("100%", "100%", defaultMapOptions);
        this.map = mapWidget.getMap();
        // this.map.addControl(new LayerSwitcher());
        if (isGoogle)
        {
            this.createOSM();
            // this.createBaseGoogleLayer();
        }
        else
        {
            WMSParams wmsParams = new WMSParams();
            wmsParams.setFormat("image/png");
            wmsParams.setLayers("basic");
            wmsParams.setStyles("");

            WMSOptions wmsLayerParams = new WMSOptions();
            wmsLayerParams.setTransitionEffect(TransitionEffect.RESIZE);

            layer = new WMS("Basic WMS", "http://labs.metacarta.com/wms/vmap0", wmsParams,
                    wmsLayerParams);
            this.map.addLayer(layer);
        }

        this.initVectorLayer();
    }

    /**
     * Creates the osm.
     */
    private void createOSM()
    {
        this.osm = OSM.THIS("OpenStreetMap Preview", OpenLayers.getProxyHost() +
                "http://tile.openstreetmap.org/${z}/${x}/${y}.png", new OSMOptions());
        ; // OSM.Mapnik("OpenStreetMap Preview");
        this.map.addLayer(osm);
    }

    /**
     * Inits the vector layer.
     */
    private void initVectorLayer()
    {
        VectorOptions vectorOption = new VectorOptions();
        vectorOption.setStyle(this.createStyle());
        vectorOption.setDisplayInLayerSwitcher(false);
        this.vector = new Vector("Geofence PREVIEW Vector Layer", vectorOption);
        this.map.addLayer(vector);
    }

    // private void createBaseGoogleLayer() {
    // GoogleOptions option = new GoogleOptions();
    // option.setType(GMapType.G_NORMAL_MAP);
    // option.setSphericalMercator(true);
    //
    // layer = new Google("Google Normal Preview", option);
    // this.map.addLayer(layer);
    // }

    /**
     * Creates the style.
     *
     * @return the style
     */
    private Style createStyle()
    {
        Style style = new Style();
        style.setStrokeColor("#000000");
        style.setStrokeWidth(1);
        style.setFillColor("#FF0000");
        style.setFillOpacity(0.5);
        style.setPointRadius(5);
        style.setStrokeOpacity(1.0);

        return style;
    }

    /**
     * Draw aoi on map.
     *
     * @param wkt
     *            the wkt
     */
    public void drawAoiOnMap(String wkt)
    {
        this.eraseFeatures();

        MultiPolygon geom = MultiPolygon.narrowToMultiPolygon(Geometry.fromWKT(wkt).getJSObject());
        geom.transform(new Projection("EPSG:4326"), new Projection("EPSG:900913"));

        VectorFeature vectorFeature = new VectorFeature(geom);
        this.vector.addFeature(vectorFeature);
        this.map.zoomToExtent(geom.getBounds());
    }

    /**
     * Erase features.
     */
    public void eraseFeatures()
    {
        this.vector.destroyFeatures();
        this.vector.redraw();
        this.map.updateSize();
    }

    /**
     * Rebuild vector layer.
     */
    public void rebuildVectorLayer()
    {
        this.map.removeLayer(this.vector);
        this.initVectorLayer();
    }

    /**
     * Gets the map widget.
     *
     * @return the map widget
     */
    public MapWidget getMapWidget()
    {
        return mapWidget;
    }

    /**
     * Gets the vector.
     *
     * @return the vector
     */
    public Vector getVector()
    {
        return vector;
    }
}

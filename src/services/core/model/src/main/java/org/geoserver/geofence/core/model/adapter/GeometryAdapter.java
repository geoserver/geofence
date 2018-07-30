/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;

// TODO: Auto-generated Javadoc
/**
 * The Class GeometryAdapter.
 * 
 * @param <G> the generic type
 */
public class GeometryAdapter<G extends Geometry> extends XmlAdapter<String, G> {

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public G unmarshal(String val) throws ParseException {
        WKTReader wktReader = new WKTReader();

        Geometry the_geom = wktReader.read(val);
        if (the_geom.getSRID() == 0)
            the_geom.setSRID(4326);

        try {
            return (G) the_geom;
        } catch (ClassCastException e) {
            throw new ParseException("WKT val is a " + the_geom.getClass().getName());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public String marshal(G the_geom) throws ParseException {
        if (the_geom != null) {
            WKTWriter wktWriter = new WKTWriter();
            if (the_geom.getSRID() == 0)
                the_geom.setSRID(4326);

            return wktWriter.write(the_geom);
        } else {
            throw new ParseException("Geometry obj is null.");
        }
    }
}

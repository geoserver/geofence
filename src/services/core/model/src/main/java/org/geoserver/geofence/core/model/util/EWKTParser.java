/* (c) 2024 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.core.model.util;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

/**
 *
 * @author etj
 */
public class EWKTParser {

    static public Geometry parse(String wkt) throws ParseException {
        if (wkt == null) {
            return null;
        }

        WKTReader reader = new WKTReader();
        Geometry result;
        if (wkt.startsWith("SRID=")) {
            String[] areaAr = wkt.split(";");
            String srid = areaAr[0].split("=")[1];
            result = reader.read(areaAr[1]);
            result.setSRID(Integer.valueOf(srid));
        } else {
            result = reader.read(wkt);
            result.setSRID(4326);
        }
        return result;
    }
}

/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.utils;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

/** @author etj */
public class GeomUtils {
    public static Geometry wktToGeom(String areaWKT) throws ParseException {
        WKTReader reader = new WKTReader();
        Geometry result;
        if (!areaWKT.contains("SRID=")) {
            result = reader.read(areaWKT);
            result.setSRID(4326);
        } else {
            String[] areaAr = areaWKT.split(";");
            String srid = areaAr[0].split("=")[1];
            result = reader.read(areaAr[1]);
            result.setSRID(Integer.parseInt(srid));
        }
        return result;
    }
}

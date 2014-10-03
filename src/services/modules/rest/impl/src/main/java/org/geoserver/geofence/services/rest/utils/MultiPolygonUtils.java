/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.utils;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

import jaitools.jts.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class MultiPolygonUtils
{
    private static final Logger LOGGER = LogManager.getLogger(MultiPolygonUtils.class);

    /**
     * Simplifies a MultiPolygon.
     * <BR/><BR/>
     * Simplification is performed by first removing collinear points, then
     * by applying DouglasPeucker simplification.
     * <BR/>Order <B>is</B> important, since it's more likely to have collinear
     * points before applying any other simplification.
     */
    public static MultiPolygon simplifyMultiPolygon(final MultiPolygon mp)
    {

        final Polygon[] simpPolys = new Polygon[mp.getNumGeometries()];

        for (int i = 0; i < mp.getNumGeometries(); i++)
        {
            Polygon p = (Polygon) mp.getGeometryN(i);
            Polygon s1 = Utils.removeCollinearVertices(p);
            TopologyPreservingSimplifier tps = new TopologyPreservingSimplifier(s1);
            Polygon s2 = (Polygon) tps.getResultGeometry();
            simpPolys[i] = s2;

            if (LOGGER.isInfoEnabled())
            {
                LOGGER.info("RCV: simplified poly " + getPoints(p) +
                    " --> " + getPoints(s1) +
                    " --> " + getPoints(s2));
            }
        }

        // reuse existing factory
        final GeometryFactory gf = mp.getFactory();

        return gf.createMultiPolygon(simpPolys);
    }

    /**
     * Return the number of points of a polygon in the format
     * E+I0+I1+...+In
     * where E is the number of points of the exterior ring and I0..In are
     * the number of points of the Internal rings.
     */
    public static String getPoints(final Polygon p)
    {
        final StringBuilder sb = new StringBuilder();
        sb.append(p.getExteriorRing().getNumPoints());
        for (int i = 0; i < p.getNumInteriorRing(); i++)
        {
            LineString ir = p.getInteriorRingN(i);
            sb.append('+').append(ir.getNumPoints());
        }

        return sb.toString();
    }

}

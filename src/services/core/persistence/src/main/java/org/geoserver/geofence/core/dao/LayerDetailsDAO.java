/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao;

import org.geoserver.geofence.core.model.LayerDetails;

/**
 * Public interface to define operations on LayerDetails
 * 
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */

public interface LayerDetailsDAO 
        extends RestrictedGenericDAO<LayerDetails>,
                AllowedStylesProvider{
}

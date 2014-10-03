/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model.adapter;


import org.geoserver.geofence.core.model.GSInstance;

/**
 * Transform a Profile into its id.
 *
 */
public class FKGSInstanceAdapter extends IdentifiableAdapter<GSInstance>  {

    @Override
    protected GSInstance createInstance() {
        return new GSInstance();
    }

}

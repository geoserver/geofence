/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model.adapter;


import org.geoserver.geofence.core.model.GSUser;

/**
 * Transform a Profile into its id.
 *
 */
public class FKUserAdapter extends IdentifiableAdapter<GSUser>  {

    @Override
    protected GSUser createInstance() {
        return new GSUser();
    }

}

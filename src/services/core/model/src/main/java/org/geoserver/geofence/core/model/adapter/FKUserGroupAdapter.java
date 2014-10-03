/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model.adapter;


import org.geoserver.geofence.core.model.UserGroup;

/**
 * Transform a UserGroup into its id.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class FKUserGroupAdapter extends IdentifiableAdapter<UserGroup>  {

    @Override
    protected UserGroup createInstance() {
        return new UserGroup();
    }

}

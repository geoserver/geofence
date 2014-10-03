/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.api.dto;

/**
 * 
 * @author ETj (etj at geo-solutions.it)
 */
public class RegisteredUser {

    protected String id;

    protected String username;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + "id:" + id + " userName:" + username + ']';
    }
}

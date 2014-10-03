/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.configuration;

import org.geoserver.geofence.gui.client.model.User;

import java.io.Serializable;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Interface IUserBeanManager.
 */
public interface IUserBeanManager extends Serializable {

    /**
     * Gets the users.
     * 
     * @return the users
     */
    public List<User> getUsers();

    /**
     * Configure users.
     */
    public void configureUsers();
}

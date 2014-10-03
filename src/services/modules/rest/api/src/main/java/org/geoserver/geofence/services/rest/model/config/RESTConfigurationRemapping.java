/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model.config;

import org.geoserver.geofence.core.model.GFUser;
import org.geoserver.geofence.core.model.GSInstance;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.services.rest.model.config.adapter.RemapperAdapter;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "Remapping")
public class RESTConfigurationRemapping {

    private Map<Long, Long> users = new HashMap<Long, Long>();
    private Map<Long, Long> userGroups = new HashMap<Long, Long>();
    private Map<Long, Long> instances = new HashMap<Long, Long>();
    private Map<Long, Long> rules = new HashMap<Long, Long>();
    private Map<Long, Long> grUsers = new HashMap<Long, Long>();

    public void remap(Long newId, GSUser old) {
        users.put(old.getId(), newId);
    }

    public void remap(Long newId, GFUser old) {
        grUsers.put(old.getId(), newId);
    }

    public void remap(Long newId, UserGroup old) {
        userGroups.put(old.getId(), newId);
    }

    public void remap(Long newId, GSInstance old) {
        instances.put(old.getId(), newId);
    }

    public void remap(Long newId, Rule old) {
        rules.put(old.getId(), newId);
    }

    @XmlJavaTypeAdapter(RemapperAdapter.class)
    @XmlElement(name = "instances")
    public Map<Long, Long> getInstances() {
        return instances;
    }

    @XmlJavaTypeAdapter(RemapperAdapter.class)
    @XmlElement(name = "userGroups")
    public Map<Long, Long> getUserGroups() {
        return userGroups;
    }

    @XmlJavaTypeAdapter(RemapperAdapter.class)
    @XmlElement(name = "rules")
    public Map<Long, Long> getRules() {
        return rules;
    }

    @XmlJavaTypeAdapter(RemapperAdapter.class)
    @XmlElement(name = "users")
    public Map<Long, Long> getUsers() {
        return users;
    }

    @XmlJavaTypeAdapter(RemapperAdapter.class)
    @XmlElement(name = "internalUsers")
    public Map<Long, Long> getGRUsers() {
        return grUsers;
    }
}

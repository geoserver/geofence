/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model.config;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;
import org.geofence.web.rest.api.model.config.adapter.RemapperAdapter;

/** @author ETj (etj at geo-solutions.it) */
@XmlRootElement(name = "Remapping")
public class RESTConfigurationRemapping {

    private Map<Long, Long> users = new HashMap<>();
    private Map<Long, Long> userGroups = new HashMap<>();
    private Map<Long, Long> instances = new HashMap<>();
    private Map<Long, Long> rules = new HashMap<>();
    private Map<Long, Long> grUsers = new HashMap<>();

    public void remap(Long newId, RESTUser old) {
        users.put(old.getId(), newId);
    }

    public void remap(Long newId, RESTUserGroup old) {
        userGroups.put(old.getId(), newId);
    }

    public void remap(Long newId, RESTInstance old) {
        instances.put(old.getId(), newId);
    }

    public void remap(Long newId, RESTRule old) {
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
}

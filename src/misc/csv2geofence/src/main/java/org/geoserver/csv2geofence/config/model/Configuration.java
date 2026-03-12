/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.csv2geofence.config.model;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class Configuration {

    private UserFileConfig userFileConfig;

    private GeofenceConfig geofenceConfig;

    private RuleFileConfig ruleFileConfig;

    public UserFileConfig getUserFileConfig() {
        return userFileConfig;
    }

    public void setUserFileConfig(UserFileConfig userFileConfig) {
        this.userFileConfig = userFileConfig;
    }

    public GeofenceConfig getGeofenceConfig() {
        return geofenceConfig;
    }

    public void setGeofenceConfig(GeofenceConfig geofenceConfig) {
        this.geofenceConfig = geofenceConfig;
    }

    public RuleFileConfig getRuleFileConfig() {
        return ruleFileConfig;
    }

    public void setRuleFileConfig(RuleFileConfig ruleFileConfig) {
        this.ruleFileConfig = ruleFileConfig;
    }

}

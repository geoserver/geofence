/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.csv2geofence.config.model.internal;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RuleOp {

    String groupName;
    String layerName;
    String verb;

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    /**
     * Case sensitive group name
     */
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"["
                +"group:" + groupName
                + " layer:" + layerName
                + " verb:" + verb+"]";
    }

}

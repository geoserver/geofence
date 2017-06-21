/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;

public class RESTRulePosition {

    public enum RulePosition {

        fixedPriority,
        offsetFromTop,
        offsetFromBottom;
    }

    private RulePosition position;
    private long value;

    public RESTRulePosition() {
    }

    public RESTRulePosition(RulePosition position, long value) {
        this.position = position;
        this.value = value;
    }

//    @XmlAttribute
    public RulePosition getPosition() {
        return position;
    }

    public void setPosition(RulePosition position) {
        this.position = position;
    }

//    @XmlAttribute
    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}

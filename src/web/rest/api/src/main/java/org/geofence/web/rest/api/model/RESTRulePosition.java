/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model;

import jakarta.xml.bind.annotation.XmlAttribute;

public class RESTRulePosition {

    public enum RESTPositionReference {
        fixedPriority,
        offsetFromTop,
        offsetFromBottom
    }

    private RESTRulePosition.RESTPositionReference position;
    private long value;

    public RESTRulePosition() {}

    public RESTRulePosition(RESTRulePosition.RESTPositionReference position, long value) {
        this.position = position;
        this.value = value;
    }

    @XmlAttribute
    public RESTRulePosition.RESTPositionReference getPosition() {
        return position;
    }

    public void setPosition(RESTRulePosition.RESTPositionReference position) {
        this.position = position;
    }

    @XmlAttribute
    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}

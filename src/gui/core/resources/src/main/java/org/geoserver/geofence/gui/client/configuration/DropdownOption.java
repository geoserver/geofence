/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.configuration;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class DropdownOption.
 */
public class DropdownOption implements Serializable {

    /** The Constant LABEL_KEY. */
    public static final String LABEL_KEY = "label";

    /** The Constant VALUE_KEY. */
    public static final String VALUE_KEY = "value";

    /** The label. */
    private String label;

    /** The value. */
    private String value;

    /**
     * Instantiates a new dropdown option.
     */
    public DropdownOption() {
    }

    /**
     * Instantiates a new dropdown option.
     * 
     * @param label
     *            the label
     * @param value
     *            the value
     */
    public DropdownOption(String label, String value) {
        this.label = label;
        this.value = value;
    }

    /**
     * Gets the label.
     * 
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label.
     * 
     * @param label
     *            the new label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Gets the value.
     * 
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     * 
     * @param value
     *            the new value
     */
    public void setValue(String value) {
        this.value = value;
    }
}

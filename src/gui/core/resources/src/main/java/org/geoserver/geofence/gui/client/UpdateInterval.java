/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client;

import com.extjs.gxt.ui.client.data.BeanModel;

// TODO: Auto-generated Javadoc
/**
 * The Class UpdateInterval.
 */
public class UpdateInterval extends BeanModel {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7274712696304091963L;

    /**
     * The Enum UpdateIntervalEnum.
     */
    public enum UpdateIntervalEnum {

        /** The TIME. */
        TIME("time");

        /** The value. */
        private String value;

        /**
         * Instantiates a new update interval enum.
         * 
         * @param value
         *            the value
         */
        UpdateIntervalEnum(String value) {
            this.value = value;
        }

        /**
         * Gets the value.
         * 
         * @return the value
         */
        public String getValue() {
            return value;
        }
    }

    /**
     * Instantiates a new update interval.
     * 
     * @param time
     *            the time
     */
    public UpdateInterval(String time) {
        setTime(time);
    }

    /**
     * Sets the time.
     * 
     * @param time
     *            the new time
     */
    public void setTime(String time) {
        set(UpdateIntervalEnum.TIME.getValue(), time);
    }

    /**
     * Gets the time.
     * 
     * @return the time
     */
    public String getTime() {
        return get(UpdateIntervalEnum.TIME.getValue());
    }
}

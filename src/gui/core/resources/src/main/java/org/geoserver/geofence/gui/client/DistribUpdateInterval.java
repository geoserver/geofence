/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client;

import com.extjs.gxt.ui.client.data.BeanModel;

// TODO: Auto-generated Javadoc
/**
 * The Class DistribUpdateInterval.
 */
public class DistribUpdateInterval extends BeanModel {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2604147167853598222L;

    /**
     * The Enum DistribUpdateIntervalEnum.
     */
    public enum DistribUpdateIntervalEnum {

        /** The TIME. */
        TIME("distribTime");

        /** The value. */
        private String value;

        /**
         * Instantiates a new distrib update interval enum.
         * 
         * @param value
         *            the value
         */
        DistribUpdateIntervalEnum(String value) {
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
     * Instantiates a new distrib update interval.
     * 
     * @param time
     *            the time
     */
    public DistribUpdateInterval(String time) {
        setTime(time);
    }

    /**
     * Sets the time.
     * 
     * @param time
     *            the new time
     */
    public void setTime(String time) {
        set(DistribUpdateIntervalEnum.TIME.getValue(), time);
    }

    /**
     * Gets the time.
     * 
     * @return the time
     */
    public String getTime() {
        return get(DistribUpdateIntervalEnum.TIME.getValue());
    }
}

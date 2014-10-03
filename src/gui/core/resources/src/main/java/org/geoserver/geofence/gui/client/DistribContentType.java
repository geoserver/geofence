/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client;

import com.extjs.gxt.ui.client.data.BeanModel;

// TODO: Auto-generated Javadoc
/**
 * The Class DistribContentType.
 */
public class DistribContentType extends BeanModel {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3782585471721426981L;

    /**
     * The Enum DistribContentTypeEnum.
     */
    public enum DistribContentTypeEnum {

        /** The TYPE. */
        TYPE("distribContentType");

        /** The value. */
        private String value;

        /**
         * Instantiates a new distrib content type enum.
         * 
         * @param value
         *            the value
         */
        DistribContentTypeEnum(String value) {
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
     * Instantiates a new distrib content type.
     * 
     * @param time
     *            the time
     */
    public DistribContentType(String time) {
        setType(time);
    }

    /**
     * Sets the type.
     * 
     * @param time
     *            the new type
     */
    public void setType(String time) {
        set(DistribContentTypeEnum.TYPE.getValue(), time);
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public String getType() {
        return get(DistribContentTypeEnum.TYPE.getValue());
    }
}

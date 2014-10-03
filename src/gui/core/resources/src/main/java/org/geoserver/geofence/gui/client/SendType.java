/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client;

import com.extjs.gxt.ui.client.data.BeanModel;

// TODO: Auto-generated Javadoc
/**
 * The Class SendType.
 */
public class SendType extends BeanModel {

    /**
     * The Enum SendTypeEnum.
     */
    public enum SendTypeEnum {

        /** The TYPE. */
        TYPE("type");

        /** The value. */
        private String value;

        /**
         * Instantiates a new send type enum.
         * 
         * @param value
         *            the value
         */
        SendTypeEnum(String value) {
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

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6754712996337001119L;

    /**
     * Instantiates a new send type.
     * 
     * @param type
     *            the type
     */
    public SendType(String type) {
        setType(type);
    }

    /**
     * Sets the type.
     * 
     * @param type
     *            the new type
     */
    public void setType(String type) {
        set(SendTypeEnum.TYPE.getValue(), type);
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public String getType() {
        return get(SendTypeEnum.TYPE.getValue());
    }

}

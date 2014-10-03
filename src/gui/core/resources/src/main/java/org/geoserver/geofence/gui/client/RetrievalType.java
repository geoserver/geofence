/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client;

import com.extjs.gxt.ui.client.data.BeanModel;

// TODO: Auto-generated Javadoc
/**
 * The Class RetrievalType.
 */
public class RetrievalType extends BeanModel {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 578387084077752687L;

    /**
     * The Enum RetrievalTypeEnum.
     */
    public enum RetrievalTypeEnum {

        /** The TYPE. */
        TYPE("retrievalType");

        /** The value. */
        private String value;

        /**
         * Instantiates a new retrieval type enum.
         * 
         * @param value
         *            the value
         */
        RetrievalTypeEnum(String value) {
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
     * Instantiates a new retrieval type.
     * 
     * @param type
     *            the type
     */
    public RetrievalType(String type) {
        setType(type);
    }

    /**
     * Sets the type.
     * 
     * @param type
     *            the new type
     */
    public void setType(String type) {
        set(RetrievalTypeEnum.TYPE.getValue(), type);
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public String getType() {
        return get(RetrievalTypeEnum.TYPE.getValue());
    }
}

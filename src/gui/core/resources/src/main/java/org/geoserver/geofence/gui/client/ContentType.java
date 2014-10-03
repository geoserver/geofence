/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client;

import com.extjs.gxt.ui.client.data.BeanModel;

// TODO: Auto-generated Javadoc
/**
 * The Class ContentType.
 */
public class ContentType extends BeanModel {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5218642838039771231L;

    /**
     * The Enum ContentTypeEnum.
     */
    public enum ContentTypeEnum {

        /** The TYPE. */
        TYPE("contentType");

        /** The value. */
        private String value;

        /**
         * Instantiates a new content type enum.
         * 
         * @param value
         *            the value
         */
        ContentTypeEnum(String value) {
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
     * Instantiates a new content type.
     * 
     * @param type
     *            the type
     */
    public ContentType(String type) {
        setType(type);
    }

    /**
     * Sets the type.
     * 
     * @param type
     *            the new type
     */
    public void setType(String type) {
        set(ContentTypeEnum.TYPE.getValue(), type);
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public String getType() {
        return get(ContentTypeEnum.TYPE.getValue());
    }
}

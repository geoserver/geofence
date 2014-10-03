/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client;

import com.extjs.gxt.ui.client.data.BeanModel;

// TODO: Auto-generated Javadoc
/**
 * The Class FilterType.
 */
public class FilterType extends BeanModel {

    /**
     * The Enum FilterTypeEnum.
     */
    public enum FilterTypeEnum {

        /** The TYPE. */
        TYPE("type");

        /** The value. */
        private String value;

        /**
         * Instantiates a new filter type enum.
         * 
         * @param value
         *            the value
         */
        FilterTypeEnum(String value) {
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
    private static final long serialVersionUID = 4342198507231122012L;

    /**
     * Instantiates a new filter type.
     * 
     * @param type
     *            the type
     */
    public FilterType(String type) {
        setTyper(type);
    }

    /**
     * Sets the typer.
     * 
     * @param type
     *            the new typer
     */
    public void setTyper(String type) {
        set(FilterTypeEnum.TYPE.getValue(), type);

    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public String getType() {
        return get(FilterTypeEnum.TYPE.getValue());
    }

}

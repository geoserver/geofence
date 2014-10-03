/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

// TODO: Auto-generated Javadoc
/**
 * The Class SaveStaus.
 */
public class SaveStaus extends StatusWidget {

    /**
     * The Enum EnumSaveStatus.
     */
    public enum EnumSaveStatus {

        /** The STATU s_ save. */
        STATUS_SAVE("x-status-ok"),
        
        /** The STATU s_ n o_ save. */
        STATUS_NO_SAVE("x-status-not-ok"),
        
        /** The STATU s_ sav e_ error. */
        STATUS_SAVE_ERROR("x-status-error"),
        
        /** The STATU s_ messag e_ save. */
        STATUS_MESSAGE_SAVE("Operation Ok"),
        
        /** The STATU s_ messag e_ no t_ save. */
        STATUS_MESSAGE_NOT_SAVE("Operation Failed"),
        
        /** The STATU s_ messag e_ sav e_ error. */
        STATUS_MESSAGE_SAVE_ERROR("Service Error");

        /** The value. */
        private String value;

        /**
         * Instantiates a new enum save status.
         * 
         * @param value
         *            the value
         */
        EnumSaveStatus(String value) {
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

}

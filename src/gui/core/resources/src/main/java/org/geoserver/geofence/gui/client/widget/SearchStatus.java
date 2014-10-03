/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

// TODO: Auto-generated Javadoc
/**
 * The Class SearchStatus.
 */
public class SearchStatus extends StatusWidget {

    /**
     * The Enum EnumSearchStatus.
     */
    public enum EnumSearchStatus {

        /** The STATU s_ search. */
        STATUS_SEARCH("x-status-ok"),
        
        /** The STATU s_ n o_ search. */
        STATUS_NO_SEARCH("x-status-not-ok"),
        
        /** The STATU s_ searc h_ error. */
        STATUS_SEARCH_ERROR("x-status-error"),
        
        /** The STATU s_ messag e_ search. */
        STATUS_MESSAGE_SEARCH("Search OK"),
        
        /** The STATU s_ messag e_ no t_ search. */
        STATUS_MESSAGE_NOT_SEARCH("No Results Found"),
        
        /** The STATU s_ messag e_ searc h_ error. */
        STATUS_MESSAGE_SEARCH_ERROR("Search Service Error"),
        
        /** The STATU s_ messag e_ use r_ detai l_ error. */
        STATUS_MESSAGE_USER_DETAIL_ERROR("User Detail Error"),
        
        /** The STATU s_ messag e_ use r_ detail. */
        STATUS_MESSAGE_USER_DETAIL("User Detail Ok"),
        
        /** The STATU s_ messag e_ ao i_ detai l_ error. */
        STATUS_MESSAGE_AOI_DETAIL_ERROR("AOI Deatil Error"),
        
        /** The STATU s_ messag e_ ao i_ detail. */
        STATUS_MESSAGE_AOI_DETAIL("AOI Deatil Ok"),
        
        /** The STATU s_ messag e_ ao i_ unshar e_ error. */
        STATUS_MESSAGE_AOI_UNSHARE_ERROR("Unshare AOI Error"),
        
        /** The STATU s_ messag e_ ao i_ unshare. */
        STATUS_MESSAGE_AOI_UNSHARE("Unshare AOI Ok"),
        
        /** The STATU s_ messag e_ membe r_ detail. */
        STATUS_MESSAGE_MEMBER_DETAIL("Member Detail Ok");

        /** The value. */
        private String value;

        /**
         * Instantiates a new enum search status.
         * 
         * @param value
         *            the value
         */
        EnumSearchStatus(String value) {
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

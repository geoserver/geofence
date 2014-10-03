/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

// TODO: Auto-generated Javadoc

import org.geoserver.geofence.gui.client.widget.StatusWidget;

/**
 * The Class LoginStatus.
 */
public class LoginStatus extends StatusWidget
{

    /**
     * The Enum EnumLoginStatus.
     */
    public enum EnumLoginStatus
    {

        /** The STATU s_ login. */
        STATUS_LOGIN("x-status-ok"),

        /** The STATU s_ n o_ login. */
        STATUS_NO_LOGIN("x-status-not-ok"),

        /** The STATU s_ logi n_ error. */
        STATUS_LOGIN_ERROR("x-status-error"),

        /** The STATU s_ messag e_ login. */
        STATUS_MESSAGE_LOGIN("Login OK"),

        /** The STATU s_ messag e_ no t_ login. */
        STATUS_MESSAGE_NOT_LOGIN("Login Failed"),

        /** The STATU s_ messag e_ logi n_ error. */
        STATUS_MESSAGE_LOGIN_ERROR("Login Service Error");

        /** The value. */
        private String value;

        /**
         * Instantiates a new enum login status.
         *
         * @param value
         *            the value
         */
        EnumLoginStatus(String value)
        {
            this.value = value;
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        public String getValue()
        {
            return value;
        }
    }

}

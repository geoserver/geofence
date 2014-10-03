/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import com.extjs.gxt.ui.client.widget.Status;

// TODO: Auto-generated Javadoc
/**
 * The Class StatusWidget.
 */
public class StatusWidget extends Status {

    /**
     * Instantiates a new status widget.
     */
    public StatusWidget() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.extjs.gxt.ui.client.widget.Status#setBusy(java.lang.String)
     */
    @Override
    public void setBusy(String text) {
        setIconStyle("x-loading-status");
        setText(text);
    }

}

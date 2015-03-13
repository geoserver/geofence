/*
 *  Copyright (C) 2007-2012 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.geoserver.geofence.gui.client.widget.tab;

import org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteServiceAsync;

import com.extjs.gxt.ui.client.widget.TabItem;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author DamianoG
 *
 */
public class TabUtils {

    /**
     * This method performs a server-side async call to retrieve the value in the configuration 
     * deactivate the tab provided as parameter
     * 
     * @param gsManagerServiceRemote
     * @param tabToDeactivate
     */
    public static void deactivateTabIfNeeded(GsUsersManagerRemoteServiceAsync gsManagerServiceRemote, final TabItem tabToDeactivate){
        
        AsyncCallback callback = new AsyncCallback() {
            public void onSuccess(Object result) {
                if(!(Boolean)result){
                    tabToDeactivate.setEnabled(false);
                    tabToDeactivate.setVisible(false);
                }
            }

            public void onFailure(Throwable caught) {
                
            }
          };
        gsManagerServiceRemote.activateUserGroupTabs(callback);
    }
}

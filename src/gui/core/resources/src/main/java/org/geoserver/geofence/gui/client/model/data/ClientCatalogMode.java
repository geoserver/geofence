/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.model.data;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.google.gwt.user.client.rpc.IsSerializable;

import org.geoserver.geofence.gui.client.model.BeanKeyValue;

public class ClientCatalogMode extends BeanModel implements IsSerializable {

    private static final long serialVersionUID = -3711302358289438532L;

    public static final String NAME_DEFAULT = "DEFAULT";
    public static final String NAME_HIDE = "HIDE";
    public static final String NAME_MIXED = "MIXED";
    public static final String NAME_CHALLENGE = "CHALLENGE";

    public static final ClientCatalogMode DEFAULT = new ClientCatalogMode(NAME_DEFAULT);
    public static final ClientCatalogMode HIDE = new ClientCatalogMode(NAME_HIDE);
    public static final ClientCatalogMode MIXED = new ClientCatalogMode(NAME_MIXED);
    public static final ClientCatalogMode CHALLENGE = new ClientCatalogMode(NAME_CHALLENGE);

    private String mode;

    protected ClientCatalogMode(String mode) {
        setCatalogMode(mode);
    }

    public ClientCatalogMode() {
    }

    public void setCatalogMode(String mode) {
        this.mode = mode;
        set(BeanKeyValue.CATALOG_MODE.getValue(), this.mode);
    }

    public String getCatalogMode() {
        return mode;
    }

    @Override
    public int hashCode() {
        return mode.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ClientCatalogMode)) {
            return false;
        }

        ClientCatalogMode other = (ClientCatalogMode) obj;

        return this.mode.equals(other.mode);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CatalogMode [");
        if (mode != null) {
            builder.append("mode=").append(mode);
        }
        builder.append("]");

        return builder.toString();
    }

}

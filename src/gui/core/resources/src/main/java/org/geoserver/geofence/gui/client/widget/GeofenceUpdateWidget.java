/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import org.geoserver.geofence.gui.client.form.GeofenceFormWidget;
import org.geoserver.geofence.gui.client.widget.binding.GeofenceUserFormBinding;

import com.extjs.gxt.ui.client.data.BaseModel;

// TODO: Auto-generated Javadoc
/**
 * The Class GeofenceUpdateWidget.
 * 
 * @param <T>
 *            the generic type
 */
public abstract class GeofenceUpdateWidget<T extends BaseModel> extends GeofenceFormWidget {

    /** The object. */
    protected T object;

    /** The form binding. */
    protected GeofenceUserFormBinding formBinding;

    /**
     * Instantiates a new geo repo update widget.
     */
    public GeofenceUpdateWidget() {
        super();
        this.formBinding = new GeofenceUserFormBinding(formPanel, true);
    }

    /**
     * Bind.
     * 
     * @param model
     *            the model
     */
    public void bind(T model) {
        this.object = model;
        this.formBinding.bind(this.object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.geoserver.geofence.gui.client.form.GEOFENCEFormWidget#reset()
     */
    @Override
    public void reset() {
        this.saveStatus.clearStatus("");
    }

}

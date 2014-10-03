/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.widget.form.FormPanel;

// TODO: Auto-generated Javadoc
/**
 * The Class GeofenceBindingWidget.
 * 
 * @param <T>
 *            the generic type
 */

public abstract class GeofenceBindingWidget<T extends BaseModel> {

    /** The form panel. */
    protected FormPanel formPanel;

    /** The form binding. */
    protected FormBinding formBinding;

    /** The model. */
    private T model;

    /**
     * Bind model.
     * 
     * @param model
     *            the model
     */
    public void bindModel(T model) {
        this.model = model;
        this.formBinding.bind(model);
    }

    /**
     * Un bind model.
     */
    public void unBindModel() {
        this.formBinding.unbind();
    }

    /**
     * Gets the form binding.
     * 
     * @return the form binding
     */
    public FormBinding getFormBinding() {
        return formBinding;
    }

    /**
     * Gets the model.
     * 
     * @return the model
     */
    public T getModel() {
        return model;
    }

    /**
     * Creates the form panel.
     * 
     * @return the form panel
     */
    public abstract FormPanel createFormPanel();

    /**
     * Gets the form panel.
     * 
     * @return the form panel
     */
    public FormPanel getFormPanel() {
        return formPanel;
    }

}
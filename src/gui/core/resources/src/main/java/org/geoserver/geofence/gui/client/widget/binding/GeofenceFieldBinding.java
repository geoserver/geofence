/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.binding;

import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.widget.form.Field;

// TODO: Auto-generated Javadoc
/**
 * The Class GeofenceFieldBinding.
 */
public class GeofenceFieldBinding extends FieldBinding {

    /** The old value. */
    private Object oldValue;

    /**
     * Instantiates a new geo repo field binding.
     * 
     * @param field
     *            the field
     * @param property
     *            the property
     */
    @SuppressWarnings("rawtypes")
    public GeofenceFieldBinding(Field field, String property) {
        super(field, property);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.extjs.gxt.ui.client.binding.FieldBinding#updateField(boolean)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void updateField(boolean updateOriginalValue) {
        Object val = onConvertModelValue(model.get(property));

        if (oldValue == null)
            oldValue = val;

        field.setValue(val);
        if (updateOriginalValue) {
            field.setOriginalValue(val);
        }
    }

    /**
     * Reset value.
     */
    @SuppressWarnings("unchecked")
    public void resetValue() {
        oldValue = onConvertModelValue(oldValue);

        field.setValue(oldValue);

        model.set(property, oldValue.toString());
    }

}

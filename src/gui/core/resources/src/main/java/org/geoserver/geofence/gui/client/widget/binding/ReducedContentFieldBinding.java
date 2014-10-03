/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.binding;

import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;

// TODO: Auto-generated Javadoc
/**
 * The Class ReducedContentFieldBinding.
 */
public class ReducedContentFieldBinding extends FieldBinding {

    /** The old value. */
    private Object oldValue;

    /**
     * Instantiates a new reduced content field binding.
     * 
     * @param field
     *            the field
     * @param property
     *            the property
     */
    @SuppressWarnings("rawtypes")
    public ReducedContentFieldBinding(Field field, String property) {
        super(field, property);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.extjs.gxt.ui.client.binding.FieldBinding#updateField(boolean)
     */
    @Override
    public void updateField(boolean updateOriginalValue) {
        Object val = onConvertModelValue(model.get(property));

        if (oldValue == null)
            oldValue = val;

        ((CheckBox) field).setValue((Boolean) val);

        if (updateOriginalValue) {
            ((CheckBox) field).setValue((Boolean) val);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.extjs.gxt.ui.client.binding.FieldBinding#updateModel()
     */
    @Override
    @SuppressWarnings("unchecked")
    public void updateModel() {
        Object val = onConvertFieldValue(field.getValue());
        if (store != null) {
            Record r = store.getRecord(model);
            if (r != null) {
                r.setValid(property, field.isValid());
                r.set(property, val);
            }
        } else {
            // model.set(property, ((UpdateInterval) val).getTime());
            //TODO
//            ((User) model).setReducedContent((Boolean) val);
        }
    }

    /**
     * Reset value.
     */
    public void resetValue() {
        oldValue = onConvertFieldValue(field.getValue());

        ((CheckBox) field).setValue((Boolean) oldValue);

        // model.set(property, oldValue.toString());
        // TODO
//        ((User) model).setReducedContent((Boolean) oldValue);
    }

}

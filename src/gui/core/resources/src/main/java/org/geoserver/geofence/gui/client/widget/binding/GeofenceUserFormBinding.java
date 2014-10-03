/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.binding;

import org.geoserver.geofence.gui.client.model.BeanKeyValue;

import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;

// TODO: Auto-generated Javadoc
/**
 * The Class GeofenceUserFormBinding.
 */
public class GeofenceUserFormBinding extends FormBinding {

    /**
     * Instantiates a new geo repo user form binding.
     * 
     * @param panel
     *            the panel
     * @param autoBind
     *            the auto bind
     */
    public GeofenceUserFormBinding(FormPanel panel, boolean autoBind) {
        super(panel, autoBind);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.extjs.gxt.ui.client.binding.FormBinding#autoBind()
     */
    @Override
    @SuppressWarnings("rawtypes")
    public void autoBind() {
        for (Field f : panel.getFields()) {
            if (!bindings.containsKey(f.getId())) {
                String name = f.getName();
                if (name != null) {
                    FieldBinding b;
//                    if (f.getId().equals(BeanKeyValue.REDUCED_CONTENT_UPDATE.getValue()))
//                        b = new ReducedContentFieldBinding(f, f.getName());
//                    else
                        b = new GeofenceFieldBinding(f, f.getName());
                    bindings.put(f.getId(), b);
                }
            }
        }
    }

}

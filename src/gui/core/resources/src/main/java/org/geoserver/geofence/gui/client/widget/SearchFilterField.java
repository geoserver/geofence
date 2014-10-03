/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.IconSupport;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

// TODO: Auto-generated Javadoc
/**
 * The Class SearchFilterField.
 * 
 * @param <T>
 *            the generic type
 */
public abstract class SearchFilterField<T extends ModelData> extends StoreFilterField<T> implements
        IconSupport {

    /** The style. */
    private String style;

    /** The icon. */
    protected AbstractImagePrototype icon;

    /**
     * Instantiates a new search filter field.
     */
    public SearchFilterField() {
        this.style = "x-menu-item";
    }

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.form.StoreFilterField#doSelect(com.extjs.gxt.ui.client.store.Store, com.extjs.gxt.ui.client.data.ModelData, com.extjs.gxt.ui.client.data.ModelData, java.lang.String, java.lang.String)
     */
    @Override
    protected abstract boolean doSelect(Store<T> store, T parent, T record, String property,
            String filter);

    /*
     * (non-Javadoc)
     * 
     * @see com.extjs.gxt.ui.client.widget.IconSupport#getIcon()
     */
    public AbstractImagePrototype getIcon() {
        return icon;
    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.extjs.gxt.ui.client.widget.IconSupport#setIcon(com.google.gwt.user.client.ui.
     * AbstractImagePrototype)
     */
    public void setIcon(AbstractImagePrototype icon) {
        if (rendered) {
            El oldIcon = el().selectNode(style);
            if (oldIcon != null) {
                oldIcon.remove();
            }
            if (icon != null) {
                Element e = icon.createElement().cast();
                El.fly(e).addStyleName(style);
                el().insertChild(e, 0);
            }
        }
        this.icon = icon;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.extjs.gxt.ui.client.widget.IconSupport#setIconStyle(java.lang.String)
     */
    public void setIconStyle(String icon) {
        setIcon(IconHelper.create(icon));
    }

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.form.TriggerField#afterRender()
     */
    @Override
    protected void afterRender() {
        super.afterRender();
        setIcon(icon);
    }

}
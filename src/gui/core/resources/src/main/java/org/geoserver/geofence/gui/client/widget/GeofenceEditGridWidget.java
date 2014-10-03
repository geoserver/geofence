/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import org.geoserver.geofence.gui.client.form.GeofenceFormWidget;

import java.util.List;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;

// TODO: Auto-generated Javadoc
/**
 * The Class GeofenceGridWidget.
 * 
 * @param <T>
 *            the generic type
 */
public abstract class GeofenceEditGridWidget<T extends BaseModel>  extends GeofenceFormWidget{

    /** The store. */
    protected ListStore<T> store;

    /** The grid. */
    protected Grid<T> grid;

    /**
     * Instantiates a new geo repo grid widget.
     */
    public GeofenceEditGridWidget() {
        createStore();
        initGrid();
    }

    /**
     * Instantiates a new geo repo grid widget.
     * 
     * @param models
     *            the models
     */
    public GeofenceEditGridWidget(List<T> models) {
        createStore();
        this.store.add(models);
        initGrid();
    }

    /**
     * Inits the grid.
     */
    private void initGrid() {
        ColumnModel cm = prepareColumnModel();

        grid = new Grid<T>(store, cm);
        grid.setBorders(true);

        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
      
        grid.setHeight("95%");
        grid.setLazyRowRender(0);
        setGridProperties();
    }

    /**
     * Sets the grid properties.
     */
    public abstract void setGridProperties();

    /**
     * Prepare column model.
     * 
     * @return the column model
     */
    public abstract ColumnModel prepareColumnModel();

    /**
     * Creates the store.
     */
    public abstract void createStore();

    /**
     * Gets the grid.
     * 
     * @return the grid
     */
    public Grid<T> getGrid() {
        return grid;
    }

    /**
     * Gets the store.
     * 
     * @return the store
     */
    public ListStore<T> getStore() {
        return store;
    }

}

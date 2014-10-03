/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import com.extjs.gxt.ui.client.widget.grid.Grid;

import org.geoserver.geofence.gui.client.model.Rule;


// TODO: Auto-generated Javadoc
/**
 * The Class GridStatus.
 */
public class GridStatus
{


    /** The model. */
    private Rule model;

    /** The grid. */
    private Grid<Rule> grid;

    /**
     * Instantiates a new grid status.
     *
     * @param grid
     *            the grid
     * @param rule
     *            the rule
     */
    public GridStatus(Grid<Rule> grid, Rule rule)
    {
        this.grid = grid;
        this.model = rule;
    }

    /**
     * Gets the model.
     *
     * @return the model
     */
    public Rule getModel()
    {
        return model;
    }

    /**
     * Sets the model.
     *
     * @param model
     *            the new model
     */
    public void setModel(Rule model)
    {
        this.model = model;
    }

    /**
     * Gets the grid.
     *
     * @return the grid
     */
    public Grid<Rule> getGrid()
    {
        return grid;
    }

    /**
     * Sets the grid.
     *
     * @param grid
     *            the new grid
     */
    public void setGrid(Grid<Rule> grid)
    {
        this.grid = grid;
    }
}

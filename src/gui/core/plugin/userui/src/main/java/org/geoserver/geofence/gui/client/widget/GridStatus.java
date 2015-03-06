/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import com.extjs.gxt.ui.client.widget.grid.Grid;

import org.geoserver.geofence.gui.client.model.RuleModel;


// TODO: Auto-generated Javadoc
/**
 * The Class GridStatus.
 */
public class GridStatus
{


    /** The model. */
    private RuleModel model;

    /** The grid. */
    private Grid<RuleModel> grid;

    /**
     * Instantiates a new grid status.
     *
     * @param grid
     *            the grid
     * @param rule
     *            the rule
     */
    public GridStatus(Grid<RuleModel> grid, RuleModel rule)
    {
        this.grid = grid;
        this.model = rule;
    }

    /**
     * Gets the model.
     *
     * @return the model
     */
    public RuleModel getModel()
    {
        return model;
    }

    /**
     * Sets the model.
     *
     * @param model
     *            the new model
     */
    public void setModel(RuleModel model)
    {
        this.model = model;
    }

    /**
     * Gets the grid.
     *
     * @return the grid
     */
    public Grid<RuleModel> getGrid()
    {
        return grid;
    }

    /**
     * Sets the grid.
     *
     * @param grid
     *            the new grid
     */
    public void setGrid(Grid<RuleModel> grid)
    {
        this.grid = grid;
    }
}

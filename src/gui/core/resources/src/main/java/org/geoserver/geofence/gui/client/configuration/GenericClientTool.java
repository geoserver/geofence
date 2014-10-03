/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.configuration;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class GenericClientTool.
 */
public class GenericClientTool implements Comparable<GenericClientTool>, Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4077220993928371589L;

    /** The id. */
    private String id;

    /** The order. */
    private int order;

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     * 
     * @param id
     *            the new id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the order.
     * 
     * @return the order
     */
    public int getOrder() {
        return order;
    }

    /**
     * Sets the order.
     * 
     * @param order
     *            the new order
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GenericClientTool [ ID = " + id + ", ORDER = " + order + "]";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(GenericClientTool o) {
        return getOrder() - o.getOrder();
    }
}

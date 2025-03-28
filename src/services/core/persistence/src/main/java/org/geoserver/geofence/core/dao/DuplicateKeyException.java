/* (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao;

/**
*
* @author ETj (etj at geo-solutions.it) */
public class DuplicateKeyException extends RuntimeException {
    static final long serialVersionUID = 3804897190745766939L;   

    public DuplicateKeyException() {
    }

    public DuplicateKeyException(String message) {
        super(message);
    }

}

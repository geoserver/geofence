/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db.dao;

import java.util.List;

/** @author Emanuele Tajariol (etj at geo-solutions.it) */
public interface BaseDAO<ENTITY> {
    public List<ENTITY> findAll();

    public ENTITY find(Long id);

    public void persist(ENTITY entity);
    //    public void persist(Collection<ENTITY> entities);
    public ENTITY merge(ENTITY entity);

    public boolean remove(ENTITY entity);

    public boolean removeById(Long id);
}

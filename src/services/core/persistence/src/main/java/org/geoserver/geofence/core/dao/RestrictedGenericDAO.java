/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao;

import java.util.List;

import com.googlecode.genericdao.search.ISearch;

/**
 * Public interface to define a restricted set of operation wrt to ones
 * defined in GenericDAO.
 * This may be useful if some constraints are implemented in the DAO, so that fewer
 * point of access are allowed.
 * 
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */

public interface RestrictedGenericDAO<ENTITY> /* extends GenericDAO<ENTITY, Long> */{

    public List<ENTITY> findAll();
    public ENTITY find(Long id);
    public void persist(ENTITY... entities);
    public ENTITY merge(ENTITY entity);
    public boolean remove(ENTITY entity);
    public boolean removeById(Long id);
    public List<ENTITY> search(ISearch search);
    public int count(ISearch search);
}

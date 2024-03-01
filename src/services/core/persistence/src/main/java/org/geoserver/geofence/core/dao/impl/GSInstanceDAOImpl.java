/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import java.util.List;


import org.geoserver.geofence.core.dao.GSInstanceDAO;
import org.geoserver.geofence.core.dao.search.Search;
import org.geoserver.geofence.core.model.GSInstance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.transaction.annotation.Transactional;


/**
 * Public implementation of the GSInstanceDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Transactional(value = "geofenceTransactionManager")
public class GSInstanceDAOImpl extends BaseDAO<GSInstance, Long> implements GSInstanceDAO
{

    private static final Logger LOGGER = LogManager.getLogger(GSInstanceDAOImpl.class);

    public GSInstanceDAOImpl() {
        super(GSInstance.class);
    }    
    
    @Override
    public void persist(GSInstance... entities)
    {
        super.persist(entities);
    }

    @Override
    public List<GSInstance> findAll()
    {
        return super.findAll();
    }

    @Override
    public List<GSInstance> search(Search search)
    {
        return super.search(search);
    }

    @Override
    public GSInstance merge(GSInstance entity)
    {
        return super.merge(entity);
    }

    @Override
    public void remove(GSInstance entity)
    {
        super.remove(entity);
    }

    @Override
    public boolean removeById(Long id)
    {
        return super.removeById(id);
    }

}

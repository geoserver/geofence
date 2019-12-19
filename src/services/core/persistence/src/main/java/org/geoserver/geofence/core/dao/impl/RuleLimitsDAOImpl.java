/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import org.geoserver.geofence.core.dao.RuleLimitsDAO;
import org.geoserver.geofence.core.dao.search.Search;
import org.geoserver.geofence.core.model.RuleLimits;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;


/**
 * Public implementation of the RuleLimitsDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Transactional(value = "geofenceTransactionManager")
public class RuleLimitsDAOImpl // 
        extends BaseDAO<RuleLimits, Long> // 
        implements RuleLimitsDAO
{
    private static final Logger LOGGER = LogManager.getLogger(RuleLimitsDAOImpl.class);

    public RuleLimitsDAOImpl() {
        super(RuleLimits.class);
    }
    
    @Override
    public void persist(RuleLimits... entities)
    {
        super.persist(entities);
    }

    @Override
    public List<RuleLimits> findAll()
    {
        return super.findAll();
    }

    @Override
    public List<RuleLimits> search(Search search)
    {
        return super.search(search);
    }

    @Override
    public RuleLimits merge(RuleLimits entity)
    {
        return super.merge(entity);
    }

    @Override
    public void remove(RuleLimits entity)
    {
        super.remove(entity);
    }

    @Override
    public boolean removeById(Long id)
    {
        return super.removeById(id);
    }

}

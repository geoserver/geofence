/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import java.util.List;

import com.googlecode.genericdao.search.ISearch;

import org.geoserver.geofence.core.dao.RuleLimitsDAO;
import org.geoserver.geofence.core.model.RuleLimits;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.transaction.annotation.Transactional;


/**
 * Public implementation of the RuleLimitsDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Transactional(value = "geofenceTransactionManager")
public class RuleLimitsDAOImpl extends BaseDAO<RuleLimits, Long> implements RuleLimitsDAO
{

    private static final Logger LOGGER = LogManager.getLogger(RuleLimitsDAOImpl.class);

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
    public List<RuleLimits> search(ISearch search)
    {
        return super.search(search);
    }

    @Override
    public RuleLimits merge(RuleLimits entity)
    {
        return super.merge(entity);
    }

    @Override
    public boolean remove(RuleLimits entity)
    {
        return super.remove(entity);
    }

    @Override
    public boolean removeById(Long id)
    {
        return super.removeById(id);
    }

}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;


import org.geoserver.geofence.core.dao.GFUserDAO;
import org.geoserver.geofence.core.model.GFUser;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.search.ISearch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Public implementation of the GSUserDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Transactional(value = "geofenceTransactionManager")
public class GFUserDAOImpl extends BaseDAO<GFUser, Long> implements GFUserDAO
{

    private static final Logger LOGGER = LogManager.getLogger(GFUserDAOImpl.class);

    @Override
    public void persist(GFUser... entities)
    {
        Date now = new Date();
        for (GFUser user : entities)
        {
            user.setDateCreation(now);
        }
        super.persist(entities);
    }

    @Override
    public List<GFUser> findAll()
    {
        return super.findAll();
    }

    @Override
    public List<GFUser> search(ISearch search)
    {
        return super.search(search);
    }

    @Override
    public GFUser merge(GFUser entity)
    {
        return super.merge(entity);
    }

    @Override
    public boolean remove(GFUser entity)
    {
        return super.remove(entity);
    }

    @Override
    public boolean removeById(Long id)
    {
        return super.removeById(id);
    }

}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import java.util.Date;
import java.util.List;

import com.googlecode.genericdao.search.ISearch;

import org.geoserver.geofence.core.dao.UserGroupDAO;
import org.geoserver.geofence.core.model.UserGroup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.transaction.annotation.Transactional;


/**
 * Public implementation of the UserGroupDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Transactional(value = "geofenceTransactionManager")
public class UserGroupDAOImpl extends BaseDAO<UserGroup, Long>
    // extends GenericDAOImpl<User, Long>
    implements UserGroupDAO
{

    private static final Logger LOGGER = LogManager.getLogger(UserGroupDAOImpl.class);

    @Override
    public void persist(UserGroup... entities)
    {
        Date now = new Date();
        for (UserGroup e : entities)
        {
            e.setDateCreation(now);
        }

        super.persist(entities);
    }

    @Override
    public List<UserGroup> findAll()
    {
        return super.findAll();
    }

    @Override
    public List<UserGroup> search(ISearch search)
    {
        return super.search(search);
    }

    @Override
    public UserGroup merge(UserGroup entity)
    {
        return super.merge(entity);
    }

    @Override
    public boolean remove(UserGroup entity)
    {
        return super.remove(entity);
    }

    @Override
    public boolean removeById(Long id)
    {
        return super.removeById(id);
    }

    // ==========================================================================

}

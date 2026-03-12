/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db.dao.impl;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.db.dao.GSInstanceDAO;
import org.geofence.core.model.GSInstance;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Public implementation of the GSInstanceDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Transactional(value = "geofenceTransactionManager")
@Repository
public class GSInstanceDAOImpl extends BaseDAOImpl<GSInstance, Long> implements GSInstanceDAO {

    private static final Logger LOGGER = LogManager.getLogger(GSInstanceDAOImpl.class);

    public GSInstanceDAOImpl() {
        super(GSInstance.class);
    }

    @Override
    public void persist(GSInstance entity) {
        super.persist(entity);
    }

    @Override
    public List<GSInstance> findAll() {
        return super.findAll();
    }

    @Override
    public GSInstance merge(GSInstance entity) {
        return super.merge(entity);
    }

    @Override
    public boolean remove(GSInstance entity) {
        return super.remove(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }
}

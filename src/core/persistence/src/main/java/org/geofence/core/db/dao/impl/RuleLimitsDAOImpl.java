/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db.dao.impl;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.db.dao.RuleLimitsDAO;
import org.geofence.core.model.RuleLimits;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Public implementation of the RuleLimitsDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Transactional(value = "geofenceTransactionManager")
@Repository("geofence_ruleLimitsDAO")
public class RuleLimitsDAOImpl //
extends BaseDAOImpl<RuleLimits, Long> //
        implements RuleLimitsDAO {
    private static final Logger LOGGER = LogManager.getLogger(RuleLimitsDAOImpl.class);

    public RuleLimitsDAOImpl() {
        super(RuleLimits.class);
    }

    @Override
    public void persist(RuleLimits rl) {
        super.persist(rl);
    }

    @Override
    public List<RuleLimits> findAll() {
        return super.findAll();
    }

    @Override
    public RuleLimits merge(RuleLimits entity) {
        return super.merge(entity);
    }

    @Override
    public boolean remove(RuleLimits entity) {
        return super.remove(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }
}

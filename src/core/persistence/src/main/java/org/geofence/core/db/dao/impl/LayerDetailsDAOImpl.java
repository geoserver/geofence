/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db.dao.impl;

import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.db.dao.LayerDetailsDAO;
import org.geofence.core.model.LayerAttribute;
import org.geofence.core.model.LayerDetails;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Public implementation of the LayerDetailsDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Transactional(value = "geofenceTransactionManager")
@Repository("geofence_layerDetailsDAO")
public class LayerDetailsDAOImpl extends BaseDAOImpl<LayerDetails, Long> implements LayerDetailsDAO {

    private static final Logger LOGGER = LogManager.getLogger(LayerDetailsDAOImpl.class);

    public LayerDetailsDAOImpl() {
        super(LayerDetails.class);
    }

    @Override
    public void persist(LayerDetails details) {
        if (details.getRule() == null) {
            throw new NullPointerException("Rule is not set");
        }
        details.setId(details.getRule().getId());

        for (LayerAttribute attr : details.getAttributes()) {
            if (attr.getAccess() == null) {
                throw new NullPointerException("Null access type for attribute " + attr.getName() + " in " + details);
            }
        }
        super.persist(details);
    }

    @Override
    public List<LayerDetails> findAll() {
        return super.findAll();
    }

    @Override
    public LayerDetails merge(LayerDetails entity) {
        return super.merge(entity);
    }

    @Override
    public boolean remove(LayerDetails entity) {
        return super.remove(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    // ==========================================================================

    @Override
    public Set<String> getAllowedStyles(Long id) {
        LayerDetails found = find(id);
        if (found != null) {
            Set<String> styles = found.getAllowedStyles();

            if ((styles != null) && !Hibernate.isInitialized(styles)) {
                Hibernate.initialize(styles); // fetch the props
            }

            return styles;
        } else {
            throw new IllegalArgumentException("LayerDetails not found");
        }
    }

    @Override
    public void setAllowedStyles(Long id, Set<String> styles) {
        LayerDetails found = find(id);
        if (found != null) {
            found.setAllowedStyles(styles);
        } else {
            throw new IllegalArgumentException("LayerDetails not found");
        }
    }
}

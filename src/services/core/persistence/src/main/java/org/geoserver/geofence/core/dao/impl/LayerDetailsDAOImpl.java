/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import java.util.List;
import java.util.Set;

import com.googlecode.genericdao.search.ISearch;

import org.geoserver.geofence.core.dao.LayerDetailsDAO;
import org.geoserver.geofence.core.model.LayerAttribute;
import org.geoserver.geofence.core.model.LayerDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Public implementation of the RuleLimitsDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Transactional(value = "geofenceTransactionManager")
public class LayerDetailsDAOImpl extends BaseDAO<LayerDetails, Long> implements LayerDetailsDAO {

    private static final Logger LOGGER = LogManager.getLogger(LayerDetailsDAOImpl.class);

    @Override
    public void persist(LayerDetails... entities) {
        for (LayerDetails details : entities) {
            if ( details.getRule() == null ) {
                throw new NullPointerException("Rule is not set");
            }
            details.setId(details.getRule().getId());

            for (LayerAttribute attr : details.getAttributes()) {
                if ( attr.getAccess() == null ) {
                    throw new NullPointerException("Null access type for attribute " + attr.getName() + " in " + details);
                }
            }
        }
        super.persist(entities);
    }

//    @Override
//    public LayerDetails find(Long id) {
//        return super.find(id);
//    }
    @Override
    public List<LayerDetails> findAll() {
        return super.findAll();
    }

    @Override
    public List<LayerDetails> search(ISearch search) {
        return super.search(search);
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
        if ( found != null ) {
            Set<String> styles = found.getAllowedStyles();

            if ( (styles != null) && !Hibernate.isInitialized(styles) ) {
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
        if ( found != null ) {
            found.setAllowedStyles(styles);
        } else {
            throw new IllegalArgumentException("LayerDetails not found");
        }
    }
}

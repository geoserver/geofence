/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import com.googlecode.genericdao.search.jpa.JPASearchProcessor;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

//import com.trg.dao.jpa.GenericDAOImpl;
//import com.trg.search.jpa.JPASearchProcessor;

import org.springframework.stereotype.Repository;


/**
 *
 * The base DAO furnish a set of methods usually used
 *
 * @author Tobia Di Pisa (tobia.dipisa@geo-solutions.it)
 */
@Repository(value = "geofence")
public class BaseDAO<T, ID extends Serializable> extends GenericDAOImpl<T, ID>
{

    @PersistenceContext(unitName = "geofenceEntityManagerFactory")
    private EntityManager entityManager;

    /**
     * EntityManager setting
     *
     * @param entityManager
     *            the entity manager to set
     */
    @Override
    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
        super.setEntityManager(this.entityManager);
    }

    /**
     * JPASearchProcessor setting
     *
     * @param searchProcessor
     *            the search processor to set
     */
    @Override
    public void setSearchProcessor(JPASearchProcessor searchProcessor)
    {
        super.setSearchProcessor(searchProcessor);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.trg.dao.jpa.JPABaseDAO#em()
     */
    @Override
    public EntityManager em()
    {
        return this.entityManager;
    }
}

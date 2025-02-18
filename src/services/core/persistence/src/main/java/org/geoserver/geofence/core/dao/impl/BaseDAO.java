/* (c) 2014 - 2020 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.core.dao.impl;

import org.geoserver.geofence.core.dao.search.LongSearch;
import org.geoserver.geofence.core.dao.search.Search;
import org.geoserver.geofence.core.dao.search.BaseSearch;
import org.geoserver.geofence.core.model.Identifiable;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 *
 * The base DAO provides a set of common methods 
 *
 */
@Repository(value = "geofence")
public class BaseDAO<E extends Identifiable, ID extends Serializable>
{
    protected final Class<E> ENTITY;

    protected BaseDAO(Class<E> entity) {
        this.ENTITY = entity;
    }

    @PersistenceContext(unitName = "geofenceEntityManagerFactory")
    private EntityManager em;


    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public EntityManager em() {
        return this.em;
    }

    public Session session() {
        return em.unwrap(Session.class);
    }
    
    public Search<E> createSearch() {
        return new Search<>(em, ENTITY);
    }

    public LongSearch<E> createLongSearch() {
        return new LongSearch<>(em, this.ENTITY);
    }
    
    public List<E> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<E> q = cb.createQuery(ENTITY);
        Root<E> c = q.from(ENTITY);
        q.select(c);
        
       return em.createQuery(q).getResultList();       
    }

    public E find(ID id) {
        return em().find(ENTITY, id);
    }

    public void persist(E... entities) {
        for (E entity : entities) {
            em.persist(entity);
        }
    }

    public E merge(E entity) {
        return em.merge(entity);
    }

    public void merge(E... entities) {
        for (E entity : entities) {
            em.merge(entity);
        }
    }

    public boolean remove(E entity) {
        return removeById(entity.getId());
    }

    public boolean removeById(Long id) {
        E e = em.find(ENTITY, id);
        if (e == null) {
            return false;
        }
        em.remove(e);
        return true;
    }

    public List<E> search(Search<E> search) {
        return search
                .getQuery()
                .getResultList();      
    }
    
    public <OUT> List<OUT> searchExt(BaseSearch<OUT,E> search) {
        return search
                .getQuery()
                .getResultList();      
    }
    
    protected <OUT> OUT searchUnique(BaseSearch<OUT, E> search) {
        List<OUT> found = searchExt(search);
        switch (found.size()) {
            case 0:
                return null;
            case 1:
                return found.get(0);
            default:
                throw new IllegalStateException("Result is not unique");
        }
    }

    public long count(LongSearch<E> search) {
        if(search == null) {
            search = createLongSearch();
        }
        
        return search.getCountQuery().getSingleResult();      
    }

}

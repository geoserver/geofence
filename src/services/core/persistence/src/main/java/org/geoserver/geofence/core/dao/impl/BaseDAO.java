/* (c) 2014 - 2020 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.core.dao.impl;

import org.geoserver.geofence.core.dao.search.Search;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.geoserver.geofence.core.model.Identifiable;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 *
 * The base DAO furnish a set of methods usually used
 *
 */
@Repository(value = "geofence")
public class BaseDAO<E extends Identifiable, ID extends Serializable> // extends GenericDAOImpl<T, ID>
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

    public class DSearch extends Search {

        public DSearch(EntityManager em, Class resultType ) {
            super(em, resultType);
        }

        public DSearch(EntityManager em, Class resultType, Class baseClass) {
            super(em, resultType, baseClass);
        }
    }
    
    public Search createSearch(Class resultType) {
        return new DSearch(em, resultType);
    }
    
    public Search createSearch() {
        return createSearch(ENTITY);
    }

    public Search createCountSearch() {
        return new DSearch(em, Long.class, ENTITY);
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

    public void remove(E entity) {
        removeById(entity.getId());
    }

    public boolean removeById(Long id) {
        E e = em.find(ENTITY, id);
        if (e == null) {
            return false;
        }
        em.remove(e);
        return true;
    }

    public List _search(Search search) {
        return search(search);
    }

    public List<E> search(Search search) {
        return search.getQuery().getResultList();      
    }

    protected Object searchUnique(Search search) {
        List found = search(search);
        switch (found.size()) {
            case 0:
                return null;
            case 1:
                return found.get(0);
            default:
                throw new IllegalStateException("Result is not unique");
        }
    }

    public long count(Search search) {
        if(search == null) {
            search = createSearch();
        }
        
        return (Long)search.getCountQuery().getSingleResult();      
    }

}

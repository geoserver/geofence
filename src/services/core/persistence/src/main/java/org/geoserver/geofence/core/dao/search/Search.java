/*
 */
package org.geoserver.geofence.core.dao.search;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author geosol
 */
public class Search<O, R> {
    
    private final Class<O> outType;    
    private final Class<R> rootClass;    
    private final EntityManager em;    
    private final CriteriaBuilder cb;
    private CriteriaQuery q;    
    private Root<R> root;
    private List<Predicate> whereClauses = new ArrayList<>() ;
    private List<Order> orderBy = new ArrayList<>() ;

    private Integer firstResult = null;
    private Integer maxResults = null;
    private Integer page = null;
    
    public static class JoinInfo{
        Join join;
        String field;

        public JoinInfo(Join join, String field) {
            this.join = join;
            this.field = field;
        }

        public String getField() {
            return field;
        }
        
    }
    
    protected Search(EntityManager em, Class<O> resultType, Class<R> rootClass) {
        this.em = em;
        this.outType = resultType;
        this.rootClass = rootClass;
                
        cb = em.getCriteriaBuilder();
        q = cb.createQuery(resultType);  
        
        root = q.from(rootClass);
    }
    
    protected Search(EntityManager em, Class resultType) {
        this(em, resultType, resultType);
    }

    public void addField(String field, Field op) {
        
        if(op == Field.OP_MAX) {
            q.select(cb.max(root.<Number>get(field)));
        } else {        
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    public static enum Field {
        OP_MAX
    }
    

    
    public void setFirstResult(int i) {
        firstResult = i;
    }

    public void setMaxResults(int i) {        
        maxResults = i;
    }

    public void setPage(int page) {
        this.page = page;      
    }
    
    public void addSortAsc(String field) {
         orderBy.add(cb.asc(root.get(field)));
//        c.addOrder(Order.asc(field));
    }

    public void setDistinct(boolean b) {
        q.distinct(b);
    }
    
    public JoinInfo addJoin(String field) {
        return new JoinInfo(root.join(field, JoinType.LEFT), field);
    }
    
    public Fetch addFetch(String field) {
        return root.fetch(field, JoinType.LEFT);
    }
        
    public <F> Fetch<R,F> addFetch(String field, Class<F> type) {
//        Fetch<Country,Capital> p = c.fetch("capital");
        return root.fetch(field, JoinType.LEFT);
//        c.setFetchMode(field, FetchMode.EAGER);
    }
    
    public void addFilterNull(String field) {
        whereClauses.add(cb.isNull(root.get(field))); // )Restrictions.isNull(field));
    }

    public void addFilterNull(JoinInfo j, String field) {
        whereClauses.add(cb.isNull(j.join.get(field))); 
    }
    
    public void addFilterEqual(JoinInfo j, String field, Object o) {
        whereClauses.add(cb.equal(j.join.get(field), o));
//        c.add(Restrictions.eq(field, o));
    }

    public void addFilterEqual(String field, Object o) {
        whereClauses.add(cb.equal(root.get(field), o));
//        c.add(Restrictions.eq(field, o));
    }
    
    public void addFilterGreaterOrEqual(String field, Long value) {
        whereClauses.add(cb.ge(root.get(field), value));

//        c.add(Restrictions.ge(field, value));
    }

    public void addFilterLessThan(String field, Long value) {
        whereClauses.add(cb.lt(root.get(field), value));
//        c.add(Restrictions.lt(field, value));
    }

    public void addFilterILike(String name, String like) {
        whereClauses.add(        
               cb.like(
                    cb.lower(root.get(name)), 
                    cb.lower(cb.literal("%" + like + "%")
        )));         
//        c.add(Restrictions.ilike(name, like));
    }
    
    public Predicate isNull(String field) {
        return cb.isNull(root.get(field));
    }

    public Predicate isEqual(String field, Object val) {
        return cb.equal(root.get(field), val);
    }
    
    public Predicate isEqual(JoinInfo j, String field, Object val) {
        return cb.equal(j.join.get(field), val);
    }

    public Predicate isGE(String field, Number val) {
        return cb.ge(root.get(field), val);
    }

    
    public void addFilterOr(Predicate f1, Predicate f2) {
        whereClauses.add(cb.or(f1, f2));    
//        c.add(Restrictions.or(f1.getCriterion(),f2.getCriterion()));
    }
    
//    public void addFilter(Predicate f) {
//        c.add(f.getCriterion());
//    }
    
    
    
    private void applyWhere(CriteriaQuery q) {
        
        switch(whereClauses.size()) {
            case 0:
                break;
            case 1:
                q.where(whereClauses.get(0));
                break;
            default:
                q.where(whereClauses.toArray(new Predicate[whereClauses.size()]));
            
        }        
    }
    
    public TypedQuery<O> getQuery() {        
        applyWhere(q);
        
        if(! orderBy.isEmpty()) {
            q.orderBy(orderBy);
        }
        
        if(q.getSelection() == null) {
            q.select(root);
        }
                
        TypedQuery<O> query = em.createQuery(q);
        
        applyPagination(query);
           
        return query;
    }

    private void applyPagination(TypedQuery<O> query) throws IllegalStateException {
        if(firstResult != null) {
            query.setFirstResult(firstResult);
        }        
        if(maxResults != null) {
            query.setMaxResults(maxResults);
        }   
        if(page != null) {
            if(maxResults == null) {
                throw new IllegalStateException("Page set without maxresults");
            }
            query.setFirstResult(page * maxResults);             
        }
    }

//    public TypedQuery<Long> _getCountQuery() {        
//        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//        cq.select(cb.count(cq.from(outType)));
////        applyWhere(cq);
//        return em.createQuery(cq);
//    }

    public TypedQuery<Long> getCountQuery() {        
        applyWhere(q);
//        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        q.select(cb.count(root));
        return em.createQuery(q);
    }


}
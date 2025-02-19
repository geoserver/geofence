/*
 */
package org.geoserver.geofence.core.dao.search;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

/**
 *
 * @author Emanuele Tajariol
 */
public abstract class BaseSearch<OUTCLASS, ROOTCLASS> {
    
    private final Class<OUTCLASS> resultClass;
    private final Class<ROOTCLASS> rootClass;    
    protected final EntityManager em;
    protected final CriteriaBuilder cb;
    protected  CriteriaQuery<OUTCLASS> cquery;
    protected Root<ROOTCLASS> root;
    private final List<Predicate> whereClauses = new ArrayList<>();
    private List<Order> orderBy = new ArrayList<>();

    private Integer firstResult = null;
    private Integer maxResults = null;
    private Integer page = null;
    
    public static class JoinInfo<ROOTCLASS, T>{
        Join<ROOTCLASS, T> join;
        String field;

        public JoinInfo(Join<ROOTCLASS, T> join, String field) {
            this.join = join;
            this.field = field;
        }

        public String getField() {
            return field;
        }
        
    }
    
    protected BaseSearch(EntityManager em, Class<OUTCLASS> resultClass, Class<ROOTCLASS> rootClass) {
        this.em = em;
        this.resultClass = resultClass;
        this.rootClass = rootClass;
                
        cb = em.getCriteriaBuilder();
        cquery = cb.createQuery(resultClass);        
        root = cquery.from(rootClass);
    }
    
    
    public void addField(String field, Field op) {        
        throw new UnsupportedOperationException("Not supported yet.");
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
        cquery.distinct(b);
    }
    
    public <T> JoinInfo<ROOTCLASS, T> addJoin(String field) {
        return new JoinInfo<>(root.join(field, JoinType.LEFT), field);
    }
    
    public Fetch addFetch(String field) {
        return root.fetch(field, JoinType.LEFT);
    }
        
    public <F> Fetch<ROOTCLASS,F> addFetch(String field, Class<F> type) {
        return root.fetch(field, JoinType.LEFT);
//        c.setFetchMode(field, FetchMode.EAGER);
    }
    
    public void addFilterNull(String field) {
        whereClauses.add(cb.isNull(root.get(field)));
    }

    public <T> void addFilterNull(JoinInfo<ROOTCLASS, T> j, String field) {
        whereClauses.add(cb.isNull(j.join.get(field))); 
    }
    
    public <T> void addFilterEqual(JoinInfo<ROOTCLASS, T> j, String field, Object o) {
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

    public Predicate isNotNull(String field) {
        return cb.isNotNull(root.get(field));
    }

    public Predicate isEqual(String field, Object val) {
        return cb.equal(root.get(field), val);
    }
    
    public <T> Predicate isEqual(JoinInfo<ROOTCLASS, T> j, String field, Object val) {
        return cb.equal(j.join.get(field), val);
    }

    public Predicate isGE(String field, Number val) {
        return cb.ge(root.get(field), val);
    }
    
    public void addFilterIsAfter(String field, Date value) {
        whereClauses.add(isAfter(field, value));
    }
    
    public void addFilterIsBefore(String field, Date value) {
        whereClauses.add(isBefore(field, value));
    }
    
    public Predicate isAfter(String field, Date value) {
        return cb.lessThan(root.get(field), value);
    }
    
    public Predicate isBefore(String field, Date value) {
         return cb.greaterThan(root.get(field), value);
    }
    
    public Predicate and(Predicate f1, Predicate f2) {
         return cb.and(f1, f2);
    }
    
    public Predicate or(Predicate f1, Predicate f2) {
         return cb.or(f1, f2);
    }
    
    public void addFilterOr(Predicate f1, Predicate f2) {
        whereClauses.add(cb.or(f1, f2));
    }
    
    public void addFilterAnd(Predicate f1, Predicate f2) {
        whereClauses.add(cb.and(f1, f2));
    }
    
//    public void addFilter(Predicate f) {
//        c.add(f.getCriterion());
//    }
    
    
    
    protected <OUT> void applyWhere(CriteriaQuery<OUT> q) {
        
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
    
    public abstract Selection<? extends OUTCLASS> getDefaultSelection();
    
    
    public TypedQuery<OUTCLASS> getQuery() {
        applyWhere(cquery);
        
        if(! orderBy.isEmpty()) {
            cquery.orderBy(orderBy);
        }
        
        if(cquery.getSelection() == null) {
            cquery.select(getDefaultSelection());
        }
                
        TypedQuery<OUTCLASS> query = em.createQuery(cquery);
        
        applyPagination(query);
           
        return query;
    }


    private void applyPagination(TypedQuery<OUTCLASS> query) throws IllegalStateException {
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

}
/*
 */
package org.geoserver.geofence.core.dao.search;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Selection;

/**
 *
 * @author etj
 */
public class LongSearch<ROOT> extends BaseSearch<Long, ROOT> {
    
    public LongSearch(EntityManager em, Class<ROOT> rootClass) {
        super(em, Long.class, rootClass);
    }

    public TypedQuery<Long> getCountQuery() {
        applyWhere(cquery);
        cquery.select(cb.count(root));
        return em.createQuery(cquery);
    }

    public void addField(String field, Field op) {
        if (op == Field.OP_MAX) {
            cquery.select(cb.max(root.get(field)));
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    @Override
    // just a sample implementation, probably never used
    public Selection<? extends Long> getDefaultSelection() {
        return cb.count(root);
    }
    
}

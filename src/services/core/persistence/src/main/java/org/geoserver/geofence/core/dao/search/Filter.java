/*
 */
package org.geoserver.geofence.core.dao.search;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author geosol
 */
public class Filter {
    
    private Criterion c;

    private Filter(Criterion c) {
        this.c = c;
    }
    
    public static Filter isNull(String o) { return new Filter(Restrictions.isNull(o));}
    public static Filter equal(String field, Object v) { return new Filter(Restrictions.eq(field, v));}

    public static Filter greaterOrEqual(String field, Long v) {
        return new Filter(Restrictions.ge(field, v));
    }

    Criterion getCriterion() {
        return c;
    }
    
}

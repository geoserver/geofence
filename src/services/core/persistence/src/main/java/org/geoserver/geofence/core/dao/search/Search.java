/*
 */
package org.geoserver.geofence.core.dao.search;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Selection;

/**
 * A search on the BASE class returning instances of the BASE class.
 * 
 * @author Emanuele Tajariol
 */
public class Search<BASE> extends BaseSearch<BASE, BASE> {

    public Search(EntityManager em, Class<BASE> baseClass) {
        super(em, baseClass, baseClass);
    }
    
    @Override
    public Selection<? extends BASE> getDefaultSelection() {
        return root;        
    }
}

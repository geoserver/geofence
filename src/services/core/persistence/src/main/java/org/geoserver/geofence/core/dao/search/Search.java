/*
 */
package org.geoserver.geofence.core.dao.search;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Selection;

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

package org.geoserver.geofence.services.util;

import org.geoserver.geofence.services.dto.RuleFilter;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 */
public class RuleFilterTest {
    @Test
    public void testRole() {
        RuleFilter f = new RuleFilter();
        f.setRole("pippo");        
        assertEquals("pippo", f.getRole().getText());
        
        f.setRole("a,b");        
        assertEquals("a,b", f.getRole().getText());

        f.setRole("b,a");        
        assertEquals("a,b", f.getRole().getText());

        f.setRole("a, b");        
        assertEquals("a,b", f.getRole().getText());

        f.setRole("  b , a   ");        
        assertEquals("a,b", f.getRole().getText());        
    }

}

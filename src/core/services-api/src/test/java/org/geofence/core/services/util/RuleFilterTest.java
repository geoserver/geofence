package org.geofence.core.services.util;

import static org.junit.jupiter.api.Assertions.*;

import org.geofence.core.services.dto.RuleFilter;
import org.junit.jupiter.api.Test;

/** */
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

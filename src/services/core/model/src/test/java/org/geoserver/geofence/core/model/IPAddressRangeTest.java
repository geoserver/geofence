/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import org.geoserver.geofence.core.model.IPAddressRange;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class IPAddressRangeTest {

    public IPAddressRangeTest() {
    }


    @Test
    public void testCIDRConstructorV4() {

        IPAddressRange r = new IPAddressRange("1.2.0.0/16");

        assertEquals(16, r.getSize());

        assertEquals((long)(1<<24 | 2 << 16), (long)r.getLow());
        assertNull(r.getHigh());
    }

    @Test
    public void testMatch() {

        IPAddressRange r = new IPAddressRange("1.2.0.0/16");

        assertTrue(r.match("1.2.3.4"));
        assertFalse(r.match("1.1.3.4"));

        assertTrue(new IPAddressRange("10.10.100.80/32").match("10.10.100.80"));
    }

    @Test
    public void testToString() {

        String s = "1.2.0.0/16";

        IPAddressRange r = new IPAddressRange(s);

        assertEquals(s, r.getCidrSignature());
        assertEquals("1.2.0.0", r.getAddress());
    }

    @Test
    public void testHigestBitV4() {

        IPAddressRange r = new IPAddressRange("255.2.127.0/20");

        assertEquals(20, r.getSize());

        assertEquals((255<<24 | 2 << 16 | 127 << 8)&0x0ffffffff, (long)r.getLow());
        assertNull(r.getHigh());
    }

}

/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.services.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IPUtilsTest {
    @Test
    public void testIPv4() {
        assertTrue(IPUtils.isAddressValid("192.168.1.1"));
        assertTrue(IPUtils.isAddressValid("127.0.0.1"));
        assertFalse(IPUtils.isAddressValid("192.168.1.1.2"));
        
        assertTrue(IPUtils.isRangeValid("192.168.1.0/32"));
        assertTrue(IPUtils.isRangeValid("127.0.0.1/8"));
        assertFalse(IPUtils.isRangeValid("127.0.0.1/1111"));
        assertFalse(IPUtils.isRangeValid("127.0.0.1/32/2"));
    }
    
    @Test
    public void testIPv6() {
        assertTrue(IPUtils.isAddressValid("0:0:0:0:0:0:0:1"));
        assertTrue(IPUtils.isAddressValid("B012:a000:361:44:f87:11:0:0"));
        assertTrue(IPUtils.isAddressValid("::1"));
        
        assertFalse(IPUtils.isAddressValid("B012:a000:361:44:f87:11:0:g0"));
        assertFalse(IPUtils.isAddressValid("B012:a000:361:44:f87:11:0"));
        
        assertTrue(IPUtils.isRangeValid("0:0:0:0:0:0:0:1/32"));
        assertFalse(IPUtils.isRangeValid("0:0:0:0:0:0:0:1/32/1"));
    }
}

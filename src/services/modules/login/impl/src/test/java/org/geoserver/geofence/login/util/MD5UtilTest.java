/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.login.util;

import static org.junit.Assert.*;

import org.junit.Test;

/** @author ETj (etj at geo-solutions.it) */
public class MD5UtilTest {

    /** Test of getHash method, of class MD5Util. */
    @Test
    public void testGetHash() {
        System.out.println("getHash");
        String password = "test";
        String expResult = "098f6bcd4621d373cade4e832627b4f6";
        String result = MD5Util.getHash(password);
        assertEquals(expResult, result);
    }
}

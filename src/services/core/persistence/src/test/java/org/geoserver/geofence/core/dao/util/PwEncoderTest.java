/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.util;

import org.geoserver.geofence.core.dao.util.PwEncoder;
import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author ETj <etj at geo-solutions.it>
 */
public class PwEncoderTest {

    public PwEncoderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testEncode() {
        testString("test");
        testString("topolino");
        testString("");

    }

    public void testString(String test) {

        String enc = PwEncoder.encode(test);
        System.out.println("ENC --> " + enc);
        String dec = PwEncoder.decode(enc);
        System.out.println("DEC --> " + dec);
        assertEquals(test, dec);
    }

}
/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.csv2geofence;

import org.geoserver.csv2geofence.impl.UserFileLoader;
import org.geoserver.csv2geofence.config.model.Configuration;
import java.io.File;
import javax.xml.bind.JAXB;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class UserLoaderTest extends BaseTest {

    public UserLoaderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of load method, of class UserFileLoader.
     */
    @Test
    public void testLoad() throws Exception {
        System.out.println("load");
        File cfgFile = loadFile("config00.xml");
        Configuration cfg = JAXB.unmarshal(cfgFile, Configuration.class);

        UserFileLoader instance = new UserFileLoader(cfg.getUserFileConfig());
        File userFile = loadFile("ldif.csv");
        instance.load(userFile);
    }


}

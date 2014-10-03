/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.csv2geofence;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.log4j.spi.LoggerFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public abstract class BaseTest {

    private final static Logger LOGGER = LogManager.getLogger(BaseTest.class);
    @Rule
    public TestName _testName = new TestName();
    private static File testDataDir = null;
    private File classDir;
    private File tempDir;

    public BaseTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        LOGGER.info("---------- Running Test " + getClass().getSimpleName() + " :: " + _testName.getMethodName());
        String className = this.getClass().getSimpleName();
        classDir = new File(getTestDataDir(), className);
        if (!classDir.exists()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Using test class dir " + classDir);
            }
            classDir.mkdir();
        }

        String testName = _testName.getMethodName();
        tempDir = new File(classDir, testName);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Using test case dir " + tempDir);
        }
    }

    @After
    public void tearDown() {
    }
    
    protected synchronized File getTempDir() {
        if( ! tempDir.exists()) // create dir lazily
            tempDir.mkdir();

        return tempDir;
    }
    protected File loadFile(String name) {
        try {
            URL url = this.getClass().getClassLoader().getResource(name);
            if (url == null) {
                throw new IllegalArgumentException("Cant get file '" + name + "'");
            }
            File file = new File(url.toURI());
            return file;
        } catch (URISyntaxException e) {
            LOGGER.error("Can't load file " + name + ": " + e.getMessage(), e);
            return null;
        }
    }

    private synchronized File getTestDataDir() {

        if (testDataDir != null) {
            return testDataDir;
        }

        String startDir = System.getProperty("buildDirectory");
        if (startDir == null) {
            LOGGER.warn("Property 'buildDirectory' is not defined");

            File f = loadFile(".");
            if (f == null || !f.exists()) {
                LOGGER.warn("Undefined current directory");

                throw new IllegalStateException("Could not find a valid current dir");
            }

            String fa = f.getParentFile().getAbsolutePath();

            if (!"target".equals(FilenameUtils.getBaseName(fa))) {
                LOGGER.warn("Can't use current dir " + fa);
                throw new IllegalStateException("Could not find a valid current dir");
            }

            startDir = fa;
        }

        testDataDir = new File(startDir, "test-data-" + System.currentTimeMillis());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Using test dir " + testDataDir);
        }

        testDataDir.mkdir();
        return testDataDir;
    }
}

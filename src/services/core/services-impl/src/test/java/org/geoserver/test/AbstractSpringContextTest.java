/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.test;

import junit.framework.TestCase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Base class for tests with a spring context loaded from the classpath.
 * 
 * @author Nate Sammons
 */
public abstract class AbstractSpringContextTest extends TestCase {
    protected Logger logger = LogManager.getLogger(getClass());

    protected ClassPathXmlApplicationContext context = null;

    /**
     * Get the filename to use for this context.
     */
    protected abstract String[] getContextFilenames();

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        context = new ClassPathXmlApplicationContext(getContextFilenames());
        logger.info("Built test context: " + context);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        logger.info("Closing test context");
        context.close();
        context = null;
    }
}

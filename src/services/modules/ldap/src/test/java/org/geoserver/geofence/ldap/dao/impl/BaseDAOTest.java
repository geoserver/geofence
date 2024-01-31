/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.ldap.dao.impl;

import static org.junit.Assert.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.geoserver.geofence.core.dao.GSUserDAO;
import org.geoserver.geofence.core.dao.UserGroupDAO;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.ldap.test.LdapTestUtils;

/** @author ETj (etj at geo-solutions.it) */
public abstract class BaseDAOTest {
    protected final Logger LOGGER;

    protected static GSUserDAO userDAO;
    protected static UserGroupDAO userGroupDAO;

    protected static ClassPathXmlApplicationContext ctx = null;

    @Rule public TestName name = new TestName();

    public BaseDAOTest() {
        LOGGER = LogManager.getLogger(getClass());

        synchronized (BaseDAOTest.class) {
            if (ctx == null) {
                String[] paths = {
                    "classpath*:applicationContext.xml",
                    //                    "applicationContext.xml",
                    //                    "applicationContext-geofence-ldap.xml"
                    //                         ,"applicationContext-test.xml"
                };
                ctx = new ClassPathXmlApplicationContext(paths);

                userDAO = (GSUserDAO) ctx.getBean("gsUserDAO_LDAP");
                userGroupDAO = (UserGroupDAO) ctx.getBean("userGroupDAO_LDAP");
            }
        }
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        // Start an LDAP server and import test data
        //        LdapTestUtils.startEmbeddedServer(10389, "", "test");
        //        LdapTestUtils.startEmbeddedServer(10389, "dc=example,dc=com", "test");
        LdapTestUtils.startEmbeddedServer(10389, "dc=com", "test");
        loadData();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        LdapTestUtils.shutdownEmbeddedServer();
    }

    @Before
    public void setUp() throws Exception {
        LOGGER.info(
                "################ Setting up -- "
                        + getClass().getSimpleName()
                        + ":: "
                        + name.getMethodName());
        //        loadData();
        LOGGER.info(
                "##### Ending setup for "
                        + getClass().getSimpleName()
                        + " ###----------------------");
    }

    protected static void loadData() throws Exception {
        // Bind to the directory
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl("ldap://127.0.0.1:10389");
        contextSource.setUserDn("uid=admin,ou=system");
        contextSource.setPassword("secret");
        contextSource.setPooled(false);
        // contextSource.setDirObjectFactory(null);
        contextSource.afterPropertiesSet();

        // Create the Sprint LDAP template
        LdapTemplate template = new LdapTemplate(contextSource);

        // Clear out any old data - and load the test data
        LdapTestUtils.clearSubContexts(contextSource, LdapUtils.newLdapName("dc=example,dc=com"));
        LdapTestUtils.loadLdif(contextSource, new ClassPathResource("data.ldif"));
    }

    @Test
    public void testCheckDAOs() {
        assertNotNull(userDAO);
        assertTrue(userDAO instanceof GSUserDAOLdapImpl);
    }
}

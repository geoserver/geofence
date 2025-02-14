/* (c) 2014 - 2020 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.ldap.dao.impl;

import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.CreateDS;
import org.apache.directory.server.core.annotations.CreatePartition;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.factory.DSAnnotationProcessor;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.apache.directory.server.factory.ServerAnnotationProcessor;
import org.apache.directory.server.ldap.LdapServer;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import org.springframework.ldap.test.LdapTestUtils;

import org.geoserver.geofence.core.dao.GSUserDAO;
import org.geoserver.geofence.core.dao.UserGroupDAO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@RunWith(FrameworkRunner.class)
public abstract class BaseDAOTest {

    protected static Logger LOGGER;

    protected static GSUserDAO userDAO;
    protected static UserGroupDAO userGroupDAO;

    protected static ClassPathXmlApplicationContext ctx = null;

    @Rule
    public TestName name = new TestName();

    public BaseDAOTest() {
        LOGGER = LogManager.getLogger(getClass());

        synchronized (BaseDAOTest.class) {
            if (ctx == null) {
                String[] paths = {
                    "classpath*:applicationContext.xml",};
                ctx = new ClassPathXmlApplicationContext(paths);

                userDAO = (GSUserDAO) ctx.getBean("gsUserDAO_LDAP");
                userGroupDAO = (UserGroupDAO) ctx.getBean("userGroupDAO_LDAP");
            }
        }
    }

    @BeforeClass
    @CreateLdapServer(name = "DSAlias", //
            transports = {@CreateTransport(protocol = "LDAP", port = 10389)}, //
            allowAnonymousAccess = true)
    @CreateDS(name = "test", //
            partitions = @CreatePartition(name = "example_com", suffix = "dc=example,dc=com"), //
            allowAnonAccess = true)
    public static void setUpClass() throws Exception {
        
        Logger logger = LogManager.getLogger(BaseDAOTest.class);
        
        DirectoryService directoryService = DSAnnotationProcessor.getDirectoryService();
        final SchemaManager schemaManager = directoryService.getSchemaManager();
        LdapServer ldapServer = ServerAnnotationProcessor.getLdapServer(directoryService);        

        logger.info("Creating test entries...");
        
        ClassPathResource ldif = new ClassPathResource("data.ldif");
        int entries = 0;
        for (LdifEntry ldifEntry : new LdifReader(ldif.getInputStream())) {

            DefaultEntry entry = new DefaultEntry(schemaManager, ldifEntry.getEntry());
            directoryService.getAdminSession().add(entry);
            ++entries;

        }
        logger.info("Created " +entries + " entries ");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        LdapTestUtils.shutdownEmbeddedServer();
    }

    @Before
    public void setUp() throws Exception {
//        LOGGER.info("################ Setting up -- " + getClass().getSimpleName() + ":: " + name.getMethodName());
//        loadData();
//        LOGGER.info("##### Ending setup for " + getClass().getSimpleName() + " ###----------------------");
    }

    @Test
    public void testCheckDAOs() {
        assertNotNull(userDAO);
        assertTrue(userDAO instanceof GSUserDAOLdapImpl);
    }

}

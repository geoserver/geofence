/* (c) 2014 - 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geofence.ldap.dao.impl;

import static org.junit.Assert.*;

import java.util.Map;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.db.dao.GSUserDAO;
import org.geofence.core.db.dao.UserGroupDAO;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.test.LdapTestUtils;

/**
 * Builds the LDAP-backed DAOs directly in Java (no Spring context needed for this) against an embedded ApacheDS server,
 * matching {@code data.ldif}'s schema: entries are keyed by {@code cn}, groups are {@code groupOfNames} with full-DN
 * {@code member} references - so unlike {@code GeofenceUserDaoConfig}'s own real-world defaults (posix-style
 * {@code uidNumber}/{@code memberUid}), the mappings here point {@code id}/{@code username}/{@code groupname} at
 * {@code cn} and {@code member} at {@code member}.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@RunWith(FrameworkRunner.class)
public abstract class BaseDAOTest {

    protected static Logger LOGGER;

    protected static GSUserDAO userDAO;
    protected static UserGroupDAO userGroupDAO;

    private static LdapTemplate template;

    @Rule
    public TestName name = new TestName();

    public BaseDAOTest() {
        LOGGER = LogManager.getLogger(getClass());

        synchronized (BaseDAOTest.class) {
            if (userDAO == null) {
                LdapContextSource contextSource = new LdapContextSource();
                contextSource.setUrl("ldap://localhost:10389");
                contextSource.setBase("dc=example,dc=com");
                contextSource.setAnonymousReadOnly(true);
                contextSource.afterPropertiesSet();

                template = new LdapTemplate(contextSource);
                template.setDefaultCountLimit(100);

                UserGroupAttributesMapper groupMapper = new UserGroupAttributesMapper();
                groupMapper.setMap(Map.of("id", "cn", "groupname", "cn", "member", "member"));

                UserGroupDAOLdapImpl groupDao = new UserGroupDAOLdapImpl();
                groupDao.setLdapTemplate(template);
                groupDao.setAttributesMapper(groupMapper);
                groupDao.setSearchBase("ou=Groups");
                groupDao.setSearchFilter("objectClass=groupOfNames");
                initialize(groupDao);

                GSUserAttributesMapper userMapper = new GSUserAttributesMapper();
                userMapper.setMap(Map.of(
                        "id", "cn",
                        "username", "cn",
                        "email", "mail",
                        "name", "givenName",
                        "surname", "sn",
                        "password", "userPassword"));

                GSUserDAOLdapImpl userDaoImpl = new GSUserDAOLdapImpl();
                userDaoImpl.setLdapTemplate(template);
                userDaoImpl.setAttributesMapper(userMapper);
                userDaoImpl.setSearchBase("ou=People");
                userDaoImpl.setSearchFilter("objectClass=inetOrgPerson");
                userDaoImpl.setUserGroupDAOLdapImpl(groupDao);
                initialize(userDaoImpl);

                userDAO = userDaoImpl;
                userGroupDAO = groupDao;
            }
        }
    }

    private static void initialize(org.springframework.beans.factory.InitializingBean bean) throws RuntimeException {
        try {
            bean.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize " + bean.getClass().getSimpleName(), e);
        }
    }

    @BeforeClass
    @CreateLdapServer(
            name = "DSAlias", //
            transports = {@CreateTransport(protocol = "LDAP", port = 10389)}, //
            allowAnonymousAccess = true)
    @CreateDS(
            name = "test", //
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
        logger.info("Created " + entries + " entries ");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        LdapTestUtils.shutdownEmbeddedServer();
    }

    @Before
    public void setUp() throws Exception {}

    @Test
    public void testCheckDAOs() {
        assertNotNull(userDAO);
        assertTrue(userDAO instanceof GSUserDAOLdapImpl);
    }
}

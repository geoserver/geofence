/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.ldap.dao.impl;

import static org.junit.Assert.assertNotNull;
import org.geoserver.geofence.core.dao.GSUserDAO;
import org.geoserver.geofence.core.dao.UserGroupDAO;

import javax.naming.directory.DirContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.test.LdapTestUtils;



/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public abstract class BaseDAOTest {
    protected final Logger LOGGER;

    protected static GSUserDAO userDAO;
    protected static UserGroupDAO userGroupDAO;
    
    protected static ClassPathXmlApplicationContext ctx = null;

    public BaseDAOTest() {
        LOGGER = LogManager.getLogger(getClass());

       
        
        synchronized(BaseDAOTest.class) {
            if(ctx == null) {
                String[] paths = {
                        "applicationContext.xml"
//                         ,"applicationContext-test.xml"
                };
                ctx = new ClassPathXmlApplicationContext(paths);

                userDAO = (GSUserDAO)ctx.getBean("gsLdapUserDAO");               
                userGroupDAO = (UserGroupDAO)ctx.getBean("ldapUserGroupDAO");
            }
            
        }
    }
    
    private static DirContext ldapContext = null;
        
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    	try {
	        // Start an LDAP server and import test data
	    	ldapContext = LdapTestUtils.startApacheDirectoryServer(10389, "dc=example,dc=com", "test", LdapTestUtils.DEFAULT_PRINCIPAL, LdapTestUtils.DEFAULT_PASSWORD, null);	    	
    	} catch(Exception e) {
    		ldapContext = null;
    	}
    	
    }
   
    @AfterClass
    public static void tearDownClass() throws Exception {
        LdapTestUtils.destroyApacheDirectoryServer(LdapTestUtils.DEFAULT_PRINCIPAL, LdapTestUtils.DEFAULT_PASSWORD);      
        if(ldapContext != null) {
        	ldapContext.close();
        	ldapContext = null;
        }
    }

    @Before
    public void setUp() throws Exception {
    	org.junit.Assume.assumeNotNull(ldapContext);
    	// Bind to the directory
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl("ldap://127.0.0.1:10389");
        contextSource.setUserDn(LdapTestUtils.DEFAULT_PRINCIPAL);
        contextSource.setPassword(LdapTestUtils.DEFAULT_PASSWORD);
        contextSource.setPooled(false);
        //contextSource.setDirObjectFactory(null);
        contextSource.afterPropertiesSet();                

        // Create the Sprint LDAP template
        LdapTemplate template = new LdapTemplate(contextSource);

        // Clear out any old data - and load the test data
        LdapTestUtils.cleanAndSetup(template.getContextSource(), new DistinguishedName("dc=example,dc=com"), new ClassPathResource("data.ldif"));
        LOGGER.info("################ Running " + getClass().getSimpleName() );
        

        LOGGER.info("##### Ending setup for " + getClass().getSimpleName() + " ###----------------------");    	
    }

    @Test
    public void testCheckDAOs() {
    	assertNotNull(userDAO);      	
    }

}

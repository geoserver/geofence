/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

// TODO: Auto-generated Javadoc
/**
 * The Class AOIServiceTest.
 */
public class AOIServiceTest extends TestCase
// AbstractDependencyInjectionSpringContextTests {
{
    // private final Logger logger = LogManager.getLogger(this.getClass());
    //
    // @Autowired
    // private GEOFENCERemoteService geofenceRemoteService;
    //
    // public void testService() {
    // assertNotNull(geofenceRemoteService);
    //
    // UserList aoiList = geofenceRemoteService.getClient().getUsers();
    //
    // if (aoiList.getList() != null)
    // logger.info("******************* TOTAL Users  ***********************"
    // + aoiList.getList().size());
    // }
    //
    // public void testUserSearch() {
    //
    // String username1 = "%user%";
    //
    // SearchRequest srq = new SearchRequest(username1);
    //
    // long userCount = geofenceRemoteService.getClient().getUsersCount(srq);
    //
    // logger.info("USER COUNT FOR %USE% ****************************** "
    // + userCount);
    //
    // PaginatedSearchRequest psr = new PaginatedSearchRequest(username1, 25,
    // 0);
    //
    // UserList ul = geofenceRemoteService.getClient().searchUsers(psr);
    //
    // if (ul.getList() != null)
    // logger.info("FOUND ELEMENTS FOR PAGINATION ************** "
    // + ul.getList().size());
    //
    // }
    //
    // public void testAOISearch() {
    // AOIList aois = geofenceRemoteService.getClient().getAois();
    //
    // if (aois.getList() != null)
    // logger.info("NUMBER OF AOI :***************************** "
    // + aois.getList().size());
    // }

    // public void testInternalService() {
    // List<RegisteredUser> usersRegistered = geofenceRemoteService
    // .getInternalService().getUsers();
    //
    // for (RegisteredUser regUser : usersRegistered) {
    // logger.info("USER_NAME: " + regUser.getUserName()
    // + "  CONNECT_ID: " + regUser.getConnectId());
    // }
    // }

    // protected String[] getConfigLocations() {
    // return new String[] { "classpath:applicationContext.xml" };
    // }

    /**
     * Instantiates a new aOI service test.
     * 
     * @param testName
     *            the test name
     */
    public AOIServiceTest(String testName) {
        super(testName);
    }

    /**
     * Suite.
     * 
     * @return the test
     */
    public static Test suite() {
        return new TestSuite(AOIServiceTest.class);
    }

    /**
     * Test app.
     */
    public void testApp() {
        assertTrue(true);
    }

}

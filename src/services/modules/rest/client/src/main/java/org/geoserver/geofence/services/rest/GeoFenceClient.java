/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest;

import org.geoserver.geofence.services.rest.RESTBatchService;
import org.geoserver.geofence.services.rest.RESTUserGroupService;
import org.geoserver.geofence.services.rest.RESTUserService;
import org.geoserver.geofence.services.rest.RESTGSInstanceService;
import org.geoserver.geofence.services.rest.RESTRuleService;
import java.util.HashMap;
import java.util.Map;
import org.apache.cxf.common.util.Base64Utility;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GeoFenceClient {

    private String username = null;
    private String password = null;
    private String restUrl = null;

    private Map<Class, Object> services  = new HashMap<Class, Object>();

    public GeoFenceClient() {
    }

    //==========================================================================

    // endpoint is how the service is mapped in the CXF servlet.
    // since it's specified in the applicationcontext, it can not be automatically retrieved by the proxy client
    
    protected <T>T getService(Class<T> clazz, String endpoint) {
        if(services.containsKey(clazz))
            return (T)services.get(clazz);

        if(restUrl == null)
            new IllegalStateException("GeoFence URL not set");

        synchronized(services) {
//            T proxy = JAXRSClientFactory.create(restUrl, clazz, username, password, null);
            
            T proxy = JAXRSClientFactory.create(restUrl+"/"+endpoint, clazz);
            String authorizationHeader = "Basic "  + Base64Utility.encode((username+":"+password).getBytes());
            WebClient.client(proxy).header("Authorization", authorizationHeader);

//        WebClient.client(proxy).accept("text/xml");
            services.put(clazz, proxy);
            return proxy;
        }
    }

    //==========================================================================

    public RESTUserGroupService getUserGroupService() {
        return getService(RESTUserGroupService.class, "groups");
    }

    public RESTUserService getUserService() {
        return getService(RESTUserService.class, "users");
    }

    public RESTGSInstanceService getGSInstanceService() {
        return getService(RESTGSInstanceService.class, "instances");
    }

    public RESTRuleService getRuleService() {
        return getService(RESTRuleService.class, "rules");
    }

    public RESTBatchService getBatchService() {
        return getService(RESTBatchService.class, "batch");
    }

    //==========================================================================

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRestUrl() {
        return restUrl;
    }

    public void setGeostoreRestUrl(String restUrl) {
        this.restUrl = restUrl;
    }

}

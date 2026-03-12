/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.client;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import java.util.HashMap;
import java.util.Map;
import org.geofence.web.rest.api.interfaces.RESTAdminRuleService;
import org.geofence.web.rest.api.interfaces.RESTBatchService;
import org.geofence.web.rest.api.interfaces.RESTGSInstanceService;
import org.geofence.web.rest.api.interfaces.RESTRuleService;
import org.geofence.web.rest.api.interfaces.RESTUserGroupService;
import org.geofence.web.rest.api.interfaces.RESTUserService;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

/** @author ETj (etj at geo-solutions.it) */
public class GeoFenceClient {

    private String username = null;
    private String password = null;
    private String restUrl = null;

    private final Map<Class, Object> services = new HashMap<>();

    public GeoFenceClient() {}

    // ==========================================================================

    protected <T> T getService(Class<T> clazz, String endpoint) {

        if (services.containsKey(clazz)) return (T) services.get(clazz);

        if (restUrl == null) throw new IllegalStateException("GeoFence URL not set");

        synchronized (services) {
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(restUrl).path(endpoint);

            T proxy = WebResourceFactory.newResource(clazz, target);

            services.put(clazz, proxy);
            return proxy;
        }
    }

    //    //==========================================================================
    //
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

    public RESTAdminRuleService getAdminRuleService() {
        return getService(RESTAdminRuleService.class, "adminrules");
    }

    public RESTBatchService getBatchService() {
        return getService(RESTBatchService.class, "batch");
    }

    // ==========================================================================

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

    public void setRestUrl(String restUrl) {
        this.restUrl = restUrl;
    }
}

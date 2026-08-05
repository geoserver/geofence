/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.client;

import org.geofence.web.rest.api.interfaces.RESTAdminRuleService;
import org.geofence.web.rest.api.interfaces.RESTBatchService;
import org.geofence.web.rest.api.interfaces.RESTConfigService;
import org.geofence.web.rest.api.interfaces.RESTGSInstanceService;
import org.geofence.web.rest.api.interfaces.RESTRuleService;
import org.geofence.web.rest.api.interfaces.RESTUserGroupService;
import org.geofence.web.rest.api.interfaces.RESTUserService;
import org.springframework.web.client.RestClient;

/**
 * Client for the 7 admin/CRUD services ({@link RESTRuleService}, {@link RESTAdminRuleService}, {@link RESTUserService},
 * {@link RESTUserGroupService}, {@link RESTGSInstanceService}, {@link RESTConfigService}, {@link RESTBatchService}) -
 * as opposed to {@link GeoFenceClient}, which only covers the read-only {@code RESTRuleReaderService}.
 *
 * <p>Unlike {@code GeoFenceClient}, these 7 services still use {@code @RequestMapping}-family annotations
 * ({@code @PostMapping}/{@code @PutMapping}/...), not {@code @HttpExchange} - their write endpoints declare dual
 * JSON+XML {@code consumes} for every other caller of this REST API, and {@code @HttpExchange}'s {@code contentType}
 * takes a single value, not an array, so switching these interfaces to it (as was done for
 * {@code RESTRuleReaderService}) would drop XML support from the server's public contract, not just from this client.
 * So each getter below returns a hand-rolled {@link RestClient} adapter instead of an {@code HttpServiceProxyFactory}
 * proxy - more code than a proxy, but zero risk to the server-side interfaces: this client always sends/reads JSON,
 * which is a choice local to the client, not a server-side restriction.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GeoFenceAdminClient {

    private String username = null;
    private String password = null;
    private String restUrl = null;

    private RestClient restClient;

    private RESTRuleService ruleService;
    private RESTAdminRuleService adminRuleService;
    private RESTUserService userService;
    private RESTUserGroupService userGroupService;
    private RESTGSInstanceService gsInstanceService;
    private RESTConfigService configService;
    private RESTBatchService batchService;

    public GeoFenceAdminClient() {}

    // ==========================================================================

    /**
     * Wipes all rules, users, groups and instances on the target GeoFence, via {@code RESTConfigService}'s {@code PUT
     * /config/cleanup}.
     */
    public void removeAll() {
        getConfigService().cleanup();
    }

    public synchronized RESTRuleService getRuleService() {
        if (ruleService == null) ruleService = new RuleServiceHttpClient(restClient());
        return ruleService;
    }

    public synchronized RESTAdminRuleService getAdminRuleService() {
        if (adminRuleService == null) adminRuleService = new AdminRuleServiceHttpClient(restClient());
        return adminRuleService;
    }

    public synchronized RESTUserService getUserService() {
        if (userService == null) userService = new UserServiceHttpClient(restClient());
        return userService;
    }

    public synchronized RESTUserGroupService getUserGroupService() {
        if (userGroupService == null) userGroupService = new UserGroupServiceHttpClient(restClient());
        return userGroupService;
    }

    public synchronized RESTGSInstanceService getGSInstanceService() {
        if (gsInstanceService == null) gsInstanceService = new GSInstanceServiceHttpClient(restClient());
        return gsInstanceService;
    }

    public synchronized RESTConfigService getConfigService() {
        if (configService == null) configService = new ConfigServiceHttpClient(restClient());
        return configService;
    }

    public synchronized RESTBatchService getBatchService() {
        if (batchService == null) batchService = new BatchServiceHttpClient(restClient());
        return batchService;
    }

    private synchronized RestClient restClient() {
        if (restClient == null) {
            restClient = RestClients.build(restUrl, username, password);
        }
        return restClient;
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

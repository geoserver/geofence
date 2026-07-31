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
 * MOSTLY STUB: client for the 7 admin/CRUD services ({@link RESTRuleService}, {@link RESTAdminRuleService},
 * {@link RESTUserService}, {@link RESTUserGroupService}, {@link RESTGSInstanceService}, {@link RESTConfigService},
 * {@link RESTBatchService}) - as opposed to {@link GeoFenceClient}, which only covers the read-only
 * {@code RESTRuleReaderService}. Only {@link #removeAll()} is implemented so far: it's a bodyless call, so it dodges
 * the open question blocking the rest - those services' write endpoints (insert/update/restore/exec) still declare dual
 * JSON+XML {@code consumes}, and {@code @HttpExchange}'s {@code contentType} attribute takes a single value, not an
 * array, so it can't express accepting either format the way {@code @PostMapping}'s {@code consumes} array can - the
 * real implementation needs a decision on how to reconcile that before it can use the same
 * {@code HttpServiceProxyFactory} approach {@link GeoFenceClient} uses.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GeoFenceAdminClient {

    private String username = null;
    private String password = null;
    private String restUrl = null;

    public GeoFenceAdminClient() {}

    // ==========================================================================

    /**
     * Wipes all rules, users, groups and instances on the target GeoFence, via {@code RESTConfigService}'s {@code PUT
     * /config/cleanup} - a bodyless call, so it needs no request-payload (de)serialization and isn't blocked by the
     * {@code @HttpExchange contentType} limitation the rest of this client's endpoints still are. Called as a plain
     * {@link RestClient} PUT rather than an {@code HttpServiceProxyFactory} proxy, since {@code RESTConfigService} uses
     * Spring MVC ({@code @PutMapping}) annotations, which the proxy factory doesn't map.
     */
    public void removeAll() {
        if (restUrl == null) throw new IllegalStateException("GeoFence URL not set");

        RestClient.Builder builder = RestClient.builder().baseUrl(restUrl);
        if (username != null) {
            builder.requestInterceptor((request, body, execution) -> {
                request.getHeaders().setBasicAuth(username, password == null ? "" : password);
                return execution.execute(request, body);
            });
        }
        builder.build().put().uri("/config/cleanup").retrieve().toBodilessEntity();
    }

    public RESTRuleService getRuleService() {
        throw new UnsupportedOperationException("GeoFenceAdminClient is not implemented yet");
    }

    public RESTAdminRuleService getAdminRuleService() {
        throw new UnsupportedOperationException("GeoFenceAdminClient is not implemented yet");
    }

    public RESTUserService getUserService() {
        throw new UnsupportedOperationException("GeoFenceAdminClient is not implemented yet");
    }

    public RESTUserGroupService getUserGroupService() {
        throw new UnsupportedOperationException("GeoFenceAdminClient is not implemented yet");
    }

    public RESTGSInstanceService getGSInstanceService() {
        throw new UnsupportedOperationException("GeoFenceAdminClient is not implemented yet");
    }

    public RESTConfigService getConfigService() {
        throw new UnsupportedOperationException("GeoFenceAdminClient is not implemented yet");
    }

    public RESTBatchService getBatchService() {
        throw new UnsupportedOperationException("GeoFenceAdminClient is not implemented yet");
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

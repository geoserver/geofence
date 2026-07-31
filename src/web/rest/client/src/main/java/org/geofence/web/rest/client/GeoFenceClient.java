/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.client;

import java.net.URI;
import java.net.URISyntaxException;
import org.geofence.web.rest.api.interfaces.RESTRuleReaderService;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Builds a {@link RESTRuleReaderService} proxy over {@code restUrl} via Spring's {@code HttpServiceProxyFactory} -
 * {@code RESTRuleReaderService}'s own {@code @HttpExchange}/{@code @PostExchange} annotations (the same ones the server
 * side uses for dispatch) drive the request URL, body, and response mapping directly, with no per-call code here.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GeoFenceClient {

    private String username = null;
    private String password = null;
    private String restUrl = null;

    private RESTRuleReaderService ruleReaderService;

    public GeoFenceClient() {}

    // ==========================================================================

    public synchronized RESTRuleReaderService getRuleReaderService() {
        if (ruleReaderService == null) {
            if (restUrl == null) throw new IllegalStateException("GeoFence URL not set");
            requireAbsoluteUrl(restUrl);

            RestClient restClient = RestClient.builder().baseUrl(restUrl).build();
            RestClientAdapter adapter = RestClientAdapter.create(restClient);
            HttpServiceProxyFactory factory =
                    HttpServiceProxyFactory.builderFor(adapter).build();
            ruleReaderService = factory.createClient(RESTRuleReaderService.class);
        }
        return ruleReaderService;
    }

    /**
     * Rejects a URL with no scheme/host upfront: left unchecked, Apache HttpClient5 (the {@link RestClient}'s
     * underlying transport here) fails on a schemeless base URL with a bare {@code NullPointerException} deep in its
     * routing code, only once a request is actually attempted - not a useful error to surface to a caller.
     */
    private static void requireAbsoluteUrl(String url) {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid GeoFence REST URL: " + url, e);
        }
        if (uri.getScheme() == null || uri.getHost() == null) {
            throw new IllegalArgumentException("Invalid GeoFence REST URL: " + url);
        }
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

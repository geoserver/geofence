/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.client;

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
            RestClient restClient = RestClients.build(restUrl, username, password);
            RestClientAdapter adapter = RestClientAdapter.create(restClient);
            HttpServiceProxyFactory factory =
                    HttpServiceProxyFactory.builderFor(adapter).build();
            ruleReaderService = factory.createClient(RESTRuleReaderService.class);
        }
        return ruleReaderService;
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

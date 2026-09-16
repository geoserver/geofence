/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.client;

import org.geofence.web.rest.api.interfaces.RESTAdminRuleService;
import org.geofence.web.rest.api.interfaces.params.RESTAdminRuleFilter;
import org.geofence.web.rest.api.model.RESTInputAdminRule;
import org.geofence.web.rest.api.model.RESTOutputAdminRule;
import org.geofence.web.rest.api.model.RESTOutputAdminRuleList;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

/** Hand-rolled {@link RestClient} adapter for {@link RESTAdminRuleService} - see {@link UserGroupServiceHttpClient}. */
class AdminRuleServiceHttpClient implements RESTAdminRuleService {

    private final RestClient restClient;

    AdminRuleServiceHttpClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ResponseEntity<Long> insert(RESTInputAdminRule rule) {
        return restClient
                .post()
                .uri("/adminrules/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(rule)
                .retrieve()
                .toEntity(Long.class);
    }

    @Override
    public RESTOutputAdminRule get(Long id) {
        return restClient
                .get()
                .uri("/adminrules/id/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(RESTOutputAdminRule.class);
    }

    @Override
    public void update(Long id, RESTInputAdminRule rule) {
        restClient
                .put()
                .uri("/adminrules/id/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rule)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public ResponseEntity<String> delete(Long id) {
        return restClient.delete().uri("/adminrules/id/{id}", id).retrieve().toEntity(String.class);
    }

    @Override
    public RESTOutputAdminRuleList get(Integer page, Integer entries, boolean full, RESTAdminRuleFilter query) {
        return restClient
                .get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/adminrules/");
                    if (page != null) uriBuilder.queryParam("page", page);
                    if (entries != null) uriBuilder.queryParam("entries", entries);
                    uriBuilder.queryParam("full", full);
                    FilterParams.appendTo(uriBuilder, query);
                    return uriBuilder.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(RESTOutputAdminRuleList.class);
    }

    @Override
    public long count(RESTAdminRuleFilter query) {
        return restClient
                .get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/adminrules/count");
                    FilterParams.appendTo(uriBuilder, query);
                    return uriBuilder.build();
                })
                .retrieve()
                .body(Long.class);
    }
}

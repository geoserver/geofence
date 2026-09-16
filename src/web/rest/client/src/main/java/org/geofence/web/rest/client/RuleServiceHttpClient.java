/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.client;

import org.geofence.web.rest.api.interfaces.RESTRuleService;
import org.geofence.web.rest.api.interfaces.params.RESTRuleFilter;
import org.geofence.web.rest.api.model.RESTInputRule;
import org.geofence.web.rest.api.model.RESTOutputRule;
import org.geofence.web.rest.api.model.RESTOutputRuleList;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

/** Hand-rolled {@link RestClient} adapter for {@link RESTRuleService} - see {@link UserGroupServiceHttpClient}. */
class RuleServiceHttpClient implements RESTRuleService {

    private final RestClient restClient;

    RuleServiceHttpClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ResponseEntity<Long> insert(RESTInputRule rule) {
        return restClient
                .post()
                .uri("/rules/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(rule)
                .retrieve()
                .toEntity(Long.class);
    }

    @Override
    public RESTOutputRule get(Long id) {
        return restClient
                .get()
                .uri("/rules/id/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(RESTOutputRule.class);
    }

    @Override
    public void update(Long id, RESTInputRule rule) {
        restClient
                .put()
                .uri("/rules/id/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(rule)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public ResponseEntity<String> delete(Long id) {
        return restClient.delete().uri("/rules/id/{id}", id).retrieve().toEntity(String.class);
    }

    @Override
    public RESTOutputRuleList get(Integer page, Integer entries, boolean full, RESTRuleFilter query) {
        return restClient
                .get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/rules/");
                    if (page != null) uriBuilder.queryParam("page", page);
                    if (entries != null) uriBuilder.queryParam("entries", entries);
                    uriBuilder.queryParam("full", full);
                    FilterParams.appendTo(uriBuilder, query);
                    return uriBuilder.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(RESTOutputRuleList.class);
    }

    @Override
    public long count(RESTRuleFilter query) {
        return restClient
                .get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/rules/count");
                    FilterParams.appendTo(uriBuilder, query);
                    return uriBuilder.build();
                })
                .retrieve()
                .body(Long.class);
    }

    @Override
    public ResponseEntity<String> move(String rulesIds, Integer targetPriority) {
        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rules/move")
                        .queryParam("rulesIds", rulesIds)
                        .queryParam("targetPriority", targetPriority)
                        .build())
                .retrieve()
                .toEntity(String.class);
    }
}

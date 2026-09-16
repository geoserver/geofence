/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.client;

import org.geofence.web.rest.api.interfaces.RESTConfigService;
import org.geofence.web.rest.api.model.RESTBatch;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/** Hand-rolled {@link RestClient} adapter for {@link RESTConfigService} - see {@link UserGroupServiceHttpClient}. */
class ConfigServiceHttpClient implements RESTConfigService {

    private final RestClient restClient;

    ConfigServiceHttpClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public RESTBatch backup(Boolean includeGRUsers) {
        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/config/backup")
                        .queryParam("includeGRUsers", includeGRUsers)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(RESTBatch.class);
    }

    @Override
    public void restore(RESTBatch batch) {
        restClient
                .put()
                .uri("/config/restore")
                .contentType(MediaType.APPLICATION_JSON)
                .body(batch)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void cleanup() {
        restClient.put().uri("/config/cleanup").retrieve().toBodilessEntity();
    }

    @Override
    public RESTBatch backupGroups() {
        return restClient
                .get()
                .uri("/config/backup/groups")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(RESTBatch.class);
    }

    @Override
    public RESTBatch backupUsers() {
        return restClient
                .get()
                .uri("/config/backup/users")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(RESTBatch.class);
    }

    @Override
    public RESTBatch backupInstances() {
        return restClient
                .get()
                .uri("/config/backup/instances")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(RESTBatch.class);
    }

    @Override
    public RESTBatch backupRules() {
        return restClient
                .get()
                .uri("/config/backup/rules")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(RESTBatch.class);
    }
}

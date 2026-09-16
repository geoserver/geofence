/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.client;

import org.geofence.web.rest.api.interfaces.RESTGSInstanceService;
import org.geofence.web.rest.api.model.RESTInputInstance;
import org.geofence.web.rest.api.model.RESTOutputInstance;
import org.geofence.web.rest.api.model.RESTShortInstanceList;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

/**
 * Hand-rolled {@link RestClient} adapter for {@link RESTGSInstanceService} - see {@link UserGroupServiceHttpClient}.
 */
class GSInstanceServiceHttpClient implements RESTGSInstanceService {

    private final RestClient restClient;

    GSInstanceServiceHttpClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public RESTShortInstanceList getList(String nameLike, Integer page, Integer entries) {
        return restClient
                .get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/instance/");
                    if (nameLike != null) uriBuilder.queryParam("nameLike", nameLike);
                    if (page != null) uriBuilder.queryParam("page", page);
                    if (entries != null) uriBuilder.queryParam("entries", entries);
                    return uriBuilder.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(RESTShortInstanceList.class);
    }

    @Override
    public long count(String nameLike) {
        return restClient
                .get()
                .uri("/instance/count/{nameLike}", nameLike)
                .retrieve()
                .body(Long.class);
    }

    @Override
    public RESTOutputInstance get(Long id) {
        return restClient
                .get()
                .uri("/instance/id/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(RESTOutputInstance.class);
    }

    @Override
    public RESTOutputInstance get(String name) {
        return restClient
                .get()
                .uri("/instance/name/{name}", name)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(RESTOutputInstance.class);
    }

    @Override
    public ResponseEntity<Long> insert(RESTInputInstance instance) {
        return restClient
                .post()
                .uri("/instance/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(instance)
                .retrieve()
                .toEntity(Long.class);
    }

    @Override
    public void update(Long id, RESTInputInstance instance) {
        restClient
                .put()
                .uri("/instance/id/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(instance)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void update(String name, RESTInputInstance instance) {
        restClient
                .put()
                .uri("/instance/name/{name}", name)
                .contentType(MediaType.APPLICATION_JSON)
                .body(instance)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public ResponseEntity<String> delete(Long id, boolean cascade) {
        return restClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/instance/id/{id}")
                        .queryParam("cascade", cascade)
                        .build(id))
                .retrieve()
                .toEntity(String.class);
    }

    @Override
    public ResponseEntity<String> delete(String name, boolean cascade) {
        return restClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/instance/name/{name}")
                        .queryParam("cascade", cascade)
                        .build(name))
                .retrieve()
                .toEntity(String.class);
    }
}

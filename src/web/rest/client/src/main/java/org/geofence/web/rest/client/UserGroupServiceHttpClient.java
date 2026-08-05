/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.client;

import org.geofence.web.rest.api.interfaces.RESTUserGroupService;
import org.geofence.web.rest.api.model.RESTInputGroup;
import org.geofence.web.rest.api.model.RESTOutputGroup;
import org.geofence.web.rest.api.model.config.RESTFullUserGroupList;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

/**
 * Hand-rolled {@link RestClient} adapter for {@link RESTUserGroupService} - not an {@code HttpServiceProxyFactory}
 * proxy, since this interface still uses {@code @RequestMapping}-family annotations (dual JSON+XML {@code consumes} on
 * the write endpoints, which {@code @HttpExchange}'s single-valued {@code contentType} can't express). This client
 * always sends/reads JSON; the server's dual-format support for other callers is untouched.
 */
class UserGroupServiceHttpClient implements RESTUserGroupService {

    private final RestClient restClient;

    UserGroupServiceHttpClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public RESTFullUserGroupList getList(String nameLike, Integer page, Integer entries) {
        return restClient
                .get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/usergroup/");
                    if (nameLike != null) uriBuilder.queryParam("nameLike", nameLike);
                    if (page != null) uriBuilder.queryParam("page", page);
                    if (entries != null) uriBuilder.queryParam("entries", entries);
                    return uriBuilder.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(RESTFullUserGroupList.class);
    }

    @Override
    public long count(String nameLike) {
        return restClient
                .get()
                .uri("/usergroup/count/{nameLike}", nameLike)
                .retrieve()
                .body(Long.class);
    }

    @Override
    public long count2(String nameLike) {
        return restClient
                .get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/usergroup/count");
                    if (nameLike != null) uriBuilder.queryParam("nameLike", nameLike);
                    return uriBuilder.build();
                })
                .retrieve()
                .body(Long.class);
    }

    @Override
    public RESTOutputGroup get(String name) {
        return restClient
                .get()
                .uri("/usergroup/name/{name}", name)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(RESTOutputGroup.class);
    }

    @Override
    public ResponseEntity<Long> insert(RESTInputGroup group) {
        return restClient
                .post()
                .uri("/usergroup/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(group)
                .retrieve()
                .toEntity(Long.class);
    }

    @Override
    public void update(String name, RESTInputGroup group) {
        restClient
                .put()
                .uri("/usergroup/name/{name}", name)
                .contentType(MediaType.APPLICATION_JSON)
                .body(group)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public ResponseEntity<String> delete(String name, boolean cascade) {
        return restClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/usergroup/name/{name}")
                        .queryParam("cascade", cascade)
                        .build(name))
                .retrieve()
                .toEntity(String.class);
    }
}

/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.client;

import org.geofence.web.rest.api.interfaces.RESTUserService;
import org.geofence.web.rest.api.model.RESTInputUser;
import org.geofence.web.rest.api.model.RESTOutputUser;
import org.geofence.web.rest.api.model.RESTShortUserList;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

/** Hand-rolled {@link RestClient} adapter for {@link RESTUserService} - see {@link UserGroupServiceHttpClient}. */
class UserServiceHttpClient implements RESTUserService {

    private final RestClient restClient;

    UserServiceHttpClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public RESTShortUserList getList(String nameLike, Integer page, Integer entries) {
        return restClient
                .get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/user/");
                    if (nameLike != null) uriBuilder.queryParam("nameLike", nameLike);
                    if (page != null) uriBuilder.queryParam("page", page);
                    if (entries != null) uriBuilder.queryParam("entries", entries);
                    return uriBuilder.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(RESTShortUserList.class);
    }

    @Override
    public long count(String nameLike) {
        return restClient
                .get()
                .uri("/user/count/{nameLike}", nameLike)
                .retrieve()
                .body(Long.class);
    }

    @Override
    public long count2(String nameLike) {
        return restClient
                .get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/user/count");
                    if (nameLike != null) uriBuilder.queryParam("nameLike", nameLike);
                    return uriBuilder.build();
                })
                .retrieve()
                .body(Long.class);
    }

    @Override
    public RESTOutputUser get(String name) {
        return restClient
                .get()
                .uri("/user/name/{name}", name)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(RESTOutputUser.class);
    }

    @Override
    public ResponseEntity<Long> insert(RESTInputUser user) {
        return restClient
                .post()
                .uri("/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(user)
                .retrieve()
                .toEntity(Long.class);
    }

    @Override
    public void update(Long id, RESTInputUser user) {
        restClient
                .put()
                .uri("/user/id/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void update(String name, RESTInputUser user) {
        restClient
                .put()
                .uri("/user/name/{name}", name)
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public ResponseEntity<String> delete(String name, boolean cascade) {
        return restClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/user/name/{name}")
                        .queryParam("cascade", cascade)
                        .build(name))
                .retrieve()
                .toEntity(String.class);
    }

    @Override
    public void addIntoGroup(String userName, String groupName) {
        restClient
                .put()
                .uri("/user/name/{userName}/group/name/{groupName}", userName, groupName)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void removeFromGroup(String userName, String groupName) {
        restClient
                .delete()
                .uri("/user/name/{userName}/group/name/{groupName}", userName, groupName)
                .retrieve()
                .toBodilessEntity();
    }
}

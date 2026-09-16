/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.client;

import org.geofence.web.rest.api.interfaces.RESTBatchService;
import org.geofence.web.rest.api.model.RESTBatch;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

/** Hand-rolled {@link RestClient} adapter for {@link RESTBatchService} - see {@link UserGroupServiceHttpClient}. */
class BatchServiceHttpClient implements RESTBatchService {

    private final RestClient restClient;

    BatchServiceHttpClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ResponseEntity<String> exec(RESTBatch batch) {
        return restClient
                .post()
                .uri("/batch/exec")
                .contentType(MediaType.APPLICATION_JSON)
                .body(batch)
                .retrieve()
                .toEntity(String.class);
    }

    /** Not a REST endpoint - {@code RESTBatchService} declares it with no mapping, server-internal use only. */
    @Override
    public void runBatch(RESTBatch batch) {
        throw new UnsupportedOperationException("runBatch() is not a REST endpoint - use exec() instead");
    }
}

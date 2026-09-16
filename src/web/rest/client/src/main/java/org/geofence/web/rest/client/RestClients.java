/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.client;

import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.web.client.RestClient;

/** Shared {@link RestClient} construction for {@link GeoFenceClient} and {@link GeoFenceAdminClient}. */
final class RestClients {

    private RestClients() {}

    static RestClient build(String restUrl, String username, String password) {
        if (restUrl == null) throw new IllegalStateException("GeoFence URL not set");
        requireAbsoluteUrl(restUrl);

        RestClient.Builder builder = RestClient.builder().baseUrl(restUrl);
        if (username != null) {
            builder.requestInterceptor((request, body, execution) -> {
                request.getHeaders().setBasicAuth(username, password == null ? "" : password);
                return execution.execute(request, body);
            });
        }
        return builder.build();
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
}

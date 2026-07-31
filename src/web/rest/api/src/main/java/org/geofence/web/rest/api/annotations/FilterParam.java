/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps a filter DTO field to the query-string parameter it's bound from - read reflectively by the REST layer's
 * argument resolver for {@link FilterBean} parameters. Field name and query-parameter name deliberately differ for some
 * fields (e.g. {@code serviceName} bound from {@code service}), which is exactly what this mapping expresses.
 *
 * <p>Replaces the former JAX-RS {@code @QueryParam}, which was kept only as inert metadata after the Jersey-to-Spring
 * migration (no Jersey runtime is involved) - this keeps the same mechanism without dragging in {@code jakarta.ws.rs}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FilterParam {
    String value();
}

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
 * Marks a controller-method parameter (a filter DTO) whose fields are bound from query-string parameters via their
 * {@link FilterParam} annotations. Replaces the former JAX-RS {@code @BeanParam} (kept only as inert metadata after the
 * Jersey-to-Spring migration).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface FilterBean {}

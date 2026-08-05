/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.client;

import java.lang.reflect.Field;
import org.geofence.web.rest.api.annotations.FilterParam;
import org.springframework.web.util.UriBuilder;

/**
 * Client-side mirror of {@code QueryParamBeanArgumentResolver} (web/rest/impl): appends one query parameter per
 * {@code @FilterParam}-annotated, non-null field on a {@code @FilterBean} filter DTO (e.g. {@code RESTRuleFilter},
 * {@code RESTAdminRuleFilter}), using the same field-to-parameter-name mapping the server resolves them with.
 */
final class FilterParams {

    private FilterParams() {}

    static void appendTo(UriBuilder uriBuilder, Object filter) {
        if (filter == null) return;
        for (Field field : filter.getClass().getFields()) {
            FilterParam filterParam = field.getAnnotation(FilterParam.class);
            if (filterParam == null) continue;
            Object value;
            try {
                value = field.get(filter);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
            if (value != null) {
                uriBuilder.queryParam(filterParam.value(), value.toString());
            }
        }
    }
}

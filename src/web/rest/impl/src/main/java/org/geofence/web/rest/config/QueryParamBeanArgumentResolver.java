/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.config;

import java.lang.reflect.Field;
import org.geofence.web.rest.api.annotations.FilterBean;
import org.geofence.web.rest.api.annotations.FilterParam;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Resolves {@code @FilterBean}-annotated controller parameters (e.g. {@code RESTRuleFilter},
 * {@code RESTAdminRuleFilter}) from query-string parameters, using the {@code @FilterParam}-annotated public fields on
 * the target type as the field-to-parameter-name mapping. These filter classes carry no other framework annotations, so
 * their query-string contract stays exactly as declared without needing Spring-specific bean properties or a per-class
 * binder.
 */
public class QueryParamBeanArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(FilterBean.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory)
            throws Exception {
        Class<?> type = parameter.getParameterType();
        Object instance = type.getDeclaredConstructor().newInstance();
        for (Field field : type.getFields()) {
            FilterParam filterParam = field.getAnnotation(FilterParam.class);
            if (filterParam == null) {
                continue;
            }
            String value = webRequest.getParameter(filterParam.value());
            if (value != null) {
                field.set(instance, convert(value, field.getType()));
            }
        }
        return instance;
    }

    private Object convert(String value, Class<?> targetType) {
        if (targetType == String.class) {
            return value;
        }
        if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.valueOf(value);
        }
        if (targetType == Long.class || targetType == long.class) {
            return Long.valueOf(value);
        }
        if (targetType == Integer.class || targetType == int.class) {
            return Integer.valueOf(value);
        }
        throw new IllegalArgumentException("Unsupported @QueryParam field type: " + targetType);
    }
}

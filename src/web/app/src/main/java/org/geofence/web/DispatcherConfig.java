/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web;

import java.util.List;
import org.geofence.web.rest.config.PlainTextNumberHttpMessageConverter;
import org.geofence.web.rest.config.QueryParamBeanArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * The {@code DispatcherServlet}'s own (child) context configuration - has no component-scan of its own. The actual
 * {@code @RestController} beans (e.g. {@code RESTRuleReaderServiceImpl}) live in the root {@code ContextLoaderListener}
 * context ({@code GeofenceRESTConfig}, in {@code geofence-rest-impl}), since that's where their {@code @Autowired}
 * dependencies on the core engine are satisfied - re-scanning them here would instantiate a second, separate copy of
 * the whole engine (duplicate DataSource/EntityManagerFactory). Instead, extends {@code WebMvcConfigurationSupport}
 * directly (rather than the {@code @EnableWebMvc} shortcut, to safely override just this one bean) and turns on
 * {@code detectHandlerMethodsInAncestorContexts} so Spring MVC's handler mapping also looks at the parent (root)
 * context for controller beans.
 *
 * <p>Lives here in {@code web/app}, not alongside {@code GeofenceRESTConfig} in {@code org.geofence.web.rest.config}:
 * it needs a real Servlet environment to instantiate ({@code WebMvcConfigurationSupport}'s bean methods require a
 * {@code ServletContext}), which {@code GeofenceRESTConfig}'s component-scan can't guarantee - that config is also
 * loaded, without a Servlet environment, by every plain Spring test context in {@code web/rest/impl}.
 */
@Configuration
public class DispatcherConfig extends WebMvcConfigurationSupport {

    @Override
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        RequestMappingHandlerMapping mapping = super.createRequestMappingHandlerMapping();
        mapping.setDetectHandlerMethodsInAncestorContexts(true);
        return mapping;
    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Jackson (JSON) is already added by Spring's defaults since it's on the classpath; the DTOs' own
        // @XmlRootElement annotations let a plain Jaxb2RootElementHttpMessageConverter handle XML directly,
        // matching the golden-payload fixtures.
        converters.add(new Jaxb2RootElementHttpMessageConverter());
        // insert() endpoints declare text/plain among their producible types (matching the original JAX-RS
        // @Produces annotation) and return a bare Long id; Spring's StringHttpMessageConverter only writes
        // CharSequence, so without this any client that doesn't force JSON/XML via Accept gets a 500.
        converters.add(new PlainTextNumberHttpMessageConverter());
    }

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new QueryParamBeanArgumentResolver());
    }

    // Bean name matters: DispatcherServlet looks up a bean literally named "multipartResolver" to enable
    // @RequestPart-bound legacy multipart/form-data endpoints (e.g. RESTRuleServiceImpl.insertMultipart).
    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}

/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.config;

/** @author etj */
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import java.util.HashSet;
import java.util.Set;
import org.geofence.web.rest.api.interfaces.RESTAdminRuleService;
import org.geofence.web.rest.api.interfaces.RESTBatchService;
import org.geofence.web.rest.api.interfaces.RESTConfigService;
import org.geofence.web.rest.api.interfaces.RESTGSInstanceService;
import org.geofence.web.rest.api.interfaces.RESTRuleReaderService;
import org.geofence.web.rest.api.interfaces.RESTRuleService;
import org.geofence.web.rest.api.interfaces.RESTUserGroupService;
import org.geofence.web.rest.api.interfaces.RESTUserService;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * JAX-RS {@code Application}, deployed directly by the Jersey servlet (see {@code web.xml}'s
 * {@code jakarta.ws.rs.Application} init-param) - not a Spring bean itself. Extends the bare JAX-RS {@code Application}
 * rather than Jersey's {@code ResourceConfig} because {@code ResourceConfig.getSingletons()} is final; the REST service
 * implementations are real Spring {@code @Service} beans with {@code @Autowired} dependencies, so they can't be
 * registered by class (Jersey would construct its own, unwired instance via HK2) - {@link #getSingletons()} is called
 * by Jersey only after {@code @Context} field injection completes, so the Spring-managed singletons are looked up there
 * instead. Looked up by their JAX-RS *interface* type, not the concrete impl class: {@code GeofenceRESTConfig} enables
 * {@code @Transactional}, and Spring's default (interface-based, JDK dynamic proxy) AOP proxying means the actual
 * singleton's runtime type implements the interface without extending the impl class.
 */
public class JerseyConfig extends Application {

    @Context
    private ServletContext servletContext;

    @Override
    public Set<Class<?>> getClasses() {
        // providers - no Spring DI needed. jersey-media-jaxb's own XML providers (for classes without
        // @XmlRootElement, e.g. RESTRuleFilter) are auto-discovered from the classpath; XmlJaxbElementProvider
        // itself is abstract and was never a valid registration target.
        Set<Class<?>> classes = new HashSet<>();
        classes.add(JacksonFeature.class);
        classes.add(LoggingFeature.class);
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        Set<Object> singletons = new HashSet<>();
        singletons.add(ctx.getBean(RESTRuleService.class));
        singletons.add(ctx.getBean(RESTAdminRuleService.class));
        singletons.add(ctx.getBean(RESTBatchService.class));
        singletons.add(ctx.getBean(RESTRuleReaderService.class));
        singletons.add(ctx.getBean(RESTUserService.class));
        singletons.add(ctx.getBean(RESTUserGroupService.class));
        singletons.add(ctx.getBean(RESTGSInstanceService.class));
        singletons.add(ctx.getBean(RESTConfigService.class));
        return singletons;
    }
}

/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.config;

/** @author etj */
import org.geofence.web.rest.impl.RESTAdminRuleServiceImpl;
import org.geofence.web.rest.impl.RESTBatchServiceImpl;
import org.geofence.web.rest.impl.RESTRuleServiceImpl;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jaxb.internal.XmlJaxbElementProvider;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {

        // register all services
        register(RESTRuleServiceImpl.class);
        register(RESTAdminRuleServiceImpl.class);
        register(RESTBatchServiceImpl.class);

        // providers
        register(XmlJaxbElementProvider.class);
        register(JacksonFeature.class);

        // logging
        register(LoggingFeature.class);
    }
}

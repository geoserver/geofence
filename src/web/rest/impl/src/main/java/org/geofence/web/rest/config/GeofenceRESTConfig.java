/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.config;

import org.geofence.core.services.config.GeofenceServiceConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// proxyTargetClass=true: a @RestController bean with a @Transactional method (e.g. RESTRuleServiceImpl) needs a
// CGLIB (class-based) proxy, not the default JDK interface-based one - a JDK proxy's class carries none of the
// target class's annotations, so Spring MVC's handler detection (which looks for @RestController on the bean's
// class) would silently skip it.
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@ComponentScan(basePackages = "org.geofence.web.rest")
@Import(GeofenceServiceConfig.class)
public class GeofenceRESTConfig {}

package org.geofence.core.services.config;

import org.geofence.core.db.config.GeofencePersistenceConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "org.geofence.core.services")
@Import(GeofencePersistenceConfig.class)
public class GeofenceServiceConfig {}

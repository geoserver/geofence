package org.geofence.core.services.config;

import org.geofence.core.db.config.GeofencePersistenceConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
// lazyInit: engine boots on first use, not at context startup, so a host with no datasource configured can still
// start. Eager consumers must inject these @Lazy to preserve that.
@ComponentScan(basePackages = "org.geofence.core.services", lazyInit = true)
@Import(GeofencePersistenceConfig.class)
public class GeofenceServiceConfig {}

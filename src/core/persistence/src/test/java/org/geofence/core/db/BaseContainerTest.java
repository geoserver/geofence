/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db;

import org.geofence.core.db.config.GeofencePersistenceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = GeofencePersistenceConfig.class)
public abstract class BaseContainerTest {

    @Autowired
    public GeofencePersistenceConfig persistenceConfig;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("geofence.datasource.url", GeofenceTestDatabase::getJdbcUrl);
        registry.add("geofence.datasource.username", GeofenceTestDatabase::getUsername);
        registry.add("geofence.datasource.password", GeofenceTestDatabase::getPassword);
        registry.add("geofence.datasource.driver", GeofenceTestDatabase::getDriverClassName);
    }
}

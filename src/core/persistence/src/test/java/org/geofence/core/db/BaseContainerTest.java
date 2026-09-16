/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db;

import org.geofence.core.db.config.GeofencePersistenceConfig;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = GeofencePersistenceConfig.class)
public abstract class BaseContainerTest {

    /** Runs before the test instance is created, hence before the application context is refreshed. */
    @BeforeAll
    static void configureDatasource() {
        Assumptions.assumeTrue(
                GeofenceTestDatabase.isDockerAvailable(), "Docker is required for the GeoFence test database");
        GeofenceTestDatabase.configureAsDatasourceOverride();
    }

    @Autowired
    public GeofencePersistenceConfig persistenceConfig;
}

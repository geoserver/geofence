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
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringJUnitConfig(classes = GeofencePersistenceConfig.class)
public abstract class BaseContainerTest {

    private static final String POSTGRES_IMAGE = "postgis/postgis:15-3.4";
    private static final String POSTGRES_DB = "geofence_test-test";
    private static final String POSTGRES_USER = "geofence_test";
    private static final String POSTGRES_PASSWORD = "geofence_test";

    @Autowired
    public GeofencePersistenceConfig persistenceConfig;

    static final PostgreSQLContainer DB;

    static {
        DB = new PostgreSQLContainer(DockerImageName.parse(POSTGRES_IMAGE).asCompatibleSubstituteFor("postgres"))
                .withDatabaseName(POSTGRES_DB)
                .withUsername(POSTGRES_USER)
                .withPassword(POSTGRES_PASSWORD);

        DB.start();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("geofence.datasource.url", DB::getJdbcUrl);
        registry.add("geofence.datasource.username", DB::getUsername);
        registry.add("geofence.datasource.password", DB::getPassword);
        registry.add("geofence.datasource.driver", DB::getDriverClassName);
    }
}

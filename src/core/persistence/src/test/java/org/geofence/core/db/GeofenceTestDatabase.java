/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db;

import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Shared Postgres/PostGIS Testcontainer for {@code geofence.datasource.*}, framework-agnostic so both JUnit5 (see
 * {@link BaseContainerTest}) and JUnit4 consumers (which can't use {@code @DynamicPropertySource}) can use it.
 */
public final class GeofenceTestDatabase {

    private static final String POSTGRES_IMAGE = "postgis/postgis:15-3.4";
    private static final String POSTGRES_DB = "geofence_test-test";
    private static final String POSTGRES_USER = "geofence_test";
    private static final String POSTGRES_PASSWORD = "geofence_test";
    private static final String DRIVER_CLASS_NAME = "org.postgresql.Driver";

    private static final PostgreSQLContainer CONTAINER;

    static {
        CONTAINER = new PostgreSQLContainer(
                        DockerImageName.parse(POSTGRES_IMAGE).asCompatibleSubstituteFor("postgres"))
                .withDatabaseName(POSTGRES_DB)
                .withUsername(POSTGRES_USER)
                .withPassword(POSTGRES_PASSWORD);
        CONTAINER.start();
    }

    private GeofenceTestDatabase() {}

    public static String getJdbcUrl() {
        return CONTAINER.getJdbcUrl();
    }

    public static String getUsername() {
        return CONTAINER.getUsername();
    }

    public static String getPassword() {
        return CONTAINER.getPassword();
    }

    public static String getDriverClassName() {
        return DRIVER_CLASS_NAME;
    }

    /** Call before context refresh (e.g. in a static initializer) so {@code @Value} picks these up. */
    public static void applyAsSystemProperties() {
        System.setProperty("geofence.datasource.url", getJdbcUrl());
        System.setProperty("geofence.datasource.username", getUsername());
        System.setProperty("geofence.datasource.password", getPassword());
        System.setProperty("geofence.datasource.driver", getDriverClassName());
    }
}

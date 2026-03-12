/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db;

import com.zaxxer.hikari.HikariDataSource;
import org.geofence.core.db.datasource.DatabaseRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

public class TestContainerBootstrap {

    static PostgreSQLContainer container = new PostgreSQLContainer("postgis/postgis:16-3.4");

    static {
        container.start();
    }

    @Bean
    public InitializingBean registerTestDatabase(DatabaseRegistry registry) {

        return () -> {
            HikariDataSource ds = new HikariDataSource();

            ds.setJdbcUrl(container.getJdbcUrl());
            ds.setUsername(container.getUsername());
            ds.setPassword(container.getPassword());
            ds.setDriverClassName("org.postgresql.Driver");

            registry.addDatabase("test", ds);
        };
    }
}

package org.geofence.core.db.datasource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;

// @Service
public class DatabaseRegistry {

    private final DynamicRoutingDataSource routingDataSource;

    private final Map<String, DataSource> databases = new ConcurrentHashMap<>();

    public DatabaseRegistry(DynamicRoutingDataSource routingDataSource) {

        this.routingDataSource = routingDataSource;
    }

    public synchronized void addDatabase(String key, DataSource dataSource) {

        databases.put(key, dataSource);

        Map<Object, Object> newMap = new ConcurrentHashMap<>(databases);

        routingDataSource.setTargetDataSources(newMap);

        routingDataSource.afterPropertiesSet();
    }
}

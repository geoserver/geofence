package org.geofence.core.db.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    public static void set(String key) {
        CURRENT.set(key);
    }

    public static void clear() {
        CURRENT.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return CURRENT.get();
    }
}

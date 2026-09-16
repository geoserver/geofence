/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.Closeable;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * A {@link DataSource} whose underlying connection pool can be swapped at runtime via {@link #reconfigure} without
 * changing this object's identity - beans holding a reference to this instance (JPA's {@code EntityManagerFactory},
 * DAOs, ...) keep working across a swap.
 *
 * <p>Implements {@link Closeable} (Spring's default "(inferred)" destroy method looks for a {@code close()} method) so
 * the pool still gets released on context shutdown, same as when a plain {@code HikariDataSource} was the bean.
 */
public class ReloadableDataSource implements DataSource, Closeable {

    private final AtomicReference<HikariDataSource> delegate = new AtomicReference<>();

    public void reconfigure(
            String url,
            String username,
            String password,
            String driverClassName,
            Map<String, String> hikariProperties) {
        Properties props = new Properties();
        props.setProperty("jdbcUrl", url);
        props.setProperty("username", username);
        props.setProperty("password", password);
        props.setProperty("driverClassName", driverClassName);
        props.putAll(hikariProperties);

        HikariDataSource newDataSource = new HikariDataSource(new HikariConfig(props));

        HikariDataSource old = delegate.getAndSet(newDataSource);
        if (old != null) {
            old.close();
        }
    }

    @Override
    public void close() {
        HikariDataSource current = delegate.get();
        if (current != null) {
            current.close();
        }
    }

    private HikariDataSource delegate() {
        HikariDataSource current = delegate.get();
        if (current == null) {
            throw new IllegalStateException("ReloadableDataSource has not been configured yet");
        }
        return current;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return delegate().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return delegate().getConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return delegate().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        delegate().setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        delegate().setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return delegate().getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return delegate().getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return delegate().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return delegate().isWrapperFor(iface);
    }
}

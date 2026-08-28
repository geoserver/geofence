package org.geofence.core.db.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.Optional;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.db.datasource.DynamicRoutingDataSource;
import org.geofence.core.db.datasource.ReloadableDataSource;
import org.hibernate.type.format.jackson.JacksonJsonFormatMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
// lazyInit: DB connection built on first use, not at startup - see GeofenceServiceConfig.
@ComponentScan(basePackages = "org.geofence.core.db", lazyInit = true)
public class GeofencePersistenceConfig {

    private static final Logger LOGGER = LogManager.getLogger(GeofencePersistenceConfig.class);

    private final DynamicRoutingDataSource routingDataSource;
    private final Optional<GeoFenceConfigDirectoryProvider> configDirProvider;
    private final Optional<DatasourcePasswordDecoder> passwordDecoder;
    private final ConfigurableApplicationContext context;

    // Eager probe (this @Configuration class isn't itself lazy) so a missing/invalid datasource config is logged at
    // startup, even though datasourceSettings() below stays @Lazy so GeoServer itself doesn't fail to start.
    public GeofencePersistenceConfig(
            Optional<GeoFenceConfigDirectoryProvider> configDirProvider,
            Optional<DatasourcePasswordDecoder> passwordDecoder,
            ConfigurableApplicationContext context) {
        this.configDirProvider = configDirProvider;
        this.passwordDecoder = passwordDecoder;
        this.context = context;
        this.routingDataSource = new DynamicRoutingDataSource();
        try {
            // No passwordDecoder: this early, the security manager isn't ready and would strip the plain: marker.
            new DatasourcePropertiesLoader().load(configDirProvider);
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARN, "GeoFence embedded engine will be unavailable until this is fixed", e);
        }
    }

    /**
     * Re-reads the datasource file into the live connection pool. Returns false if the new configuration couldn't be
     * applied, leaving the old pool in place for the caller to decide what to do; true if applied, or if the embedded
     * engine hasn't started yet and there is nothing to reconfigure.
     */
    public boolean reloadDatasource() {
        if (!context.getBeanFactory().containsSingleton("dataSource")) {
            return true;
        }
        try {
            DatasourceSettings settings = new DatasourcePropertiesLoader().load(configDirProvider, passwordDecoder);
            ReloadableDataSource dataSource = context.getBean("dataSource", ReloadableDataSource.class);
            dataSource.reconfigure(
                    settings.url(),
                    settings.username(),
                    settings.password(),
                    settings.driverClassName(),
                    settings.hikariProperties());
            return true;
        } catch (RuntimeException e) {
            LOGGER.log(Level.ERROR, "Could not apply the new GeoFence datasource configuration", e);
            return false;
        }
    }

    @Bean
    @Lazy
    public DatasourceSettings datasourceSettings(
            Optional<GeoFenceConfigDirectoryProvider> configDirProvider,
            Optional<DatasourcePasswordDecoder> passwordDecoder) {
        return new DatasourcePropertiesLoader().load(configDirProvider, passwordDecoder);
    }

    @Bean
    @Lazy
    public ReloadableDataSource dataSource(DatasourceSettings settings) {
        ReloadableDataSource dataSource = new ReloadableDataSource();
        dataSource.reconfigure(
                settings.url(),
                settings.username(),
                settings.password(),
                settings.driverClassName(),
                settings.hikariProperties());
        return dataSource;
    }

    // Returns EntityManagerFactory rather than the LocalContainerEntityManagerFactoryBean built below: the latter
    // implements LoadTimeWeaverAware, which Spring eagerly instantiates regardless of @Lazy. afterPropertiesSet()
    // replaces Spring's own FactoryBean lifecycle management.
    @Bean(name = "geofenceEntityManagerFactory", destroyMethod = "close")
    @Lazy
    public EntityManagerFactory entityManagerFactory(DataSource dataSource, DatasourceSettings settings) {

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();

        emf.setDataSource(dataSource);
        emf.setPackagesToScan(new String[] {"org.geofence.core.model", "org.geofence.core.db"});
        emf.setPersistenceUnitName("geofenceEntityManagerFactory");

        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();

        emf.setJpaVendorAdapter(adapter);

        Properties props = new Properties();
        props.put("hibernate.hbm2ddl.auto", "update");

        // WORKAROUND for a GeoTools gt-geojson-core SPI bug (stale Jackson 2 registration on a
        // Jackson 3 class) that otherwise crashes Hibernate's default Jackson auto-discovery.
        // Remove once GeoTools fixes it upstream.
        props.put("hibernate.type.json_format_mapper", new JacksonJsonFormatMapper(new ObjectMapper()));
        props.putAll(settings.hibernateProperties());

        emf.setJpaProperties(props);

        emf.afterPropertiesSet();

        return emf.getObject();
    }

    @Bean(name = "geofenceTransactionManager")
    @Lazy
    public PlatformTransactionManager geofenceTransactionManager(
            @Qualifier("geofenceEntityManagerFactory") EntityManagerFactory emf) {

        return new JpaTransactionManager(emf);
    }
}

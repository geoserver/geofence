package org.geofence.core.db.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.geofence.core.db.datasource.DynamicRoutingDataSource;
import org.geofence.core.db.datasource.ReloadableDataSource;
import org.hibernate.type.format.jackson.JacksonJsonFormatMapper;
import org.springframework.beans.factory.annotation.Qualifier;
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

    private static final Logger LOGGER = Logger.getLogger(GeofencePersistenceConfig.class.getName());

    private final DynamicRoutingDataSource routingDataSource;

    // Eager probe (this @Configuration class isn't itself lazy) so a missing/invalid datasource config is logged at
    // startup, even though datasourceSettings() below stays @Lazy so GeoServer itself doesn't fail to start.
    public GeofencePersistenceConfig(
            Optional<GeoFenceConfigDirectoryProvider> configDirProvider,
            Optional<DatasourcePasswordDecoder> passwordDecoder) {
        this.routingDataSource = new DynamicRoutingDataSource();
        try {
            new DatasourcePropertiesLoader().load(configDirProvider, passwordDecoder);
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "GeoFence embedded engine will be unavailable until this is fixed", e);
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

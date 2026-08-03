package org.geofence.core.db.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.Optional;
import java.util.Properties;
import javax.sql.DataSource;
import org.geofence.core.db.datasource.DynamicRoutingDataSource;
import org.geofence.core.db.datasource.ReloadableDataSource;
import org.hibernate.type.format.jackson.JacksonJsonFormatMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "org.geofence.core.db")
public class GeofencePersistenceConfig {

    private DynamicRoutingDataSource routingDataSource;

    public GeofencePersistenceConfig() {
        routingDataSource = new DynamicRoutingDataSource();
    }

    //    @Bean
    //    public DataSource dataSource() {
    //        return this.routingDataSource;
    //    }

    @Bean
    public DatasourceSettings datasourceSettings(
            Optional<GeoFenceConfigDirectoryProvider> configDirProvider,
            Optional<DatasourcePasswordDecoder> passwordDecoder) {
        return new DatasourcePropertiesLoader().load(configDirProvider, passwordDecoder);
    }

    @Bean
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

    @Bean(name = "geofenceEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource, DatasourceSettings settings) {

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

        return emf;
    }

    @Bean(name = "geofenceTransactionManager")
    public PlatformTransactionManager geofenceTransactionManager(
            @Qualifier("geofenceEntityManagerFactory") EntityManagerFactory emf) {

        return new JpaTransactionManager(emf);
    }
}

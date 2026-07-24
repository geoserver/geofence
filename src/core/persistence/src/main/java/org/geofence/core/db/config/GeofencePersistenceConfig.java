package org.geofence.core.db.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import org.geofence.core.db.datasource.DynamicRoutingDataSource;
import org.hibernate.type.format.jackson.JacksonJsonFormatMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "org.geofence.core.db")
public class GeofencePersistenceConfig {

    private static final String OVR_FILENAME = "geofence-datasource-ovr.properties";

    private DynamicRoutingDataSource routingDataSource;

    public GeofencePersistenceConfig() {
        routingDataSource = new DynamicRoutingDataSource();
    }

    //    @Bean
    //    public DataSource dataSource() {
    //        return this.routingDataSource;
    //    }

    static PropertySourcesPlaceholderConfigurer pspc = null;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        if (pspc == null) {
            pspc = new PropertySourcesPlaceholderConfigurer();
            pspc.setIgnoreResourceNotFound(true);
            pspc.setLocations(overrideLocations());
        }

        return pspc;
    }

    /** Optional {@value #OVR_FILENAME} locations, checked in order; none are required. */
    private static Resource[] overrideLocations() {
        List<Resource> locations = new ArrayList<>();
        locations.add(new ClassPathResource(OVR_FILENAME));

        String geofenceDir = systemProperty("geofence.dir");
        if (geofenceDir != null) {
            locations.add(new FileSystemResource(geofenceDir + "/" + OVR_FILENAME));
        }

        String geoserverDataDir = systemProperty("GEOSERVER_DATA_DIR");
        if (geoserverDataDir != null) {
            locations.add(new FileSystemResource(geoserverDataDir + "/geofence/" + OVR_FILENAME));
        }

        String explicitFile = systemProperty("geofence-datasource-file");
        if (explicitFile != null) {
            locations.add(new FileSystemResource(explicitFile));
        }

        return locations.toArray(new Resource[0]);
    }

    private static String systemProperty(String name) {
        String value = System.getProperty(name);
        return value != null ? value : System.getenv(name);
    }

    @Bean
    public DataSource dataSource(
            @Value("${geofence.datasource.url}") String url,
            @Value("${geofence.datasource.username}") String username,
            @Value("${geofence.datasource.password}") String password,
            @Value("${geofence.datasource.driver}") String driver) {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driver);
        return dataSource;
    }

    @Bean(name = "geofenceEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();

        emf.setDataSource(dataSource);
        emf.setPackagesToScan(new String[] {"org.geofence.core.model", "org.geofence.core.db"});
        emf.setPersistenceUnitName("geofenceEntityManagerFactory");

        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();

        emf.setJpaVendorAdapter(adapter);

        Properties props = new Properties();
        props.put("hibernate.hbm2ddl.auto", "update");
        //        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        // WORKAROUND for a GeoTools gt-geojson-core SPI bug (stale Jackson 2 registration on a
        // Jackson 3 class) that otherwise crashes Hibernate's default Jackson auto-discovery.
        // Remove once GeoTools fixes it upstream.
        props.put("hibernate.type.json_format_mapper", new JacksonJsonFormatMapper(new ObjectMapper()));

        emf.setJpaProperties(props);

        return emf;
    }

    @Bean(name = "geofenceTransactionManager")
    public PlatformTransactionManager geofenceTransactionManager(
            @Qualifier("geofenceEntityManagerFactory") EntityManagerFactory emf) {

        return new JpaTransactionManager(emf);
    }
}

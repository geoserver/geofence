package org.geofence.core.db.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import java.util.Properties;
import javax.sql.DataSource;
import org.geofence.core.db.datasource.DynamicRoutingDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
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

    static PropertySourcesPlaceholderConfigurer pspc = null;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        if (pspc == null) {
            pspc = new PropertySourcesPlaceholderConfigurer();
        }

        return pspc;
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

        emf.setJpaProperties(props);

        return emf;
    }

    @Bean(name = "geofenceTransactionManager")
    public PlatformTransactionManager geofenceTransactionManager(
            @Qualifier("geofenceEntityManagerFactory") EntityManagerFactory emf) {

        return new JpaTransactionManager(emf);
    }
}

package org.geofence.core.db.config;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.hibernate.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class HibernateConfig {

    @Bean
    public PlatformTransactionManager transactionManager(SessionFactory sessionFactory) {

        return new HibernateTransactionManager(sessionFactory);
    }
}

/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.ldap.config;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geofence.core.db.config.GeoFenceConfigDirectoryProvider;
import org.geofence.core.db.dao.GSUserDAO;
import org.geofence.core.db.dao.UserGroupDAO;
import org.geofence.core.db.dao.impl.GSUserDAOImpl;
import org.geofence.core.db.dao.impl.UserGroupDAOImpl;
import org.geofence.ldap.dao.impl.GSUserAttributesMapper;
import org.geofence.ldap.dao.impl.GSUserDAOLdapImpl;
import org.geofence.ldap.dao.impl.UserGroupAttributesMapper;
import org.geofence.ldap.dao.impl.UserGroupDAOLdapImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * Explicit DAO selection: {@code GSUserDAO}/{@code UserGroupDAO} are LDAP-backed when {@link LdapSettings} are found,
 * DB-backed ({@link GSUserDAOImpl}/{@link UserGroupDAOImpl}) otherwise - a real decision made once at startup from a
 * typed settings object, not the {@code daos.get(0)} list-order guess this replaces (the old
 * {@code GeofenceUserDAOSelector}, deleted - it wasn't even a {@code @Configuration} class, so it was silently never
 * invoked at all; DB was the sole candidate by default already).
 *
 * <p>{@code @Primary} is the disambiguation mechanism when both a DB and an LDAP bean of the same interface exist in
 * the context - a real, well-defined Spring mechanism (not list-order/property-override "magic"), and it correctly
 * degrades to a no-op (both candidates resolve to the same {@code dbImpl} instance) when LDAP isn't configured.
 */
@Configuration
public class GeofenceUserDaoConfig {

    private static final Logger LOGGER = Logger.getLogger(GeofenceUserDaoConfig.class.getName());

    private final Optional<LdapDaos> ldapDaos;

    public GeofenceUserDaoConfig(Optional<GeoFenceConfigDirectoryProvider> configDirProvider) {
        this.ldapDaos = new LdapPropertiesLoader().load(configDirProvider).map(GeofenceUserDaoConfig::buildLdapDaos);
        ldapDaos.ifPresent(d -> LOGGER.log(
                Level.INFO, "LDAP configuration found - GSUserDAO/UserGroupDAO will be backed by the LDAP directory"));
    }

    private record LdapDaos(GSUserDAO gsUserDAO, UserGroupDAO userGroupDAO) {}

    @Bean
    @Primary
    public GSUserDAO gsUserDAO(GSUserDAOImpl dbImpl) {
        return ldapDaos.map(LdapDaos::gsUserDAO).orElse(dbImpl);
    }

    @Bean
    @Primary
    public UserGroupDAO userGroupDAO(UserGroupDAOImpl dbImpl) {
        return ldapDaos.map(LdapDaos::userGroupDAO).orElse(dbImpl);
    }

    private static LdapDaos buildLdapDaos(LdapSettings settings) {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(settings.url());
        contextSource.setBase(settings.base());
        if (settings.userDn() != null) {
            contextSource.setUserDn(settings.userDn());
        }
        if (settings.password() != null) {
            contextSource.setPassword(settings.password());
        }
        contextSource.afterPropertiesSet();

        LdapTemplate template = new LdapTemplate(contextSource);
        template.setDefaultCountLimit(settings.defaultCountLimit());

        UserGroupAttributesMapper groupMapper = new UserGroupAttributesMapper();
        groupMapper.setMap(settings.groupAttributeMapping());

        UserGroupDAOLdapImpl groupDao = new UserGroupDAOLdapImpl();
        groupDao.setLdapTemplate(template);
        groupDao.setAttributesMapper(groupMapper);
        groupDao.setSearchBase(settings.groupSearchBase());
        groupDao.setSearchFilter(settings.groupSearchFilter());
        initialize(groupDao);

        GSUserAttributesMapper userMapper = new GSUserAttributesMapper();
        userMapper.setMap(settings.userAttributeMapping());

        GSUserDAOLdapImpl userDao = new GSUserDAOLdapImpl();
        userDao.setLdapTemplate(template);
        userDao.setAttributesMapper(userMapper);
        userDao.setSearchBase(settings.userSearchBase());
        userDao.setSearchFilter(settings.userSearchFilter());
        userDao.setUserGroupDAOLdapImpl(groupDao);
        initialize(userDao);

        return new LdapDaos(userDao, groupDao);
    }

    private static void initialize(org.springframework.beans.factory.InitializingBean bean) {
        try {
            bean.afterPropertiesSet();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to initialize " + bean.getClass().getSimpleName(), e);
        }
    }
}

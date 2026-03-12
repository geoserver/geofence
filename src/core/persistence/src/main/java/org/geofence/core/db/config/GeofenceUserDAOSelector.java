package org.geofence.core.db.config;

import java.util.List;
import org.geofence.core.db.dao.GSUserDAO;
import org.geofence.core.db.dao.UserGroupDAO;
import org.springframework.context.annotation.Bean;

/** @author etj */
public class GeofenceUserDAOSelector {
    @Bean
    public GSUserDAO gsUserDAO(List<GSUserDAO> daos) {
        return daos.get(0);
    }

    @Bean
    public UserGroupDAO userGroupDAO(List<UserGroupDAO> daos) {
        return daos.get(0);
    }
}

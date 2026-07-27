/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db;

import org.geofence.core.db.config.GeofencePersistenceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = GeofencePersistenceConfig.class)
public abstract class BaseContainerTest {

    static {
        GeofenceTestDatabase.configureAsDatasourceOverride();
    }

    @Autowired
    public GeofencePersistenceConfig persistenceConfig;
}

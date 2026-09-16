/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db.config;

import java.util.Optional;

/**
 * Supplies the directory where GeoFence's own config files (e.g. {@link DatasourcePropertiesLoader}'s override file)
 * should be looked for, when hosted in an environment that has one to offer. Absent (no bean of this type in the
 * context) when running standalone. Implementations own the full path, including any host-specific subdirectory
 * convention - this interface only asks "where," not "how is the host's data directory laid out."
 */
@FunctionalInterface
public interface GeoFenceConfigDirectoryProvider {

    Optional<String> getConfigDirectory();
}

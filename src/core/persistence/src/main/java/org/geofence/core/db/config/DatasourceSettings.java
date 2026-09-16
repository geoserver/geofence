/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db.config;

import java.util.Map;

public record DatasourceSettings(
        String url,
        String username,
        String password,
        String driverClassName,
        Map<String, String> hibernateProperties,
        Map<String, String> hikariProperties) {}

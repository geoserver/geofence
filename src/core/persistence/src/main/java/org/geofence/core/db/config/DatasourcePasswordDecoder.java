/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db.config;

import java.util.Optional;

/**
 * Optional hook to decrypt the datasource password read from {@code geofence-datasource.properties}. GeoFence embedded
 * in GeoServer supplies a bean backed by GeoServer's own config-password encryption; a plaintext value with no
 * recognized marker passes through unchanged.
 */
public interface DatasourcePasswordDecoder {

    Result decode(String storedPassword);

    /**
     * {@code valueToPersist}, if present, is written back to the properties file (e.g. after encrypting a
     * {@code plain:} value).
     */
    record Result(String plaintext, Optional<String> valueToPersist) {

        public Result {
            valueToPersist = valueToPersist == null ? Optional.empty() : valueToPersist;
        }

        public static Result asIs(String plaintext) {
            return new Result(plaintext, Optional.empty());
        }

        public static Result persist(String plaintext, String valueToPersist) {
            return new Result(plaintext, Optional.of(valueToPersist));
        }
    }
}

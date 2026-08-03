/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db.config;

import java.util.Optional;

/**
 * Optional hook to decrypt the datasource password read from {@code geofence-datasource.properties}.
 *
 * <p>A host that stores the password in an encrypted form provides a bean implementing this; when one is present in the
 * context, {@link DatasourcePropertiesLoader}'s loaded password is passed through it before the connection pool sees
 * it. GeoFence embedded in GeoServer supplies such a bean, backed by GeoServer's own config-password encryption. When
 * no bean is present (e.g. the standalone webapp), the password is used verbatim.
 *
 * <p>Two behaviours, mirroring how GeoServer treats store connection passwords:
 *
 * <ul>
 *   <li>An encrypted value is decrypted; a plaintext value (no recognizable marker) passes through unchanged, so an
 *       unencrypted configuration keeps working.
 *   <li>A value the operator wrote in clear behind an explicit marker (GeoServer's {@code plain:} prefix) is decrypted
 *       to its plaintext <em>and</em> a freshly-encrypted form is returned via {@link Result#valueToPersist()}, so the
 *       loader can write it back to the file - leaving the credential encrypted at rest without any manual tooling.
 * </ul>
 */
public interface DatasourcePasswordDecoder {

    /**
     * @param storedPassword the raw value read from the properties file (may be {@code null})
     * @return the plaintext to connect with, plus optionally an encrypted value to persist back to the file
     */
    Result decode(String storedPassword);

    /**
     * @param plaintext the decrypted password to actually connect with
     * @param valueToPersist if present, the value the loader should write back into the properties file in place of
     *     what was there (e.g. the freshly-encrypted form of a {@code plain:}-marked password); empty to leave the file
     *     untouched
     */
    record Result(String plaintext, Optional<String> valueToPersist) {

        public Result {
            valueToPersist = valueToPersist == null ? Optional.empty() : valueToPersist;
        }

        /** The file already holds the canonical form (encrypted or plain); use {@code plaintext}, don't rewrite. */
        public static Result asIs(String plaintext) {
            return new Result(plaintext, Optional.empty());
        }

        /** Use {@code plaintext} to connect, and rewrite the file's stored value to {@code valueToPersist}. */
        public static Result persist(String plaintext, String valueToPersist) {
            return new Result(plaintext, Optional.of(valueToPersist));
        }
    }
}

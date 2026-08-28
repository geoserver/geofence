/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DatasourcePropertiesLoaderTest {

    @TempDir
    Path tempDir;

    private static final String CONTENT =
            """
            geofence.datasource.url=jdbc:postgresql://localhost:5432/geofence
            geofence.datasource.username=geofence
            geofence.datasource.password=plain:mypassword
            geofence.datasource.driver=org.postgresql.Driver
            """;

    /** Regression test: the constructor-probe path (no decoder) must never touch a plain: marked password. */
    @Test
    void loadWithoutDecoderLeavesPlainMarkedPasswordUntouched() throws IOException {
        Path file = writeDatasourceFile();

        DatasourceSettings settings = new DatasourcePropertiesLoader().load(configDirProvider());

        assertEquals("plain:mypassword", settings.password());
        assertEquals(CONTENT, Files.readString(file));
    }

    @Test
    void loadWithDecoderPersistsEncryptedValue() throws IOException {
        Path file = writeDatasourceFile();
        DatasourcePasswordDecoder decoder =
                stored -> DatasourcePasswordDecoder.Result.persist("mypassword", "crypt1:FAKE");

        DatasourceSettings settings = new DatasourcePropertiesLoader().load(configDirProvider(), Optional.of(decoder));

        assertEquals("mypassword", settings.password());
        assertTrue(Files.readString(file).contains("geofence.datasource.password=crypt1:FAKE"));
    }

    @Test
    void encryptStoredPasswordRewritesPlainMarkedValue() throws IOException {
        Path file = writeDatasourceFile();
        DatasourcePasswordDecoder decoder =
                stored -> DatasourcePasswordDecoder.Result.persist("mypassword", "crypt1:FAKE");

        new DatasourcePropertiesLoader().encryptStoredPassword(configDirProvider(), decoder);

        assertTrue(Files.readString(file).contains("geofence.datasource.password=crypt1:FAKE"));
    }

    /** Once encrypted there's nothing marked plain, so a second pass must leave the file alone. */
    @Test
    void encryptStoredPasswordIsIdempotent() throws IOException {
        Path file = writeDatasourceFile();
        DatasourcePasswordDecoder noop = DatasourcePasswordDecoder.Result::asIs;

        new DatasourcePropertiesLoader().encryptStoredPassword(configDirProvider(), noop);

        assertEquals(CONTENT, Files.readString(file));
    }

    private Path writeDatasourceFile() throws IOException {
        Path file = tempDir.resolve(DatasourcePropertiesLoader.DEFAULT_FILENAME);
        Files.writeString(file, CONTENT);
        return file;
    }

    private Optional<GeoFenceConfigDirectoryProvider> configDirProvider() {
        return Optional.of(() -> Optional.of(tempDir.toString()));
    }
}

/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.db.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resolves and loads {@code geofence.datasource.*} settings from a {@code geofence-datasource.properties} file.
 *
 * <p>Location is determined as follows:
 *
 * <ol>
 *   <li>Filename: system property/env var {@code GEOFENCE_DATASOURCE_FILE}, or {@value #DEFAULT_FILENAME} if unset.
 *   <li>If that filename is absolute, it is the only location checked.
 *   <li>Otherwise, checked in order: {@code <config directory>/<filename>} (if a
 *       {@link GeoFenceConfigDirectoryProvider} bean is present), then {@code <filename>} relative to the current
 *       working directory.
 * </ol>
 *
 * There is no built-in default and no classpath lookup; if nothing resolves, loading fails - but if a config directory
 * is available and doesn't already have a sample file, one is written there first, as a starting point for whoever
 * manages that directory.
 */
public class DatasourcePropertiesLoader {

    private static final Logger LOGGER = Logger.getLogger(DatasourcePropertiesLoader.class.getName());

    public static final String DEFAULT_FILENAME = "geofence-datasource.properties";

    public DatasourceSettings load(Optional<GeoFenceConfigDirectoryProvider> configDirProvider) {
        String filename = systemProperty("GEOFENCE_DATASOURCE_FILE").orElse(DEFAULT_FILENAME);
        File configDirCandidate = configDirCandidate(filename, configDirProvider);
        List<File> candidates = candidateFiles(filename, configDirCandidate);

        for (File candidate : candidates) {
            if (candidate.isFile()) {
                return loadFrom(candidate);
            }
        }

        String sampleHint = configDirCandidate != null ? writeSampleIfAbsent(configDirCandidate) : "";

        StringBuilder checked = new StringBuilder();
        candidates.forEach(f -> checked.append("\n  - ").append(f.getAbsolutePath()));
        throw new IllegalStateException("No geofence datasource configuration found. Checked:" + checked + sampleHint);
    }

    /**
     * The config-directory-based candidate, or {@code null} if {@code filename} is absolute or no provider is present.
     */
    private File configDirCandidate(String filename, Optional<GeoFenceConfigDirectoryProvider> configDirProvider) {
        if (new File(filename).isAbsolute()) {
            return null;
        }
        return configDirProvider
                .flatMap(GeoFenceConfigDirectoryProvider::getConfigDirectory)
                .map(configDir -> new File(configDir, filename))
                .orElse(null);
    }

    private List<File> candidateFiles(String filename, File configDirCandidate) {
        File asGiven = new File(filename);
        if (asGiven.isAbsolute()) {
            return List.of(asGiven);
        }

        List<File> candidates = new ArrayList<>();
        if (configDirCandidate != null) {
            candidates.add(configDirCandidate);
        }
        candidates.add(new File(filename));
        return candidates;
    }

    /**
     * Best-effort: writes a {@code <filename>.sample} file next to {@code configDirCandidate} if neither the real file
     * nor a sample already exist there. Never throws - a failure here shouldn't obscure the "no config found" error
     * it's meant to help with.
     */
    private String writeSampleIfAbsent(File configDirCandidate) {
        File sample = new File(configDirCandidate.getParentFile(), configDirCandidate.getName() + ".sample");
        if (sample.exists()) {
            return "";
        }
        try {
            File dir = configDirCandidate.getParentFile();
            if (!dir.isDirectory() && !dir.mkdirs()) {
                return "";
            }
            try (FileWriter out = new FileWriter(sample)) {
                out.write(SAMPLE_CONTENT);
            }
            return "\nWrote a sample file to " + sample.getAbsolutePath() + " - copy it to "
                    + configDirCandidate.getName() + " in the same directory and fill in real credentials.";
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not write sample datasource config to " + sample.getAbsolutePath(), e);
            return "";
        }
    }

    private static final String SAMPLE_CONTENT = "# Sample GeoFence datasource configuration.\n"
            + "#\n"
            + "# Copy this file to the same name without the \".sample\" suffix, in the same directory, and\n"
            + "# fill in real credentials. All four properties are required; there is no built-in default.\n"
            + "\n"
            + "geofence.datasource.url=jdbc:postgresql://localhost:5432/geofence\n"
            + "geofence.datasource.username=geofence\n"
            + "geofence.datasource.password=geofence\n"
            + "geofence.datasource.driver=org.postgresql.Driver\n";

    private DatasourceSettings loadFrom(File file) {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + file.getAbsolutePath(), e);
        }

        return new DatasourceSettings(
                requireProperty(props, "geofence.datasource.url", file),
                requireProperty(props, "geofence.datasource.username", file),
                requireProperty(props, "geofence.datasource.password", file),
                requireProperty(props, "geofence.datasource.driver", file));
    }

    private String requireProperty(Properties props, String key, File file) {
        String value = props.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Missing property '" + key + "' in " + file.getAbsolutePath());
        }
        return value;
    }

    private Optional<String> systemProperty(String name) {
        String value = System.getProperty(name);
        if (value == null) {
            value = System.getenv(name);
        }
        return Optional.ofNullable(value);
    }
}

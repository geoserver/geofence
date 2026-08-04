/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.ldap.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import org.geofence.core.db.config.GeoFenceConfigDirectoryProvider;

/**
 * Resolves and loads {@code geofence.ldap.*} settings from a {@code geofence-ldap.properties} file, replacing the old
 * {@code PropertyOverrideConfigurer}-based LDAP setup (see {@code GEOFENCE_SESSION_HANDOFF.claude.md}'s 2026-07-29
 * addenda for the full canonical field list this mirrors).
 *
 * <p>Unlike {@code DatasourcePropertiesLoader}, LDAP is optional: if no file is found at any candidate location, this
 * returns {@link Optional#empty()} rather than failing - the DB-backed DAOs are a complete, working default, and most
 * deployments never need LDAP at all. A file that exists but is missing a required property still fails fast.
 *
 * <p>Location is determined exactly as {@code DatasourcePropertiesLoader} does: filename from the
 * {@code GEOFENCE_LDAP_FILE} system property/env var (default {@value #DEFAULT_FILENAME}), checked first relative to
 * the {@link GeoFenceConfigDirectoryProvider}'s directory (if any), then relative to the current working directory.
 */
public class LdapPropertiesLoader {

    public static final String DEFAULT_FILENAME = "geofence-ldap.properties";

    private static final String USER_ATTR_PREFIX = "geofence.ldap.user.attribute.";
    private static final String GROUP_ATTR_PREFIX = "geofence.ldap.group.attribute.";

    public Optional<LdapSettings> load(Optional<GeoFenceConfigDirectoryProvider> configDirProvider) {
        String filename = systemProperty("GEOFENCE_LDAP_FILE").orElse(DEFAULT_FILENAME);
        List<File> candidates = candidateFiles(filename, configDirProvider);

        for (File candidate : candidates) {
            if (candidate.isFile()) {
                return Optional.of(loadFrom(candidate));
            }
        }

        return Optional.empty();
    }

    private List<File> candidateFiles(String filename, Optional<GeoFenceConfigDirectoryProvider> configDirProvider) {
        File asGiven = new File(filename);
        if (asGiven.isAbsolute()) {
            return List.of(asGiven);
        }

        List<File> candidates = new ArrayList<>();
        configDirProvider
                .flatMap(GeoFenceConfigDirectoryProvider::getConfigDirectory)
                .ifPresent(configDir -> candidates.add(new File(configDir, filename)));
        candidates.add(asGiven);
        return candidates;
    }

    private LdapSettings loadFrom(File file) {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + file.getAbsolutePath(), e);
        }

        Map<String, String> userAttributeMapping = propertiesWithPrefix(props, USER_ATTR_PREFIX);
        Map<String, String> groupAttributeMapping = propertiesWithPrefix(props, GROUP_ATTR_PREFIX);
        if (userAttributeMapping.isEmpty()) {
            throw new IllegalStateException("No " + USER_ATTR_PREFIX + "* properties found in " + file.getAbsolutePath()
                    + " - at least the LDAP attribute backing 'username' must be mapped");
        }
        if (groupAttributeMapping.isEmpty()) {
            throw new IllegalStateException("No " + GROUP_ATTR_PREFIX + "* properties found in "
                    + file.getAbsolutePath() + " - at least the LDAP attribute backing 'groupname' must be mapped");
        }

        return new LdapSettings(
                requireProperty(props, "geofence.ldap.url", file),
                props.getProperty("geofence.ldap.base", ""),
                props.getProperty("geofence.ldap.userDn"),
                props.getProperty("geofence.ldap.password"),
                Integer.parseInt(props.getProperty("geofence.ldap.defaultCountLimit", "100")),
                props.getProperty("geofence.ldap.user.searchBase", "ou=People"),
                props.getProperty("geofence.ldap.user.searchFilter", "objectClass=inetOrgPerson"),
                props.getProperty("geofence.ldap.group.searchBase", "ou=Groups"),
                props.getProperty("geofence.ldap.group.searchFilter", "objectClass=posixGroup"),
                userAttributeMapping,
                groupAttributeMapping);
    }

    private Map<String, String> propertiesWithPrefix(Properties props, String prefix) {
        Map<String, String> result = new LinkedHashMap<>();
        for (String name : props.stringPropertyNames()) {
            if (name.startsWith(prefix)) {
                result.put(name.substring(prefix.length()), props.getProperty(name));
            }
        }
        return result;
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

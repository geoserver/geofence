/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.ldap.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.geofence.core.db.config.GeoFenceConfigDirectoryProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Unit tests for {@link LdapPropertiesLoader} - no real LDAP server needed, just file resolution/parsing. */
public class LdapPropertiesLoaderTest {

    @TempDir
    Path tempDir;

    private GeoFenceConfigDirectoryProvider configDirProvider() {
        return () -> Optional.of(tempDir.toString());
    }

    private void write(String filename, String content) throws IOException {
        try (Writer w = Files.newBufferedWriter(tempDir.resolve(filename))) {
            w.write(content);
        }
    }

    @Test
    public void testNoFileMeansDisabled() {
        Optional<LdapSettings> settings = new LdapPropertiesLoader().load(Optional.of(configDirProvider()));
        assertTrue(settings.isEmpty());
    }

    @Test
    public void testNoConfigDirProviderMeansDisabledWhenNoCwdFileEither() {
        Optional<LdapSettings> settings = new LdapPropertiesLoader().load(Optional.empty());
        assertTrue(settings.isEmpty());
    }

    @Test
    public void testFullFileLoadsAllFields() throws IOException {
        write(
                LdapPropertiesLoader.DEFAULT_FILENAME,
                """
                geofence.ldap.url=ldap://localhost:10389
                geofence.ldap.base=dc=example,dc=com
                geofence.ldap.userDn=uid=admin,ou=system
                geofence.ldap.password=secret
                geofence.ldap.defaultCountLimit=200
                geofence.ldap.user.searchBase=ou=Users
                geofence.ldap.user.searchFilter=objectClass=person
                geofence.ldap.group.searchBase=ou=Teams
                geofence.ldap.group.searchFilter=objectClass=group
                geofence.ldap.user.attribute.username=uid
                geofence.ldap.user.attribute.email=mail
                geofence.ldap.group.attribute.groupname=cn
                """);

        Optional<LdapSettings> result = new LdapPropertiesLoader().load(Optional.of(configDirProvider()));
        assertTrue(result.isPresent());
        LdapSettings settings = result.get();

        assertEquals("ldap://localhost:10389", settings.url());
        assertEquals("dc=example,dc=com", settings.base());
        assertEquals("uid=admin,ou=system", settings.userDn());
        assertEquals("secret", settings.password());
        assertEquals(200, settings.defaultCountLimit());
        assertEquals("ou=Users", settings.userSearchBase());
        assertEquals("objectClass=person", settings.userSearchFilter());
        assertEquals("ou=Teams", settings.groupSearchBase());
        assertEquals("objectClass=group", settings.groupSearchFilter());
        assertEquals("uid", settings.userAttributeMapping().get("username"));
        assertEquals("mail", settings.userAttributeMapping().get("email"));
        assertEquals("cn", settings.groupAttributeMapping().get("groupname"));
    }

    @Test
    public void testDefaultsAppliedWhenOptionalPropertiesOmitted() throws IOException {
        write(
                LdapPropertiesLoader.DEFAULT_FILENAME,
                """
                geofence.ldap.url=ldap://localhost:10389
                geofence.ldap.user.attribute.username=uid
                geofence.ldap.group.attribute.groupname=cn
                """);

        LdapSettings settings = new LdapPropertiesLoader()
                .load(Optional.of(configDirProvider()))
                .orElseThrow();

        assertEquals("", settings.base());
        assertEquals(100, settings.defaultCountLimit());
        assertEquals("ou=People", settings.userSearchBase());
        assertEquals("objectClass=inetOrgPerson", settings.userSearchFilter());
        assertEquals("ou=Groups", settings.groupSearchBase());
        assertEquals("objectClass=posixGroup", settings.groupSearchFilter());
    }

    @Test
    public void testMissingUrlFailsFast() throws IOException {
        write(
                LdapPropertiesLoader.DEFAULT_FILENAME,
                """
                geofence.ldap.user.attribute.username=uid
                geofence.ldap.group.attribute.groupname=cn
                """);

        assertThrows(
                IllegalStateException.class, () -> new LdapPropertiesLoader().load(Optional.of(configDirProvider())));
    }

    @Test
    public void testMissingUserAttributeMappingFailsFast() throws IOException {
        write(
                LdapPropertiesLoader.DEFAULT_FILENAME,
                """
                geofence.ldap.url=ldap://localhost:10389
                geofence.ldap.group.attribute.groupname=cn
                """);

        assertThrows(
                IllegalStateException.class, () -> new LdapPropertiesLoader().load(Optional.of(configDirProvider())));
    }

    @Test
    public void testMissingGroupAttributeMappingFailsFast() throws IOException {
        write(
                LdapPropertiesLoader.DEFAULT_FILENAME,
                """
                geofence.ldap.url=ldap://localhost:10389
                geofence.ldap.user.attribute.username=uid
                """);

        assertThrows(
                IllegalStateException.class, () -> new LdapPropertiesLoader().load(Optional.of(configDirProvider())));
    }

    @Test
    public void testCustomFilenameViaSystemProperty() throws IOException {
        write(
                "custom-ldap.properties",
                """
                geofence.ldap.url=ldap://localhost:10389
                geofence.ldap.user.attribute.username=uid
                geofence.ldap.group.attribute.groupname=cn
                """);

        System.setProperty("GEOFENCE_LDAP_FILE", "custom-ldap.properties");
        try {
            Optional<LdapSettings> result = new LdapPropertiesLoader().load(Optional.of(configDirProvider()));
            assertTrue(result.isPresent());
        } finally {
            System.clearProperty("GEOFENCE_LDAP_FILE");
        }
    }
}

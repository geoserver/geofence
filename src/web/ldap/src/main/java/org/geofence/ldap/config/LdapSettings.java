/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.ldap.config;

import java.util.Map;

/**
 * Everything needed to back {@code GSUserDAO}/{@code UserGroupDAO} with an LDAP directory instead of the database -
 * connection, per-entity search scope, and the LDAP-attribute-name-to-internal-field mappings the old
 * {@code PropertyOverrideConfigurer}-based setup injected as indexed properties (e.g.
 * {@code geofenceLdapUserMapper.map[username]=uid}). {@code userAttributeMapping}/{@code groupAttributeMapping} replace
 * that indexed syntax with a plain map - same semantics, no Spring property-path magic.
 */
public record LdapSettings(
        String url,
        String base,
        String userDn,
        String password,
        int defaultCountLimit,
        String userSearchBase,
        String userSearchFilter,
        String groupSearchBase,
        String groupSearchFilter,
        Map<String, String> userAttributeMapping,
        Map<String, String> groupAttributeMapping) {}

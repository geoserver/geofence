/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web;

import org.geofence.web.rest.config.GeofenceRESTConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The standalone webapp's root ({@code ContextLoaderListener}) context config - {@code web.xml}'s
 * {@code contextConfigLocation} points here, not directly at {@link GeofenceRESTConfig}.
 *
 * <p>LDAP support ({@code org.geofence.ldap.config.GeofenceUserDaoConfig}) is picked up via the
 * {@code org.geofence.ldap} component scan below rather than an {@code @Import} here, because only {@code web/app} -
 * and then only with the {@code ldap} Maven profile active - actually depends on {@code geofence-ldap}. A base-package
 * scan needs no compile-time reference to that class, so this module keeps compiling when the profile (and so the
 * dependency) isn't active: with the profile off, the scan simply finds nothing under {@code org.geofence.ldap}. Same
 * pattern as {@link GeofenceRESTConfig}'s scan of {@code org.geofence.web.rest}.
 */
@Configuration
@ComponentScan(basePackages = "org.geofence.ldap")
@Import(GeofenceRESTConfig.class)
public class WebAppConfig {}

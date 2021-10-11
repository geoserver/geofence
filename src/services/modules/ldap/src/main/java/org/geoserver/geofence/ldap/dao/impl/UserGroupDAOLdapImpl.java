/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.ldap.dao.impl;

import org.geoserver.geofence.core.dao.UserGroupDAO;
import org.geoserver.geofence.core.model.UserGroup;

/**
 * UserGroupDAO implementation, using an LDAP server as a primary source.
 *
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public class UserGroupDAOLdapImpl extends LDAPBaseDAO<UserGroupDAO, UserGroup>
        implements UserGroupDAO {

    public UserGroupDAOLdapImpl() {
        super();
        // set default search base and filter for groups
        setSearchBase("ou=Groups");
        setSearchFilter("objectClass=posixGroup");
    }
}

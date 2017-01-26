/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.ldap.dao.impl;

import org.geoserver.geofence.ldap.LdapAttributesMapper;

import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

/**
 * Base class for the LDAP attribute mappers. Implements mappings from LDAP attributes to DAO ones.
 *
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public abstract class BaseAttributesMapper implements LdapAttributesMapper
{
    protected Map<String, String> map;

    /**
     * Sets mappings from ldap attributes to GSUser and UserGroup ones.
     *
     * @param ldapAttributeMappings the ldapAttributeMappings to set
     */
    public void setMap(Map<String, String> ldapAttributeMappings)
    {
        this.map = ldapAttributeMappings;
    }

    public Map<String, String> getMap()
    {
        return map;
    }

    /**
     * Gets the LDAP mapping for the given DAO attribute.
     *
     * @param attributeName
     * @return
     */
    @Override
    public String getLdapAttribute(String attributeName)
    {
        return map.get(attributeName);
    }

    /**
     * Gets an attribute value from the given list. Automatically maps the attributeName (in DAO form) to LDAP form.
     *
     * @param attrs
     * @return
     * @throws NamingException
     */
    protected String getAttribute(Attributes attrs, String attributeName)
            throws javax.naming.NamingException
    {
        Attribute attrValue = attrs.get(map.get(attributeName));
        if (attrValue != null) {
            Object value = attrValue.get();
            if (value instanceof String) {
                return (String) value;
            }
            if (value instanceof byte[]) {
                return new String((byte[]) value);
            } else {
                return value.toString();
            }
        }
        return "";
    }
}

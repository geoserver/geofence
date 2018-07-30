/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.ldap.dao.impl;

import org.geoserver.geofence.core.model.UserGroup;

import java.util.Collections;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * AttributeMapper for UserGroup objects.
 *
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public class UserGroupAttributesMapper extends BaseAttributesMapper
{
    private static final Logger LOGGER = LogManager.getLogger(UserGroupAttributesMapper.class);

    @Override
    public Object mapFromAttributes(Attributes attrs) throws NamingException
    {
        UserGroup group = new UserGroup();

        String id = getAttribute(attrs, "id");
        if(StringUtils.isBlank(id)) {
            LOGGER.warn("Empty id for UserGroup");
            if(LOGGER.isDebugEnabled()) {
                for(Object oa: Collections.list(attrs.getAll())) {
                    Attribute a = (Attribute)oa;
                    LOGGER.debug("---> " + a);
                }
            }
        }
        group.setExtId(id);
        group.setName(getAttribute(attrs, "groupname"));
        group.setEnabled(true);

        return group;
    }

}

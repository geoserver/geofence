/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.ldap.dao.impl;

import org.geoserver.geofence.core.model.GSUser;

import java.util.Collections;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * AttributeMapper for GSUser objects.
 *
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public class GSUserAttributesMapper extends BaseAttributesMapper
{
    private static final Logger LOGGER = LogManager.getLogger(GSUserAttributesMapper.class);

    @Override
    public Object mapFromAttributes(Attributes attrs) throws NamingException
    {
        GSUser user = new GSUser();
        String id = getAttribute(attrs, "id");
        if(StringUtils.isBlank(id)) {
            LOGGER.warn("Empty id for GSUser");
            if(LOGGER.isDebugEnabled()) {
                for(Object oa: Collections.list(attrs.getAll())) {
                    Attribute a = (Attribute)oa;
                    LOGGER.debug("---> " + a);
                }
            }
        }
        user.setExtId(id);
        user.setName(getAttribute(attrs, "username"));
        user.setEmailAddress(getAttribute(attrs, "email"));
        user.setEnabled(true);
        user.setFullName(getAttribute(attrs, "name") + " " + getAttribute(attrs, "surname"));
        user.setPassword(getAttribute(attrs, "password"));

        return user;
    }

}

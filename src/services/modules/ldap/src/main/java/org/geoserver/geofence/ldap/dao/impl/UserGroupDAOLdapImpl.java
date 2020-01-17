/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.ldap.dao.impl;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.geoserver.geofence.core.dao.UserGroupDAO;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.ldap.utils.LdapUtils;

/**
 * UserGroupDAO implementation, using an LDAP server as a primary source.
 *
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public class UserGroupDAOLdapImpl //
        extends LDAPBaseDAO<UserGroupDAO, UserGroup> // 
        implements UserGroupDAO
{

    public UserGroupDAOLdapImpl()
    {
        super();
        // set default search base and filter for groups
        setSearchBase("ou=Groups");
        setSearchFilter("objectClass=posixGroup");
    }

    @Override
    public UserGroup get(String name) {
    
        String filter = LdapUtils.createLDAPFilterEqual("groupname", name, getAttributesMapper());
        List<UserGroup> groups = search(filter);

        if (groups.isEmpty())
            return null;
        else if (groups.size() > 1)
            throw new IllegalArgumentException(
                    "Given filter (" + name + ") returns too many groups (" + groups.size() + ")");
        else
            return groups.get(0);
    }            

    @Override
    public List<UserGroup> search(String nameLike, Integer page, Integer entries) throws IllegalArgumentException {

        if (StringUtils.isBlank(nameLike)) {
            return paginate(findAll(), entries, page);
        }

        // filtering needed -- we'll perform filtering by hand, and contectually
        // pagination will be evalueated, in order to save memory and time
        
        int firstIndex = getFirstPaginationIndex(entries, page);
        int lastIndex = getLastPaginationIndex(entries, page);
        
        List<UserGroup> ret = new LinkedList<>();
        int index = 0;        
        for (UserGroup user : findAll()) { 
            if(user.getName().contains(nameLike)) {
                if(++index > firstIndex ) {
                    ret.add(user); 
                }
                
                if(index >= lastIndex) {
                    break;
                }
            }
        }
        
        return ret;
    }
    
    @Override
    public long countByNameLike(String nameLike) {
        
        if (StringUtils.isBlank(nameLike)) {
            return findAll().size();
        }

        int cnt = 0;        
        for (UserGroup user : findAll()) { 
            if(user.getName().contains(nameLike)) {
                ++cnt;
            }
        }
        
        return cnt;
    }    
}

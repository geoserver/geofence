/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.ldap.dao.impl;

import org.geoserver.geofence.core.dao.GSUserDAO;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.UserGroup;

import java.util.HashSet;
import java.util.List;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import org.apache.commons.lang.StringUtils;

/**
 * GSUserDAO implementation, using an LDAP server as a primary source.
 *
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public class GSUserDAOLdapImpl extends LDAPBaseDAO<GSUserDAO, GSUser> implements GSUserDAO
{

    private UserGroupDAOLdapImpl userGroupDAOLdapImpl;

    /**
     *
     */
    public GSUserDAOLdapImpl()
    {
        super();
        // set default search base and filter for users
        setSearchBase("ou=People");
        setSearchFilter("objectClass=inetOrgPerson");
    }

    /**
     * Gets the list of user groups from the LDAP server for the given user.
     *
     * @param user
     * @return
     */
    private List<UserGroup> getGroups(GSUser user)
    {
        Filter filter;

        String dn = user.getExtId();
        if(StringUtils.isNotBlank(dn)) {
            filter = new Filter("member", dn);
        } else {
            String userName = user.getName();
            LOGGER.info("User id is null, using username '"+userName+"'");
            String nameAttr = getLDAPAttribute("username");
            String exp = nameAttr + "=" + userName;
            filter = new Filter("member", exp);
        }

        List<UserGroup> groups = userGroupDAOLdapImpl.search(filter);
        return groups;
    }

    @Override
    public GSUser getFull(String name)
    {
        GSUser user = searchByName(name);
        if(user == null)
            return null;
        
        return fillWithGroups(user);
    }

    /**
     * Updates the groups list for the given user.
     *
     * @param gsUser
     * @return
     */
    private GSUser fillWithGroups(GSUser user)
    {
        user.setGroups(new HashSet(getGroups(user)));
        return user;
    }

    public GSUser searchByName(String name) {

        Search search = new Search();
        search.addFilter(new Filter("username", name));
        List<GSUser> users = search(search);

        if(users.isEmpty())
            return null;
        else if(users.size() > 1)
            throw new IllegalArgumentException("Given filter ("+name+") returns too many users ("+users.size()+")");
        GSUser user = users.get(0);
        return user;
    }

    public void setUserGroupDAOLdapImpl(UserGroupDAOLdapImpl userGroupDAOLdapImpl)
    {
        this.userGroupDAOLdapImpl = userGroupDAOLdapImpl;
    }

}

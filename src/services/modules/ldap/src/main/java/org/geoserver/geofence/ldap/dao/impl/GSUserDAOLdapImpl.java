/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.ldap.dao.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.geoserver.geofence.core.dao.GSUserDAO;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.UserGroup;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

/**
 * GSUserDAO implementation, using an LDAP server as a primary source.
 *
 * @author "Mauro Bartolomeoli - mauro.bartolomeoli@geo-solutions.it"
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public class GSUserDAOLdapImpl extends LDAPBaseDAO<GSUserDAO, GSUser> implements GSUserDAO {

    private UserGroupDAOLdapImpl userGroupDAOLdapImpl;

    private String memberFilter = null;

    private String nestedMemberFilter = null;

    private boolean enableHierarchicalGroups = false;

    private int maxLevelGroupsSearch = Integer.MAX_VALUE;

    /**
     *
     */
    public GSUserDAOLdapImpl() {
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
    private List<UserGroup> getGroups(GSUser user) {
        Filter filter = new Filter();
        String member;
        List<UserGroup> groups;
        String filterStr = null;

        String dn = user.getExtId();
        String userName = user.getName();
        if (memberFilter != null) {
            filterStr = MessageFormat.format(memberFilter, new String[] { dn, userName });
        } else if (StringUtils.isNotBlank(dn)) {
            filter = new Filter("member", dn);
        } else {
            LOGGER.info("User id is null, using username '" + userName + "'");
            String nameAttr = getLDAPAttribute("username");
            String memberSearchFilterAttr = getLDAPAttribute("memberSearchFilter");
            // e.g memberSearchFilter = uniqueMember=uid={0},ou=users,ou=project,dc=edata,dc=comp,dc=be
            if (memberSearchFilterAttr != null) {
                // e.g String member = nameAttr + "=" + userName;
                String val = memberSearchFilterAttr.split("=")[0]; // e.g get uniqueMember part
                member = memberSearchFilterAttr.split("=", 2)[1]; // e.g remove uniqueMember part
                member = member.replace("{0}", userName);
                filterStr = val + '=' + member;
            } else {
                String exp = nameAttr + "=" + userName;
                filter = new Filter("member", exp);
            }
        }
        if (filterStr == null) {
            groups = userGroupDAOLdapImpl.search(filter);
        } else {
            groups = userGroupDAOLdapImpl.search(filterStr);
        }
        if (enableHierarchicalGroups && nestedMemberFilter != null) {
            for (UserGroup group : groups) {
                groups = addParentGroups(groups, group, 0);
            }
        }
        return groups;
    }

    private List<UserGroup> addParentGroups(List<UserGroup> groups, UserGroup group, int level) {
        if (level < maxLevelGroupsSearch) {
            List<UserGroup> newGroups = new ArrayList<UserGroup>();
            newGroups.addAll(groups);
            String filter = MessageFormat.format(nestedMemberFilter,
                    new String[] { group.getExtId(), group.getName() });
            for(UserGroup parentGroup : (List<UserGroup>)userGroupDAOLdapImpl.search(filter)) {
                if (!newGroups.contains(parentGroup)) {
                    newGroups.add(parentGroup);
                    newGroups = addParentGroups(newGroups, parentGroup, level + 1);
                }
            }
            return newGroups;
        }
        return groups;
    }

    @Override
    public GSUser getFull(String name) {
        GSUser user = searchByName(name);
        if (user == null)
            return null;

        return fillWithGroups(user);
    }

    /**
     * Updates the groups list for the given user.
     *
     * @param gsUser
     * @return
     */
    private GSUser fillWithGroups(GSUser user) {
        user.setGroups(new HashSet(getGroups(user)));
        return user;
    }

    public GSUser searchByName(String name) {

        Search search = new Search();
        search.addFilter(new Filter("username", name));
        List<GSUser> users = search(search);

        if (users.isEmpty())
            return null;
        else if (users.size() > 1)
            throw new IllegalArgumentException(
                    "Given filter (" + name + ") returns too many users (" + users.size() + ")");
        GSUser user = users.get(0);
        return user;
    }

    public void setUserGroupDAOLdapImpl(UserGroupDAOLdapImpl userGroupDAOLdapImpl) {
        this.userGroupDAOLdapImpl = userGroupDAOLdapImpl;
    }

    public void setMemberFilter(String memberFilter) {
        this.memberFilter = memberFilter;
        if (this.nestedMemberFilter == null) {
            this.nestedMemberFilter = memberFilter;
        }
    }

    public void setNestedMemberFilter(String nestedMemberFilter) {
        this.nestedMemberFilter = nestedMemberFilter;
    }

    public void setEnableHierarchicalGroups(boolean enableHierarchicalGroups) {
        this.enableHierarchicalGroups = enableHierarchicalGroups;
    }
}

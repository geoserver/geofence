/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import com.googlecode.genericdao.search.Search;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.geoserver.geofence.core.dao.GSUserDAO;
import org.geoserver.geofence.core.dao.UserGroupDAO;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.spi.UserResolver;

/**
 * A UserResolver that uses GeoFence internal DAOs 
 * to retrieve users and roles.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class DefaultUserResolver implements UserResolver {

    private GSUserDAO userDAO;
    private UserGroupDAO userGroupDAO;


    @Override
    public boolean existsUser(String username) {
        return userDAO.getFull(username) != null;
    }

    @Override
    public Set<String> getRoles(String username) {
        GSUser user = userDAO.getFull(username);

        Set<String> ret = new HashSet<String>();
        for (UserGroup role : user.getGroups()) {
            ret.add(role.getName());
        }
        return ret;
    }

    @Override
    public boolean existsRole(String rolename) {
        Search search = new Search(UserGroup.class);
        search.addFilterEqual("name", rolename);
        List<UserGroup> groups = userGroupDAO.search(search);

        return ! groups.isEmpty();
    }

    //=========================================================================
    
    public void setGsUserDAO(GSUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setUserGroupDAO(UserGroupDAO userGroupDAO) {
        this.userGroupDAO = userGroupDAO;
    }

}

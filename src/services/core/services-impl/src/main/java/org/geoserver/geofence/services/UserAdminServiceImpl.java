/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.services.UserAdminService;
import com.googlecode.genericdao.search.Search;
import org.geoserver.geofence.core.dao.GSUserDAO;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.services.dto.ShortUser;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import org.geoserver.geofence.services.exception.BadRequestServiceEx;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;
import java.util.Set;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class UserAdminServiceImpl implements UserAdminService {

    private final static Logger LOGGER = LogManager.getLogger(UserAdminServiceImpl.class);

    private GSUserDAO userDAO;

    // ==========================================================================
    @Override
    public long insert(GSUser user) {
        userDAO.persist(user);
        return user.getId();
    }

    @Override
    public long update(GSUser user) throws NotFoundServiceEx {
        GSUser orig = userDAO.find(user.getId());
        if (orig == null) {
            throw new NotFoundServiceEx("User not found", user.getId());
        }

        userDAO.merge(user);
        return orig.getId();
    }

    @Override
    public GSUser get(long id) throws NotFoundServiceEx {
        GSUser user = userDAO.find(id);

        if (user == null) {
            throw new NotFoundServiceEx("User not found", id);
        }

        return user;
    }

    @Override
    public GSUser get(String name) {
        Search search = new Search(GSUser.class);
        search.addFilterEqual("name", name);
        List<GSUser> users = userDAO.search(search);

        if(users.isEmpty())
            throw new NotFoundServiceEx("User not found  '"+ name + "'");
        else if(users.size() > 1)
            throw new IllegalStateException("Found more than one user with name '"+name+"'");
        else
            return users.get(0);
    }


    @Override
    public GSUser getFull(long id) throws NotFoundServiceEx {

        GSUser user = userDAO.getFull(id);
        if(user == null)
            throw new NotFoundServiceEx("User not found", id);
        return user;
    }
//        Search search = new Search(GSUser.class);
//        search.addFetch("userGroups");
//        search.addFilterEqual("id", id);
//        List<GSUser> users = userDAO.search(search);
//        switch(users.size()) {
//            case 0:
//                throw new NotFoundServiceEx("User not found", id);
//            case 1:
//                return users.get(0);
//            default:
//                if(users.size() == users.get(0).getGroups().size()) { // normal hibernate behaviour
//                    if(LOGGER.isDebugEnabled()) { // perform some more consistency tests only when debugging
//                        for (GSUser user : users) {
//                            if(user.getId() != users.get(0).getId() ||
//                               user.getGroups().size() != users.get(0).getGroups().size()) {
//                                LOGGER.error("Inconsistent userlist " + user);
//                            }
//                        }
//                    }
//
//                    return users.get(0);
//                } else {
//                    LOGGER.error("Too many users with id " + id);
//                    for (GSUser user : users) {
//                        LOGGER.error("   " + user + " grp:"+user.getGroups().size());
//                    }
//                    throw new IllegalStateException("Found more than one user (id:"+id+")");
//                }
//        }
//    }

    @Override
    public Set<UserGroup> getUserGroups(long id) {
        return userDAO.getGroups(id);
    }

    @Override
    public boolean delete(long id) throws NotFoundServiceEx {
        // data on ancillary tables should be deleted by cascading
        return userDAO.removeById(id);
    }

    @Override
    public List<GSUser> getFullList(String nameLike, Integer page, Integer entries) throws BadRequestServiceEx {
        return getFullList(nameLike, page, entries, false);
    }

    @Override
    public List<GSUser> getFullList(String nameLike, Integer page, Integer entries, boolean fetchGroups) throws BadRequestServiceEx {

        if( (page != null && entries == null) || (page ==null && entries != null)) {
            throw new BadRequestServiceEx("Page and entries params should be declared together.");
        }

        Search searchCriteria = new Search(GSUser.class);

        if(page != null) {
            searchCriteria.setMaxResults(entries);
            searchCriteria.setPage(page);
        }

        if(fetchGroups) {
            searchCriteria.addFetch("userGroups");
        }

        searchCriteria.addSortAsc("name");

        if (nameLike != null) {
            searchCriteria.addFilterILike("name", nameLike);
        }

        List<GSUser> found = userDAO.search(searchCriteria);
        return found;
    }

    @Override
    public List<ShortUser> getList(String nameLike, Integer page, Integer entries) {
        return convertToShortList(getFullList(nameLike, page, entries));
    }

    @Override
    public long getCount(String nameLike) {
        Search searchCriteria = new Search(GSUser.class);

        if (nameLike != null) {
            searchCriteria.addFilterILike("name", nameLike);
        }

        return userDAO.count(searchCriteria);
    }

    // ==========================================================================

    private List<ShortUser> convertToShortList(List<GSUser> list) {
        List<ShortUser> swList = new ArrayList<ShortUser>(list.size());
        for (GSUser user : list) {
            swList.add(new ShortUser(user));
        }

        return swList;
    }

    // ==========================================================================

    public void setGsUserDAO(GSUserDAO userDao) {
        this.userDAO = userDao;
    }

}

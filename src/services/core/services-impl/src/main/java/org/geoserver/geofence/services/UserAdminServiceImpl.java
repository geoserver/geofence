/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.core.model.GSUser;

import org.geoserver.geofence.core.dao.GSUserDAO;

import org.geoserver.geofence.services.dto.ShortUser;

import org.geoserver.geofence.services.exception.BadRequestServiceEx;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    public GSUser getFull(String name) throws NotFoundServiceEx {

        GSUser user = userDAO.getFull(name);
        if(user == null)
            throw new NotFoundServiceEx("User not found", name);
        return user;
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

        return userDAO.search(nameLike, page, entries, fetchGroups);
    }

    @Override
    public List<ShortUser> getList(String nameLike, Integer page, Integer entries) {
        return convertToShortList(getFullList(nameLike, page, entries));
    }

    @Override
    public long getCount(String nameLike) {
        return userDAO.countByNameLike(nameLike);
    }

    // ==========================================================================

    private List<ShortUser> convertToShortList(List<GSUser> list) {
        List<ShortUser> swList = new ArrayList<>(list.size());
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

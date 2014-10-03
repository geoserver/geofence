/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.services.GFUserAdminService;
import com.googlecode.genericdao.search.Search;
import org.geoserver.geofence.services.dto.ShortUser;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import org.geoserver.geofence.core.dao.GFUserDAO;
import org.geoserver.geofence.core.model.GFUser;
import org.geoserver.geofence.services.exception.BadRequestServiceEx;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GFUserAdminServiceImpl implements GFUserAdminService {

    private final static Logger LOGGER = LogManager.getLogger(GFUserAdminServiceImpl.class);

    private GFUserDAO gfUserDAO;

    // ==========================================================================
    @Override
    public long insert(GFUser user) {
        gfUserDAO.persist(user);
        return user.getId();
    }

    @Override
    public long update(GFUser user) throws NotFoundServiceEx {
        GFUser orig = gfUserDAO.find(user.getId());
        if (orig == null) {
            throw new NotFoundServiceEx("User not found", user.getId());
        }

        gfUserDAO.merge(user);
        return orig.getId();
    }

    @Override
    public GFUser get(long id) throws NotFoundServiceEx {
        GFUser user = gfUserDAO.find(id);

        if (user == null) {
            throw new NotFoundServiceEx("User not found", id);
        }

        return user;
    }

    @Override
    public GFUser get(String name) {
        Search search = new Search(GFUser.class);
        search.addFilterEqual("name", name);
        List<GFUser> users = gfUserDAO.search(search);

        if(users.isEmpty())
            throw new NotFoundServiceEx("User not found  '"+ name + "'");
        else if(users.size() > 1)
            throw new IllegalStateException("Found more than one user with name '"+name+"'");
        else
            return users.get(0);
    }



    @Override
    public boolean delete(long id) throws NotFoundServiceEx {
        // data on ancillary tables should be deleted by cascading
        return gfUserDAO.removeById(id);
    }

    @Override
    public List<GFUser> getFullList(String nameLike, Integer page, Integer entries) {

        if( (page != null && entries == null) || (page ==null && entries != null)) {
            throw new BadRequestServiceEx("Page and entries params should be declared together.");
        }

        Search searchCriteria = new Search(GFUser.class);

        if(page != null) {
            searchCriteria.setMaxResults(entries);
            searchCriteria.setPage(page);
        }

        searchCriteria.addSortAsc("name");

        if (nameLike != null) {
            searchCriteria.addFilterILike("name", nameLike);
        }

        List<GFUser> found = gfUserDAO.search(searchCriteria);
        return found;
    }

    @Override
    public List<ShortUser> getList(String nameLike, Integer page, Integer entries) {
        return convertToShortList(getFullList(nameLike, page, entries));
    }

    @Override
    public long getCount(String nameLike) {
        Search searchCriteria = new Search(GFUser.class);

        if (nameLike != null) {
            searchCriteria.addFilterILike("name", nameLike);
        }

        return gfUserDAO.count(searchCriteria);
    }

    // ==========================================================================

    private List<ShortUser> convertToShortList(List<GFUser> list) {
        List<ShortUser> swList = new ArrayList<ShortUser>(list.size());
        for (GFUser user : list) {
            swList.add(new ShortUser(user));
        }

        return swList;
    }

    // ==========================================================================

    public void setGfUserDAO(GFUserDAO userDao) {
        this.gfUserDAO = userDao;
    }

}

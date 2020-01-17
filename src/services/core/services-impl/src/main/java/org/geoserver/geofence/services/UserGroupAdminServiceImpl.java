/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.core.dao.UserGroupDAO;
import org.geoserver.geofence.services.dto.ShortGroup;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;

import java.util.List;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class UserGroupAdminServiceImpl implements UserGroupAdminService {

    private final static Logger LOGGER = LogManager.getLogger(UserGroupAdminServiceImpl.class);
    private UserGroupDAO userGroupDAO;

    // ==========================================================================
    @Override
    public long insert(ShortGroup group) {
        UserGroup p = new UserGroup();
        p.setName(group.getName());

        if(group.isEnabled() !=null)
            p.setEnabled(group.isEnabled());

        userGroupDAO.persist(p);
        return p.getId();
    }

    @Override
    public long update(ShortGroup group) throws NotFoundServiceEx {
        UserGroup orig = userGroupDAO.find(group.getId());
        if ( orig == null ) {
            throw new NotFoundServiceEx("UserGroup not found", group.getId());
        }

//        orig.setName(group.getName());

        if ( group.isEnabled() != null ) {
            orig.setEnabled(group.isEnabled());
        }

        if ( group.getExtId() != null ) {
            orig.setExtId(group.getExtId());
        }

        userGroupDAO.merge(orig);
        return orig.getId();
    }

    @Override
    public UserGroup get(long id) throws NotFoundServiceEx {
        UserGroup group = userGroupDAO.find(id);

        if ( group == null ) {
            throw new NotFoundServiceEx("UserGroup not found", id);
        }

        return group;
    }

    @Override
    public UserGroup get(String name) {        
        UserGroup group = userGroupDAO.get(name);

        if ( group == null ) {
            throw new NotFoundServiceEx("UserGroup not found  '" + name + "'");
        } 
        
        return group;
    }

    @Override
    public boolean delete(long id) throws NotFoundServiceEx {
        UserGroup group = userGroupDAO.find(id);

        if ( group == null ) {
            throw new NotFoundServiceEx("Group not found", id);
        }

        // data on ancillary tables should be deleted by cascading
        return userGroupDAO.remove(group);
    }

    @Override
    public List<ShortGroup> getList(String nameLike, Integer page, Integer entries) {
        List<UserGroup> found = userGroupDAO.search(nameLike, page, entries);
        return convertToShortList(found);
    }

    @Override
    public long getCount(String nameLike) {
        return userGroupDAO.countByNameLike(nameLike);
    }

    // ==========================================================================
    private List<ShortGroup> convertToShortList(List<UserGroup> list) {
        List<ShortGroup> swList = new ArrayList<>(list.size());
        for (UserGroup group : list) {
            swList.add(new ShortGroup(group));
        }

        return swList;
    }

    // ==========================================================================
    public void setUserGroupDAO(UserGroupDAO userGroupDAO) {
        this.userGroupDAO = userGroupDAO;
    }
}

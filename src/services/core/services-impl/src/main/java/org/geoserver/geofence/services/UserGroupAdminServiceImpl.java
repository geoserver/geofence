/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import org.geoserver.geofence.services.UserGroupAdminService;
import com.googlecode.genericdao.search.Search;
import org.geoserver.geofence.core.dao.UserGroupDAO;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.UserGroup;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import org.geoserver.geofence.services.dto.ShortGroup;
import org.geoserver.geofence.services.exception.BadRequestServiceEx;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;
import java.util.ArrayList;

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
        Search search = new Search(UserGroup.class);
        search.addFilterEqual("name", name);
        List<UserGroup> groups = userGroupDAO.search(search);

        if ( groups.isEmpty() ) {
            throw new NotFoundServiceEx("UserGroup not found  '" + name + "'");
        } else if ( groups.size() > 1 ) {
            throw new IllegalStateException("Found more than one UserGroup with name '" + name + "'");
        } else {
            return groups.get(0);
        }
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
        Search searchCriteria = buildCriteria(page, entries, nameLike);
        List<UserGroup> found = userGroupDAO.search(searchCriteria);
        return convertToShortList(found);
    }

    @Override
    public long getCount(String nameLike) {
        Search searchCriteria = buildCriteria(null, null, nameLike);
        return userGroupDAO.count(searchCriteria);
    }

    protected Search buildCriteria(Integer page, Integer entries, String nameLike) throws BadRequestServiceEx {
        if ( (page != null && entries == null) || (page == null && entries != null) ) {
            throw new BadRequestServiceEx("Page and entries params should be declared together.");
        }
        Search searchCriteria = new Search(UserGroup.class);
        if ( page != null ) {
            searchCriteria.setMaxResults(entries);
            searchCriteria.setPage(page);
        }
        searchCriteria.addSortAsc("name");
        if ( nameLike != null ) {
            searchCriteria.addFilterILike("name", nameLike);
        }
        return searchCriteria;
    }

    // ==========================================================================
//    @Override
//    public Map<String, String> getCustomProps(Long id) {
//        return userGroupDAO.getCustomProps(id);
//    }
//
//    @Override
//    public void setCustomProps(Long id, Map<String, String> props) {
//        userGroupDAO.setCustomProps(id, props);
//    }
    // ==========================================================================
    private List<ShortGroup> convertToShortList(List<UserGroup> list) {
        List<ShortGroup> swList = new ArrayList<ShortGroup>(list.size());
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

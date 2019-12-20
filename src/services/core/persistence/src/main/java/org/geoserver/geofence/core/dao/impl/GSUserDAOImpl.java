/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import org.geoserver.geofence.core.dao.GSUserDAO;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.dao.search.Search;

import java.util.Date;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Public implementation of the GSUserDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Transactional(value = "geofenceTransactionManager")
public class GSUserDAOImpl extends BaseDAO<GSUser, Long> implements GSUserDAO
{
    private static final Logger LOGGER = LogManager.getLogger(GSUserDAOImpl.class);

    public GSUserDAOImpl() {
        super(GSUser.class);
    }

    @Override
    public void persist(GSUser... entities)
    {
        Date now = new Date();
        for (GSUser user : entities)
        {
            user.setDateCreation(now);
        }
        super.persist(entities);
    }

    @Override
    public List<GSUser> findAll()
    {
        return super.findAll();
    }

    @Override
    public List<GSUser> search(Search search)
    {
        return super.search(search);
    }

    @Override
    public GSUser getFull(String name) {
        Search search = createSearch();
        search.addFilterEqual("name", name);
        return searchFull(search);
    }

    /**
     * Fetch a GSUser with all of its related groups
     */
    protected GSUser searchFull(Search search) {
        search.addFetch("userGroups");
        search.setDistinct(true);
        List<GSUser> users = super.search(search);

        // When fetching users with multiple groups, the gsusers list id multiplied for the number of groups found.
        // Next there is a workaround to this problem; maybe this:
        //    search.setDistinct(true);
        // Dunno if some annotations in the GSUser definition are wrong, some deeper checks have to be performed.

        switch(users.size()) {
            case 0:
                return null;
            case 1:
                return users.get(0);
            default:
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
//                    LOGGER.error("Too many users in unique search " + search);
//                    for (GSUser user : users) {
//                        LOGGER.error("   " + user + " grp:"+user.getGroups().size());
//                    }
                    throw new IllegalStateException("Found more than one user (search:"+search+")");
//                }
        }
    }


    @Override
    public GSUser merge(GSUser entity)
    {
        return super.merge(entity);
    }

    @Override
    public boolean remove(GSUser entity)
    {
        return super.remove(entity);
    }

    @Override
    public boolean removeById(Long id)
    {
        return super.removeById(id);
    }

}

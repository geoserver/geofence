/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.impl;

import org.geoserver.geofence.core.dao.GSUserDAO;
import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.dao.search.LongSearch;
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
    public GSUser getFull(String name) {
        Search<GSUser> search = createSearch();
        search.addFilterEqual("name", name);
        return searchFull(search);
    }

    /**
     * Fetch a GSUser with all of its related groups
     */
    protected GSUser searchFull(Search<GSUser> search) {
        search.addFetch("userGroups");
        // When fetching users with multiple groups, the gsusers list id multiplied for the number of groups found
        search.setDistinct(true);
        List<GSUser> users = super.search(search);
        
        switch(users.size()) {
            case 0:
                return null;
            case 1:
                return users.get(0);
            default:
                throw new IllegalStateException("Found more than one user (search:"+search+")");
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

    @Override    
    public List<GSUser> search(String nameLike, Integer page, Integer entries, boolean fetchGroups) throws IllegalArgumentException {

        if( (page != null && entries == null) || (page ==null && entries != null)) {
            throw new IllegalArgumentException("Page and entries params should be declared together.");
        }

        Search<GSUser> search = createSearch();

        if(page != null) {
            search.setMaxResults(entries);
            search.setPage(page);
        }

        if(fetchGroups) {
            search.addFetch("userGroups");
            search.setDistinct(true);            
        }

        search.addSortAsc("name");

        if (nameLike != null) {
            search.addFilterILike("name", nameLike);
        }

        return search(search);
    }
    

    @Override
    public long countByNameLike(String nameLike) {
        LongSearch<GSUser> search = createLongSearch();

        if (nameLike != null) {
            search.addFilterILike("name", nameLike);
        }

        return count(search);
    }

}

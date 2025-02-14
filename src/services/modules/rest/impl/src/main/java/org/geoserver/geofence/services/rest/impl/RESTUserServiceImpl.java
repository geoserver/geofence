/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.impl;

import org.geoserver.geofence.services.rest.exception.ConflictRestEx;
import org.geoserver.geofence.services.rest.exception.GeoFenceRestEx;
import org.geoserver.geofence.services.rest.exception.BadRequestRestEx;
import org.geoserver.geofence.services.rest.exception.InternalErrorRestEx;
import org.geoserver.geofence.services.rest.exception.NotFoundRestEx;
import java.util.List;


import org.geoserver.geofence.core.model.GSUser;
import org.geoserver.geofence.core.model.UserGroup;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.RuleFilter.SpecialFilterType;
import org.geoserver.geofence.services.exception.BadRequestServiceEx;
import org.geoserver.geofence.services.exception.NotFoundServiceEx;
import org.geoserver.geofence.services.rest.RESTUserService;
import org.geoserver.geofence.services.rest.model.RESTInputUser;
import org.geoserver.geofence.services.rest.model.RESTOutputUser;
import org.geoserver.geofence.services.rest.model.RESTShortUser;
import org.geoserver.geofence.services.rest.model.RESTShortUserList;
import org.geoserver.geofence.services.rest.model.util.IdName;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RESTUserServiceImpl
        extends BaseRESTServiceImpl
        implements RESTUserService {

    private static final Logger LOGGER = LogManager.getLogger(RESTUserServiceImpl.class);

//    private UserAdminService userAdminService;
//    private UserGroupAdminService userGroupAdminService;

    @Override
    public Response delete(String username, boolean cascade) throws ConflictRestEx, NotFoundRestEx, InternalErrorRestEx {
        try {
            if ( cascade ) {
                ruleAdminService.deleteRulesByUser(username);
            } else {
                RuleFilter filter = new RuleFilter(SpecialFilterType.ANY);
                filter.setUser(username);
                filter.getUser().setIncludeDefault(false);
                long cnt = ruleAdminService.count(filter);
                if ( cnt > 0 ) {
                    throw new ConflictRestEx("Existing rules reference the user " + username);
                }
            }

            GSUser user = userAdminService.getFull(username); // may throw NotFoundServiceEx

            if ( ! userAdminService.delete(user.getId())) {
                LOGGER.warn("ILLEGAL STATE -- User not found: " + user); // this should not happen
                throw new NotFoundRestEx("ILLEGAL STATE -- User not found: " + user);
            }

            return Response.status(Status.OK).entity("OK\n").build();

        } catch (GeoFenceRestEx ex) { // already handled
            throw ex;
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("User not found: " + username);
            throw new NotFoundRestEx("User not found: " +username);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    @Override
    public RESTOutputUser get(String name) throws NotFoundRestEx, InternalErrorRestEx {
        try {
            GSUser ret = userAdminService.getFull(name);
            return toOutputUser(ret);
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("User not found: " + name);
            throw new NotFoundRestEx("User not found: " + name);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    @Override
    public Response insert(RESTInputUser user) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx, ConflictRestEx {


        boolean exists;
        // check that no user with same name exists
        try {
            userAdminService.getFull(user.getName());
            exists = true;
        } catch (NotFoundServiceEx ex) {
            // well, ok, user does not exist
            exists = false;
        } catch (Exception ex) {
            // something went wrong
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }

        if ( exists ) {
            throw new ConflictRestEx("User '" + user.getName() + "' already exists");
        }


        try {
            Set<UserGroup> groups = new HashSet<>();
            // resolve groups
            List<IdName> inputGroups = user.getGroups();
            if ( inputGroups == null || inputGroups.isEmpty() ) {
//                throw new BadRequestRestEx("Can't insert user without group");
                LOGGER.warn("No groups defined for user " + user.getName());
            } else {
                for (IdName identifier : inputGroups) {
                    if ( identifier == null ) {
                        throw new BadRequestRestEx("Bad group identifier");
                    }
                    UserGroup group = getUserGroup(identifier);
                    groups.add(group);
                }
            }

            GSUser u = new GSUser();
            u.setGroups(groups);
            u.setExtId(user.getExtId());
            u.setName(user.getName());
            u.setPassword(user.getPassword());
            u.setEnabled(user.isEnabled());
            u.setAdmin(user.isAdmin());
            u.setFullName(user.getFullName());
            u.setEmailAddress(user.getEmailAddress());

            Long ret = userAdminService.insert(u);

            return Response.status(Status.CREATED).tag(ret.toString()).entity(ret).build();

        } catch (GeoFenceRestEx ex) {
            // already handled
            throw ex;
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("Problems inserting user: " + ex.getMessage(), ex);
            throw new NotFoundRestEx(ex.getMessage());
        } catch (BadRequestServiceEx ex) {
            LOGGER.warn("Problems inserting user: " + ex.getMessage(), ex);
            throw new BadRequestRestEx(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    @Override
    public void update(String name, RESTInputUser user) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        try {
            GSUser old = userAdminService.getFull(name);
            update(old.getId(), user);
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("User not found: " + name);
            throw new NotFoundRestEx("User not found: " + name);
        }
    }

    @Override
    public void update(Long id, RESTInputUser user) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {

        try {
            GSUser old = userAdminService.get(id);

            if ( (user.getExtId() != null) && !user.getExtId().equals(old.getExtId()) ) {
                throw new BadRequestRestEx("ExtId can't be updated");
            }

            if ( (user.getName() != null) ) {
                throw new BadRequestRestEx("Name can't be updated");
            }

            if ( user.getPassword() != null ) {
                old.setPassword(user.getPassword());
            }

            if ( user.getEmailAddress() != null ) {
                old.setEmailAddress(user.getEmailAddress());
            }

            if ( user.isAdmin() != null ) {
                old.setAdmin(user.isAdmin());
            }

            if ( user.isEnabled() != null ) {
                old.setEnabled(user.isEnabled());
            }

            if ( user.getGroups() != null ) {
                Set<UserGroup> groups = new HashSet<UserGroup>();
                for (IdName identifier : user.getGroups()) {
                    UserGroup group = getUserGroup(identifier);
                    groups.add(group);
                }
                old.setGroups(groups);
            }

            userAdminService.update(old);

        } catch (GeoFenceRestEx ex) {
            // already handled
            throw ex;
        } catch (NotFoundServiceEx ex) {
            LOGGER.warn("Problems updating user " + id + ": " + ex.getMessage(), ex);
            throw new NotFoundRestEx(ex.getMessage());
        } catch (BadRequestServiceEx ex) {
            LOGGER.warn("Problems updating user " + id + ": " + ex.getMessage(), ex);
            throw new BadRequestRestEx(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    @Override
    public RESTShortUserList getList(String nameLike, Integer page, Integer entries) throws BadRequestRestEx, InternalErrorRestEx {
        try {
            List<GSUser> list = userAdminService.getFullList(nameLike, page, entries);
            RESTShortUserList ret = new RESTShortUserList(list.size());
            for (GSUser user : list) {
                ret.add(toShortUser(user));
            }
            return ret;

        } catch (BadRequestServiceEx ex) {
            LOGGER.warn(ex.getMessage());
            throw new BadRequestRestEx(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.warn("Unexpected exception", ex);
            throw new InternalErrorRestEx(ex.getMessage());
        }
    }

    @Override
    public long count(String nameLike) {
        return userAdminService.getCount(nameLike);
    }

    // ==========================================================================
    public static RESTShortUser toShortUser(GSUser user) {
        RESTShortUser shu = new RESTShortUser();
        shu.setId(user.getId());
        shu.setExtId(user.getExtId());
        shu.setUserName(user.getName());
        shu.setEnabled(user.getEnabled());

        return shu;
    }

    public static RESTOutputUser toOutputUser(GSUser user) {
        RESTOutputUser ret = new RESTOutputUser();
        ret.setId(user.getId());
        ret.setExtId(user.getExtId());
        ret.setName(user.getName());
        ret.setEnabled(user.getEnabled());
        ret.setAdmin(user.isAdmin());
        ret.setFullName(user.getFullName());
        ret.setEmailAddress(user.getEmailAddress());

        List<IdName> groups = new ArrayList<IdName>();
        for (UserGroup group : user.getGroups()) {
            IdName nameId = new IdName(group.getId(), group.getName());
            groups.add(nameId);
        }
        ret.setGroups(groups);

        return ret;
    }


    // ==========================================================================
    // ==========================================================================

    @Override
    public void addIntoGroup(String userName, String groupName) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        addIntoGroup(new IdName(userName), new IdName(groupName));
    }

    private void addIntoGroup(IdName userId, IdName groupId) throws InternalErrorRestEx, BadRequestRestEx, NotFoundRestEx {
        try {
            UserGroup group = getUserGroup(groupId);

            GSUser user = getUser(userId); // needed for param check
            GSUser fulluser = userAdminService.getFull(user.getName());
            fulluser.getGroups().add(group);

            userAdminService.update(fulluser);

        } catch (BadRequestRestEx e) {
            throw e;
        } catch (NotFoundRestEx e) {
            throw e;
        } catch (Exception e) {
            LOGGER.warn("Unexpected exception", e);
            throw new InternalErrorRestEx(e.getMessage());
        }
    }
    
    // ==========================================================================
    // ==========================================================================

    @Override
    public void removeFromGroup(String userName, String groupName) throws NotFoundRestEx, InternalErrorRestEx, BadRequestRestEx {
        removeFromGroup(new IdName(userName), new IdName(groupName));
    }

    private void removeFromGroup(IdName userId, IdName groupId) throws InternalErrorRestEx, BadRequestRestEx, NotFoundRestEx {
        try {
            UserGroup groupToRemove = getUserGroup(groupId);

            GSUser user = getUser(userId);
            GSUser fulluser = userAdminService.getFull(user.getName());

            UserGroup found = null;
            for (UserGroup group : fulluser.getGroups()) {
                if (group.getId().equals(groupToRemove.getId())) {
                    found = group;
                    break;
                }
            }
            if (found != null) {
                fulluser.getGroups().remove(found);
                userAdminService.update(fulluser);
            } else {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("User " + user.getName() + " not in group " + groupToRemove);
                }
            }
        } catch (NotFoundRestEx e) {
            throw e;
        } catch (BadRequestRestEx e) {
            throw e;
        } catch (NotFoundServiceEx e) {
            LOGGER.warn(e.getMessage());
            throw new NotFoundRestEx(e.getMessage());
        } catch (Exception e) {
            LOGGER.warn("Unexpected exception", e);
            throw new InternalErrorRestEx(e.getMessage());
        }
    }


}

/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.vividsolutions.jts.geom.Geometry;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.geoserver.geofence.core.dao.AdminRuleDAO;
import org.geoserver.geofence.core.dao.LayerDetailsDAO;
import org.geoserver.geofence.core.dao.RuleDAO;
import org.geoserver.geofence.core.model.*;
import org.geoserver.geofence.core.model.enums.AccessType;
import org.geoserver.geofence.core.model.enums.AdminGrantType;
import org.geoserver.geofence.core.model.enums.CatalogMode;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.services.dto.AccessInfo;
import org.geoserver.geofence.services.dto.AuthUser;
import org.geoserver.geofence.services.dto.RuleFilter;
import org.geoserver.geofence.services.dto.RuleFilter.FilterType;
import org.geoserver.geofence.services.dto.RuleFilter.IdNameFilter;
import org.geoserver.geofence.services.dto.RuleFilter.SpecialFilterType;
import org.geoserver.geofence.services.dto.RuleFilter.TextFilter;
import org.geoserver.geofence.services.dto.ShortRule;
import org.geoserver.geofence.services.exception.BadRequestServiceEx;
import org.geoserver.geofence.services.util.AccessInfoInternal;
import org.geoserver.geofence.spi.UserResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import java.util.Map.Entry;

import static org.geoserver.geofence.services.util.FilterUtils.filterByAddress;

/**
 *
 * <P>
 * <B>Note:</B> <TT>service</TT> and <TT>request</TT> params are usually set by
 * the client, and by OGC specs they are not case sensitive, so we're going to
 * turn all of them uppercase. See also {@link RuleAdminServiceImpl}.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RuleReaderServiceImpl implements RuleReaderService {

    private final static Logger LOGGER = LogManager.getLogger(RuleReaderServiceImpl.class);

    private RuleDAO ruleDAO;
    private AdminRuleDAO adminRuleDAO;
    private LayerDetailsDAO detailsDAO;

    private UserResolver userResolver;
    private AuthorizationService authorizationService;

    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public List<ShortRule> getMatchingRules(
                    String userName, String profileName, String instanceName,
                    String sourceAddress,
                    String service, String request,
                    String workspace, String layer) {

        return getMatchingRules(new RuleFilter(userName, profileName, instanceName, sourceAddress, service, request, workspace, layer));
    }

    /**
     * <B>TODO: REFACTOR</B>
     *
     * @param filter
     * @return a plain List of the grouped matching Rules.
     */
    @Override
    public List<ShortRule> getMatchingRules(RuleFilter filter) {
        Map<String, List<Rule>> found = getRules(filter);

        Map<Long, Rule> sorted = new TreeMap<>();
        for (List<Rule> list : found.values()) {
            for (Rule rule : list) {
                sorted.put(rule.getPriority(), rule);
            }
        }

        LOGGER.warn(sorted.size() + " matching rules for filter " + filter);
        List<Rule> plainList = new ArrayList<>();
        for (Rule rule : sorted.values()) {
            LOGGER.warn( " -- " + rule);
            plainList.add(rule);
        }

        return convertToShortList(plainList);
    }


    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public AccessInfo getAccessInfo(String userName, String roleName, String instanceName,
            String sourceAddress,
            String service, String request,
            String workspace, String layer) {
        return getAccessInfo(new RuleFilter(userName, roleName, instanceName, sourceAddress, service, request, workspace, layer));
    }

    @Override
    public AccessInfo getAccessInfo(RuleFilter filter)
    {
        LOGGER.info("Requesting access for " + filter);
        Map<String, List<Rule>> groupedRules = getRules(filter);

        AccessInfoInternal currAccessInfo = null;
        
        for (Entry<String, List<Rule>> ruleGroup : groupedRules.entrySet()) {
            String role = ruleGroup.getKey();
            List<Rule> rules = ruleGroup.getValue();

            AccessInfoInternal accessInfo = resolveRuleset(rules);
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("Filter " + filter + " on role " + role + " has access " + accessInfo);
            }

            currAccessInfo = enlargeAccessInfo(currAccessInfo, accessInfo);
        }

        AccessInfo ret;

        if(currAccessInfo == null) {
            LOGGER.warn("No access for filter " + filter);
            // Denying by default
            ret = new AccessInfo(GrantType.DENY);
        } else {
            ret = currAccessInfo.toAccessInfo();
        }

        if(ret.getGrant() == GrantType.ALLOW) {
            ret.setAdminRights(getAdminAuth(filter));
        }

        LOGGER.info("Returning " + ret + " for " + filter);
        return ret;
    }

    @Override
    public AccessInfo getAdminAuthorization(RuleFilter filter)
    {
        AccessInfo ret = new AccessInfo(GrantType.ALLOW);
        ret.setAdminRights(getAdminAuth(filter));
        return ret;
    }

    private AccessInfoInternal enlargeAccessInfo(AccessInfoInternal baseAccess, AccessInfoInternal moreAccess)
    {
        if(baseAccess == null) {
            if(moreAccess == null)
                return null;
            else if(moreAccess.getGrant() == GrantType.ALLOW)
                return moreAccess;            
            else
                return null;
        } else {
            if(moreAccess == null)
                return baseAccess;
            else if(moreAccess.getGrant() == GrantType.DENY)
                return baseAccess;
            else {
                // ok: extending grants
                AccessInfoInternal ret = new AccessInfoInternal(GrantType.ALLOW);

                ret.setCqlFilterRead(unionCQL(baseAccess.getCqlFilterRead(), moreAccess.getCqlFilterRead()));
                ret.setCqlFilterWrite(unionCQL(baseAccess.getCqlFilterWrite(), moreAccess.getCqlFilterWrite()));

                ret.setCatalogMode(getLarger(baseAccess.getCatalogMode(), moreAccess.getCatalogMode()));

                if(baseAccess.getDefaultStyle() == null || moreAccess.getDefaultStyle()==null)
                    ret.setDefaultStyle(null);
                else
                    ret.setDefaultStyle(baseAccess.getDefaultStyle()); // just pick one

                ret.setAllowedStyles(unionAllowedStyles(baseAccess.getAllowedStyles(), moreAccess.getAllowedStyles()));
                ret.setAttributes(unionAttributes(baseAccess.getAttributes(), moreAccess.getAttributes()));
                ret.setArea(unionGeometry(baseAccess.getArea(), moreAccess.getArea()));

                return ret;
            }
        }        
    }

    private String unionCQL(String c1, String c2) {
          if(c1 == null || c2 == null)
              return null;

          return "("+c1+") OR ("+c2+")";
    }

    private Geometry unionGeometry(Geometry g1, Geometry g2) {
          if(g1 == null || g2 == null)
              return null;

          return union(g1, g2);
    }

    private static Set<LayerAttribute> unionAttributes(Set<LayerAttribute> a0, Set<LayerAttribute> a1) {
        // TODO: check how geoserver deals with empty set

        if(a0 == null || a0.isEmpty())
            return Collections.EMPTY_SET;
//            return a1;
        if(a1==null || a1.isEmpty())
            return Collections.EMPTY_SET;
//            return a0;

        Set<LayerAttribute> ret = new HashSet<LayerAttribute>();
        // add both attributes only in a0, and enlarge common attributes
        for (LayerAttribute attr0 : a0) {
            LayerAttribute attr1 = getAttribute(attr0.getName(), a1);
            if(attr1 == null) {
                ret.add(attr0.clone());
            } else {
                LayerAttribute attr = attr0.clone();
                if(attr0.getAccess()==AccessType.READWRITE || attr1.getAccess()==AccessType.READWRITE)
                    attr.setAccess(AccessType.READWRITE);
                else if(attr0.getAccess()==AccessType.READONLY || attr1.getAccess()==AccessType.READONLY)
                    attr.setAccess(AccessType.READONLY);
                ret.add(attr);
            }
        }
        // now add attributes that are only in a1
        for (LayerAttribute attr1 : a1) {
            LayerAttribute attr0 = getAttribute(attr1.getName(), a0);
            if(attr0 == null) {
                ret.add(attr1.clone());

            }
        }

        return ret;
    }


    private static LayerAttribute getAttribute(String name, Set<LayerAttribute> set) {
        for (LayerAttribute layerAttribute : set) {
            if(layerAttribute.getName().equals(name) )
                return layerAttribute;
        }
        return null;
    }

    private static Set<String> unionAllowedStyles(Set<String> a0, Set<String> a1) {

        // if at least one of the two set is empty, the result will be an empty set,
        // that means styles are not restricted
        if(a0 == null || a0.isEmpty())
            return Collections.EMPTY_SET;

        if(a1==null || a1.isEmpty())
            return Collections.EMPTY_SET;

        Set<String> allowedStyles = new HashSet<String>();
        allowedStyles.addAll(a0);
        allowedStyles.addAll(a1);
        return allowedStyles;
    }

    private AccessInfoInternal resolveRuleset(List<Rule> ruleList) {

        List<RuleLimits> limits = new ArrayList<>();
        AccessInfoInternal ret = null;

        for (Rule rule : ruleList) {
            if(ret != null)
                break;

            switch(rule.getAccess()) {
                case LIMIT:

                   RuleLimits rl = rule.getRuleLimits();
                   if(rl != null) {
                       LOGGER.info("Collecting limits: " + rl);
                       limits.add(rl);
                    } else
                       LOGGER.warn(rule + " has no associated limits");
                    break;

                case DENY:
                    ret = new AccessInfoInternal(GrantType.DENY);
                    break;

                case ALLOW:
                    ret = buildAllowAccessInfo(rule, limits, null);
                    break;

                default:
                    throw new IllegalStateException("Unknown GrantType " + rule.getAccess());
            }
        }

//        if(ret == null) {
//            LOGGER.warn("No rule matching filter " + filter);
//            // Denying by default
//            ret = new AccessInfo(GrantType.DENY);
//        }

//        LOGGER.info("Returning " + ret + " for " + filter);
        return ret;
    }



    private String validateUsername(TextFilter filter) {

        switch(filter.getType()) {
            case NAMEVALUE:
                String name = filter.getText();
                if(StringUtils.isBlank(name) )
                    throw new BadRequestServiceEx("Blank user name");
                return name.trim();
            case DEFAULT:
            case ANY:
                return null;
            default:
                throw new BadRequestServiceEx("Unknown user filter type '"+filter+"'");
        }
    }

    private String validateRolename(TextFilter filter) {

        switch(filter.getType()) {
            case NAMEVALUE:
                String name = filter.getText();
                if(StringUtils.isBlank(name) )
                    throw new BadRequestServiceEx("Blank role name");
                return name.trim();
            case DEFAULT:
            case ANY:
                return null;
            default:
                throw new BadRequestServiceEx("Unknown role filter type '"+filter+"'");
        }
    }

    private AccessInfoInternal buildAllowAccessInfo(Rule rule, List<RuleLimits> limits, IdNameFilter userFilter) {
        AccessInfoInternal accessInfo = new AccessInfoInternal(GrantType.ALLOW);

        Geometry area = intersect(limits);
        CatalogMode cmode = resolveCatalogMode(limits);

        LayerDetails details = rule.getLayerDetails();
        if(details != null ) {
            area = intersect(area, details.getArea());
            cmode = getStricter(cmode, details.getCatalogMode());

            accessInfo.setAttributes(details.getAttributes());
            accessInfo.setCqlFilterRead(details.getCqlFilterRead());
            accessInfo.setCqlFilterWrite(details.getCqlFilterWrite());
            accessInfo.setDefaultStyle(details.getDefaultStyle());
            accessInfo.setAllowedStyles(details.getAllowedStyles());
        }

        accessInfo.setCatalogMode(cmode);

        if (area != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Attaching an area to Accessinfo: " + area.getClass().getName() + " " + area.toString());
            }
//            accessInfo.setAreaWkt(area.toText());
            accessInfo.setArea(area);
        }

        return accessInfo;
    }

    private Geometry intersect(List<RuleLimits> limits) {
        Geometry g = null;
        for (RuleLimits limit : limits) {
            Geometry area = limit.getAllowedArea();
            if(area != null) {
                if( g == null) {
                    g = area;
                } else {
                    g = g.intersection(area);
                }
            }
        }
        return g;
    }

    private Geometry intersect(Geometry g1, Geometry g2) {
        if(g1!=null) {
            if(g2==null)
                return g1;
            else
                return g1.intersection(g2);
        } else {
            return g2;
        }
    }

    private Geometry union(Geometry g1, Geometry g2) {
        if(g1!=null) {
            if(g2==null)
                return g1;
            else
                return g1.union(g2);
        } else {
            return g2;
        }
    }

    /**
     * Returns the stricter catalog mode.
     */
    private CatalogMode resolveCatalogMode(List<RuleLimits> limits) {
        CatalogMode ret = null;
        for (RuleLimits limit : limits) {
            ret = getStricter(ret, limit.getCatalogMode());
        }
        return ret;
    }

    protected static CatalogMode getStricter(CatalogMode m1, CatalogMode m2) {

        if(m1 == null)
            return m2;
        if(m2 == null)
            return m1;

        if(CatalogMode.HIDE == m1 || CatalogMode.HIDE == m2)
            return CatalogMode.HIDE;

        if(CatalogMode.MIXED == m1 || CatalogMode.MIXED == m2)
            return CatalogMode.MIXED;

        return CatalogMode.CHALLENGE;
    }

    protected static CatalogMode getLarger(CatalogMode m1, CatalogMode m2) {

        if(m1 == null)
            return m2;
        if(m2 == null)
            return m1;

        if(CatalogMode.CHALLENGE == m1 || CatalogMode.CHALLENGE == m2)
            return CatalogMode.CHALLENGE;

        if(CatalogMode.MIXED == m1 || CatalogMode.MIXED == m2)
            return CatalogMode.MIXED;

        return CatalogMode.HIDE;
    }


    //==========================================================================

    /**
     * Returns Rules matching a filter.
     *
     * Compatible filters:
     *   username assigned and rolename:ANY      -> should consider all the roles the user belongs to
     *   username:ANY      and rolename assigned -> should consider all the users belonging to the given role
     *
     *
     * @return a Map having role names as keys, and the list of matching Rules as values. The NULL key holds the rules for the DEFAULT group.
     */
    protected Map<String, List<Rule>> getRules(RuleFilter filter) throws BadRequestServiceEx {

        Set<String> finalRoleFilter = validateUserRoles(filter);

        if(finalRoleFilter == null) {
            return Collections.EMPTY_MAP; // shortcut here, in order to avoid loading the rules
        }

        Map<String, List<Rule>> ret = new HashMap<>();

        if(finalRoleFilter.isEmpty()) {
            List<Rule> found = getRuleAux(filter, filter.getRole());
            ret.put(null, found);
        } else {
            for (String role : finalRoleFilter) {
                TextFilter roleFilter = new TextFilter(role);
                roleFilter.setIncludeDefault(true);
                List<Rule> found = getRuleAux(filter, roleFilter);
                ret.put(role, found);
            }
        }

        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Filter " + filter + " is matching the following Rules:");
            boolean ruleFound = false;
            for (Entry<String, List<Rule>> entry : ret.entrySet()) {
                String role = entry.getKey();
                LOGGER.debug("    Role:"+ role );
                for (Rule rule : entry.getValue()) {
                    LOGGER.debug("    Role:"+ role + " ---> " + rule);
                    ruleFound = true;
                }
            }
            if( ! ruleFound)
                LOGGER.debug("No rules matching filter " + filter);

        }

        return ret;
    }

    /**
     * Check requested user and group fileter.
     *
     * <br/>
     * The input filter <b>may be altered</b> for fixing some request inconsistencies.
     *
     * @param filter
     * @return a Set of group names, or null if provided user/group are invalid.
     * @throws BadRequestServiceEx
     */
    protected Set<String> validateUserRoles(RuleFilter filter) throws BadRequestServiceEx {

        // username can be null if the user filter asks for ANY or DEFAULT
        String username = validateUsername(filter.getUser());

        // rolename can be null if the group filter asks for ANY or DEFAULT
        String rolename = validateRolename(filter.getRole());

        // filtering by both user and role is pointless
        if(username != null && rolename != null) {
            throw new BadRequestServiceEx("You can filter either by user or role");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities;
        if (authentication == null) {
            authorities = new HashSet<>();
        } else {
            authorities = authentication.getAuthorities();
        }
        Set<String> finalRoleFilter = new HashSet<>();
        // If both user and group are defined in filter
        //   if user doensn't belong to group, no rule is returned
        //   otherwise assigned or default rules are searched for
        if(username != null) {
            Set<String> assignedRoles = userResolver.getRoles(username);
            if (authorities != null) {
                for (GrantedAuthority authority : authorities) {
                    assignedRoles.add(authority.getAuthority());
                }
            }
            if(rolename != null) {
                if( assignedRoles.contains(rolename)) {
                    finalRoleFilter = Collections.singleton(rolename);
                } else {
                    LOGGER.warn("User does not belong to role [User:"+filter.getUser()+"] [Role:"+filter.getRole()+"] [Roles:"+assignedRoles+"]");
                    return null;
                }
            } else {
                // User set and found, role (ANY, DEFAULT or notfound):

                if(filter.getRole().getType() == FilterType.ANY) {
                    if( ! assignedRoles.isEmpty()) {
                        finalRoleFilter = assignedRoles;
                    } else {
                        filter.setRole(SpecialFilterType.DEFAULT);
                    }
                } else {
                    // role is DEFAULT or not found:
                    // if role filter request DEFAULT -> ok, apply the filter
                    // if role does not exists, just apply the filter, even if probably no rule will match
                }
            }
        } else {
            // user is null: then either:
            //  1) no filter on user was requested (ANY or DEFAULT)
            //  2) user has not been found
            if(rolename != null) {
                finalRoleFilter.add(rolename);
            } else if(filter.getUser().getType() != FilterType.ANY) {
                filter.setRole(SpecialFilterType.DEFAULT);
            } else {
                // group is ANY, DEFAULT or not found:
                // no grouping, use requested filtering
            }
        }

        return finalRoleFilter;
    }

    protected List<Rule> getRuleAux(RuleFilter filter, TextFilter roleFilter) {
        Search searchCriteria = new Search(Rule.class);
        searchCriteria.addSortAsc("priority");
        addStringCriteria(searchCriteria, "username", filter.getUser());
        addStringCriteria(searchCriteria, "rolename", roleFilter);
        addCriteria(searchCriteria, "instance", filter.getInstance());
        addStringCriteria(searchCriteria, "service", filter.getService()); // see class' javadoc
        addStringCriteria(searchCriteria, "request", filter.getRequest()); // see class' javadoc
        addStringCriteria(searchCriteria, "workspace", filter.getWorkspace());
        addStringCriteria(searchCriteria, "layer", filter.getLayer());

        List<Rule> found = ruleDAO.search(searchCriteria);
        found = filterByAddress(filter, found);

        return found;
    }

    private void addCriteria(Search searchCriteria, String fieldName, IdNameFilter filter) {
        switch (filter.getType()) {
            case ANY:
                break; // no filtering

            case DEFAULT:
                searchCriteria.addFilterNull(fieldName);
                break;

            case IDVALUE:
                searchCriteria.addFilterOr(
                        Filter.isNull(fieldName),
                        Filter.equal(fieldName + ".id", filter.getId()));
                break;

            case NAMEVALUE:
                searchCriteria.addFilterOr(
                        Filter.isNull(fieldName),
                        Filter.equal(fieldName + ".name", filter.getName()));
                break;

            default:
                throw new AssertionError();
        }
    }

    private void addStringCriteria(Search searchCriteria, String fieldName, TextFilter filter) {
        switch (filter.getType()) {
            case ANY:
                break; // no filtering

            case DEFAULT:
                searchCriteria.addFilterNull(fieldName);
                break;

            case NAMEVALUE:
                searchCriteria.addFilterOr(
                        Filter.isNull(fieldName),
                        Filter.equal(fieldName, filter.getText()));
                break;

            case IDVALUE:
            default:
                throw new AssertionError();
        }
    }

    // ==========================================================================

    /**
     * @deprecated Please use the method in {@link AuthorizationService#authorize(String, String)}.
     */
    @Override
    @Deprecated
    public AuthUser authorize(String username, String password) {
        return authorizationService.authorize(username, password);
    }


    // ==========================================================================

    private List<ShortRule> convertToShortList(List<Rule> list) {
        List<ShortRule> shortList = new ArrayList<>(list.size());
        for (Rule rule : list) {
            shortList.add(new ShortRule(rule));
        }

        return shortList;
    }

    // ==========================================================================

    public void setRuleDAO(RuleDAO ruleDAO) {
        this.ruleDAO = ruleDAO;
    }

    public void setAdminRuleDAO(AdminRuleDAO adminRuleDAO) {
        this.adminRuleDAO = adminRuleDAO;
    }

    public void setLayerDetailsDAO(LayerDetailsDAO detailsDAO) {
        this.detailsDAO = detailsDAO;
    }

    public void setUserResolver(UserResolver userResolver) {
        this.userResolver = userResolver;
    }

    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }



    private boolean getAdminAuth(RuleFilter filter) {
        Set<String> finalRoleFilter = validateUserRoles(filter);

        if(finalRoleFilter == null) {
            return false;
        }

        boolean isAdmin = false;

        if(finalRoleFilter.isEmpty()) {
            AdminRule rule = getAdminAuthAux(filter, filter.getRole());
            isAdmin = rule == null ? false : rule.getAccess() == AdminGrantType.ADMIN;
        } else {
            for (String role : finalRoleFilter) {
                TextFilter roleFilter = new TextFilter(role);
                roleFilter.setIncludeDefault(true);
                AdminRule rule = getAdminAuthAux(filter, roleFilter);
                // if it's admin in at least one group, the admin auth is granted
                if(rule != null && rule.getAccess() == AdminGrantType.ADMIN) {
                    isAdmin = true;
                }
            }
        }

        return isAdmin;
    }

    protected AdminRule getAdminAuthAux(RuleFilter filter, TextFilter roleFilter) {
        Search searchCriteria = new Search(AdminRule.class);
        searchCriteria.addSortAsc("priority");
        addStringCriteria(searchCriteria, "username", filter.getUser());
        addStringCriteria(searchCriteria, "rolename", roleFilter);
        addCriteria(searchCriteria, "instance", filter.getInstance());
        addStringCriteria(searchCriteria, "workspace", filter.getWorkspace());

        // we only need the first match, no need to aggregate (no LIMIT rules here)
        searchCriteria.setMaxResults(1);

        List<AdminRule> found = adminRuleDAO.search(searchCriteria);
        found = filterByAddress(filter, found);

        switch(found.size()) {
            case 0:
                return null;
            case 1:
                return found.get(0);
            default:
                // should not happen
                throw new IllegalStateException("Too many admin auth rules");
        }
    }


}

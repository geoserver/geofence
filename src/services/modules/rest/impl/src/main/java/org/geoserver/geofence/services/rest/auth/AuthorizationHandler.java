/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.auth;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.frontend.MethodDispatcher;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.security.AccessDeniedException;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.security.SecurityContext;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class AuthorizationHandler extends AbstractPhaseInterceptor<Message>
{

    private static final Logger LOGGER = LogManager.getLogger(AuthorizationHandler.class);
//      private static final String ALL_ROLES = "*";

    private static Map<String, List<String>> parseRolesMap(Map<String, String> rolesMap)
    {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (Map.Entry<String, String> entry : rolesMap.entrySet())
        {
            map.put(entry.getKey(), Arrays.asList(entry.getValue().split(" ")));
        }

        return map;
    }

    private Map<String, List<String>> methodRolesMap = new HashMap<String, List<String>>();
    private Map<String, List<String>> userRolesMap = Collections.emptyMap();
    private List<String> globalRoles = Collections.emptyList();

    public AuthorizationHandler()
    {
        super(Phase.PRE_INVOKE);
    }

    public void handleMessage(Message message) throws Fault
    {
        SecurityContext sc = message.get(SecurityContext.class);
        if (sc == null)
        {
            return;
        }

        Method method = getTargetMethod(message);

        if (authorize(sc, method))
        {
            return;
        }

        throw new AccessDeniedException("Unauthorized");
    }

    protected Method getTargetMethod(Message m)
    {
        BindingOperationInfo bop = m.getExchange().get(BindingOperationInfo.class);
        if (bop != null)
        {
            MethodDispatcher md = (MethodDispatcher) m.getExchange().get(Service.class).get(MethodDispatcher.class.getName());

            return md.getMethod(bop);
        }

        Method method = (Method) m.get("org.apache.cxf.resource.method");
        if (method != null)
        {
            return method;
        }
        throw new AccessDeniedException("Method is not available : Unauthorized");
    }

    protected boolean authorize(SecurityContext sc, Method method)
    {
        List<String> expectedRoles = getExpectedRoles(method);
        if (expectedRoles.isEmpty())
        {

            List<String> denyRoles = getDenyRoles(method);

            return denyRoles.isEmpty() ? true : isUserInRole(sc, denyRoles, true);
        }

        if (isUserInRole(sc, expectedRoles, false))
        {
            return true;
        }
        if (sc.getUserPrincipal() != null)
        {
            LOGGER.error(sc.getUserPrincipal().getName() + " is not authorized");
        }

        return false;
    }

//    protected boolean isUserAllRole(SecurityContext sc, List<String> roles, boolean deny) {
//
//        if (roles.size() == 1 && ALL_ROLES.equals(roles.get(0))) {
//            return !deny;
//        }
//
//        for (String role : roles) {
//            if (sc.isUserInRole(role)) {
//                return !deny;
//            }
//        }
//        return deny;
//    }

    protected boolean isUserInRole(SecurityContext sc, List<String> roles, boolean deny)
    {
//      System.out.println("::::::::::::::::::::::::  0");
//        if (!isUserAllRole(sc, roles, deny)) {
//              System.out.println("::::::::::::::::::::::::  1");
//            return false;
//        }

        // Additional check.
        if (!userRolesMap.isEmpty())
        {
            List<String> userRoles = userRolesMap.get(sc.getUserPrincipal().getName());
            if (userRoles == null)
            {
                return false;
            }
            for (String role : roles)
            {
                if (userRoles.contains(role))
                {
                    return true;
                }
            }

            return false;
        }
        else
        {
            return true;
        }
    }

    protected List<String> getExpectedRoles(Method method)
    {
        List<String> roles = methodRolesMap.get(method.getName());
        if (roles != null)
        {
            return roles;
        }

        return globalRoles;
    }


    public void setMethodRolesMap(Map<String, String> rolesMap)
    {
        methodRolesMap.putAll(parseRolesMap(rolesMap));
    }

    public void setUserRolesMap(Map<String, String> rolesMap)
    {
        userRolesMap = parseRolesMap(rolesMap);
    }

    public void setGlobalRoles(String roles)
    {
        globalRoles = Arrays.asList(roles.split(" "));
    }

    /**
     * Returns a list of roles to be denied for a given method.
     * @param method Method
     * @return list, empty if no roles are available
     */
    protected List<String> getDenyRoles(Method method)
    {
        return Collections.emptyList();
    }

}

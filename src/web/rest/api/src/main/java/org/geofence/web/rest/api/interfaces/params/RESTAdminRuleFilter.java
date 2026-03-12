/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.interfaces.params;

import jakarta.ws.rs.QueryParam;

/** @author etj */
public class RESTAdminRuleFilter {

    public RESTAdminRuleFilter() {}

    public @QueryParam("userName") String userName;
    public @Deprecated @QueryParam("userAny") Boolean userAny;
    public @QueryParam("userDefault") Boolean userDefault;

    public @QueryParam("groupName") String groupName;
    public @Deprecated @QueryParam("groupAny") Boolean groupAny;
    public @QueryParam("groupDefault") Boolean groupDefault;

    public @Deprecated @QueryParam("instanceId") Long instanceId;
    public @QueryParam("instanceName") String instanceName;
    public @Deprecated @QueryParam("instanceAny") Boolean instanceAny;
    public @QueryParam("instanceDefault") Boolean instanceDefault;

    public @QueryParam("workspace") String workspace;
    public @Deprecated @QueryParam("workspaceAny") Boolean workspaceAny;
    public @QueryParam("workspaceDefault") Boolean workspaceDefault;
}

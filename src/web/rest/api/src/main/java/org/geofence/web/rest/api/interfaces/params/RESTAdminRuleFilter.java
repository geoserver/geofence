/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.interfaces.params;

import org.geofence.web.rest.api.annotations.FilterParam;

/** @author etj */
public class RESTAdminRuleFilter {

    public RESTAdminRuleFilter() {}

    public @FilterParam("userName") String userName;
    public @Deprecated @FilterParam("userAny") Boolean userAny;
    public @FilterParam("userDefault") Boolean userDefault;

    public @FilterParam("groupName") String groupName;
    public @Deprecated @FilterParam("groupAny") Boolean groupAny;
    public @FilterParam("groupDefault") Boolean groupDefault;

    public @Deprecated @FilterParam("instanceId") Long instanceId;
    public @FilterParam("instanceName") String instanceName;
    public @Deprecated @FilterParam("instanceAny") Boolean instanceAny;
    public @FilterParam("instanceDefault") Boolean instanceDefault;

    public @FilterParam("workspace") String workspace;
    public @Deprecated @FilterParam("workspaceAny") Boolean workspaceAny;
    public @FilterParam("workspaceDefault") Boolean workspaceDefault;
}

/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.interfaces.params;

import jakarta.ws.rs.QueryParam;

/** @author etj */
public class RESTRuleFilter {

    public RESTRuleFilter() {}

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

    public @QueryParam("ipAddress") String ipAddress;
    public @Deprecated @QueryParam("ipAddressAny") Boolean ipAddressAny;
    public @QueryParam("ipAddressDefault") Boolean ipAddressDefault;

    public @QueryParam("date") String date;
    public @Deprecated @QueryParam("dateAny") Boolean dateAny;
    public @QueryParam("dateDefault") Boolean dateDefault;

    public @QueryParam("service") String serviceName;
    public @Deprecated @QueryParam("serviceAny") Boolean serviceAny;
    public @QueryParam("serviceDefault") Boolean serviceDefault;

    public @QueryParam("request") String requestName;
    public @Deprecated @QueryParam("requestAny") Boolean requestAny;
    public @QueryParam("requestDefault") Boolean requestDefault;

    public @QueryParam("subfield") String subfieldName;
    public @Deprecated @QueryParam("subfieldAny") Boolean subfieldAny;
    public @QueryParam("subfieldDefault") Boolean subfieldDefault;

    public @QueryParam("workspace") String workspace;
    public @Deprecated @QueryParam("workspaceAny") Boolean workspaceAny;
    public @QueryParam("workspaceDefault") Boolean workspaceDefault;

    public @QueryParam("layer") String layer;
    public @Deprecated @QueryParam("layerAny") Boolean layerAny;
    public @QueryParam("layerDefault") Boolean layerDefault;
}

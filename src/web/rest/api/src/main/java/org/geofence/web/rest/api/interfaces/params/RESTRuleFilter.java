/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.interfaces.params;

import org.geofence.web.rest.api.annotations.FilterParam;

/** @author etj */
public class RESTRuleFilter {

    public RESTRuleFilter() {}

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

    public @FilterParam("ipAddress") String ipAddress;
    public @Deprecated @FilterParam("ipAddressAny") Boolean ipAddressAny;
    public @FilterParam("ipAddressDefault") Boolean ipAddressDefault;

    public @FilterParam("date") String date;
    public @Deprecated @FilterParam("dateAny") Boolean dateAny;
    public @FilterParam("dateDefault") Boolean dateDefault;

    public @FilterParam("service") String serviceName;
    public @Deprecated @FilterParam("serviceAny") Boolean serviceAny;
    public @FilterParam("serviceDefault") Boolean serviceDefault;

    public @FilterParam("request") String requestName;
    public @Deprecated @FilterParam("requestAny") Boolean requestAny;
    public @FilterParam("requestDefault") Boolean requestDefault;

    public @FilterParam("subfield") String subfieldName;
    public @Deprecated @FilterParam("subfieldAny") Boolean subfieldAny;
    public @FilterParam("subfieldDefault") Boolean subfieldDefault;

    public @FilterParam("workspace") String workspace;
    public @Deprecated @FilterParam("workspaceAny") Boolean workspaceAny;
    public @FilterParam("workspaceDefault") Boolean workspaceDefault;

    public @FilterParam("layer") String layer;
    public @Deprecated @FilterParam("layerAny") Boolean layerAny;
    public @FilterParam("layerDefault") Boolean layerDefault;
}

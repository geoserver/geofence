/* (c) 2014, 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model.config;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.Date;
import org.geofence.web.rest.api.model.enums.RESTGrantType;

@XmlRootElement(name = "Rule")
@XmlType(
        propOrder = {
            "id",
            "priority",
            "username",
            "rolename",
            "instance",
            "addressRange",
            "validAfter",
            "validBefore",
            "service",
            "request",
            "workspace",
            "layer",
            "access",
            "layerDetails",
            "ruleLimits"
        })
public class RESTRule {

    private Long id;
    private long priority;
    private String username;
    private String rolename;
    private String instance;
    private String service;
    private String addressRange;
    private Date validAfter;
    private Date validBefore;
    private String request;
    private String subfield;
    private String workspace;
    private String layer;
    private RESTGrantType access;
    // private LayerDetails layerDetails; // TODO
    // private RuleLimits ruleLimits; // TODO

    public RESTRule() {}

    public RESTRule(long priority, RESTGrantType access) {
        this.priority = priority;
        this.access = access;
    }

    public RESTRule(
            long priority,
            String username,
            String rolename,
            String instance,
            String addressRange,
            Date validAfter,
            Date validBefore,
            String service,
            String request,
            String subfield,
            String workspace,
            String layer,
            RESTGrantType access) {
        this.priority = priority;
        this.username = username;
        this.rolename = rolename;
        this.instance = instance;
        this.addressRange = addressRange;
        this.validAfter = validAfter;
        this.validBefore = validBefore;
        this.service = service;
        this.request = request;
        this.subfield = subfield;
        this.workspace = workspace;
        this.layer = layer;
        this.access = access;
    }

    public RESTRule(
            long priority,
            String username,
            String rolename,
            String instance,
            String addressRange,
            String service,
            String request,
            String subfield,
            String workspace,
            String layer,
            RESTGrantType access) {
        this(
                priority,
                username,
                rolename,
                instance,
                addressRange,
                null,
                null,
                service,
                request,
                subfield,
                workspace,
                layer,
                access);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddressRange() {
        return addressRange;
    }

    public RESTRule setAddressRange(String cidr) {
        this.addressRange = cidr;
        return this;
    }

    public Date getValidAfter() {
        return validAfter;
    }

    public String getValidAfterString() {
        return validAfter == null ? null : validAfter.toString();
    }

    public RESTRule setValidAfter(Date validAfter) {
        this.validAfter = validAfter;
        return this;
    }

    public Date getValidBefore() {
        return validBefore;
    }

    public String getValidBeforeString() {
        return validBefore == null ? null : validBefore.toString();
    }

    public RESTRule setValidBefore(Date validBefore) {
        this.validBefore = validBefore;
        return this;
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public String getUsername() {
        return username;
    }

    public RESTRule setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getRolename() {
        return rolename;
    }

    public RESTRule setRolename(String rolename) {
        this.rolename = rolename;
        return this;
    }

    public String getInstance() {
        return instance;
    }

    public RESTRule setInstance(String instance) {
        this.instance = instance;
        return this;
    }

    public String getLayer() {
        return layer;
    }

    public RESTRule setLayer(String layer) {
        this.layer = layer;
        return this;
    }

    public String getService() {
        return service;
    }

    public RESTRule setService(String service) {
        this.service = service;
        return this;
    }

    public String getRequest() {
        return request;
    }

    public RESTRule setRequest(String request) {
        this.request = request;
        return this;
    }

    public String getSubfield() {
        return subfield;
    }

    public RESTRule setSubfield(String subfield) {
        this.subfield = subfield;
        return this;
    }

    public String getWorkspace() {
        return workspace;
    }

    public RESTRule setWorkspace(String workspace) {
        this.workspace = workspace;
        return this;
    }

    public RESTGrantType getAccess() {
        return access;
    }

    public RESTRule setAccess(RESTGrantType access) {
        this.access = access;
        return this;
    }

    // TODO
    //    public RuleLimits getRuleLimits() {
    //        return ruleLimits;
    //    }
    //
    //    public void setRuleLimits(RuleLimits ruleLimits) {
    //        if (this.ruleLimits != null) {
    //            // disassociate old limits to be automatically deleted
    //            this.ruleLimits.rule = null;
    //        }
    //        this.ruleLimits = ruleLimits;
    //        if (ruleLimits != null) {
    //            ruleLimits.rule = this;
    //        }
    //    }

    // TODO
    //    public LayerDetails getLayerDetails() {
    //        return layerDetails;
    //    }
    //
    //    public void setLayerDetails(LayerDetails layerDetails) {
    //        if (this.layerDetails != null) {
    //            // disassociate old limits to be automatically deleted
    //            this.layerDetails.rule = null;
    //        }
    //        this.layerDetails = layerDetails;
    //        if (layerDetails != null) {
    //            layerDetails.rule = this;
    //        }
    //    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName())
                .append("[id:")
                .append(id)
                .append(" pri:")
                .append(priority);

        _sbappend(sb, "user", username);
        _sbappend(sb, "role", rolename);

        if (instance != null) {
            sb.append(" iName:").append(instance);
        }
        _sbappend(sb, "addr", addressRange);
        _sbappend(sb, "aft", validAfter);
        _sbappend(sb, "bfr", validBefore);
        _sbappend(sb, "srv", service);
        _sbappend(sb, "req", request);
        _sbappend(sb, "sub", subfield);
        _sbappend(sb, "ws", workspace);
        _sbappend(sb, "l", layer);

        sb.append(" acc:").append(access);
        sb.append(']');

        return sb.toString();
    }

    private static void _sbappend(StringBuilder sb, String label, Object value) {
        if (value != null) {
            sb.append(" ").append(label).append(":").append(value);
        }
    }
}

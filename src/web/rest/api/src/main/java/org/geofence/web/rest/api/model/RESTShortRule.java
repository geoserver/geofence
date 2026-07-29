/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geofence.web.rest.api.model.enums.RESTGrantType;

/**
 * A compact representation of a matching Rule, as returned by the runtime rule-evaluation API.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "ShortRule")
@XmlType(
        propOrder = {
            "id",
            "priority",
            "userName",
            "roleName",
            "instanceId",
            "instanceName",
            "addressRange",
            "validAfter",
            "validBefore",
            "service",
            "request",
            "subfield",
            "workspace",
            "layer",
            "access"
        })
public class RESTShortRule {

    private Long id;
    private Long priority;
    private String userName;
    private String roleName;
    private Long instanceId;
    private String instanceName;
    private String addressRange;
    private String validAfter;
    private String validBefore;
    private String service;
    private String request;
    private String subfield;
    private String workspace;
    private String layer;
    private RESTGrantType access;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getAddressRange() {
        return addressRange;
    }

    public void setAddressRange(String addressRange) {
        this.addressRange = addressRange;
    }

    public String getValidAfter() {
        return validAfter;
    }

    public void setValidAfter(String validAfter) {
        this.validAfter = validAfter;
    }

    public String getValidBefore() {
        return validBefore;
    }

    public void setValidBefore(String validBefore) {
        this.validBefore = validBefore;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getSubfield() {
        return subfield;
    }

    public void setSubfield(String subfield) {
        this.subfield = subfield;
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public RESTGrantType getAccess() {
        return access;
    }

    public void setAccess(RESTGrantType access) {
        this.access = access;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id:" + id + " pri:" + priority + " acc:" + access + ']';
    }
}

/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.services.rest.model.util.IdName;

/**
 * A compact representation of Rule
 *
 * @author Etj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "rule")
@XmlType(
    name = "Rule",
    propOrder = {
        "position",
        "grant",
        "username",
        "rolename",
        "instance",
        "ipaddress",
        "service",
        "request",
        "workspace",
        "layer",
        "constraints"
    }
)
public class RESTInputRule extends AbstractRESTPayload {

    private RESTRulePosition position;

    private String username;
    private String rolename;

    private IdName instance;

    private String ipaddress;

    private String service;
    private String request;

    private String workspace;
    private String layer;

    private GrantType grant;

    private RESTLayerConstraints constraints;

    public RESTInputRule() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public void setInstanceId(Long id) {
        instance = new IdName(id);
    }

    public void setInstanceName(String name) {
        instance = new IdName(name);
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public void setInstance(IdName instance) {
        this.instance = instance;
    }

    public IdName getInstance() {
        return instance;
    }

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public RESTLayerConstraints getConstraints() {
        return constraints;
    }

    public void setConstraints(RESTLayerConstraints constraints) {
        this.constraints = constraints;
    }

    public RESTRulePosition getPosition() {
        return position;
    }

    public void setPosition(RESTRulePosition position) {
        this.position = position;
    }

    @XmlAttribute
    public GrantType getGrant() {
        return grant;
    }

    public void setGrant(GrantType grant) {
        this.grant = grant;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());

        sb.append('[').append(grant);
        if (position != null && position.getPosition() != null) {
            if (position.getPosition() == RESTRulePosition.RulePosition.fixedPriority)
                sb.append('=');
            else if (position.getPosition() == RESTRulePosition.RulePosition.offsetFromTop)
                sb.append('+');
            else if (position.getPosition() == RESTRulePosition.RulePosition.offsetFromBottom)
                sb.append('-');
            sb.append(position.getValue());
        }

        if (rolename != null) {
            sb.append(" rolename:").append(rolename);
        }
        if (username != null) {
            sb.append(" username:").append(username);
        }
        if (instance != null) {
            sb.append(" instance:").append(instance);
        }
        if (ipaddress != null) {
            sb.append(" ipaddr:").append(ipaddress);
        }
        if (service != null) {
            sb.append(" service:").append(service);
        }
        if (request != null) {
            sb.append(" request:").append(request);
        }
        if (workspace != null) {
            sb.append(" workspace:").append(workspace);
        }
        if (layer != null) {
            sb.append(" layer:").append(layer);
        }
        sb.append(']');

        return sb.toString();
    }
}

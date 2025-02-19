/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;

import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.services.rest.model.util.IdName;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A compact representation of Rule
 *
 * @author Etj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "rule")
@XmlType(name="Rule", propOrder={"position","grant","username","rolename","instance","ipaddress","validafter","validbefore","service","request","subfield","workspace","layer","constraints"})
public class RESTInputRule extends AbstractRESTPayload {

    private RESTRulePosition position;

    private String username;
    private String rolename;

    private IdName instance;

    private String ipaddress;
    
    private String validafter;
    private String validbefore;

    private String service;
    private String request;

    private String subfield;

    private String workspace;
    private String layer;

    private GrantType grant;

    private RESTLayerConstraints constraints;

    public RESTInputRule() {
    }

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

    public String getIpaddress()
    {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress)
    {
        this.ipaddress = ipaddress;
    }

    public String getValidafter() {
        return validafter;
    }

    public void setValidafter(String validafter) {
        this.validafter = validafter;
    }

    public String getValidbefore() {
        return validbefore;
    }

    public void setValidbefore(String validbefore) {
        this.validbefore = validbefore;
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

    public String getSubfield() {
       return subfield;
    }

    public void setSubfield(String subfield) {
       this.subfield = subfield;
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
        if(position != null && position.getPosition() != null) {
            switch (position.getPosition()) {
                case fixedPriority:
                    sb.append('=');
                    break;
                case offsetFromTop:
                    sb.append('+');
                    break;
                case offsetFromBottom:
                    sb.append('-');
                    break;
                default:
                    break;
            }
            sb.append(position.getValue());
        }

        _sbappend(sb, "role", rolename);
        _sbappend(sb, "user", username);
        _sbappend(sb, "instance", instance);
        _sbappend(sb, "ipaddr", ipaddress);
        _sbappend(sb, "after", validafter);
        _sbappend(sb, "before", validbefore);
        _sbappend(sb, "service", service);
        _sbappend(sb, "request", request);
        _sbappend(sb, "sub", subfield);
        _sbappend(sb, "workspace", workspace);
        _sbappend(sb, "layer", layer);
        
        sb.append(']');

        return sb.toString();
    }
    
    private static void _sbappend(StringBuilder sb, String label, Object value) {
        if (value != null) {
            sb.append(" ").append(label).append(":").append(value);
        }    
    }    

}

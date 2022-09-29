/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;

import org.geoserver.geofence.core.model.enums.GrantType;
import org.geoserver.geofence.services.rest.model.util.IdName;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A compact representation of Rule
 *
 * @author Etj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "Rule")
@XmlType(propOrder={"id", "priority","grant","username","rolename","instance","ipaddress","service","request","subfield","workspace","layer","constraints"})
public class RESTOutputRule implements Serializable {

    private Long id;

    private Long priority;

    private String username;
    private String rolename;
    private IdName instance;

    private String ipaddress;

    private String service;
    private String request;
    
    private String subfield;

    private String workspace;
    private String layer;

    private GrantType grant;

    private RESTLayerConstraints constraints;

    public RESTOutputRule() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setInstance(IdName instance) {
        this.instance = instance;
    }

    public IdName getInstance() {
        return instance;
    }

    public String getIpaddress()
    {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress)
    {
        this.ipaddress = ipaddress;
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

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
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
        StringBuilder sb = new StringBuilder(getClass().getSimpleName())
                .append("[id:").append(id)
                .append(" pri:").append(priority);

        if (username != null) {
            sb.append(" user:").append(username);
        }
        if (rolename != null) {
            sb.append(" role:").append(rolename);
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
        if (subfield != null) {
            sb.append(" sub:").append(subfield);
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

    //=========================================================================
    
}

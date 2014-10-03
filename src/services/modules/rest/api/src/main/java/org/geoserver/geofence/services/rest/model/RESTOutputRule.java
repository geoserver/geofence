/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
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
@XmlType(propOrder={"id", "priority","grant","user","group","instance","service","request","workspace","layer","constraints"})
public class RESTOutputRule implements Serializable {

    private Long id;

    private Long priority;

    private IdName user;
    private IdName group;
    private IdName instance;

    private String service;
    private String request;

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

    public void setGroup(IdName group) {
        this.group = group;
    }

    public void setInstance(IdName instance) {
        this.instance = instance;
    }

    public void setUser(IdName user) {
        this.user = user;
    }

    public IdName getGroup() {
        return group;
    }

    public IdName getInstance() {
        return instance;
    }

    public IdName getUser() {
        return user;
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

        if (user != null) {
            sb.append(" user:").append(user);
        }
        if (group != null) {
            sb.append(" group:").append(group);
        }
        if (instance != null) {
            sb.append(" instance:").append(instance);
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

    //=========================================================================
    
}

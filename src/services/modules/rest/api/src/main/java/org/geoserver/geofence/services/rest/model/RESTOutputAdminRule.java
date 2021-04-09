/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geoserver.geofence.core.model.enums.AdminGrantType;
import org.geoserver.geofence.services.rest.model.util.IdName;

/**
 * A compact representation of AdminRule
 *
 * @author Etj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "Rule")
@XmlType(propOrder = {"id", "priority", "grant", "username", "rolename", "instance", "workspace"})
public class RESTOutputAdminRule implements Serializable {

    private Long id;

    private Long priority;

    private String username;
    private String rolename;
    private IdName instance;

    private String workspace;

    private AdminGrantType grant;

    public RESTOutputAdminRule() {}

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

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    @XmlAttribute
    public AdminGrantType getGrant() {
        return grant;
    }

    public void setGrant(AdminGrantType grant) {
        this.grant = grant;
    }

    @Override
    public String toString() {
        StringBuilder sb =
                new StringBuilder(getClass().getSimpleName())
                        .append("[id:")
                        .append(id)
                        .append(" pri:")
                        .append(priority);

        if (username != null) {
            sb.append(" user:").append(username);
        }
        if (rolename != null) {
            sb.append(" role:").append(rolename);
        }
        if (instance != null) {
            sb.append(" instance:").append(instance);
        }
        if (workspace != null) {
            sb.append(" workspace:").append(workspace);
        }

        sb.append(" grant:").append(grant);

        sb.append(']');

        return sb.toString();
    }

    // =========================================================================

}

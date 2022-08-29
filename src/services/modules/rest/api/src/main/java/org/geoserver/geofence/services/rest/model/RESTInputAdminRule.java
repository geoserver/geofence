/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;

import org.geoserver.geofence.core.model.enums.AdminGrantType;
import org.geoserver.geofence.services.rest.model.util.IdName;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A compact representation of AdminRule
 *
 * @author Etj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "adminrule")
@XmlType(name="AminRule", propOrder={"position","grant","username","rolename","instance","workspace"})
public class RESTInputAdminRule extends AbstractRESTPayload
{
    private RESTRulePosition position;

    private String username;
    private String rolename;
    private IdName instance;
    private String workspace;

    private AdminGrantType grant;


    public RESTInputAdminRule() {
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

    public RESTRulePosition getPosition() {
        return position;
    }

    public void setPosition(RESTRulePosition position) {
        this.position = position;
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
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());

        sb.append('[').append(grant);
        if(position != null && position.getPosition() != null) {
            if(position.getPosition() == RESTRulePosition.RulePosition.fixedPriority)
                sb.append('=');
            else if(position.getPosition() == RESTRulePosition.RulePosition.offsetFromTop)
                sb.append('+');
            else if(position.getPosition() == RESTRulePosition.RulePosition.offsetFromBottom)
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
        if (workspace != null) {
            sb.append(" workspace:").append(workspace);
        }
        sb.append(']');

        return sb.toString();
    }

}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "GeofenceConfiguration")
@XmlType(propOrder = {"userGroupList", "userList", "grUserList", "gsInstanceList", "ruleList"})
public class RESTFullConfiguration {

    private RESTFullUserGroupList userGroupList;
    private RESTFullUserList userList;
    private RESTFullGRUserList grUserList;
    private RESTFullGSInstanceList gsInstanceList;
    private RESTFullRuleList ruleList;

    public RESTFullConfiguration() {
    }

    @XmlElement(name = "GSInstances")
    public RESTFullGSInstanceList getGsInstanceList() {
        return gsInstanceList;
    }

    public void setGsInstanceList(RESTFullGSInstanceList gsInstanceList) {
        this.gsInstanceList = gsInstanceList;
    }

    @XmlElement(name = "UserGroups")
    public RESTFullUserGroupList getUserGroupList() {
        return userGroupList;
    }

    public void setUserGroupList(RESTFullUserGroupList profileList) {
        this.userGroupList = profileList;
    }

    @XmlElement(name = "Rules")
    public RESTFullRuleList getRuleList() {
        return ruleList;
    }

    public void setRuleList(RESTFullRuleList ruleList) {
        this.ruleList = ruleList;
    }

    @XmlElement(name = "Users")
    public RESTFullUserList getUserList() {
        return userList;
    }

    public void setUserList(RESTFullUserList userList) {
        this.userList = userList;
    }

    @XmlElement(name = "InternalUsers")
    public RESTFullGRUserList getGrUserList() {
        return grUserList;
    }

    public void setGrUserList(RESTFullGRUserList grUserList) {
        this.grUserList = grUserList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append('[');
        if ( userGroupList != null ) {
            sb.append(userGroupList);
        }
        if ( userList != null ) {
            sb.append(", ").append(userList);
        }
        if ( gsInstanceList != null ) {
            sb.append(", ").append(gsInstanceList);
        }
        if ( ruleList != null ) {
            sb.append(", ").append(ruleList);
        }
        if ( (grUserList != null) && (grUserList.getList() != null) ) {
            sb.append(", ").append(grUserList.getList().size()).append(" internal users");
        }
        sb.append(']');

        return sb.toString();
    }
}

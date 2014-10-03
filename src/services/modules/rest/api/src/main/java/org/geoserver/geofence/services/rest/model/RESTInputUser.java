/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;

import org.geoserver.geofence.services.rest.model.util.IdName;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "user")
@XmlType(name="User", propOrder = {"extId", "name", "password", "fullName", "emailAddress", "groups"})
public class RESTInputUser extends AbstractRESTPayload {

    private String extId;
    private String name;
    private String password;
    private String fullName;
    private String emailAddress;
    private Boolean enabled;
    private Boolean admin;
    private List<IdName> groups;

//    @XmlAttribute(name = "extid")
    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    @XmlAttribute(name = "admin")
    public Boolean isAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @XmlAttribute(name = "enabled")
    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @XmlElementWrapper(name = "groups")
    @XmlElement(name = "group")
    public List<IdName> getGroups() {
        return groups;
    }

    public void setGroups(List<IdName> groups) {
        this.groups = groups;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    @Override
//    public String toString()
//    {
//        return this.getClass().getSimpleName() +
//            '[' +
//            "sguId=" + extId +
//            " userName=" + userName +
//            " profile=" + profile +
//            (enabled ? " enabled" : " disabled") +
//            ']';
//    }
}

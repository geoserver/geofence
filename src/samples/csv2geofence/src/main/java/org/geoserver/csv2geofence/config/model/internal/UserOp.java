/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.csv2geofence.config.model.internal;

import java.util.List;

/**
 * Represent a line in the user CSV file
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class UserOp {
    private OperationType type;
    String userName;
    String mailAddress;
    String fullName;

    List<String> groups;

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"["
                + type
                +" user " + userName
                + (fullName != null? " ("+fullName+") ":"")
                + (mailAddress != null? "<"+mailAddress+">":"")
                + " in groups " + groups+"]";
    }

}

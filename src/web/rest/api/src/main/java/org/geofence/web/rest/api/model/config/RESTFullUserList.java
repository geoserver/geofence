/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model.config;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/** @author ETj (etj at geo-solutions.it) */
@XmlRootElement(name = "UserList")
public class RESTFullUserList {

    private List<RESTUser> list;

    public RESTFullUserList() {
        this(10);
    }

    public RESTFullUserList(int initialCapacity) {
        list = new ArrayList<RESTUser>(initialCapacity);
    }

    @XmlElement(name = "User")
    public List<RESTUser> getList() {
        return list;
    }

    public void setList(List<RESTUser> userList) {
        this.list = userList;
    }

    public void add(RESTUser user) {
        list.add(user);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " users]";
    }
}

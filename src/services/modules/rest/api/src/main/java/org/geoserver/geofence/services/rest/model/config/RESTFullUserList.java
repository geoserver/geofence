/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model.config;

import org.geoserver.geofence.core.model.GSUser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "UserList")
public class RESTFullUserList {

    private List<GSUser> list;

    public RESTFullUserList() {
        this(10);
    }

    public RESTFullUserList(int initialCapacity) {
        list = new ArrayList<GSUser>(initialCapacity);
    }

    @XmlElement(name = "User")
    public List<GSUser> getList() {
        return list;
    }

    public void setList(List<GSUser> userList) {
        this.list = userList;
    }

    public void add(GSUser user) {
        list.add(user);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " users]";
    }
}

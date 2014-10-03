/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "UserList")
public class RESTShortUserList implements Iterable<RESTShortUser>{

    private List<RESTShortUser> list;

    public RESTShortUserList() {
        this(10);
    }

    public RESTShortUserList(int initialCapacity) {
        list = new ArrayList<RESTShortUser>(initialCapacity);
    }

    @XmlElement(name = "User")
    public List<RESTShortUser> getUserList() {
        return list;
    }

    public void setUserList(List<RESTShortUser> userList) {
        this.list = userList;
    }

    public void add(RESTShortUser user) {
        list.add(user);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " users]";
    }

    @Override
    public Iterator<RESTShortUser> iterator() {
        return list.iterator();
    }
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/** @author ETj (etj at geo-solutions.it) */
@XmlRootElement(name = "UserGroupList")
public class RESTShortUserGroupList {

    private List<RESTShortUserGroup> list;

    public RESTShortUserGroupList() {
        this(10);
    }

    public RESTShortUserGroupList(int initialCapacity) {
        list = new ArrayList<>(initialCapacity);
    }

    @XmlElement(name = "userGroup")
    public List<RESTShortUserGroup> getList() {
        return list;
    }

    public void setList(List<RESTShortUserGroup> list) {
        this.list = list;
    }

    public void add(RESTShortUserGroup group) {
        list.add(group);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " groups]";
    }
}

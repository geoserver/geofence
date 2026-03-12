/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model.config;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.geofence.web.rest.api.model.RESTOutputGroup;

/** @author ETj (etj at geo-solutions.it) */
@XmlRootElement(name = "UserGroupList")
public class RESTFullUserGroupList implements Iterable<RESTOutputGroup> {

    private List<RESTOutputGroup> list;

    public RESTFullUserGroupList() {
        this(10);
    }

    public RESTFullUserGroupList(List<RESTOutputGroup> list) {
        this.list = list;
    }

    public RESTFullUserGroupList(int initialCapacity) {
        list = new ArrayList<>(initialCapacity);
    }

    @XmlElement(name = "UserGroup")
    public List<RESTOutputGroup> getList() {
        return list;
    }

    public void setList(List<RESTOutputGroup> list) {
        this.list = list;
    }

    public void add(RESTOutputGroup group) {
        list.add(group);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " groups]";
    }

    @Override
    public Iterator<RESTOutputGroup> iterator() {
        return list.iterator();
    }
}

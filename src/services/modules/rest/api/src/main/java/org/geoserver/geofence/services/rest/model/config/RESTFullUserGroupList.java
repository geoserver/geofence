/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model.config;

import org.geoserver.geofence.services.dto.ShortGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "UserGroupList")
public class RESTFullUserGroupList implements Iterable<ShortGroup> {

    private List<ShortGroup> list;

    public RESTFullUserGroupList() {
        this(10);
    }

    public RESTFullUserGroupList(List<ShortGroup> list) {
        this.list = list;
    }

    public RESTFullUserGroupList(int initialCapacity) {
        list = new ArrayList<ShortGroup>(initialCapacity);
    }

    @XmlElement(name = "UserGroup")
    public List<ShortGroup> getList() {
        return list;
    }

    public void setList(List<ShortGroup> list) {
        this.list = list;
    }

    public void add(ShortGroup group) {
        list.add(group);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " groups]";
    }

    @Override
    public Iterator<ShortGroup> iterator() {
        return list.iterator();
    }
}

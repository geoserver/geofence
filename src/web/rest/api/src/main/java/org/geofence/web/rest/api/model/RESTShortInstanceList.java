/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** @author ETj (etj at geo-solutions.it) */
@XmlRootElement(name = "GSInstanceList")
public class RESTShortInstanceList implements Iterable<RESTShortInstance> {

    private List<RESTShortInstance> list;

    public RESTShortInstanceList() {
        this(10);
    }

    public RESTShortInstanceList(List<RESTShortInstance> list) {
        this.list = list;
    }

    public RESTShortInstanceList(int initialCapacity) {
        list = new ArrayList<>(initialCapacity);
    }

    @XmlElement(name = "Instance")
    public List<RESTShortInstance> getList() {
        return list;
    }

    public void setList(List<RESTShortInstance> list) {
        this.list = list;
    }

    public void add(RESTShortInstance instance) {
        list.add(instance);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " gs instances]";
    }

    @Override
    public Iterator<RESTShortInstance> iterator() {
        return list.iterator();
    }
}

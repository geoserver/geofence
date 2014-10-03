/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;

import org.geoserver.geofence.services.dto.ShortInstance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "GSInstanceList")
public class RESTShortInstanceList implements Iterable<ShortInstance> {

    private List<ShortInstance> list;

    public RESTShortInstanceList() {
        this(10);
    }

    public RESTShortInstanceList(List<ShortInstance> list) {
        this.list = list;
    }

    public RESTShortInstanceList(int initialCapacity) {
        list = new ArrayList<ShortInstance>(initialCapacity);
    }

    @XmlElement(name = "Instance")
    public List<ShortInstance> getList() {
        return list;
    }

    public void setList(List<ShortInstance> list) {
        this.list = list;
    }

    public void add(ShortInstance instance) {
        list.add(instance);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " gs instances]";
    }

    @Override
    public Iterator<ShortInstance> iterator() {
        return list.iterator();
    }
}

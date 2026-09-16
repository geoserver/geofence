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
@XmlRootElement(name = "GSInstanceList")
public class RESTFullGSInstanceList {

    private List<RESTInstance> list;

    public RESTFullGSInstanceList() {
        this(10);
    }

    public RESTFullGSInstanceList(int initialCapacity) {
        list = new ArrayList<>(initialCapacity);
    }

    @XmlElement(name = "GSInstance")
    public List<RESTInstance> getList() {
        return list;
    }

    public void setList(List<RESTInstance> list) {
        this.list = list;
    }

    public void add(RESTInstance gs) {
        list.add(gs);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " items]";
    }
}

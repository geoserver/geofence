/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model.config;

import org.geoserver.geofence.core.model.GSInstance;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "GSInstanceList")
public class RESTFullGSInstanceList {

    private List<GSInstance> list;

    public RESTFullGSInstanceList() {
        this(10);
    }

    public RESTFullGSInstanceList(int initialCapacity) {
        list = new ArrayList<GSInstance>(initialCapacity);
    }

    @XmlElement(name = "GSInstance")
    public List<GSInstance> getList() {
        return list;
    }

    public void setList(List<GSInstance> list) {
        this.list = list;
    }

    public void add(GSInstance gs) {
        list.add(gs);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " items]";
    }
}

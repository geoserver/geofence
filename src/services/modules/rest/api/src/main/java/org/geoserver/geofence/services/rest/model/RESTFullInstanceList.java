package org.geoserver.geofence.services.rest.model;

import org.geoserver.geofence.core.model.GSInstance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email giuseppe.lascaleia@geosdi.org
 */
@XmlRootElement(name = "GSInstanceList")
public class RESTFullInstanceList implements Iterable<GSInstance> {

    private List<GSInstance> list;

    public RESTFullInstanceList() {
        this(10);
    }

    public RESTFullInstanceList(List<GSInstance> list) {
        this.list = list;
    }

    public RESTFullInstanceList(int initialCapacity) {
        list = new ArrayList<GSInstance>(initialCapacity);
    }

    @XmlElement(name = "Instance")
    public List<GSInstance> getList() {
        return list;
    }

    public void setList(List<GSInstance> list) {
        this.list = list;
    }

    public void add(GSInstance instance) {
        list.add(instance);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " gs instances]";
    }

    @Override
    public Iterator<GSInstance> iterator() {
        return list.iterator();
    }
}
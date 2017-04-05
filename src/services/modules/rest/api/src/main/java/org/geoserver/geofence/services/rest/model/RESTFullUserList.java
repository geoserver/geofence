package org.geoserver.geofence.services.rest.model;

import org.geoserver.geofence.core.model.GSUser;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Giuseppe La Scaleia - CNR IMAA geoSDI Group
 * @email giuseppe.lascaleia@geosdi.org
 */
@XmlRootElement(name = "UserList")
public class RESTFullUserList implements Iterable<GSUser> {

    private List<GSUser> list;

    public RESTFullUserList() {
        this(10);
    }

    public RESTFullUserList(int initialCapacity) {
        list = new ArrayList<GSUser>(initialCapacity);
    }

    @XmlElement(name = "User")
    public List<GSUser> getUserList() {
        return list;
    }

    public void setUserList(List<GSUser> userList) {
        this.list = userList;
    }

    public void add(GSUser user) {
        list.add(user);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " users]";
    }

    @Override
    public Iterator<GSUser> iterator() {
        return list.iterator();
    }
}

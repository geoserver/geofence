package org.geoserver.geofence.services.rest.model;

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
public class RESTFullUserList implements Iterable<RESTFullUser> {

    private List<RESTFullUser> list;

    public RESTFullUserList() {
        this(10);
    }

    public RESTFullUserList(int initialCapacity) {
        list = new ArrayList<RESTFullUser>(initialCapacity);
    }

    @XmlElement(name = "User")
    public List<RESTFullUser> getUserList() {
        return list;
    }

    public void setUserList(List<RESTFullUser> userList) {
        this.list = userList;
    }

    public void add(RESTFullUser user) {
        list.add(user);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " users]";
    }

    @Override
    public Iterator<RESTFullUser> iterator() {
        return list.iterator();
    }
}

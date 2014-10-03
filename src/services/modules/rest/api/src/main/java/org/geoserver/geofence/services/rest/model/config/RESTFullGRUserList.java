/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model.config;

import org.geoserver.geofence.core.model.GFUser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "GRUserList")
public class RESTFullGRUserList {

    private List<GFUser> list;

    public RESTFullGRUserList() {
        this(10);
    }

    public RESTFullGRUserList(int initialCapacity) {
        list = new ArrayList<GFUser>(initialCapacity);
    }

    @XmlElement(name = "User")
    public List<GFUser> getList() {
        return list;
    }

    public void setList(List<GFUser> userList) {
        this.list = userList;
    }

    public void add(GFUser user) {
        list.add(user);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " users]";
    }
}

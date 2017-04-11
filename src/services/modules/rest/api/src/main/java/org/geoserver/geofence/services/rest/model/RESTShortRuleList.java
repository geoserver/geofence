/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;

import org.geoserver.geofence.services.dto.ShortRule;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "ShortRuleList")
public class RESTShortRuleList implements Iterable<ShortRule>{

    private List<ShortRule> list;

    public RESTShortRuleList() {
        this(10);
    }

    public RESTShortRuleList(int initialCapacity) {
        list = new ArrayList<ShortRule>(initialCapacity);
    }

    @XmlElement(name = "rule")
    public List<ShortRule> getList() {
        return list;
    }

    public void setList(List<ShortRule> userList) {
        this.list = userList;
    }

    public void add(ShortRule user) {
        list.add(user);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " users]";
    }

    @Override
    public Iterator<ShortRule> iterator() {
        return list.iterator();
    }
}

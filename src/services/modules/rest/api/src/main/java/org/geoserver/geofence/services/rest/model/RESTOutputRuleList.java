/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "RuleList")
public class RESTOutputRuleList implements Iterable<RESTOutputRule>{

    private List<RESTOutputRule> list;

    public RESTOutputRuleList() {
        this(10);
    }

    public RESTOutputRuleList(int initialCapacity) {
        list = new ArrayList<RESTOutputRule>(initialCapacity);
    }

    @XmlElement(name = "rule")
    public List<RESTOutputRule> getList() {
        return list;
    }

    public void setList(List<RESTOutputRule> userList) {
        this.list = userList;
    }

    public void add(RESTOutputRule user) {
        list.add(user);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " users]";
    }

    @Override
    public Iterator<RESTOutputRule> iterator() {
        return list.iterator();
    }
}

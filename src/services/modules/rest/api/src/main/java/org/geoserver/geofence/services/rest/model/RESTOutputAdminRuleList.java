/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
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
@XmlRootElement(name = "AdminRuleList")
public class RESTOutputAdminRuleList implements Iterable<RESTOutputAdminRule>{

    private List<RESTOutputAdminRule> list;

    public RESTOutputAdminRuleList() {
        this(10);
    }

    public RESTOutputAdminRuleList(int initialCapacity) {
        list = new ArrayList(initialCapacity);
    }

    @XmlElement(name = "adminrule")
    public List<RESTOutputAdminRule> getList() {
        return list;
    }

    public void setList(List<RESTOutputAdminRule> userList) {
        this.list = userList;
    }

    public void add(RESTOutputAdminRule rule) {
        list.add(rule);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " adminrules]";
    }

    @Override
    public Iterator<RESTOutputAdminRule> iterator() {
        return list.iterator();
    }
}

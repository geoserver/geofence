/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model.config;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** @author ETj (etj at geo-solutions.it) */
@XmlRootElement(name = "RuleList")
public class RESTFullRuleList implements Iterable<RESTRule> {

    private List<RESTRule> list;

    public RESTFullRuleList() {
        this(10);
    }

    public RESTFullRuleList(List<RESTRule> list) {
        this.list = list;
    }

    public RESTFullRuleList(int initialCapacity) {
        list = new ArrayList<>(initialCapacity);
    }

    @XmlElement(name = "Rule")
    public List<RESTRule> getList() {
        return list;
    }

    public void setList(List<RESTRule> list) {
        this.list = list;
    }

    public void add(RESTRule rule) {
        list.add(rule);
    }

    @Override
    public Iterator<RESTRule> iterator() {
        return list.iterator();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " items]";
    }
}

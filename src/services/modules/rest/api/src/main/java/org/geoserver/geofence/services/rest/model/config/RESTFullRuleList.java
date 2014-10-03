/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model.config;

import org.geoserver.geofence.core.model.Rule;

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
public class RESTFullRuleList implements Iterable<Rule> {

    private List<Rule> list;

    public RESTFullRuleList() {
        this(10);
    }

    public RESTFullRuleList(List<Rule> list) {
        this.list = list;
    }

    public RESTFullRuleList(int initialCapacity) {
        list = new ArrayList<Rule>(initialCapacity);
    }

    @XmlElement(name = "Rule")
    public List<Rule> getList() {
        return list;
    }

    public void setList(List<Rule> list) {
        this.list = list;
    }

    public void add(Rule rule) {
        list.add(rule);
    }
    
    @Override
    public Iterator<Rule> iterator() {
        return list.iterator();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " items]";
    }
}

/* (c) 2026 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** @author ETj (etj at geo-solutions.it) */
@XmlRootElement(name = "ShortRuleList")
public class RESTShortRuleList implements Iterable<RESTShortRule> {

    private List<RESTShortRule> list;

    public RESTShortRuleList() {
        this(10);
    }

    public RESTShortRuleList(int initialCapacity) {
        list = new ArrayList<>(initialCapacity);
    }

    @XmlElement(name = "ShortRule")
    public List<RESTShortRule> getRuleList() {
        return list;
    }

    public void setRuleList(List<RESTShortRule> ruleList) {
        this.list = ruleList;
    }

    public void add(RESTShortRule rule) {
        list.add(rule);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " rules]";
    }

    @Override
    public Iterator<RESTShortRule> iterator() {
        return list.iterator();
    }
}

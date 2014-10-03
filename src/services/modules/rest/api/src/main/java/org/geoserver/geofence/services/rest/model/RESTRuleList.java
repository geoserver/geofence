/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "RuleList")
public class RESTRuleList {

    private List<RESTOutputRule> list;

    public RESTRuleList() {
        this(10);
    }

    public RESTRuleList(int initialCapacity) {
        list = new ArrayList<RESTOutputRule>(initialCapacity);
    }

    @XmlElement(name = "Rule")
    public List<RESTOutputRule> getList() {
        return list;
    }

    public void setList(List<RESTOutputRule> ruleList) {
        this.list = ruleList;
    }

    public void add(RESTOutputRule rule) {
        list.add(rule);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " rules]";
    }
}

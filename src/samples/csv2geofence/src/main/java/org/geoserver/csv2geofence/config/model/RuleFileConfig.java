/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.csv2geofence.config.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RuleFileConfig {
    private String fieldSeparator = ",";

    private int layerNameIndex;
    private String validLayernameRegEx = ".*";

    private int offsetFromBottom = 0;

    private List<Group> groups = new ArrayList<Group>();

    private Map<String,List<ServiceRequest>> ruleMapping = new HashMap<String, List<ServiceRequest>>();

    public String getFieldSeparator() {
        return fieldSeparator;
    }

    public void setFieldSeparator(String fieldSeparator) {
        this.fieldSeparator = fieldSeparator.substring(0,1);
    }

    public int getLayerNameIndex() {
        return layerNameIndex;
    }

    public void setLayerNameIndex(int layerNameIndex) {
        this.layerNameIndex = layerNameIndex;
    }

    public String getValidLayernameRegEx() {
        return validLayernameRegEx;
    }

    public void setValidLayernameRegEx(String validLayernameRegEx) {
        this.validLayernameRegEx = validLayernameRegEx;
    }

    public int getOffsetFromBottom() {
        return offsetFromBottom;
    }

    public void setOffsetFromBottom(int offsetFromBottom) {
        this.offsetFromBottom = offsetFromBottom;
    }


    @XmlElement(name="group")
    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    @XmlJavaTypeAdapter(RuleFileConfig.ServiceRequest.Adapter.class)
    public Map<String, List<ServiceRequest>> getRuleMapping() {
        return ruleMapping;
    }

    public void setRuleMapping(Map<String, List<ServiceRequest>> mapping) {
        this.ruleMapping = mapping;
    }


    public void addServiceMapping(String s, ServiceRequest serviceRequest) {
        List<ServiceRequest> srl = ruleMapping.get(s);
        if(srl == null) {
            srl = new ArrayList<ServiceRequest>();
            ruleMapping.put(s, srl);
        }
        srl.add(serviceRequest);
    }

    @XmlType(propOrder={"service","request","grant"})
    public static class ServiceRequest {

        public static enum Type {
            allow, deny;
        }

        private String service;
        private String request;
        private Type   grant;

        public ServiceRequest() {
        }

        public ServiceRequest(String service, String request, Type type) {
            this.service = service;
            this.request = request;
            this.grant = type;
        }

        @XmlAttribute(required=true)
        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        @XmlAttribute
        public String getRequest() {
            return request;
        }

        public void setRequest(String request) {
            this.request = request;
        }

        @XmlAttribute(required=true)
        public Type getGrant() {
            return grant;
        }

        public void setGrant(Type type) {
            this.grant = type;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName()+"["
                + (service != null?  "service=" + service : "")
                + (request != null? " request=" + request : "")
                +  " grant=" + grant + ']';
        }



        public static class Adapter extends XmlAdapter<MyMap, Map<String, List<ServiceRequest>>> {

            @Override
            public HashMap<String, List<ServiceRequest>> unmarshal(MyMap v) throws Exception {
                HashMap<String, List<ServiceRequest>> ret = new HashMap<String, List<ServiceRequest>>();
                for (MyMap.MyEntry entry : v.getEntries()) {
                    List<ServiceRequest> val = entry.getList();
                    if(val==null)
                        val = new ArrayList<ServiceRequest>();
                    ret.put(entry.getKey(), val);
                }
                return ret;
            }

            @Override
            public MyMap marshal(Map<String, List<ServiceRequest>> v) throws Exception {
                MyMap ret = new MyMap();
                for (Map.Entry<String, List<ServiceRequest>> entry : v.entrySet()) {
                    ret.addEntry(new MyMap.MyEntry(entry.getKey(), entry.getValue()));
                }
                return ret;
            }
        }

        public static class MyMap {

            private List<MyEntry> entries = new ArrayList<MyEntry>();

            @XmlElement(name="mapping")
            public List<MyEntry> getEntries() {
                return entries;
            }

            public void setEntries(List<MyEntry> entries) {
                this.entries = entries;
            }

            public void addEntry(MyEntry entry) {
                entries.add(entry);
            }

            public static class MyEntry {
                String key;
                List<ServiceRequest> list;

                public MyEntry() {
                }

                public MyEntry(String key, List<ServiceRequest> list) {
                    this.key = key;
                    this.list = list;
                }

                @XmlAttribute
                public String getKey() {
                    return key;
                }

                public void setKey(String key) {
                    this.key = key;
                }

                @XmlElement(name="rule")
                public List<ServiceRequest> getList() {
                    return list;
                }

                public void setList(List<ServiceRequest> list) {
                    this.list = list;
                }                
            }
        }

    }

    public static class Group {
        private int index;

        public Group() {
        }

        public Group(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }


    }


}

package org.geoserver.geofence.services.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author etj
 */
@XmlRootElement(name = "PermsResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class PermsResult implements Serializable {
    
    private String cqlFilter;

    @XmlElement(name = "resource")
    private SortedSet<String> accessibleResources = new TreeSet<String>();

    public PermsResult() {
    }    

    public PermsResult(String cqlFilter, Map<String, Set<String>> resourcesMap) {
        this.cqlFilter = cqlFilter;
        addAccessibleResources(resourcesMap);
    }    

    public String getCqlFilter() { return cqlFilter; }
    public void setCqlFilter(String cqlFilter) {
        this.cqlFilter = cqlFilter;
    }

    public SortedSet<String> getAccessibleResources() {
        return accessibleResources;
    }

    public void setAccessibleResources(SortedSet<String> accessibleResources) {
        this.accessibleResources = accessibleResources;
    }        
    
    
    public void addAccessibleResources(Map<String, Set<String>> resourcesMap) {
        if (resourcesMap == null || resourcesMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Set<String>> entry : resourcesMap.entrySet()) {
            String ws = entry.getKey();
            for(String layer: entry.getValue()) {
                this.accessibleResources.add(ws+":"+layer);
            }
        }
    }
                 
}
/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model.util;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "identifier")
@XmlType(propOrder={"id", "name"})
public class IdName {
    private  String name;
    private  Long id;

    protected IdName() {
    }

    public IdName(Long id, String name) {
        this.name = name;
        this.id = id;
    }

    public IdName(String name) {
        setName(name);
    }

    public IdName(Long id) {
        setId(id);
    }

    @XmlElement
    public Long getId() {
        return id;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
        this.name = null;
    }

    public void setName(String name) {
        this.name = name;
        this.id=null;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
//        sb.append(getClass().getSimpleName()).append('[');
        sb.append('[');
        if(id != null)
            sb.append("id:").append(id);
        if(name != null)
            sb.append("name:").append(name);
        sb.append(']');
        return sb.toString();
    }
}

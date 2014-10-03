/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.dto;

import org.geoserver.geofence.core.model.GSInstance;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A compact representation of GSInstance useful in lists.
 *
 * @author Etj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "GSInstance")
@XmlType(propOrder = {"id", "name", "url", "description"})
public class ShortInstance implements Serializable {

    private long id;
    private String name;
    private String url;
    private String description;

    public ShortInstance() {
    }

    public ShortInstance(GSInstance i) {
        this.id = i.getId();
        this.name = i.getName();
        this.url = i.getBaseURL();
        this.description = i.getDescription();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "[id:" + id
                + " name:" + name
                + " url:" + url
                + ']';
    }
}

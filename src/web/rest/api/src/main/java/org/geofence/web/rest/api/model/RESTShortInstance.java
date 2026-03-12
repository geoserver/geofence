/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * A compact representation of GSInstance useful in lists.
 *
 * @author Etj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "GSInstance")
@XmlType(propOrder = {"id", "name", "url", "description"})
public class RESTShortInstance implements Serializable {

    private long id;
    private String name;
    private String url;
    private String description;

    public RESTShortInstance() {}

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
        return getClass().getSimpleName() + "[id:" + id + " name:" + name + " url:" + url + ']';
    }
}

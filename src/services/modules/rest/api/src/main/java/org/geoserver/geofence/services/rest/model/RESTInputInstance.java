/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;


import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A compact representation of UserGroup holding only the insertable/updatadable fields
 *
 * @author Etj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "instance")
@XmlType(name="Instance", propOrder={"name","description","baseURL","username","password"})
public class RESTInputInstance extends AbstractRESTPayload {

    private String name;
    private String description;

    private String baseURL;
    private String username;
    private String password;

    public RESTInputInstance() {
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "[name:" + name
                + (username!=null? " user:" + username : "")
                + (password!=null? " pw" : "")
                + (baseURL!=null? " url:" + baseURL : "")
                + ']';
    }
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "Operation")
@XmlType(propOrder={"payload"})
public class RESTBatchOperation {

    public enum ServiceName {
        users,
        groups,
        instances,
        rules
    }

    public enum TypeName {
        insert,
        update,
        delete,
        addGroup,
        delgroup
    }

    private ServiceName service;
    private TypeName type;
    private Long id;
    private String name;
    private Boolean cascade;

    /** used in group reassign. */
    private Long userId;
    /** used in group reassign. */
    private String userName;
    /** used in group reassign. */
    private Long groupId;
    /** used in group reassign. */
    private String groupName;

    private AbstractRESTPayload payload;

    public RESTBatchOperation() {
    }

    @XmlAttribute
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public ServiceName getService() {
        return service;
    }

    public void setService(ServiceName service) {
        this.service = service;
    }

    @XmlAttribute
    public TypeName getType() {
        return type;
    }

    public void setType(TypeName type) {
        this.type = type;
    }

    @XmlAttribute
    public Boolean getCascade() {
        return cascade;
    }

    public void setCascade(Boolean cascade) {
        this.cascade = cascade;
    }

    @XmlElements({
        @XmlElement(name="user",        type=RESTInputUser.class),
        @XmlElement(name="userGroup",   type=RESTInputGroup.class),
        @XmlElement(name="instance",    type=RESTInputInstance.class),
        @XmlElement(name="rule",        type=RESTInputRule.class)
    })
    public AbstractRESTPayload getPayload() {
        return payload;
    }

    public void setPayload(AbstractRESTPayload payload) {
        this.payload = payload;
    }

    @XmlAttribute
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @XmlAttribute
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @XmlAttribute
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @XmlAttribute
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }



    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" 
                + service + "." + type
                + (id!=null? " id:"+id : "")
                + (name != null? " name:"+name:"")
                + (cascade != null? " cascade:"+cascade:"")
                + (payload!=null? " payload is a " + payload.getClass().getSimpleName() : "")
                + (userId!=null?    " uId:"+userId : "")
                + (userName!=null?  " uName:"+userName : "")
                + (groupId!=null?   " gId:"+groupId : "")
                + (groupName!=null? " gName:"+groupName : "")
                + "]";
    }
}

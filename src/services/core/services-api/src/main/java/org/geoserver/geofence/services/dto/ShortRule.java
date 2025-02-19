/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.dto;

import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.core.model.enums.GrantType;

import java.io.Serializable;


/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class ShortRule implements Serializable
{

    private static final long serialVersionUID = 3800101015688510864L;

    private Long id;
    private Long priority;

    private String userName;

    private String roleName;

    private Long instanceId;
    private String instanceName;

    private String addressRange;
    private String validAfter;
    private String validBefore;

    private String service;
    private String request;
    private String subfield;
    private String workspace;
    private String layer;

    private GrantType access;

    public ShortRule()
    {
    }

    public ShortRule(Rule rule)
    {
        setId(rule.getId());
        setPriority(rule.getPriority());
        setUserName(rule.getUsername());
        setRoleName(rule.getRolename());

        if (rule.getInstance() != null) 
        {
            setInstanceId(rule.getInstance().getId());
            setInstanceName(rule.getInstance().getName());
        }

        setService(rule.getService());
        setAddressRange(rule.getAddressRangeString());
        setValidAfter(rule.getValidAfterString());
        setValidBefore(rule.getValidBeforeString());        
        setRequest(rule.getRequest());
        setSubfield(rule.getSubfield());
        setWorkspace(rule.getWorkspace());
        setLayer(rule.getLayer());
        setAccess(rule.getAccess());        
    }

    public GrantType getAccess()
    {
        return access;
    }

    public void setAccess(GrantType access)
    {
        this.access = access;
    }

    public String getAddressRange() 
    {
        return addressRange;
    }

    public void setAddressRange(String ipAddressRange) 
    {
        this.addressRange = ipAddressRange;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(Long instanceId)
    {
        this.instanceId = instanceId;
    }

    public String getInstanceName()
    {
        return instanceName;
    }

    public void setInstanceName(String instanceName)
    {
        this.instanceName = instanceName;
    }

    public String getLayer()
    {
        return layer;
    }

    public void setLayer(String layer)
    {
        this.layer = layer;
    }

    public long getPriority()
    {
        return priority;
    }

    public void setPriority(long priority)
    {
        this.priority = priority;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRequest()
    {
        return request;
    }

    public void setRequest(String request)
    {
        this.request = request;
    }

    public String getSubfield() {
        return subfield;
    }

    public void setSubfield(String subfield) {
        this.subfield = subfield;
    }

    public String getService()
    {
        return service;
    }

    public void setService(String service)
    {
        this.service = service;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getValidAfter() {
        return validAfter;
    }

    public void setValidAfter(String validAfter) {
        this.validAfter = validAfter;
    }

    public String getValidBefore() {
        return validBefore;
    }

    public void setValidBefore(String validBefore) {
        this.validBefore = validBefore;
    }
    
    public String getWorkspace()
    {
        return workspace;
    }

    public void setWorkspace(String workspace)
    {
        this.workspace = workspace;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName())
                .append("[id:").append(id)
                .append(" pri:").append(priority);

        _sbappend(sb, "user", userName);
        _sbappend(sb, "role", roleName);
        _sbappend(sb, "iId", instanceId);
        _sbappend(sb, "iName", instanceName);
        _sbappend(sb, "ip", addressRange);
        _sbappend(sb, "aft", validAfter);
        _sbappend(sb, "bfr", validBefore);
        _sbappend(sb, "srv", service);
        _sbappend(sb, "req", request);
        _sbappend(sb, "sub", subfield);
        _sbappend(sb, "ws", workspace);
        _sbappend(sb, "l", layer);
        
        sb.append(" acc:").append(access);
        sb.append(']');

        return sb.toString();
    }
    
    private static void _sbappend(StringBuilder sb, String label, Object value) {
        if (value != null) {
            sb.append(" ").append(label).append(":").append(value);
        }    
    }
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.dto;

import org.geoserver.geofence.core.model.IPAddressRange;
import org.geoserver.geofence.core.model.Rule;
import org.geoserver.geofence.core.model.enums.GrantType;

import java.io.Serializable;


/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class ShortRule implements Serializable
{

    private static final long serialVersionUID = -9127101015688510863L;

    private Long id;
    private Long priority;

    private String userName;

    private String roleName;

    private Long instanceId;
    private String instanceName;

    private String service;
    private String request;
    private String workspace;
    private String layer;
    private String addressRange;

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
        setRequest(rule.getRequest());
        setWorkspace(rule.getWorkspace());
        setLayer(rule.getLayer());
        setAddressRange(rule.getAddressRange() == null ? "" : rule.getAddressRange().toString());
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

    public String getAddressRange() {
		return addressRange;
	}

	
	public void setAddressRange(String ipAddressRange) {
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
        StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append("[id:").append(id).append(" pri:").append(
                priority);

        if (userName != null)
        {
            sb.append(" user:").append(userName);
        }

        if (roleName != null)
        {
            sb.append(" role:").append(roleName);
        }

        if (instanceId != null)
        {
            sb.append(" iId:").append(instanceId);
        }
        if (instanceName != null)
        {
            sb.append(" iName:").append(instanceName);
        }

        if (service != null)
        {
            sb.append(" srv:").append(service);
        }
        if (request != null)
        {
            sb.append(" req:").append(request);
        }

        if (workspace != null)
        {
            sb.append(" ws:").append(workspace);
        }
        if (layer != null)
        {
            sb.append(" l:").append(layer);
        }

        sb.append(" acc:").append(access);
        sb.append(']');

        return sb.toString();

    }


}

/* (c) 2014, 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import org.geoserver.geofence.core.model.adapter.FKGSInstanceAdapter;
import org.geoserver.geofence.core.model.enums.GrantType;
import java.io.Serializable;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

/**
 * A Rule expresses if a given combination of request access is allowed or not.
 * <P>
 * In a given Rule, you may specify a precise combination of filters or a general
 * behavior. <BR>
 * Filtering can be done on <UL>
 * <LI> the requesting user </LI>
 * <LI> the profile associated to the requesting user</LI>
 * <LI> the instance of the accessed geoserver</LI>
 * <LI> the accessed service (e.g.: WMS)</LI>
 * <LI> the requested operation inside the accessed service (e.g.: getMap)</LI>
 * <LI> the workspace in geoserver</LI>
 * <LI> the requested layer </LI>
 * </UL>
 * <P><B>Example</B>: In order to allow access to every request to the WMS service in the instance GS1,
 * you will need to create a Rule, by only setting Service=WMS and Instance=GS1,
 * leaving the other fields to <TT>null</TT>.
 * <P>
 * When an access has to be checked for filtering, all the matching rules are read;
 * they are then evaluated according to their priority: the first rule found having
 * accessType <TT><B>{@link GrantType#ALLOW}</B></TT> or <TT><B>{@link GrantType#DENY}</B></TT> wins,
 * and the access is granted or denied accordingly.
 * <BR>Matching rules with accessType=<TT><B>{@link GrantType#LIMIT}</B></TT> are collected and evaluated at the end,
 * only if the request is Allowed by some other rule with lower priority.
 * <BR>These rules will have an associated {@link RuleLimits RuleLimits} that
 * defines some restrictions for using the data (such as area limitation).
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Entity(name = "Rule")
@Table(name = "gf_rule", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username", "rolename", "instance_id", "service", "request", "workspace", "layer"})})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "Rule")
@XmlRootElement(name = "Rule")
@XmlType(propOrder={"id","priority","username","rolename","instance","addressRange","service","request","workspace","layer","access","layerDetails","ruleLimits"})
public class Rule implements Identifiable, Serializable, Prioritizable, IPRangeProvider {

    private static final long serialVersionUID = -5127129225384707164L;

    /** The id. */
    @Id
    @GeneratedValue
    @Column
    private Long id;

    /** Lower numbers have higher priority */
    @Column(nullable = false)
    @Index(name = "idx_rule_priority")
    private long priority;

    @Column(name = "username", nullable = true)
    private String username;

    @Column(name = "rolename", nullable = true)
    private String rolename;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @ForeignKey(name = "fk_rule_instance")
    private GSInstance instance;

    @Column
    @Index(name = "idx_rule_service")
    private String service;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="low", column=@Column(name="ip_low")),
        @AttributeOverride(name="high", column=@Column(name="ip_high")),
        @AttributeOverride(name="size", column=@Column(name="ip_size"))   })
    private IPAddressRange addressRange;

    @Column
    @Index(name = "idx_rule_request")
    private String request;

    @Column
    private String subfield;
    
    @Column
    @Index(name = "idx_rule_workspace")
    private String workspace;

    @Column
    @Index(name = "idx_rule_layer")
    private String layer;

    @Enumerated(EnumType.STRING)
    @Column(name = "grant_type", nullable = false)
    private GrantType access;

    @OneToOne(optional = true, cascade = CascadeType.REMOVE, mappedBy = "rule") // main ref is in LayerDetails
    @ForeignKey(name = "fk_rule_details")
    private LayerDetails layerDetails;

    @OneToOne(optional = true, cascade = CascadeType.REMOVE, mappedBy = "rule") // main ref is in ruleLimits
    @ForeignKey(name = "fk_rule_limits")
    private RuleLimits ruleLimits;

    public Rule() {
    }

    public Rule(long priority, String username, String rolename, GSInstance instance, IPAddressRange addressRange,
                               String service, String request, String subfield, String workspace, String layer, GrantType access) {
        this.priority = priority;
        this.username = username;
        this.rolename = rolename;
        this.instance = instance;
        this.addressRange = addressRange;
        this.service = service;
        this.request = request;
        this.subfield = subfield;
        this.workspace = workspace;
        this.layer = layer;
        this.access = access;
    }

    /**
     * @deprecated need new subfield argument
     */
    @Deprecated
    public Rule(long priority, String username, String rolename, GSInstance instance, IPAddressRange addressRange,
                               String service, String request, String workspace, String layer, GrantType access) {
        this(priority, username, rolename, instance, addressRange, service, request, null, workspace, layer, access);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IPAddressRange getAddressRange() {
        return addressRange;
    }

    public void setAddressRange(IPAddressRange addressRange) {
        this.addressRange = addressRange;
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }


    @XmlJavaTypeAdapter(FKGSInstanceAdapter.class)
    public GSInstance getInstance() {
        return instance;
    }

    public void setInstance(GSInstance instance) {
        this.instance = instance;
    }

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getSubfield() {
       return subfield;
    }

    public void setSubfield(String subfield) {
       this.subfield = subfield;
    }        

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public GrantType getAccess() {
        return access;
    }

    public void setAccess(GrantType access) {
        this.access = access;
    }

    public RuleLimits getRuleLimits() {
        return ruleLimits;
    }

    /**
     * @deprecated  This setter is only used by hibernate, should not be called by the user.
     * @param ruleLimits
     */
    public void setRuleLimits(RuleLimits ruleLimits) {
        this.ruleLimits = ruleLimits;
    }

    public LayerDetails getLayerDetails() {
        return layerDetails;
    }

    /**
     * @deprecated This setter is only used by hibernate, should not be called by the user.
     */
    public void setLayerDetails(LayerDetails layerDetails) {
        this.layerDetails = layerDetails;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName())
                .append("[id:").append(id)
                .append(" pri:").append(priority);

        if (username != null) {
            sb.append(" user:").append(prepare(username));
        }

        if (rolename != null) {
            sb.append(" role:").append(prepare(rolename));
        }

        if (instance != null) {
            sb.append(" iId:").append(instance.getId());
            sb.append(" iName:").append(prepare(instance.getName()));
        }

        if (addressRange != null) {
            sb.append(" addr:").append(addressRange.getCidrSignature());
        }

        if (service != null) {
            sb.append(" srv:").append(service);
        }
        if (request != null) {
            sb.append(" req:").append(request);
        }
        if (subfield != null) {
            sb.append(" sub:").append(subfield);
        }

        if (workspace != null) {
            sb.append(" ws:").append(workspace);
        }
        if (layer != null) {
            sb.append(" l:").append(layer);
        }

        sb.append(" acc:").append(access);
        sb.append(']');

        return sb.toString();

    }
    
    private static String prepare(String s) {
        if(s==null)
            return "(null)";
        else if (s.isEmpty())
            return "(empty)";
        else
            return s;
    }
}

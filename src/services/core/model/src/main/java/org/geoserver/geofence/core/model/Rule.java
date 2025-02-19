/* (c) 2014, 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import org.geoserver.geofence.core.model.adapter.FKGSInstanceAdapter;
import org.geoserver.geofence.core.model.enums.GrantType;

import java.io.Serializable;
import java.sql.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Rule expresses if a given combination of request access is allowed or not.
 * <P>
 * In a given Rule, you may specify a precise combination of filters or a general
 * behavior. <BR>
 * Filtering can be done on <UL>
 * <LI> the requesting user </LI>
 * <LI> the group associated to the requesting user</LI>
 * <LI> the instance of the accessed geoserver</LI>
 * <LI> the ip address of the requesting client</LI>
 * <LI> the date the request is performed</LI>
 * <LI> the accessed service (e.g.: WMS)</LI>
 * <LI> the requested operation inside the accessed service (e.g.: getMap)</LI>
 * <LI> a custom field </LI> 
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
@Table(name = "gf_rule", 
        uniqueConstraints = { // @InternalModel
            @UniqueConstraint(
                    columnNames = {                                                          // @InternalModel
                        "username", "rolename", "instance_id",                               // @InternalModel
                        "ip_low", "ip_high", "ip_size", "valid_after", "valid_before",       // @InternalModel
                        "service", "request", "subfield", "workspace", "layer"},             // @InternalModel
                    name = "gf_rule_username_rolename_instance_id_service_request_works_key" // @InternalModel
            )},                                                                              // @InternalModel
        indexes = {                                                                          // @InternalModel
            @Index(name = "idx_rule_priority", columnList = "priority"),
            @Index(name = "idx_rule_service", columnList = "service"),
            @Index(name = "idx_rule_request", columnList = "request"),
            @Index(name = "idx_rule_workspace", columnList = "workspace"),
            @Index(name = "idx_rule_layer", columnList = "layer"),
            @Index(name = "idx_rule_after", columnList = "valid_after"),
            @Index(name = "idx_rule_before", columnList = "valid_before")
        }   // @InternalModel
)           // @InternalModel
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "Rule")
@XmlRootElement(name = "Rule")
@XmlType(propOrder={"id","priority","username","rolename","instance",
    "addressRange", "validAfter", "validBefore",
    "service","request","workspace","layer","access","layerDetails","ruleLimits"})
public class Rule implements Identifiable, Serializable, Prioritizable, IPRangeProvider {

    private static final long serialVersionUID = -3800109225384707164L;

    /** The id. */
    @Id
    @GeneratedValue
    @Column
    private Long id;

    /** Lower numbers have higher priority */
    @Column(nullable = false)
    private long priority;

    @Column(name = "username", nullable = true)
    private String username;

    @Column(name = "rolename", nullable = true)
    private String rolename;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(foreignKey = @ForeignKey(name="fk_rule_instance"))
    private GSInstance instance;

    @Column
    private String service;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="low", column=@Column(name="ip_low")),
        @AttributeOverride(name="high", column=@Column(name="ip_high")),
        @AttributeOverride(name="size", column=@Column(name="ip_size"))   })
    private IPAddressRange addressRange;
    
    @Column(name = "valid_after")
    private Date validAfter;

    @Column(name = "valid_before")
    private Date validBefore;
    
    @Column
    private String request;

    @Column
    private String subfield;
    
    @Column
    private String workspace;

    @Column
    private String layer;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "grant_type", nullable = false)
    private GrantType access;

    @OneToOne(optional = true, cascade = CascadeType.REMOVE, mappedBy = "rule") // main ref is in LayerDetails
    @JoinColumn(foreignKey = @ForeignKey(name="fk_rule_details"))    
    private LayerDetails layerDetails;

    @OneToOne(optional = true, cascade = CascadeType.REMOVE, mappedBy = "rule") // main ref is in ruleLimits
    @JoinColumn(foreignKey = @ForeignKey(name="fk_rule_limits"))    
    private RuleLimits ruleLimits;

    public Rule() {
    }

    public Rule(long priority, GrantType access) {
        this.priority = priority;
        this.access = access;
    }    
    
    public Rule(long priority, String username, String rolename, GSInstance instance, 
                IPAddressRange addressRange, Date validAfter, Date validBefore,
                String service, String request, String subfield, String workspace, String layer, GrantType access) {
        this.priority = priority;
        this.username = username;
        this.rolename = rolename;
        this.instance = instance;
        this.addressRange = addressRange;
        this.validAfter = validAfter;
        this.validBefore = validBefore;
        this.service = service;
        this.request = request;
        this.subfield = subfield;
        this.workspace = workspace;
        this.layer = layer;
        this.access = access;
    }

    public Rule(long priority, String username, String rolename, GSInstance instance, IPAddressRange addressRange,
                               String service, String request, String subfield, String workspace, String layer, GrantType access) {
        this(priority, username, rolename, instance, 
                addressRange, null, null, 
                service, request, subfield, workspace, layer, access);        
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

    public String getAddressRangeString() {
        return addressRange == null ? null: addressRange.getCidrSignature();
    }
        
    public Rule setAddressRange(IPAddressRange addressRange) {
        this.addressRange = addressRange;
        return this;
    }

    public Rule setAddressRange(String cidr) {
        this.addressRange = new IPAddressRange(cidr);
        return this;
    }
    
    public Date getValidAfter() {
        return validAfter;
    }
    
    public String getValidAfterString() {
        return validAfter == null ? null : validAfter.toString();
    }

    public Rule setValidAfter(Date validAfter) {
        this.validAfter = validAfter;
        return this;
    }

    public Date getValidBefore() {
        return validBefore;
    }
    
    public String getValidBeforeString() {
        return validBefore == null ? null : validBefore.toString();
    }

    public Rule setValidBefore(Date validBefore) {
        this.validBefore = validBefore;
        return this;
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

    public Rule setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getRolename() {
        return rolename;
    }

    public Rule setRolename(String rolename) {
        this.rolename = rolename;
        return this;
    }


    @XmlJavaTypeAdapter(FKGSInstanceAdapter.class)
    public GSInstance getInstance() {
        return instance;
    }

    public Rule setInstance(GSInstance instance) {
        this.instance = instance;
        return this;
    }

    public String getLayer() {
        return layer;
    }

    public Rule setLayer(String layer) {
        this.layer = layer;
        return this;
    }

    public String getService() {
        return service;
    }

    public Rule setService(String service) {
        this.service = service;
        return this;
    }

    public String getRequest() {
        return request;
    }

    public Rule setRequest(String request) {
        this.request = request;
        return this;
    }

    public String getSubfield() {
       return subfield;
    }

    public Rule setSubfield(String subfield) {
       this.subfield = subfield;
        return this;       
    }        

    public String getWorkspace() {
        return workspace;
    }

    public Rule setWorkspace(String workspace) {
        this.workspace = workspace;
        return this;
    }

    public GrantType getAccess() {
        return access;
    }

    public Rule setAccess(GrantType access) {
        this.access = access;
        return this;
    }

    public RuleLimits getRuleLimits() {
        return ruleLimits;
    }

    /**
     * @deprecated  This setter is only used by hibernate, should not be called by the user.
     * @param ruleLimits
     */
    @Deprecated
    public void setRuleLimits(RuleLimits ruleLimits) {
        this.ruleLimits = ruleLimits;
    }

    public LayerDetails getLayerDetails() {
        return layerDetails;
    }

    /**
     * @deprecated This setter is only used by hibernate, should not be called by the user.
     */
    @Deprecated
    public void setLayerDetails(LayerDetails layerDetails) {
        this.layerDetails = layerDetails;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName())
                .append("[id:").append(id)
                .append(" pri:").append(priority);

        _sbappend(sb, "user", username);
        _sbappend(sb, "role", rolename);

        if (instance != null) {
            sb.append(" iId:").append(instance.getId());
            sb.append(" iName:").append(prepare(instance.getName()));
        }
        _sbappend(sb, "addr", addressRange);
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
    private static String prepare(String s) {
        if(s==null)
            return "(null)";
        else if (s.isEmpty())
            return "(empty)";
        else
            return s;
    }
}

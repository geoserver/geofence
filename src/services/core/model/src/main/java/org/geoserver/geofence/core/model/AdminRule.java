/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import org.geoserver.geofence.core.model.adapter.FKGSInstanceAdapter;
import org.geoserver.geofence.core.model.enums.AdminGrantType;
import java.io.Serializable;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * An AdminRule expresses if a given combination of request access is allowed or not.
 * <P>
 * It's used for setting admin privileges on workspaces.
 * </P><P>
 * AdminRule filtering and selection is almost identical to {@see Rule}.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Entity(name = "AdminRule")
@Table(name = "gf_adminrule", 
        uniqueConstraints = { // @InternalModel
            @UniqueConstraint(
                    columnNames = {"username", "rolename", "instance_id", "workspace"}, // @InternalModel
                    name = "gf_adminrule_username_rolename_instance_id_workspace_key" // @InternalModel
                    )}, // @InternalModel
        indexes = { // @InternalModel
            @Index(name="idx_adminrule_priority", columnList = "priority"),
            @Index(name = "idx_adminrule_workspace", columnList = "workspace")
        } // @InternalModel
) // @InternalModel
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "Rule")
@XmlRootElement(name = "AdminRule")
@XmlType(propOrder={"id","priority","username","rolename","instance","addressRange","workspace","access"})
public class AdminRule implements Identifiable, Serializable, Prioritizable, IPRangeProvider {

    private static final long serialVersionUID = -3807129225384707999L;

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
    @JoinColumn(foreignKey = @ForeignKey(name="fk_adminrule_instance"))
    private GSInstance instance;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="low", column=@Column(name="ip_low")),
        @AttributeOverride(name="high", column=@Column(name="ip_high")),
        @AttributeOverride(name="size", column=@Column(name="ip_size"))   })
    private IPAddressRange addressRange;

    @Column
    private String workspace;

    @Enumerated(EnumType.STRING)
    @Column(name = "grant_type", nullable = false)
    private AdminGrantType access;

    public AdminRule() {
    }

    public AdminRule(long priority, String username, String rolename, GSInstance instance, IPAddressRange addressRange,
                               String workspace, AdminGrantType access) {
        this.priority = priority;
        this.username = username;
        this.rolename = rolename;
        this.instance = instance;
        this.addressRange = addressRange;
        this.workspace = workspace;
        this.access = access;
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

    @Override
    public long getPriority() {
        return priority;
    }

    @Override
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

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public AdminGrantType getAccess() {
        return access;
    }

    public void setAccess(AdminGrantType access) {
        this.access = access;
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

        if (workspace != null) {
            sb.append(" ws:").append(workspace);
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

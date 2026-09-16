/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import org.geofence.core.model.enums.AdminGrantType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * An AdminRule expresses if a given combination of request access is allowed or not.
 *
 * <p>It's used for setting admin privileges on workspaces.
 *
 * <p>AdminRule filtering and selection is almost identical to {@see Rule}.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Entity(name = "AdminRule")
@Table(
        name = "gf_adminrule",
        uniqueConstraints = { // @InternalModel
            @UniqueConstraint(
                    columnNames = {"username", "rolename", "instance_id", "workspace"}, // @InternalModel
                    name = "gf_adminrule_username_rolename_instance_id_workspace_key" // @InternalModel
                    )
        }, // @InternalModel
        indexes = { // @InternalModel
            @Index(name = "idx_adminrule_priority", columnList = "priority"),
            @Index(name = "idx_adminrule_workspace", columnList = "workspace")
        } // @InternalModel
        ) // @InternalModel
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "Rule")
@XmlRootElement(name = "AdminRule")
@XmlType(propOrder = {"id", "priority", "username", "rolename", "instance", "addressRange", "workspace", "access"})
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
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_adminrule_instance"))
    private GSInstance instance;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "low", column = @Column(name = "ip_low")),
        @AttributeOverride(name = "high", column = @Column(name = "ip_high")),
        @AttributeOverride(name = "size", column = @Column(name = "ip_size"))
    })
    private IPAddressRange addressRange;

    @Column
    private String workspace;

    @Enumerated(EnumType.STRING)
    @Column(name = "grant_type", nullable = false)
    private AdminGrantType access;

    public AdminRule() {}

    public AdminRule(
            long priority,
            String username,
            String rolename,
            GSInstance instance,
            IPAddressRange addressRange,
            String workspace,
            AdminGrantType access) {
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
                .append("[id:")
                .append(id)
                .append(" pri:")
                .append(priority);

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
        if (s == null) return "(null)";
        else if (s.isEmpty()) return "(empty)";
        else return s;
    }
}

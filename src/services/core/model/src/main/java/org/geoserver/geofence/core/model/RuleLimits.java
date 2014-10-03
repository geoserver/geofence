/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import com.vividsolutions.jts.geom.MultiPolygon;
import org.geoserver.geofence.core.model.adapter.MultiPolygonAdapter;
import org.geoserver.geofence.core.model.enums.CatalogMode;
import org.geoserver.geofence.core.model.enums.GrantType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Type;

/**
 * Defines general limits (such as an Area ) for a {@link Rule}.
 * <BR>RuleLimits may be set only for rules with a {@link GrantType#LIMIT} access type.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Entity(name = "RuleLimits")
@Table(name = "gf_rule_limits", uniqueConstraints=
        @UniqueConstraint(columnNames="rule_id"))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "RuleLimits")
@XmlRootElement(name = "RuleLimits")
public class RuleLimits implements Serializable {

    private static final long serialVersionUID = 2829839552804345725L;

    /** The id. */
    @Id
//    @GeneratedValue
    @Column
    private Long id;

    @OneToOne(optional=false)
    @Check(constraints="rule.access='LIMIT'") // ??? check this 
    @ForeignKey(name="fk_limits_rule")
    private Rule rule;

	@Type(type = "org.hibernatespatial.GeometryUserType")
	@Column(name = "area")
	private MultiPolygon allowedArea;

    @Enumerated(EnumType.STRING)
    @Column(name = "catalog_mode", nullable = true)
    private CatalogMode catalogMode;

    @XmlJavaTypeAdapter(MultiPolygonAdapter.class)
    public MultiPolygon getAllowedArea() {
        return allowedArea;
    }

    public void setAllowedArea(MultiPolygon allowedArea) {
        this.allowedArea = allowedArea;
    }

    public CatalogMode getCatalogMode() {
        return catalogMode;
    }

    public void setCatalogMode(CatalogMode catalogMode) {
        this.catalogMode = catalogMode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlTransient
    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    @Override
    public String toString() {
        return "RuleLimits["
                + "id=" + id
                + " rule=" + rule
                + " allowedArea=" + allowedArea
                + (catalogMode == null ? "" : (" mode="+catalogMode))
                + ']';
    }
}

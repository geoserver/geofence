/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import com.vividsolutions.jts.geom.MultiPolygon;
import org.geoserver.geofence.core.model.adapter.MultiPolygonAdapter;
import org.geoserver.geofence.core.model.enums.CatalogMode;
import org.geoserver.geofence.core.model.enums.LayerType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Type;

/**
 * Details may be set only for ules with non-wildcarded profile, instance, workspace,layer.
 *
 * <P>
 * <B>TODO</B> <UL>
 * <LI>What about externally defined styles?</LI>
 * </UL>
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Entity(name = "LayerDetails")
@Table(name = "gf_layer_details")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "LayerDetails")
@XmlRootElement(name = "LayerDetails")
public class LayerDetails implements Serializable {

    private static final long serialVersionUID = -4150963895550551513L;

    /** The id. */
    @Id
//    @GeneratedValue
    @Column
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true /*false*/)
    private LayerType type;
    
    @Column
    private String defaultStyle;

    @Column(length=4000)
    private String cqlFilterRead;

    @Column(length=4000)
    private String cqlFilterWrite;

	@Type(type = "org.hibernatespatial.GeometryUserType")
	@Column(name = "area")
	private MultiPolygon area;

    @Enumerated(EnumType.STRING)
    @Column(name = "catalog_mode", nullable = true)
    private CatalogMode catalogMode;

    @OneToOne(optional=false)
//    @Check(constraints="rule.access='LIMIT'") // ??? check this
    @ForeignKey(name="fk_details_rule")
    private Rule rule;

    /** Styles allowed for this layer */
    @org.hibernate.annotations.CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable( name = "gf_layer_styles", joinColumns = @JoinColumn(name = "details_id"))
    @ForeignKey(name="fk_styles_layer")
    @Column(name="styleName")
    private Set<String> allowedStyles = new HashSet<String>();

    /** Feature Attributes associated to the Layer
     * <P>We'll use the pair <TT>(details_id, name)</TT> as PK for the associated table.
     * To do so, we have to perform some trick on the <TT>{@link LayerAttribute#access}</TT> field.
     */
    @org.hibernate.annotations.CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable( name = "gf_layer_attributes",  joinColumns = @JoinColumn(name = "details_id"),  uniqueConstraints = @UniqueConstraint(columnNames={"details_id", "name"}))
    // override is used to set the pk as {"details_id", "name"}
//    @AttributeOverride( name="access", column=@Column(name="access", nullable=false) )
    @ForeignKey(name="fk_attribute_layer")
    @Fetch(FetchMode.SELECT) // without this, hibernate will duplicate results(!)
    private Set<LayerAttribute> attributes = new HashSet<LayerAttribute>();

    @XmlJavaTypeAdapter(MultiPolygonAdapter.class)
    public MultiPolygon getArea() {
        return area;
    }

    public void setArea(MultiPolygon area) {
        this.area = area;
    }

    public CatalogMode getCatalogMode() {
        return catalogMode;
    }

    public void setCatalogMode(CatalogMode catalogMode) {
        this.catalogMode = catalogMode;
    }
    
    public String getCqlFilterRead() {
        return cqlFilterRead;
    }

    public void setCqlFilterRead(String cqlFilterRead) {
        this.cqlFilterRead = cqlFilterRead;
    }

    public String getCqlFilterWrite() {
        return cqlFilterWrite;
    }

    public void setCqlFilterWrite(String cqlFilterWrite) {
        this.cqlFilterWrite = cqlFilterWrite;
    }

    public String getDefaultStyle() {
        return defaultStyle;
    }

    public void setDefaultStyle(String defaultStyle) {
        this.defaultStyle = defaultStyle;
    }

    public Set<String> getAllowedStyles() {
        return allowedStyles;
    }

    public void setAllowedStyles(Set<String> allowedStyles) {
        this.allowedStyles = allowedStyles;
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

    @XmlElement(name="attribute")
    public Set<LayerAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<LayerAttribute> attributes) {
        this.attributes = attributes;
    }

    public LayerType getType() {
        return type;
    }

    public void setType(LayerType type) {
        this.type = type;
    }    

    @Override
    public String toString() {
        return "LayerDetails{" 
                + "id=" + id
                + " type=" + type
                + " defStyle=" + defaultStyle
                + " cqlr=" + cqlFilterRead
                + " cqlw=" + cqlFilterWrite
                + " catmode" + catalogMode
                + " area=" + area
                + " rule=" + rule
                + " attrs=" + attributes
                + " styles=" + allowedStyles
                + '}';
    }

}

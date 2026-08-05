/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model.util;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/** @author ETj (etj at geo-solutions.it) */
@XmlRootElement(name = "identifier")
@XmlType(propOrder = {"id", "name"})
public class IdName {
    private String name;
    private Long id;

    protected IdName() {}

    public IdName(Long id, String name) {
        this.name = name;
        this.id = id;
    }

    public IdName(String name) {
        setName(name);
    }

    public IdName(Long id) {
        setId(id);
    }

    @XmlElement
    public Long getId() {
        return id;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    /**
     * Plain field assignment, no side effect on {@code name}. This class doubles as both a query filter ("look this up
     * by id, or else by name" - exactly one set, enforced by the {@link #IdName(Long)}/{@link #IdName(String)}
     * convenience constructors used for that case) and a resolved reference (both fields legitimately set at once, e.g.
     * a rule's instance echoed back with its real id and name together). A setter that clears the other field would
     * make the second shape unrepresentable: Jackson (unlike JAXB unmarshalling, which never calls a setter for an
     * absent element) calls every setter, including with nulls, so whichever of {@code id}/{@code name} a clearing
     * setter processed last would always win and silently wipe the other - order depending on incidental JSON key
     * order, not on which fields the source object actually had set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /** See {@link #setId(Long)}. */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        //        sb.append(getClass().getSimpleName()).append('[');
        sb.append('[');
        if (id != null) sb.append("id:").append(id);
        if (name != null) sb.append("name:").append(name);
        sb.append(']');
        return sb.toString();
    }
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model.config.adapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "item")
public class RemappedType {

    private Long oldId;
    private Long newId;

    public RemappedType(Long key, Long value) {
        this.oldId = key;
        this.newId = value;
    }

    public RemappedType() {
    }

    @XmlAttribute
    public Long getOld() {
        return oldId;
    }

    public void setOld(Long key) {
        this.oldId = key;
    }

    @XmlAttribute
    public Long getNew() {
        return newId;
    }

    public void setNew(Long value) {
        this.newId = value;
    }
}

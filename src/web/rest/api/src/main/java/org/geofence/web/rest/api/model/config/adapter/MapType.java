/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.api.model.config.adapter;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** @author ETj (etj at geo-solutions.it) */
@XmlRootElement(name = "Map")
public class MapType implements Iterable<RemappedType> {

    List<RemappedType> entries = new LinkedList<RemappedType>();

    @XmlElement(name = "item")
    public List<RemappedType> getEntries() {
        return entries;
    }

    public void setEntries(List<RemappedType> entries) {
        this.entries = entries;
    }

    public void add(Map.Entry<Long, Long> entry) {
        entries.add(new RemappedType(entry.getKey(), entry.getValue()));
    }

    @Override
    public Iterator<RemappedType> iterator() {
        return entries.iterator();
    }
}

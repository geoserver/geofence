/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model.adapter.dual;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "Map")
public class MapType implements Iterable<MapEntryType> {


    List<MapEntryType> entries = new LinkedList<MapEntryType>();

    @XmlElement(name = "entry")
    public List<MapEntryType> getEntries() {
        return entries;
    }

    public void setEntries(List<MapEntryType> entries) {
        this.entries = entries;
    }

    public void add(Map.Entry<String, String> entry) {
        entries.add(new MapEntryType(entry.getKey(), entry.getValue()));
    }

    @Override
    public Iterator<MapEntryType> iterator() {
        return entries.iterator();
    }
}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model.adapter;

import org.geoserver.geofence.core.model.adapter.dual.MapEntryType;
import org.geoserver.geofence.core.model.adapter.dual.MapType;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class MapAdapter extends XmlAdapter<MapType,Map<String,String>> {

    @Override
    public MapType marshal(Map<String,String> v) throws Exception {
        MapType ret = new MapType();
//        System.out.println("marshalling...");
        for (Map.Entry<String, String> entry : v.entrySet()) {
//            System.out.println("marshalling " + entry.getKey()+":"+entry.getValue());
            ret.add(entry);
        }

        return ret;
    }

    @Override
    public Map<String,String> unmarshal(MapType v) throws Exception {
        Map<String,String> ret = new HashMap<String, String>();
//        System.out.println("unmarshalling...");
        for (MapEntryType entry : v) {
//            System.out.println("unmarshalling " + entry.getKey() + ":" + entry.getValue());
            ret.put(entry.getKey(), entry.getValue());
        }
        return ret;
    }


}

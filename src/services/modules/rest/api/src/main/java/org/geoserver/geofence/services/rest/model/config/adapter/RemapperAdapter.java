/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model.config.adapter;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RemapperAdapter extends XmlAdapter<MapType, Map<Long, Long>> {

    @Override
    public MapType marshal(Map<Long, Long> v) throws Exception {
        MapType ret = new MapType();
//        System.out.println("marshalling...");
        for (Map.Entry<Long, Long> entry : v.entrySet()) {
//            System.out.println("marshalling " + entry.getKey()+":"+entry.getValue());
            ret.add(entry);
        }

        return ret;
    }

    @Override
    public Map<Long, Long> unmarshal(MapType v) throws Exception {
        Map<Long, Long> ret = new HashMap<Long, Long>();
//        System.out.println("unmarshalling...");
        for (RemappedType entry : v) {
//            System.out.println("unmarshalling " + entry.getKey() + ":" + entry.getValue());
            ret.put(entry.getOld(), entry.getNew());
        }

        return ret;
    }
}

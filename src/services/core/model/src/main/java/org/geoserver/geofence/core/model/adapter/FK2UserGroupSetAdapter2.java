/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model.adapter;

import org.geoserver.geofence.core.model.adapter.dual.IdNameBundle;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vividsolutions.jts.io.ParseException;
import org.geoserver.geofence.core.model.UserGroup;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Transform a UserGroup into its id.
 *
 */
public class FK2UserGroupSetAdapter2 extends XmlAdapter<ArrayList<Long>, Set<UserGroup>> {

    @Override
    public Set<UserGroup> unmarshal(ArrayList<Long> inSet) throws ParseException {
        if(inSet == null)
            return null;

        Set<UserGroup> ret = new HashSet<UserGroup>();
        for (Long in : inSet) {
            UserGroup ug = new UserGroup();
            ug.setId(in);
            ret.add(ug);
        }

        return ret;
    }

    @Override
    public ArrayList<Long> marshal(Set<UserGroup> inSet) throws ParseException {
        if(inSet == null)
            return null;

        System.out.println("Marshalling " + inSet.size() + " groups");

        ArrayList<Long> ret = new ArrayList<Long>(inSet.size());
        for (UserGroup ug : inSet) {
            if (ug != null) {
                ret.add(ug.getId());
                System.out.println("Added group " + ug.getId());
            }
        }

        return ret;
    }
}

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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;

/**
 * Transform a UserGroup into its id.
 *
 */
public class FK2UserGroupSetAdapter extends XmlAdapter<FK2UserGroupSetAdapter.GroupList, Set<UserGroup>> {

    @Override
    public Set<UserGroup> unmarshal(GroupList inSet) throws ParseException {
        if(inSet == null)
            return null;

        Set<UserGroup> ret = new HashSet<UserGroup>();
        for (IdNameBundle in : inSet) {
            System.out.println("Unmarshalling group " + in);

            UserGroup ug = new UserGroup();
            ug.setId(in.getId());
            ug.setName(in.getName());
            ret.add(ug);
        }

        System.out.println("Unmarshalled user has " + ret.size() + " groups");
        return ret;
    }

    @Override
    public GroupList marshal(Set<UserGroup> inSet) throws ParseException {
        if(inSet == null)
            return null;

        System.out.println("Marshalling " + inSet.size() + " groups");

        GroupList ret = new GroupList(inSet.size());
        for (UserGroup ug : inSet) {
            if (ug != null) {
                IdNameBundle in = new IdNameBundle();
                in.setId(ug.getId());
                in.setName(ug.getName());
                ret.add(in);
                System.out.println("Marshalling group " + in);
            }
        }

        return ret;
    }

    static class GroupList implements Iterable<IdNameBundle> {
        List<IdNameBundle> list;

        public GroupList() {
        }

        public GroupList(int size) {
            list = new ArrayList<IdNameBundle>(size);
        }

        @XmlElement(name="group")
        public List<IdNameBundle> getList() {
            return list;
        }

        public void setList(List<IdNameBundle> list) {
            this.list = list;
        }

        public boolean add(IdNameBundle e) {
            return list.add(e);
        }

        @Override
        public Iterator<IdNameBundle> iterator() {
            return list.iterator();
        }

    }
}

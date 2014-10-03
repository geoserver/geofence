/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model.adapter;

import org.geoserver.geofence.core.model.adapter.dual.IdNameBundle;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vividsolutions.jts.io.ParseException;
import org.geoserver.geofence.core.model.UserGroup;

/**
 * Transform a UserGroup into its id.
 *
 */
public class FK2UserGroupAdapter extends XmlAdapter<IdNameBundle, UserGroup> {

    @Override
    public UserGroup unmarshal(IdNameBundle in) throws ParseException {

            UserGroup ret = new UserGroup();
            ret.setId(in.getId());
            ret.setName(in.getName());
            return ret;
    }

    @Override
    public IdNameBundle marshal(UserGroup u) throws ParseException {
        IdNameBundle in = new IdNameBundle();
        if (u != null) {
            in.setId(u.getId());
            in.setName(u.getName());
        }
        return in;
    }
}

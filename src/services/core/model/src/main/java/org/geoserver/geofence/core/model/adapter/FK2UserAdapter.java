/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model.adapter;

import org.geoserver.geofence.core.model.adapter.dual.IdNameBundle;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vividsolutions.jts.io.ParseException;
import org.geoserver.geofence.core.model.GSUser;

/**
 * Transform a Profile into its id.
 *
 */
public class FK2UserAdapter extends XmlAdapter<IdNameBundle, GSUser> {

    @Override
    public GSUser unmarshal(IdNameBundle in) throws ParseException {

            GSUser ret = new GSUser();
            ret.setId(in.getId());
            ret.setName(in.getName());
            return ret;
    }

    @Override
    public IdNameBundle marshal(GSUser u) throws ParseException {
        IdNameBundle in = new IdNameBundle();
        if (u != null) {
            in.setId(u.getId());
            in.setName(u.getName());
        }
        return in;
    }
}

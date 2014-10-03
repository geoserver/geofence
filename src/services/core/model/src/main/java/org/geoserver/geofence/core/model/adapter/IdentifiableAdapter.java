/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.vividsolutions.jts.io.ParseException;
import org.geoserver.geofence.core.model.Identifiable;

/**
 * Transform a Profile into its id.
 *
 */
public abstract class IdentifiableAdapter<I extends Identifiable> extends XmlAdapter<String, I> {


    protected abstract I createInstance();
    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public I unmarshal(String val) throws ParseException {

        I ret = createInstance();
        try {
            ret.setId(Long.valueOf(val));
            return ret;
        } catch (NumberFormatException e) {
            throw new ParseException("Bad "+ ret.getClass().getSimpleName()+" id " + val);
        }
    }

    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public String marshal(I p) throws ParseException {
        if (p != null) {
            return p.getId().toString();
        } else {
            throw new ParseException("Obj is null");
        }
    }
}

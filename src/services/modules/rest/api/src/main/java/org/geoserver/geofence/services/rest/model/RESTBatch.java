/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.model;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
@XmlRootElement(name = "batch")
public class RESTBatch {

    private List<RESTBatchOperation> list;

    public RESTBatch() {
        list = new LinkedList<RESTBatchOperation>();
    }

    @XmlElement(name = "operation")
    public List<RESTBatchOperation> getList() {
        return list;
    }

    public void setList(List<RESTBatchOperation> list) {
        this.list = list;
    }

    public void add(RESTBatchOperation op) {
        list.add(op);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + list.size() + " ops]";
    }
}

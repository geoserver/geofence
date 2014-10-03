/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * 
 */
public class GrantedAuths implements Serializable {

    private List<Authority> auths;

    public GrantedAuths() {
        if (auths == null)
            auths = new ArrayList<Authority>();
    }

    /**
     * @param authorities
     *            the authorities to set
     */
    public void setAuthorities(List<Authority> authorities) {
        this.auths = authorities;
    }

    /**
     * @return the authorities
     */
    public List<Authority> getAuthorities() {
        return auths;
    }
}

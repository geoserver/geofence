/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client;

import java.util.Iterator;
import java.util.List;

import org.geoserver.geofence.gui.client.model.GSInstance;
import org.geoserver.geofence.gui.client.model.GSUser;
import org.geoserver.geofence.gui.client.model.UserGroup;
import org.geoserver.geofence.gui.client.model.Rule;

public class Constants {

    public final static int DEFAULT_PAGESIZE = 25;

    public static final int NORTH_PANEL_DIMENSION = 30;

    public static final int SOUTH_PANEL_DIMENSION = 400;

    public static final int EASTH_PANEL_DIMENSION = 250;

    public static final int WEST_PANEL_DIMENSION = 0;

    public static Constants instance = new Constants();

    public static Constants getInstance() {
        return instance;
    }

    /**
     * Creates the new rule.
     * 
     * @param model
     *            the model
     * @return the rule
     */
    public Rule createNewRule(Rule model) {
        Rule new_rule = new Rule();
        new_rule.setId(-1);
        if (model == null) {
            new_rule.setPriority(0);
        } else {
            new_rule.setPriority(model.getPriority() + 1);
        }

        GSUser all_user = new GSUser();
        all_user.setId(-1);
        all_user.setName("*");
        new_rule.setUser(all_user);
        UserGroup all_profile = new UserGroup();
        all_profile.setId(-1);
        all_profile.setName("*");
        new_rule.setProfile(all_profile);
        GSInstance all_instance = new GSInstance();
        all_instance.setId(-1);
        all_instance.setName("*");
        all_instance.setBaseURL("*");
        // new_rule.setInstance(all_instance);
        new_rule.setGrant("ALLOW");
        new_rule.setService("*");
        new_rule.setLayer("*");
        new_rule.setRequest("*");
        new_rule.setWorkspace("*");
        return new_rule;
    }

    /**
     * gsuser_id, profile_id, instance_id, service, request, workspace, layer
     * 
     * @param list
     * @param rule
     * @return
     */
    public boolean checkUniqueRule(List<Rule> list, Rule rule) {
        boolean res = false;
        if (list.size() > 0) {
            Iterator itr = list.iterator();
            while (itr.hasNext() && !res) {
                Rule r = (Rule) itr.next();
                if (((r.getUser() != null && rule.getUser() != null && r.getUser().getName()
                        .equals(rule.getUser().getName())) || (r.getUser() == null && rule
                        .getUser() == null))
                        && (r.getProfile() != null && rule.getProfile() != null
                                && r.getProfile().getName().equals(rule.getProfile().getName()) || (r
                                .getProfile() == null && rule.getProfile() == null))
                        && (r.getInstance() != null && rule.getInstance() != null
                                && r.getInstance().getName().equals(rule.getInstance().getName()) || (r
                                .getInstance() == null && rule.getInstance() == null))
                        && (r.getService() != null && rule.getService() != null
                                && r.getService().equals(rule.getService()) || (r.getService() == null && rule
                                .getService() == null))
                        && (r.getRequest() != null && rule.getRequest() != null
                                && r.getRequest().equals(rule.getRequest()) || (r.getRequest() == null && rule
                                .getRequest() == null))
                        && (r.getWorkspace() != null && rule.getWorkspace() != null
                                && r.getWorkspace().equals(rule.getWorkspace()) || (r
                                .getWorkspace() == null && rule.getWorkspace() == null))
                        && (r.getLayer() != null && rule.getLayer() != null
                                && r.getLayer().equals(rule.getLayer()) || (r.getLayer() == null && rule
                                .getLayer() == null))) {
                    res = true;
                }
            }
        }
        return res;
    }

}

/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.configuration;

import org.geoserver.geofence.gui.client.configuration.IUserBeanManager;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.geoserver.geofence.gui.client.model.User;

import org.springframework.stereotype.Component;


// TODO: Auto-generated Javadoc
/**
 * The Class UserBeanManager.
 */
@Component("userBeanManager")
public class UserBeanManager implements IUserBeanManager
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 575619421702010379L;

    /** The users. */
    private List<User> users = new ArrayList<User>();

    /**
     * Sets the users.
     *
     * @param users
     *            the new users
     */
    public void setUsers(List<User> users)
    {
        this.users = users;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.configuration.IUserBeanManager#getUsers ()
     */
    public List<User> getUsers()
    {
        // TODO Auto-generated method stub
        return users;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.configuration.IUserBeanManager#configureUsers()
     */
    @PostConstruct
    public void configureUsers()
    {
        for (int i = 0; i < 200; i++)
        {
            User user = new User();
            user.setPath("geofence/resources/images/userChoose.jpg");
            user.setName("TEST" + i);
            user.setFullName("profile" + i);
            user.setPassword("password" + i);
            user.setEmailAddress("profile" + i + "@test.it");
            this.users.add(user);
        }
    }

}

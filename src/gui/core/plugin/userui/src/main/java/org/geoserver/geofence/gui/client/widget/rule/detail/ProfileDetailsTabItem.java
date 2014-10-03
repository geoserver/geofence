/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.rule.detail;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.TabItem;

import org.geoserver.geofence.gui.client.Resources;
import org.geoserver.geofence.gui.client.model.UserGroup;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;


/**
 * The Class ProfileDetailsTabItem.
 */
public class ProfileDetailsTabItem extends TabItem
{

    /** The profile details widget. */
    private ProfileDetailsWidget profileDetailsWidget;

    private UserGroup profile;

    /**
     * Instantiates a new profile details tab item.
     *
     * @param tabItemId
     *            the tab item id
     */
    private ProfileDetailsTabItem(String tabItemId)
    {
        // TODO: add I18n message
        // super(I18nProvider.getMessages().profiles());
        super("Group Details");
        setId(tabItemId);
        setIcon(Resources.ICONS.table());
    }

    /**
     * Instantiates a new Profile Details tab item.
     *
     * @param tabItemId
     *            the tab item id
     * @param model
     * @param rulesService
     *            the rules service
     */
    public ProfileDetailsTabItem(String tabItemId, UserGroup profile, ProfilesManagerRemoteServiceAsync profilesService)
    {
        this(tabItemId);
        this.profile = profile;

        setProfileDetailsWidget(new ProfileDetailsWidget(this.profile, profilesService));
        add(getProfileDetailsWidget());

        setScrollMode(Scroll.NONE);

        getProfileDetailsWidget().getProfileDetailsInfo().getLoader().load(0, org.geoserver.geofence.gui.client.Constants.DEFAULT_PAGESIZE);

    }

    /**
     * @return the profileDetailsWidget
     */
    public ProfileDetailsWidget getProfileDetailsWidget()
    {
        return profileDetailsWidget;
    }

    /**
     * @param profileDetailsWidget the profileDetailsWidget to set
     */
    public void setProfileDetailsWidget(ProfileDetailsWidget profileDetailsWidget)
    {
        this.profileDetailsWidget = profileDetailsWidget;
    }

}

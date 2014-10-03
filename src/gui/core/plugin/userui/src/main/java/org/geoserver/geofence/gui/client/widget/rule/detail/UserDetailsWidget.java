/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.rule.detail;

import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.Resources;
import org.geoserver.geofence.gui.client.i18n.I18nProvider;
import org.geoserver.geofence.gui.client.model.GSUser;
import org.geoserver.geofence.gui.client.model.UserGroup;
import org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;

import java.util.HashSet;
import java.util.Set;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ComponentManager;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The Class UserDetailsWidget.
 *
 */
public class UserDetailsWidget extends ContentPanel
{

    /** The user. */
    private GSUser user;

    /** The user details info. */
    private UserDetailsInfoWidget userDeatilsInfo;
    
    /** The profiles info. */
    private ProfilesGridWidget profilesInfo;

    /** The tool bar. */
    private ToolBar toolBar;

    /** The save user details button. */
    private Button saveUserDetailsButton;

    private Button cancelButton;

	private GsUsersManagerRemoteServiceAsync usersService;

	private ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote;

    /**
     * Instantiates a new user limits widget.
     *
     * @param model
     *            the model
     * @param usersService
     *            the user service
     * @param profilesManagerServiceRemote 
     */
    public UserDetailsWidget(GSUser model, 
    		GsUsersManagerRemoteServiceAsync usersService, 
    		final ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote)
    {
        this.user = model;
        this.usersService = usersService;
        this.profilesManagerServiceRemote = profilesManagerServiceRemote;
        setHeaderVisible(false);
        setFrame(true);
        setHeight(330);
        setWidth(690);
        setLayout(new FitLayout());

        userDeatilsInfo = new UserDetailsInfoWidget(this.user, usersService, this);
        //add(userDeatilsInfo.getFormPanel());

        profilesInfo = new ProfilesGridWidget(this.user, usersService, profilesManagerServiceRemote, this);
        add(profilesInfo.getGrid());
        
        super.setMonitorWindowResize(true);

        setScrollMode(Scroll.AUTOY);

        this.toolBar = new ToolBar();

        this.saveUserDetailsButton = new Button("Save");
        saveUserDetailsButton.setIcon(Resources.ICONS.save());
        saveUserDetailsButton.disable();

        saveUserDetailsButton.addListener(Events.OnClick, new Listener<ButtonEvent>()
            {

                public void handleEvent(ButtonEvent be)
                {

                    disableSaveButton();

                    /*UserLimitsInfo userInfoModel = userDeatilsInfo.getModelData();
                    Dispatcher.forwardEvent(GeofenceEvents.SAVE_USER_LIMITS, userInfoModel);*/

                    profilesManagerServiceRemote.getProfiles(-1, -1, true, new AsyncCallback<PagingLoadResult<UserGroup>>() {
						
						public void onSuccess(PagingLoadResult<UserGroup> result) {
		                    Set<UserGroup> groups = new HashSet<UserGroup>();
		                    for (UserGroup gg : profilesInfo.getStore().getModels())
		                    {
		                    	for(UserGroup gg_r : result.getData()){
			                    	if(gg.getName().equals(gg_r.getName()) && gg.isEnabled()){
			                    		groups.add(gg_r);
			                    	}
		                    		
		                    	}
		                    }
							user.setUserGroups(groups);
		                    Dispatcher.forwardEvent(GeofenceEvents.SAVE_USER_GROUPS, user);
		                    
		                    Dispatcher.forwardEvent(GeofenceEvents.SEND_INFO_MESSAGE,
		                        new String[] { "GeoServer Users: Users Groups", "Apply Changes Successfull!" });

						}
						
						public void onFailure(Throwable caught) {
		                    Dispatcher.forwardEvent(GeofenceEvents.SEND_ERROR_MESSAGE,
			                        new String[] { "GeoServer Users: Users Groups", "Error Occurred while assigning groups to the User!" });
						}
					});

                }
            });

        cancelButton = new Button("Cancel");
        cancelButton.addListener(Events.OnClick, new Listener<ButtonEvent>()
            {
                public void handleEvent(ButtonEvent be)
                {
                    // /////////////////////////////////////////////////////////
                    // Getting the GS USER limits edit dialogs and hiding this
                    // /////////////////////////////////////////////////////////
                    ComponentManager.get().get(I18nProvider.getMessages().userDialogId()).hide();
                }
            });

        this.toolBar.add(new FillToolItem());
        this.toolBar.add(saveUserDetailsButton);
        this.toolBar.add(cancelButton);
        setBottomComponent(this.toolBar);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.extjs.gxt.ui.client.widget.Component#onWindowResize(int, int)
     */
    @Override
    protected void onWindowResize(int width, int height)
    {
        super.layout();
    }

    /**
     * Sets the user limits info.
     *
     * @param userDetailsInfoWidget
     *            the new user limits info
     */
    public void setUserDetailsInfo(UserDetailsInfoWidget userDetailsInfoWidget)
    {
        this.userDeatilsInfo = userDetailsInfoWidget;
    }

    /**
     * Gets the user limits info.
     *
     * @return the user limits info
     */
    public UserDetailsInfoWidget getUserDetailsInfo()
    {
        return userDeatilsInfo;
    }

    /**
     * Disable save button.
     */
    public void disableSaveButton()
    {
        if (this.saveUserDetailsButton.isEnabled())
        {
            this.saveUserDetailsButton.disable();
        }
    }

    /**
     * Enable save button.
     */
    public void enableSaveButton()
    {
        if (!this.saveUserDetailsButton.isEnabled())
        {
            this.saveUserDetailsButton.enable();
        }
    }

	/**
	 * @param profilesInfo the profilesInfo to set
	 */
	public void setProfilesInfo(ProfilesGridWidget profilesInfo) {
		this.profilesInfo = profilesInfo;
	}

	/**
	 * @return the profilesInfo
	 */
	public ProfilesGridWidget getProfilesInfo() {
		return profilesInfo;
	}

}

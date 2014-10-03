/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.dialog;

import org.geoserver.geofence.gui.client.i18n.I18nProvider;
import org.geoserver.geofence.gui.client.model.GSUser;
import org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.widget.SaveStaus;
import org.geoserver.geofence.gui.client.widget.rule.detail.UserDetailsTabItem;
import org.geoserver.geofence.gui.client.widget.tab.TabWidget;

import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;


/**
 * UserDetailsEditDialog class.
 *
 * @author Tobia di Pisa
 *
 */
public class UserDetailsEditDialog extends Dialog
{

    /** The Constant USER_DETAILS_DIALOG_ID. */
    public static final String USER_DETAILS_DIALOG_ID = "userDetailsDialog";

    /** The save status. */
    private SaveStaus saveStatus;

    /** The model. */
    private GSUser user;

    /** The user manager service remote. */
    private GsUsersManagerRemoteServiceAsync usersManagerServiceRemoteAsync;

    /** The profiles manager service remote. */
    private ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote;
    
    /** The tab widget. */
    private TabWidget tabWidget;

    /**
     * Instantiates a new rule details edit dialog.
     *
     * @param rulesManagerServiceRemote
     *            the rules manager service remote
     */
    public UserDetailsEditDialog(GsUsersManagerRemoteServiceAsync usersManagerServiceRemoteAsync, ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote)
    {
        this.usersManagerServiceRemoteAsync = usersManagerServiceRemoteAsync;
        this.profilesManagerServiceRemote = profilesManagerServiceRemote;

        setTabWidget(new TabWidget());

        setResizable(false);
        setButtons("");
        setClosable(true);
        setModal(true);
        setWidth(700);
        setHeight(427);
        setId(I18nProvider.getMessages().userDialogId());

        add(this.getTabWidget());
    }

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.Dialog#createButtons()
     */
    @Override
    protected void createButtons()
    {
        super.createButtons();

        this.saveStatus = new SaveStaus();
        this.saveStatus.setAutoWidth(true);

        getButtonBar().add(saveStatus);

        getButtonBar().add(new FillToolItem());

    }

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.Window#show()
     */
    @Override
    public void show()
    {
        super.show();

        if (getModel() != null)
        {
            setHeading("Editing User Details for User #" + user.getId());
            UserDetailsTabItem usersDetailsTabItem = new UserDetailsTabItem(USER_DETAILS_DIALOG_ID, user,
                    usersManagerServiceRemoteAsync, profilesManagerServiceRemote);
            this.tabWidget.add(usersDetailsTabItem);
            this.tabWidget.setSelection(usersDetailsTabItem);
        }

    }

    /**
     * Reset.
     */
    public void reset()
    {
        this.tabWidget.removeAll();
        this.saveStatus.clearStatus("");
    }

    /**
     * Sets the model.
     *
     * @param model
     *            the new model
     */
    public void setModel(GSUser user)
    {
        this.user = user;
    }

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.Component#getModel()
     */
    public GSUser getModel()
    {
        return this.user;
    }

    /**
     * Sets the tab widget.
     *
     * @param tabWidget
     *            the new tab widget
     */
    public void setTabWidget(TabWidget tabWidget)
    {
        this.tabWidget = tabWidget;
    }

    /**
     * Gets the tab widget.
     *
     * @return the tab widget
     */
    public TabWidget getTabWidget()
    {
        return tabWidget;
    }

}

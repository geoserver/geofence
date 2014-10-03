/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget.dialog;

import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;

import org.geoserver.geofence.gui.client.i18n.I18nProvider;
import org.geoserver.geofence.gui.client.model.UserGroup;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.widget.SaveStaus;
import org.geoserver.geofence.gui.client.widget.rule.detail.ProfileDetailsTabItem;
import org.geoserver.geofence.gui.client.widget.tab.TabWidget;


/**
 * ProfileDetailsEditDialog class.
 *
 * @author Tobia di Pisa
 *
 */
public class ProfileDetailsEditDialog extends Dialog
{

    /** The Constant PROFILE_DETAILS_DIALOG_ID. */
    public static final String PROFILE_DETAILS_DIALOG_ID = "profileDetailsDialog";

    /** The save status. */
    private SaveStaus saveStatus;

    /** The done. */
    private Button done;

    /** The model. */
    private UserGroup profile;

    /** The rules manager service remote. */
    private ProfilesManagerRemoteServiceAsync ProfilesManagerRemoteServiceAsync;

    /** The tab widget. */
    private TabWidget tabWidget;

    /**
     * Instantiates a new rule details edit dialog.
     *
     * @param rulesManagerServiceRemote
     *            the rules manager service remote
     */
    public ProfileDetailsEditDialog(ProfilesManagerRemoteServiceAsync ProfilesManagerRemoteServiceAsync)
    {
        this.ProfilesManagerRemoteServiceAsync = ProfilesManagerRemoteServiceAsync;

        setTabWidget(new TabWidget());

        setResizable(false);
        setButtons("");
        setClosable(true);
        setModal(true);
        setWidth(700);
        setHeight(427);
        setId(I18nProvider.getMessages().profileDialogId());

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
            setHeading("Editing Profile Details for Profile #" + profile.getId());
            this.tabWidget.add(new ProfileDetailsTabItem(PROFILE_DETAILS_DIALOG_ID, profile,
                    ProfilesManagerRemoteServiceAsync));

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
    public void setModel(UserGroup profile)
    {
        this.profile = profile;
    }

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.Component#getModel()
     */
    public UserGroup getModel()
    {
        return this.profile;
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

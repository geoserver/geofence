/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import java.util.Date;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

import org.geoserver.geofence.gui.client.form.GeofenceFormWidget;
import org.geoserver.geofence.gui.client.model.UserGroup;
import org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;


// TODO: Auto-generated Javadoc
/**
 * The Class AddProfileWidget.
 */
public class AddProfileWidget extends GeofenceFormWidget
{

    /** The submit event. */
    private EventType submitEvent;

    /** The close on submit. */
    private boolean closeOnSubmit;

    /** The profile. */
    protected UserGroup profile = new UserGroup();

    /** The gs manager service remote. */
    private GsUsersManagerRemoteServiceAsync gsManagerServiceRemote;

    /** The profiles manager service remote. */
    private ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote;

    /** The profile name. */
    private TextField<String> profileName;

    /**
     * Instantiates a new adds the profile widget.
     *
     * @param submitEvent
     *            the submit event
     * @param closeOnSubmit
     *            the close on submit
     */
    public AddProfileWidget(EventType submitEvent, boolean closeOnSubmit)
    {
        this.submitEvent = submitEvent;
        this.closeOnSubmit = closeOnSubmit;
    }

    /**
     * Gets the submit event.
     *
     * @return the submit event
     */
    protected EventType getSubmitEvent()
    {
        return this.submitEvent;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.form.IForm#execute()
     */
    public void execute()
    {
        this.saveStatus.setBusy("Operation in progress");
        this.profile.setId(-1);
        this.profile.setName(profileName.getValue());
        this.profile.setDateCreation(new Date());
        this.profile.setEnabled(true);

        if (this.closeOnSubmit)
        {
            cancel();
        }

        this.injectEvent();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.form.GeofenceFormWidget#addComponentToForm ()
     */
    @Override
    public void addComponentToForm()
    {
        fieldSet = new FieldSet();
        fieldSet.setHeading("Group Information");

        FormLayout layout = new FormLayout();
        layout.setLabelWidth(80);
        fieldSet.setLayout(layout);

        profileName = new TextField<String>();
        profileName.setAllowBlank(false);
        profileName.setFieldLabel("Group name");
        fieldSet.add(profileName);

        this.formPanel.add(fieldSet);

        addOtherComponents();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.form.GeofenceFormWidget#cancel()
     */
    @SuppressWarnings("deprecation")
    @Override
    public void cancel()
    {
        resetComponents();
        super.close();

    }

    /**
     * Reset components.
     */
    public void resetComponents()
    {
        this.profileName.reset();
        this.saveStatus.clearStatus("");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.widget.AddGenericAOIWidget# addOtherComponents()
     */
    /**
     * Adds the other components.
     */
    public void addOtherComponents()
    {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.form.GeofenceFormWidget#initSize()
     */
    @Override
    public void initSize()
    {
        setHeading( /* TODO: I18nProvider.getMessages().addAoiDialogTitle() */"Create new Group");
        setSize(420, 300);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.form.GeofenceFormWidget#initSizeFormPanel ()
     */
    @Override
    public void initSizeFormPanel()
    {
        formPanel.setHeaderVisible(false);
        formPanel.setSize(450, 350);
    }

    /* (non-Javadoc)
     * @see org.geoserver.geofence.gui.client.form.GeofenceFormWidget#injectEvent()
     */
    @Override
    public void injectEvent()
    {
        Dispatcher.forwardEvent(getSubmitEvent(), this.profile);
    }

    /**
     * Gets the profile.
     *
     * @return the profile
     */
    public UserGroup getProfile()
    {
        return profile;
    }

    /**
     * Sets the gs user service.
     *
     * @param gsManagerServiceRemote
     *            the new gs user service
     */
    public void setGsUserService(GsUsersManagerRemoteServiceAsync gsManagerServiceRemote)
    {
        this.gsManagerServiceRemote = gsManagerServiceRemote;
    }

    /**
     * Sets the profile service.
     *
     * @param profilesManagerServiceRemote
     *            the new profile service
     */
    public void setProfileService(ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote)
    {
        this.profilesManagerServiceRemote = profilesManagerServiceRemote;
    }

}

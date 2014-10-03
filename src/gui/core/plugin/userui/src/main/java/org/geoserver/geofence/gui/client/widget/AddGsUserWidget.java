/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import org.geoserver.geofence.gui.client.form.GeofenceFormWidget;
import org.geoserver.geofence.gui.client.model.BeanKeyValue;
import org.geoserver.geofence.gui.client.model.GSUser;
import org.geoserver.geofence.gui.client.model.UserGroup;
import org.geoserver.geofence.gui.client.service.GsUsersManagerRemoteServiceAsync;
import org.geoserver.geofence.gui.client.service.ProfilesManagerRemoteServiceAsync;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;


// TODO: Auto-generated Javadoc
/**
 * The Class AddGsUserWidget.
 */
public class AddGsUserWidget extends GeofenceFormWidget
{

    /** The submit event. */
    private EventType submitEvent;

    /** The close on submit. */
    private boolean closeOnSubmit;

    /** The user. */
    protected GSUser user = new GSUser();

    /** The user name. */
    private TextField<String> userName;

    /** The password. */
    private TextField<String> password;

    /** The full name. */
    private TextField<String> fullName;

    /** The e mail. */
    private TextField<String> eMail;

    private CheckBox isAdmin;

    /** The profiles combo box. */
    private ComboBox<UserGroup> profilesComboBox;

    /** The gs manager service remote. */
    private final GsUsersManagerRemoteServiceAsync gsManagerServiceRemote;

    /** The profiles manager service remote. */
    private final ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote;

    /**
     * Instantiates a new adds the gs user widget.
     *
     * @param submitEvent
     *            the submit event
     * @param closeOnSubmit
     *            the close on submit
     * @param profilesManagerServiceRemote
     * @param gsManagerServiceRemote
     */
    public AddGsUserWidget(EventType submitEvent, boolean closeOnSubmit, 
    		GsUsersManagerRemoteServiceAsync gsManagerServiceRemote, ProfilesManagerRemoteServiceAsync profilesManagerServiceRemote)
    {
        this.submitEvent = submitEvent;
        this.closeOnSubmit = closeOnSubmit;
        this.gsManagerServiceRemote = gsManagerServiceRemote;
        this.profilesManagerServiceRemote = profilesManagerServiceRemote;
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
        this.user.setId(-1);
        this.user.setName(userName.getValue());
        this.user.setPassword(password.getValue());
        this.user.setFullName(fullName.getValue());
        this.user.setEmailAddress(eMail.getValue());
        this.user.setDateCreation(new Date());
        this.user.setEnabled(true);
        this.user.setAdmin(isAdmin.getValue());
        //this.user.setProfile(profilesComboBox.getValue());

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
        fieldSet.setHeading("User Information");

        FormLayout layout = new FormLayout();
        layout.setLabelWidth(80);
        fieldSet.setLayout(layout);

        userName = new TextField<String>();
        userName.setAllowBlank(false);
        userName.setFieldLabel("User name");
        fieldSet.add(userName);

        password = new TextField<String>();
        password.setPassword(true);
        password.setAllowBlank(false);
        password.setFieldLabel("Password");
        fieldSet.add(password);

        fullName = new TextField<String>();
        fullName.setAllowBlank(false);
        fullName.setFieldLabel("Full name");
        fieldSet.add(fullName);

        eMail = new TextField<String>();
        eMail.setAllowBlank(false);
        eMail.setFieldLabel("e-mail");
        fieldSet.add(eMail);

        isAdmin = new CheckBox();
        isAdmin.setFieldLabel("Admin");
        fieldSet.add(isAdmin);

        //createProfilesComboBox();

        this.formPanel.add(fieldSet);

        addOtherComponents();
    }

    /**
     * Creates the profiles combo box.
     */
    private void createProfilesComboBox()
    {
        profilesComboBox = new ComboBox<UserGroup>();
        profilesComboBox.setFieldLabel("Group");
        profilesComboBox.setEmptyText("(No group available)");
        profilesComboBox.setDisplayField(BeanKeyValue.NAME.getValue());
        profilesComboBox.setEditable(false);
        profilesComboBox.setStore(getAvailableProfiles());
        profilesComboBox.setTypeAhead(true);
        profilesComboBox.setTriggerAction(TriggerAction.ALL);
        profilesComboBox.setAllowBlank(false);
        profilesComboBox.setLazyRender(false);
        // profilesComboBox.setWidth(150);

        profilesComboBox.addListener(Events.TriggerClick, new Listener<FieldEvent>()
            {

                public void handleEvent(FieldEvent be)
                {
                    profilesComboBox.getStore().getLoader().load();
                }
            });

        fieldSet.add(profilesComboBox);
    }

    /**
     * Gets the available profiles.
     *
     * @return the available profiles
     */
    private ListStore<UserGroup> getAvailableProfiles()
    {
        ListStore<UserGroup> availableProfiles = new ListStore<UserGroup>();
        RpcProxy<PagingLoadResult<UserGroup>> profileProxy = new RpcProxy<PagingLoadResult<UserGroup>>()
            {

                @Override
                protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<UserGroup>> callback)
                {
                    profilesManagerServiceRemote.getProfiles(
                        ((PagingLoadConfig) loadConfig).getOffset(), ((PagingLoadConfig) loadConfig).getLimit(),
                        false, callback);
                }

            };

        BasePagingLoader<PagingLoadResult<ModelData>> profilesLoader =
            new BasePagingLoader<PagingLoadResult<ModelData>>(
                profileProxy);
        profilesLoader.setRemoteSort(false);
        availableProfiles = new ListStore<UserGroup>(profilesLoader);

        return availableProfiles;
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
        this.userName.reset();
        this.password.reset();
        this.fullName.reset();
        this.eMail.reset();
        this.isAdmin.reset();
        //this.profilesComboBox.reset();
        //this.profilesComboBox.getStore().getLoader().load();
        //this.profiles.getProfilesInfo().getLoader().load();
        this.saveStatus.clearStatus("");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.widget.AddGenericAOIWidget#addOtherComponents()
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
        setHeading( /* TODO: I18nProvider.getMessages().addAoiDialogTitle() */"Create new User");
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
        Dispatcher.forwardEvent(getSubmitEvent(), this.user);
    }

    /**
     * Gets the profile.
     *
     * @return the profile
     */
    public GSUser getUser()
    {
        return user;
    }

}

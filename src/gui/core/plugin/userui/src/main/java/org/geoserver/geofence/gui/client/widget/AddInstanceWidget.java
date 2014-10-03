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
import org.geoserver.geofence.gui.client.model.GSInstance;
import org.geoserver.geofence.gui.client.service.InstancesManagerRemoteServiceAsync;


// TODO: Auto-generated Javadoc
/**
 * The Class AddInstanceWidget.
 */
public class AddInstanceWidget extends GeofenceFormWidget
{

    /** The submit event. */
    private EventType submitEvent;

    /** The close on submit. */
    private boolean closeOnSubmit;

    /** The instance. */
    protected GSInstance instance = new GSInstance();

    /** The instances manager service remote. */
    private InstancesManagerRemoteServiceAsync instancesManagerServiceRemote;

    /** The instance name. */
    private TextField<String> instanceName;

    /** The instance description. */
    private TextField<String> instanceDescription;

    /** The instance base url. */
    private TextField<String> instanceBaseURL;

    /** The instance username. */
    private TextField<String> instanceUsername;

    /** The instance password. */
    private TextField<String> instancePassword;

    /**
     * Instantiates a new adds the instance widget.
     *
     * @param submitEvent
     *            the submit event
     * @param closeOnSubmit
     *            the close on submit
     */
    public AddInstanceWidget(EventType submitEvent, boolean closeOnSubmit)
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
        this.instance.setId(-1);
        this.instance.setName(instanceName.getValue());
        this.instance.setDateCreation(new Date());
        this.instance.setDescription(instanceDescription.getValue());
        this.instance.setBaseURL(instanceBaseURL.getValue());
        this.instance.setUsername(instanceUsername.getValue());
        this.instance.setPassword(instancePassword.getValue());
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
        fieldSet.setHeading("Instance Information");

        FormLayout layout = new FormLayout();
        layout.setLabelWidth(80);
        fieldSet.setLayout(layout);

        instanceName = new TextField<String>();
        instanceName.setAllowBlank(false);
        instanceName.setFieldLabel("name");
        fieldSet.add(instanceName);

        instanceDescription = new TextField<String>();
        instanceDescription.setAllowBlank(false);
        instanceDescription.setFieldLabel("description");
        fieldSet.add(instanceDescription);

        instanceBaseURL = new TextField<String>();
        instanceBaseURL.setAllowBlank(false);
        instanceBaseURL.setFieldLabel("base url");
        fieldSet.add(instanceBaseURL);

        instanceUsername = new TextField<String>();
        instanceUsername.setAllowBlank(false);
        instanceUsername.setFieldLabel("username");
        fieldSet.add(instanceUsername);

        instancePassword = new TextField<String>();
        instancePassword.setAllowBlank(false);
        instancePassword.setPassword(true);
        instancePassword.setFieldLabel("password");
        fieldSet.add(instancePassword);

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
        this.instanceName.reset();
        this.instanceDescription.reset();
        this.instanceBaseURL.reset();
        this.instanceUsername.reset();
        this.instancePassword.reset();
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
        setHeading( /* TODO: I18nProvider.getMessages().addAoiDialogTitle() */"Create new Instance");
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
        Dispatcher.forwardEvent(getSubmitEvent(), this.instance);
    }

    /**
     * Gets the instance.
     *
     * @return the instance
     */
    public GSInstance getInstance()
    {
        return instance;
    }

    /*public void setGsUserService(GsUsersManagerServiceRemoteAsync gsManagerServiceRemote) {
        this.gsManagerServiceRemote = gsManagerServiceRemote;
    }*/

    /**
     * Sets the instance service.
     *
     * @param instancesManagerServiceRemote
     *            the new instance service
     */
    public void setInstanceService(InstancesManagerRemoteServiceAsync instancesManagerServiceRemote)
    {
        this.instancesManagerServiceRemote = instancesManagerServiceRemote;
    }

}

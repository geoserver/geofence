/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.form;

import org.geoserver.geofence.gui.client.widget.SaveStaus;
import org.geoserver.geofence.gui.client.widget.SaveStaus.EnumSaveStatus;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;

// TODO: Auto-generated Javadoc
/**
 * The Class GeofenceFormWidget.
 */
public abstract class GeofenceFormWidget extends Window implements IForm {

    /** The form panel. */
    public FormPanel formPanel = new FormPanel();

    /** The submit. */
    protected Button submit;

    /** The cancel. */
    protected Button cancel;

    /** The field set. */
    protected FieldSet fieldSet;

    /** The save status. */
    protected SaveStaus saveStatus;

    /**
     * Instantiates a new geo repo form widget.
     */
    public GeofenceFormWidget() {
        this.initializeWindow();
        this.initializeFormPanel();
        this.add(this.formPanel);
    }

    /**
     * Initialize form panel.
     */
    public void initializeFormPanel() {
        this.formPanel.setFrame(true);
        this.formPanel.setLayout(new FlowLayout());

        initSizeFormPanel();
        addComponentToForm();
        addButtons();
    }

    /**
     * Initialize window.
     */
    private void initializeWindow() {
        initSize();
        setResizable(false);

        addWindowListener(new WindowListener() {

            @Override
            public void windowHide(WindowEvent we) {
                reset();
            }

        });

        setLayout(new FitLayout());
        setModal(true);
        setPlain(true);
    }

    /**
     * Adds the buttons.
     */
    public void addButtons() {
        formPanel.setButtonAlign(HorizontalAlignment.LEFT);

        this.saveStatus = new SaveStaus();
        saveStatus.setAutoWidth(true);

        formPanel.getButtonBar().add(saveStatus);

        formPanel.getButtonBar().add(new FillToolItem());

        this.submit = new Button("SUBMIT");

        this.submit.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (formPanel.isValid())
                    execute();
            }

        });

        submit.setIconStyle("x-geofence-submit");

        formPanel.addButton(submit);

        this.cancel = new Button("CANCEL");

        this.cancel.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                cancel();
            }
        });

        this.cancel.setIconStyle("x-geofence-cancel");

        formPanel.addButton(cancel);
    }

    /**
     * Sets the save status.
     * 
     * @param status
     *            the status
     * @param message
     *            the message
     */
    public void setSaveStatus(EnumSaveStatus status, EnumSaveStatus message) {
        this.saveStatus.setIconStyle(status.getValue());
        this.saveStatus.setText(message.getValue());
    }

    /**
     * Reset.
     */
    public void reset() {

    }

    /**
     * Adds the component to form.
     */
    public abstract void addComponentToForm();

    /**
     * Inits the size.
     */
    public abstract void initSize();

    /**
     * Inits the size form panel.
     */
    public abstract void initSizeFormPanel();

    /**
     * Cancel.
     */
    public abstract void cancel();

    public void injectEvent() {
        // TODO Auto-generated method stub
        
    }
}

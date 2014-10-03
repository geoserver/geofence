/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.client.widget;

import org.geoserver.geofence.gui.client.widget.GeofenceUpdateWidget;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

import org.geoserver.geofence.gui.client.GeofenceEvents;
import org.geoserver.geofence.gui.client.GeofenceWidgetsData;
import org.geoserver.geofence.gui.client.SendType;
import org.geoserver.geofence.gui.client.SendType.SendTypeEnum;
import org.geoserver.geofence.gui.client.UpdateInterval;
import org.geoserver.geofence.gui.client.UpdateInterval.UpdateIntervalEnum;
import org.geoserver.geofence.gui.client.model.BeanKeyValue;
import org.geoserver.geofence.gui.client.model.User;


// TODO: Auto-generated Javadoc
/**
 * The Class UpdateUserWidget.
 */
public class UpdateUserWidget extends GeofenceUpdateWidget<User>
{

    /**
     * The Enum UpdateUserKey.
     */
    private enum UpdateUserKey
    {

        /** The USE r_ nam e_ id. */
        USER_NAME_ID("USER_NAME_UPDATE"),

        /** The EMAI l_ update. */
        EMAIL_UPDATE("EMAIL_UPDATE");

        /** The value. */
        private String value;

        /**
         * Instantiates a new update user key.
         *
         * @param value
         *            the value
         */
        UpdateUserKey(String value)
        {
            this.value = value;
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        public String getValue()
        {
            return value;
        }
    }

    /** The user name. */
    private TextField<String> userName;

    /** The email. */
    private TextField<String> email;

    /** The reduced content. */
    private CheckBox reducedContent;

    /** The combo types. */
    private ComboBox<SendType> comboTypes;

    /** The combo times. */
    private ComboBox<UpdateInterval> comboTimes;

    /** The store types. */
    private ListStore<SendType> storeTypes;

    /** The store times. */
    private ListStore<UpdateInterval> storeTimes;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.geoserver.geofence.gui.client.widget.GEOFENCEUpdateWidget#bind(com.extjs.gxt.ui.client
     * .data.BaseModel)
     */
    @Override
    public void bind(User user)
    {
        super.bind(user);
        // checkComponentsModifiability();
    }

    // private void checkComponentsModifiability() {
    // if (object.isEnabled()) {
    // this.email.setEnabled(false);
    // } else {
    // this.email.setAllowBlank(false);
    //
    // }
    //
    // }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.form.IForm#execute()
     */
    public void execute()
    {
        this.saveStatus.setBusy("Operation in progress");
        Dispatcher.forwardEvent(GeofenceEvents.UPDATE_USER, this.object);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.form.GEOFENCEFormWidget#addComponentToForm()
     */
    @Override
    public void addComponentToForm()
    {
        fieldSet = new FieldSet();
        fieldSet.setHeading("User Information");

        FormLayout layout = new FormLayout();
        layout.setLabelWidth(150);
        fieldSet.setLayout(layout);

        userName = new TextField<String>();
        userName.setEnabled(false);
        userName.setId(UpdateUserKey.USER_NAME_ID.getValue());
        userName.setName(BeanKeyValue.USER_NAME.getValue());
        userName.setFieldLabel("User Name");
        fieldSet.add(userName);

        email = new TextField<String>();
        email.setFieldLabel("Email");
        email.setId(UpdateUserKey.EMAIL_UPDATE.getValue());
        email.setName(BeanKeyValue.EMAIL.getValue());
        email.setEnabled(false);

        // email.setValidator(new Validator() {
        //
        // public String validate(Field<?> field, String value) {
        // if (((String) field.getValue()).matches(".+@.+\\.[a-z]+"))
        // return null;
        // return "Email not valid";
        // }
        // });

        fieldSet.add(email);

//        reducedContent = new CheckBox();
//        reducedContent.setId(BeanKeyValue.REDUCED_CONTENT_UPDATE.getValue());
//        reducedContent.setName(BeanKeyValue.REDUCED_CONTENT.getValue());
//        reducedContent.setFieldLabel("Hide Attributions");
//        reducedContent.setWidth(150);
//        reducedContent.setEnabled(true);
//
//        fieldSet.add(reducedContent);

        this.initCombo();

        this.formPanel.add(fieldSet);

    }

    /**
     * Inits the combo.
     */
    private void initCombo()
    {

        this.storeTypes = new ListStore<SendType>();
        this.storeTypes.add(GeofenceWidgetsData.getSendTypes());

        comboTypes = new ComboBox<SendType>();
        comboTypes.setFieldLabel("Type");
        comboTypes.setId(SendTypeEnum.TYPE.getValue());
        comboTypes.setName(SendTypeEnum.TYPE.getValue());
        comboTypes.setEmptyText("Select Type ...");
        comboTypes.setDisplayField(SendTypeEnum.TYPE.getValue());
        comboTypes.setEditable(false);
        comboTypes.setAllowBlank(false);
        comboTypes.setForceSelection(true);
        comboTypes.setStore(storeTypes);
        comboTypes.setTypeAhead(true);
        comboTypes.setTriggerAction(TriggerAction.ALL);

        fieldSet.add(comboTypes);

        this.storeTimes = new ListStore<UpdateInterval>();
        this.storeTimes.add(GeofenceWidgetsData.getTimes());

        comboTimes = new ComboBox<UpdateInterval>();
        comboTimes.setFieldLabel("Interval");
        comboTimes.setId(UpdateIntervalEnum.TIME.getValue());
        comboTimes.setName(UpdateIntervalEnum.TIME.getValue());
        comboTimes.setEmptyText("Select interval ...");
        comboTimes.setDisplayField(UpdateIntervalEnum.TIME.getValue());
        comboTimes.setEditable(false);
        comboTimes.setAllowBlank(false);
        comboTimes.setForceSelection(true);
        comboTimes.setStore(storeTimes);
        comboTimes.setTypeAhead(true);
        comboTimes.setTriggerAction(TriggerAction.ALL);
        fieldSet.add(comboTimes);

    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.form.GEOFENCEFormWidget#initSize()
     */
    @Override
    public void initSize()
    {
        setHeading("Update User");
        setSize(470, 250);

    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.form.GEOFENCEFormWidget#initSizeFormPanel()
     */
    @Override
    public void initSizeFormPanel()
    {
        formPanel.setHeaderVisible(false);
        formPanel.setSize(470, 250);

    }

    /*
     * (non-Javadoc)
     *
     * @see org.geoserver.geofence.gui.client.form.GEOFENCEFormWidget#cancel()
     */
    @SuppressWarnings("deprecation")
    @Override
    public void cancel()
    {
        super.close();
        this.formBinding.unbind();
    }
}
